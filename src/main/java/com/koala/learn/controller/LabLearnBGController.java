package com.koala.learn.controller;

import com.google.gson.Gson;
import com.koala.learn.commen.ServerResponse;
import com.koala.learn.component.JedisAdapter;
import com.koala.learn.dao.*;
import com.koala.learn.entity.*;
import com.koala.learn.service.LabLearnService;
import com.koala.learn.utils.RedisKeyUtil;
import com.koala.learn.utils.WekaUtils;
import com.koala.learn.utils.divider.IDivider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.WebApplicationContextUtils;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class LabLearnBGController {

    @Autowired
    Gson mGson;
    @Autowired
    JedisAdapter mJedisAdapter;
    @Autowired
    FeatureMapper mFeatureMapper;

    @Autowired
    LabMapper mLabMapper;

    @Autowired
    LabInstanceMapper mLabInstanceMapper;

    @Autowired
    ClassifierMapper mClassifierMapper;

    @Autowired
    ClassifierParamMapper mClassifierParamMapper;


    @Autowired
    DividerMapper mDividerMapper;

    @Autowired
    LabLearnService mLabLearnService;
    @RequestMapping("/learn/{labId}/{instance}/feature/{featureId}")
    @ResponseBody
    public ServerResponse addFeature(@RequestParam Map<String,String> param,
                                     @PathVariable("labId") Integer labId,
                                     @PathVariable("featureId") Integer featureId,
                                     @PathVariable("instance") Integer instanceId,
                                     HttpSession session) throws Exception {
        Lab lab = mLabMapper.selectByPrimaryKey(labId);
        Feature feature = mFeatureMapper.selectByPrimaryKey(featureId);
        LabInstance instance = mLabInstanceMapper.selectByPrimaryKey(instanceId);
        try {
            mLabLearnService.addFeature(session, lab,feature,param,instance);
            return ServerResponse.createBySuccess();
        } catch (IOException e) {
            e.printStackTrace();
            return ServerResponse.createByError();
        }
    }

    @RequestMapping("/learn/{labId}/{instanceId}/feature/handle")
    @ResponseBody
    public ServerResponse handlFeature(@RequestParam Map<String,String> param,
                                       @PathVariable("labId") Integer labId,
                                       @PathVariable("instanceId") Integer instanceId){
        try {
            return handleFeature(param,labId,instanceId);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage(e.getMessage());
        }
    }

    @RequestMapping("/learn/{labId}/{instance}/classifier/{classifierId}")
    @ResponseBody
    public ServerResponse addClassifier(@RequestParam Map<String,String> param,
                                        @PathVariable("labId") Integer labId,
                                        @PathVariable("classifierId") Integer classifierId,
                                        @PathVariable("instance") Integer instanceId){
        Classifier classifier = mClassifierMapper.selectByPrimaryKey(classifierId);
        List<ClassifierParam> paramList = mClassifierParamMapper.selectByClassifierId(classifierId);

        LabInstance instance = mLabInstanceMapper.selectByPrimaryKey(instanceId);
        String key = RedisKeyUtil.getClassifierInstanceKey(labId,instance.getId());

        for (ClassifierParam cp:paramList){
            if (param.containsKey(cp.getParamName())){
                cp.setDefaultValue(param.get(cp.getParamName()));
            }
        }
        classifier.setParams(paramList);
        mJedisAdapter.lpush(key,mGson.toJson(classifier));
        return ServerResponse.createBySuccess();
    }

    @RequestMapping("/learn/divider/{labId}/{instance}")
    @ResponseBody
    public ServerResponse divideData(@PathVariable("labId") Integer labId,
                                     @PathVariable("instance") Integer instanceId,
                                     @RequestParam Map<String,String> param,
                                     HttpSession session){
        try {
            String fileInstanceKey = RedisKeyUtil.getFileInstanceKey(labId,instanceId);
            String key = RedisKeyUtil.getDividerInstanceKey(labId,instanceId);
            Divider d = mDividerMapper.selectByPrimaryKey(Integer.valueOf(param.get("dividerId")));
            d.setRadio(param.get("radio"));
            param.put("instanceId",instanceId+"");
            param.put("labId",labId+"");
            mJedisAdapter.set(key,mGson.toJson(d));
            File input = new File(mJedisAdapter.lrange(fileInstanceKey,0,1).get(0));
            ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
            IDivider divider = (IDivider) ac.getBean(param.get("type"));
            divider.divide(input,param);
            return ServerResponse.createBySuccessMessage("训练集和测试集划分完成");
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ServerResponse.createByErrorMessage(e.getMessage());
        }
    }

    public ServerResponse handleFeature(Map<String,String> param,
                                        Integer labId,Integer instanceId) throws Exception {
        Lab lab = mLabMapper.selectByPrimaryKey(labId);
        System.out.println(param);
        Instances instances = WekaUtils.readFromFile(lab.getFile().replace("csv","arff"));
        instances.setClassIndex(instances.numAttributes()-1);
        String[] options = new String[2];
        options[0] = "-R";
        List<String> selected = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (String key:param.keySet()){
            if (key.startsWith("feature")){
                selected.add(param.get(key));
            }
        }
        for (int i=0;i<instances.numAttributes()-1;i++){
            if (!selected.contains(instances.attribute(i).name())){
                sb.append(i+1).append(",");
            }
        }
        options[1] = sb.substring(0,sb.length()-1).toString();
        System.out.println(Arrays.toString(options));
        Filter filter = new Remove();
        filter.setOptions(options);
        filter.setInputFormat(instances);
        Instances out = Filter.useFilter(instances,filter);
        ArffSaver saver = new ArffSaver();
        File parent = new File(new File(lab.getFile()).getParent()+"/");
        File outFile = new File(parent,sb.toString().hashCode()+".arff");
        System.out.println(outFile.getAbsolutePath());
        saver.setFile(outFile);
        saver.setInstances(out);
        saver.writeBatch();
        String fileKey = RedisKeyUtil.getFileInstanceKey(lab.getId(),instanceId);
        mJedisAdapter.lpush(fileKey,outFile.getAbsolutePath());
        return ServerResponse.createBySuccess();
    }
}
