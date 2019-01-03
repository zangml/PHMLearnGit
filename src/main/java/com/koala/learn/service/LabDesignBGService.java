package com.koala.learn.service;

import com.google.gson.Gson;
import com.koala.learn.Const;
import com.koala.learn.commen.ServerResponse;
import com.koala.learn.component.HostHolder;
import com.koala.learn.component.JedisAdapter;
import com.koala.learn.dao.*;
import com.koala.learn.entity.*;
import com.koala.learn.utils.PythonUtils;
import com.koala.learn.utils.RedisKeyUtil;
import com.koala.learn.utils.WekaUtils;
import com.koala.learn.utils.divider.IDivider;
import com.koala.learn.utils.feature.IFeature;
import com.koala.learn.vo.FeatureVo;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpSession;

import org.springframework.web.multipart.MultipartFile;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;

/**
 * Created by koala on 2018/1/12.
 */
@Service
public class LabDesignBGService {

    private static Logger logger = LoggerFactory.getLogger(LabDesignBGService.class);
    @Autowired
    JedisAdapter mJedisAdapter;
    @Autowired
    ClassifierMapper mClassifierMapper;

    @Autowired
    FeatureParamMapper mFeatureParamMapper;

    @Autowired
    ClassifierParamMapper mClassifierParamMapper;

    @Autowired
    DividerMapper mDividerMapper;

    @Autowired
    LabGroupMapper groupMapper;

    @Autowired
    HostHolder mHolder;

    @Autowired
    BuildinFileMapper fileMapper;

    @Autowired
    Gson mGson;

    public BuildinFile getFileById(int id){
        return fileMapper.selectByPrimaryKey(id);
    }

    public void saveFeature(Lab lab, Feature feature, Map<String,String> map){

        String featureKey = RedisKeyUtil.getFeatureKey(lab.getId());
        List<FeatureParam> paramList = mFeatureParamMapper.selectAllByFeatureId(feature.getId());

        FeatureVo vo = new FeatureVo();
        vo.setFeature(feature);
        for (FeatureParam param:paramList){
            if (map.get(param.getShell()) != null){
                param.setDefaultValue(map.get(param.getShell()));
            }
        }
        vo.setParamList(paramList);
        Gson gson = new Gson();
        mJedisAdapter.lpush(featureKey,gson.toJson(vo));
    }
    public void savePre(Lab lab, Feature feature, Map<String,String> map){

        String preKey = RedisKeyUtil.getPreKey(lab.getId());
        System.out.println(preKey);
        List<FeatureParam> paramList = mFeatureParamMapper.selectAllByFeatureId(feature.getId());

        FeatureVo vo = new FeatureVo();
        vo.setFeature(feature);
        for (FeatureParam param:paramList){
            if (map.get(param.getShell()) != null){
                param.setDefaultValue(map.get(param.getShell()));
            }
        }
        vo.setParamList(paramList);
        Gson gson = new Gson();
        mJedisAdapter.lpush(preKey,gson.toJson(vo));
    }


    public File addFeature(HttpSession session, Feature feature, Map<String,String> param, Lab lab) throws Exception {
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
        IFeature filter = (IFeature) ac.getBean(feature.getLabel());
        System.out.println(feature.getName());
        System.out.println(param.size());
        List<String> paramList = new ArrayList<>();
        for (String key:param.keySet()){
            if (StringUtils.isNotBlank(param.get(key))){
                paramList.add(key);
                paramList.add(param.get(key));
            }
        }
        String[] options;
        StringBuilder name = new StringBuilder(feature.getLabel());
        if (paramList.size()>0){
            options = new String[paramList.size()];
            for (int i=0;i<options.length;i++){
                options[i] = paramList.get(i);
                name.append(paramList.get(i));
            }
            System.out.println(options.length);
            System.out.println(options[0]+" " +options[1]);
            filter.setOptions(options);
        }

        String fileKey = RedisKeyUtil.getFileKey(lab.getId());
        File input = null;
        if (mJedisAdapter.llen(fileKey) >0){
            input = new File(mJedisAdapter.lrange(fileKey,0,1).get(0));
        }else {
            input = new File(lab.getFile().replace("csv","arff"));
        }

        ArffLoader loader = new ArffLoader();
        try {
            System.out.println(input.getAbsolutePath());
            loader.setSource(input);
            Instances instances = loader.getDataSet();
            instances.setClassIndex(instances.numAttributes()-1);
            if(feature.getType()==2||feature.getType()==3) {
                System.out.println("feature.getType==2||feature.getType()==3");
                File out = new File(input.getParent(), input.getName().replace(".arff", "") + name + ".csv");
                filter.filter(instances, input, out);
                out= WekaUtils.csv2arff(out);
                return out;
            }else{
                File out = new File(input.getParent(), input.getName().replace(".arff", "") + name + ".arff");
                filter.filter(instances, input, out);
                return out;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }

    }

    public void saveClassifier(Map<String,String> param,Integer labId){
        Classifier classifier = mClassifierMapper.selectByLabel(param.get("classifier"));
        List<ClassifierParam> paramList = mClassifierParamMapper.selectByClassifierId(classifier.getId());
        for (ClassifierParam cp:paramList){
            if (param.containsKey(cp.getParamName())){
                cp.setDefaultValue(param.get(cp.getParamName()));
            }
        }
        classifier.setParams(paramList);
        String classifierKey = RedisKeyUtil.getClassifierKey(labId);
        Gson gson = new Gson();
        mJedisAdapter.lpush(classifierKey,gson.toJson(classifier));
    }

    public void saveDivider(Map<String,String> param,Integer labId,HttpSession session){
        String key = RedisKeyUtil.getDividerKey(labId);
        String id = param.get("dividerId");
        Divider d = mDividerMapper.selectByPrimaryKey(Integer.valueOf(id));
        d.setRadio(param.get("radio"));
        Gson gson = new Gson();
        mJedisAdapter.set(key,gson.toJson(d));
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
        IDivider divider = (IDivider) ac.getBean(param.get("type"));

        String fileKey = RedisKeyUtil.getFileKey(labId);
        long length = mJedisAdapter.llen(fileKey);
        param.put("labId",labId+"");
        if (length>0){
            String path = mJedisAdapter.lrange(fileKey,0,1).get(0);
            File input = new File(path);
            divider.divide(input,param);
        }
    }

    public ServerResponse finish(Integer groupId){
        LabGroup group = groupMapper.selectByPrimaryKey(groupId);
        User user = mHolder.getUser();
        if (group==null || group.getOwner() != user.getId() ){
            return ServerResponse.createByErrorMessage("缺乏权限");
        }
        group.setPublish(1);
        group.setPublishTime(new Date());
        groupMapper.updateByPrimaryKey(group);
        return ServerResponse.createBySuccessMessage("申请成功，我们会尽快审核上线");
    }

    public ServerResponse uploadClassifier(HttpSession session,MultipartFile classifierFile,MultipartFile testFile, Map<String,Object> param) throws IOException {
        File cFile = new File(Const.UPDATE_CLASS_ROOT,classifierFile.getOriginalFilename());
        classifierFile.transferTo(cFile);
        Classifier classifier = new Classifier();
        classifier.setDes(param.get("des").toString());
        classifier.setName(param.get("name").toString());
        classifier.setLabId(mHolder.getUser().getId());
        classifier.setPath(cFile.getAbsolutePath());
        mClassifierMapper.insert(classifier);
        for (String key:param.keySet()){
            if (key.startsWith("paramName")){
                if (StringUtils.isNotBlank(param.get(key).toString())){
                    ClassifierParam classifierParam = new ClassifierParam();
                    classifierParam.setClassifierId(classifier.getId());
                    classifierParam.setParamName(param.get(key).toString());
                    classifierParam.setParamDes(param.get(key.replace("Name","Des")).toString());
                    classifierParam.setDefaultValue(param.get(key.replace("Name","Value")).toString());
                    mClassifierParamMapper.insert(classifierParam);
                }
            }
        }
        File test = new File(Const.UPDATE_CLASS_TEST_ROOT,testFile.getOriginalFilename());
        testFile.transferTo(test);
        List<File> dividerFiles = divideFile(test,0.8f);
        StringBuilder sb = new StringBuilder("python ");
        sb.append(cFile.getAbsolutePath()).append(" ");
        sb.append("test=").append(dividerFiles.get(0).getAbsolutePath()).append(" ");
        sb.append("train=").append(dividerFiles.get(1).getAbsolutePath());
        logger.info(sb.toString());
        String res = PythonUtils.execPy(sb.toString());
        System.out.println(res);
        try{
            Result result = mGson.fromJson(res,Result.class);
            mJedisAdapter.set(RedisKeyUtil.getUpClassKey(classifier.getId()),res);
            return ServerResponse.createBySuccess();
        }catch (Exception e){
            return ServerResponse.createBySuccessMessage(res);
        }


    }

    private List<File> divideFile(File source,float radio) throws IOException {
        File testFile = new File(source.getParent(),source.getName().replace(".csv","test.csv"));
        File trainFile = new File(source.getParent(),source.getName().replace(".csv","train.csv"));
        CSVLoader csvLoader = new CSVLoader();
        csvLoader.setSource(source);
        Instances instances = csvLoader.getDataSet();
        Instances train = new Instances(instances,0);
        Instances test = new Instances(instances,0);
        int size = (int) (instances.size()*radio);
        Set<Integer> set = new HashSet<>();
        Random random = new Random();
        while (set.size()<size){
            set.add(random.nextInt(instances.size()));
        }
        for (int i=0;i<instances.size();i++){
            if (set.contains(i)){
                train.add(instances.get(i));
            }else {
                test.add(instances.get(i));
            }
        }

        CSVSaver saver = new CSVSaver();
        saver.setFile(trainFile);
        saver.setInstances(train);
        saver.writeBatch();

        saver.setFile(testFile);
        saver.setInstances(test);
        saver.writeBatch();
        return Arrays.asList(testFile,trainFile);

    }
}
