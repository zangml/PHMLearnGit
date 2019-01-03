package com.koala.learn.service;

import com.google.gson.Gson;
import com.koala.learn.component.JedisAdapter;
import com.koala.learn.dao.FeatureParamMapper;
import com.koala.learn.entity.*;
import com.koala.learn.utils.PythonUtils;
import com.koala.learn.utils.RedisKeyUtil;
import com.koala.learn.utils.WekaUtils;
import com.koala.learn.utils.feature.IFeature;
import com.koala.learn.utils.treat.ViewUtils;
import com.koala.learn.vo.FeatureVo;
import com.koala.learn.vo.LabViewVo;

import org.apache.commons.lang.StringUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * Created by koala on 2018/1/16.
 */
@Service
public class LabLearnService {

    @Autowired
    JedisAdapter mJedisAdapter;
    @Autowired
    Gson mGson;
    @Autowired
    FeatureParamMapper mFeatureParamMapper;

    private static final Logger logger = LoggerFactory.getLogger(LabLearnService.class);

    public List<String> resolveAttribute(Lab lab,Integer instanceId) throws IOException {
        List<String> list = null;
        String value = mJedisAdapter.get(RedisKeyUtil.getAttributeListKey(lab.getId()));
        if (value==null){
            String fileInstanceKey = RedisKeyUtil.getFileInstanceKey(lab.getId(),instanceId);
            System.out.println(fileInstanceKey);
            File input = null;
            if (mJedisAdapter.llen(fileInstanceKey) >0){
                input = new File(mJedisAdapter.lrange(fileInstanceKey,0,1).get(0));
            }else {
                input = new File(lab.getFile().replace("csv","arff"));
            }
            list = resolveAttribute(input,lab.getId());
        }else {
            list = mGson.fromJson(value,List.class);
        }
        return list;
    }

    public List<LabViewVo> getLabViewList(Lab lab){
        List<LabViewVo> vos = new ArrayList<>();
        Map<String,String> map = mJedisAdapter.hget(RedisKeyUtil.getLabViewKey(lab.getId()));
        Map<Integer,String> typeMap = ViewUtils.getTypeMap();
        for (String key:map.keySet()){
            LabViewVo vo = new LabViewVo(Integer.valueOf(key),map.get(key),typeMap.get(Integer.valueOf(key)));
            vos.add(vo);
        }
        return vos;
    }


    private List<String> resolveAttribute(File file,Integer id) throws IOException {
        List<String> attributeList = new ArrayList<>();
        Instances instances = WekaUtils.readFromFile(file.getAbsolutePath());
        for (int i=0;i<instances.numAttributes()-1;i++){
            attributeList.add(instances.attribute(i).name());
        }
        mJedisAdapter.set(RedisKeyUtil.getAttributeListKey(id),mGson.toJson(attributeList));
        return attributeList;
    }

    public void addFeature(HttpSession session, Lab lab, Feature feature, Map<String,String> map, LabInstance instance) throws Exception {
        String featureKey = RedisKeyUtil.getFeatureInstanceKey(lab.getId(),instance.getId());
        System.out.println(featureKey);
        File parent = new File(new File(lab.getFile()).getParent()+"/");
        if (!parent.exists()){
            parent.mkdirs();
        }
        System.out.println(parent.getAbsolutePath());
        System.out.println(map);
        String[] options = getOptions(map);
        File input = null;
        File output = null;
        String name = null;

        String fileKey = RedisKeyUtil.getFileInstanceKey(lab.getId(),instance.getId());
        if (mJedisAdapter.llen(fileKey)>0){
            String lastFile = mJedisAdapter.lrange(fileKey,0,1).get(0);
            input = new File(lastFile);
            name = getName(options,input.getName().replace(".arff","")+feature.getLabel());
            if(feature.getType()==2||feature.getType()==3) {
                output = new File(parent, name + ".csv");
            }else {
                output = new File(parent, name + ".arff");
            }
        }else {
            name = getName(options,feature.getLabel());
            input = new File(lab.getFile().replace("csv","arff"));
            if(feature.getType()==2||feature.getType()==3) {
                output = new File(parent, name + ".csv");
            }else {
                output = new File(parent, name + ".arff");
            }
        }

        System.out.println(input.getAbsolutePath());
        System.out.println(output.getAbsolutePath());
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
        IFeature filter = (IFeature) ac.getBean(feature.getLabel());
        filter.setOptions(options);
        ArffLoader loader = new ArffLoader();
        loader.setFile(input);
        Instances instances = loader.getDataSet();
        instances.setClassIndex(instances.numAttributes()-1);
        filter.filter(instances,input,output);
        output=WekaUtils.csv2arff(output);
        mJedisAdapter.lpush(fileKey,output.getAbsolutePath());


        List<FeatureParam> paramList = mFeatureParamMapper.selectAllByFeatureId(feature.getId());
        FeatureVo vo = new FeatureVo();
        vo.setFeature(feature);
        for (FeatureParam param:paramList){
            if (map.get(param.getShell()) != null){
                param.setDefaultValue(map.get(param.getShell()));
            }
        }
        vo.setParamList(paramList);
        vo.setName(name);
        Gson gson = new Gson();
        mJedisAdapter.lpush(featureKey,gson.toJson(vo));
    }


    private String[] getOptions(Map<String,String> param){
        List<String> paramList = new ArrayList<>();
        for (String key:param.keySet()){
            if (StringUtils.isNotBlank(param.get(key))){
                paramList.add(key);
                paramList.add(param.get(key));
            }
        }
        String[] options;
        if (paramList.size()>0){
            options = new String[paramList.size()];
            for (int i=0;i<options.length;i++){
                options[i] = paramList.get(i);
            }
            return options;
        }
        return null;
    }

    public String getName(String[] options,String featureName){
        StringBuilder sb = new StringBuilder(featureName);
        if (options != null){
            for (String s:options){
                sb.append(s);
            }
        }

        return sb.toString();
    }

    public Result findCache(Integer labId,Integer instanceId,Classifier classifier){
        String dividerOutKey = RedisKeyUtil.getDividerOutKey(labId,instanceId);
        File train = new File(mJedisAdapter.hget(dividerOutKey,"train"));
        String classifierStr = mGson.toJson(classifier);
        String cacheKye = RedisKeyUtil.getCacheKey(labId,train.getAbsolutePath(),classifier.getName()+classifierStr.hashCode());
        String cacheValue = mJedisAdapter.get(cacheKye);
        System.out.println(cacheKye);
        System.out.println(cacheValue);
        if (cacheValue != null){
            Result result = mGson.fromJson(cacheValue,Result.class);
            String resKey = RedisKeyUtil.getResInstanceKey(labId,instanceId,classifier);
            System.out.println(resKey);
            mJedisAdapter.hset(resKey,"Accuracy",result.getAccuracy()+"");
            mJedisAdapter.hset(resKey,"Precision",result.getPrecision()+"");
            mJedisAdapter.hset(resKey,"Recall",result.getRecall()+"");
            mJedisAdapter.hset(resKey,"F-Measure",result.getfMeasure()+"");
            mJedisAdapter.hset(resKey,"ROC-Area",result.getRocArea()+"");
            mJedisAdapter.hset(resKey,"featureImportances",mGson.toJson(result.getFeatureImportances()));
            return result;
        }
        return null;
    }
    public RegResult findCache2(Integer labId, Integer instanceId, Classifier classifier){
        String dividerOutKey = RedisKeyUtil.getDividerOutKey(labId,instanceId);
        File train = new File(mJedisAdapter.hget(dividerOutKey,"train"));
        String classifierStr = mGson.toJson(classifier);
        String cacheKye = RedisKeyUtil.getCacheKey(labId,train.getAbsolutePath(),classifier.getName()+classifierStr.hashCode());
        String cacheValue = mJedisAdapter.get(cacheKye);
        System.out.println(cacheKye);
        System.out.println(cacheValue);
        if (cacheValue != null){
            RegResult regResult = mGson.fromJson(cacheValue,RegResult.class);
            String resKey = RedisKeyUtil.getResInstanceKey(labId,instanceId,classifier);
            System.out.println(resKey);
            mJedisAdapter.hset(resKey,"varianceScore",regResult.getVarianceScore()+"");
            mJedisAdapter.hset(resKey,"absoluteError",regResult.getAbsoluteError()+"");
            mJedisAdapter.hset(resKey,"squaredError",regResult.getSquaredError()+"");
            mJedisAdapter.hset(resKey,"medianSquaredError",regResult.getMedianSquaredError()+"");
            mJedisAdapter.hset(resKey,"r2Score",regResult.getR2Score()+"");
            mJedisAdapter.hset(resKey,"featureImportances",mGson.toJson(regResult.getFeatureImportances()));
            return regResult;
        }
        return null;
    }

    public List<Classifier> getSelectedClassifier(Integer labId,Integer instanceId){
        String key = RedisKeyUtil.getClassifierInstanceKey(labId,instanceId);
        List<Classifier> classifiers = new ArrayList<>();
        if (mJedisAdapter.llen(key)==0){
            return classifiers;
        }else {
            List<String> values = mJedisAdapter.lrange(key,0,mJedisAdapter.llen(key));
            for (String value:values){
                Classifier classifier = mGson.fromJson(value,Classifier.class);
                StringBuilder sb = new StringBuilder();
                for (ClassifierParam param:classifier.getParams()){
                    if (!TextUtils.isEmpty(param.getParamName())){
                        sb.append(param.getParamName()).append(":").append(param.getDefaultValue()).append("\n");
                    }
                }
                classifier.setDes(sb.toString());
                classifiers.add(classifier);
            }
            return classifiers;
        }
    }
    public Result cal(Integer labId,Integer instanceId,HttpSession session,Classifier classifier){
        try {
            String dividerOutKey = RedisKeyUtil.getDividerOutKey(labId,instanceId);
            File train = new File(mJedisAdapter.hget(dividerOutKey,"train"));
            File test = new File(mJedisAdapter.hget(dividerOutKey,"test"));
            String classifierStr = mGson.toJson(classifier);
            String[] options = resolveOptions(classifier);
            if (classifier.getPath().endsWith("py")){
                File csvTrain = WekaUtils.arff2csv(train);
                File csvTest = WekaUtils.arff2csv(test);
                StringBuilder sb = new StringBuilder("python ");
                sb.append(classifier.getPath());
                for (int i=0;i<options.length;i=i+2){
                    sb.append(" ").append(options[i]).append("=").append(options[i+1]);
                }
                sb.append(" train=").append(csvTrain.getAbsolutePath()).append(" test=").append(csvTest.getAbsolutePath());
                logger.info(sb.toString());
                String resParam = PythonUtils.execPy(sb.toString());
                Result result = mGson.fromJson(resParam,Result.class);
                List<String> res = Arrays.asList(classifier.getName(),result.getRecall()+"",result.getAccuracy()+"",result.getPrecision()+"",result.getfMeasure()+"",result.getRocArea()+"");
                String resKey = RedisKeyUtil.getResInstanceKey(labId,instanceId,classifier);
                mJedisAdapter.hset(resKey,"Accuracy",result.getAccuracy()+"");
                mJedisAdapter.hset(resKey,"Precision",result.getPrecision()+"");
                mJedisAdapter.hset(resKey,"Recall",result.getRecall()+"");
                mJedisAdapter.hset(resKey,"F-Measure",result.getfMeasure()+"");
                mJedisAdapter.hset(resKey,"ROC-Area",result.getRocArea()+"");
                mJedisAdapter.hset(resKey,"FeatureImportances",mGson.toJson(result.getFeatureImportances()));
                String cacheKye = RedisKeyUtil.getCacheKey(labId,train.getAbsolutePath(),classifier.getName()+classifierStr.hashCode());
                mJedisAdapter.set(cacheKye,mGson.toJson(result));
                return result;
            }else {
                ArffLoader loader = new ArffLoader();
                loader.setFile(train);
                Instances itrain = loader.getDataSet();
                itrain.setClassIndex(itrain.numAttributes()-1);

                loader.setFile(test);
                Instances itest = loader.getDataSet();
                itest.setClassIndex(itrain.numAttributes()-1);

                ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
                AbstractClassifier c = (AbstractClassifier) ac.getBean(classifier.getPath());
                c.buildClassifier(itrain);
                Evaluation evaluation = new Evaluation(itrain);
                evaluation.evaluateModel(c,itest);
                System.out.println(evaluation.toClassDetailsString());
                System.out.println(evaluation.toSummaryString());
                System.out.println(evaluation.toMatrixString());
                List<String> res = Arrays.asList(classifier.getName(),evaluation.recall(1)+"",
                        evaluation.pctCorrect()/100+"",
                        evaluation.precision(1)+"",
                        evaluation.fMeasure(1)+"",
                        evaluation.areaUnderROC(1)+"");
                String resKey = RedisKeyUtil.getResInstanceKey(labId,instanceId,classifier);
                mJedisAdapter.hset(resKey,"Accuracy",evaluation.pctCorrect()/100+"");
                mJedisAdapter.hset(resKey,"Precision",evaluation.precision(1)+"");
                mJedisAdapter.hset(resKey,"Recall",evaluation.recall(1)+"");
                mJedisAdapter.hset(resKey,"F-Measure",evaluation.fMeasure(1)+"");
                mJedisAdapter.hset(resKey,"ROC-Area",evaluation.areaUnderROC(1)+"");

                Result result = new Result();
                result.setAccuracy(evaluation.pctCorrect()/100);
                result.setPrecision(evaluation.precision(1));
                result.setRecall(evaluation.recall(1));
                result.setfMeasure(evaluation.fMeasure(1));
                result.setRocArea(evaluation.areaUnderROC(1));
                String cacheKye = RedisKeyUtil.getCacheKey(labId,train.getAbsolutePath(),classifier.getName()+classifierStr.hashCode());
                mJedisAdapter.set(cacheKye,mGson.toJson(result));
                return result;
            }

        } catch (Exception e) {
            logger.error("运算失败"+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public RegResult cal2(Integer labId,Integer instanceId,HttpSession session,Classifier classifier){
        try {
            String dividerOutKey = RedisKeyUtil.getDividerOutKey(labId,instanceId);
            File train = new File(mJedisAdapter.hget(dividerOutKey,"train"));
            File test = new File(mJedisAdapter.hget(dividerOutKey,"test"));
            String classifierStr = mGson.toJson(classifier);
            String[] options = resolveOptions(classifier);
            if (classifier.getPath().endsWith("py")){
                File csvTrain = WekaUtils.arff2csv(train);
                File csvTest = WekaUtils.arff2csv(test);
                StringBuilder sb = new StringBuilder("python ");
                sb.append(classifier.getPath());
                for (int i=0;i<options.length;i=i+2){
                    sb.append(" ").append(options[i]).append("=").append(options[i+1]);
                }
                sb.append(" train=").append(csvTrain.getAbsolutePath()).append(" test=").append(csvTest.getAbsolutePath());
                logger.info(sb.toString());
                String resParam = PythonUtils.execPy(sb.toString());
                RegResult regResult = mGson.fromJson(resParam,RegResult.class);
                List<String> res = Arrays.asList(classifier.getName(),regResult.getVarianceScore() + "", regResult.getAbsoluteError() + "",
                        regResult.getSquaredError() + "", regResult.getMedianSquaredError() + "", regResult.getR2Score() + "");
                String resKey = RedisKeyUtil.getResInstanceKey(labId,instanceId,classifier);
                mJedisAdapter.hset(resKey,"varianceScore",regResult.getVarianceScore()+"");
                mJedisAdapter.hset(resKey,"absoluteError",regResult.getAbsoluteError()+"");
                mJedisAdapter.hset(resKey,"squaredError",regResult.getSquaredError()+"");
                mJedisAdapter.hset(resKey,"medianSquaredError",regResult.getMedianSquaredError()+"");
                mJedisAdapter.hset(resKey,"r2Score",regResult.getR2Score()+"");
                mJedisAdapter.hset(resKey,"featureImportances",mGson.toJson(regResult.getFeatureImportances()));
                String cacheKye = RedisKeyUtil.getCacheKey(labId,train.getAbsolutePath(),classifier.getName()+classifierStr.hashCode());
                mJedisAdapter.set(cacheKye,mGson.toJson(regResult));
                return regResult;
            }

        } catch (Exception e) {
            logger.error("运算失败"+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public String[] resolveOptions(Classifier classifier){
        List<String> optionList = new ArrayList<>();
        for (ClassifierParam cp:classifier.getParams()){
            if (StringUtils.isNotBlank(cp.getDefaultValue())){
                optionList.add(cp.getParamName());
                optionList.add(cp.getDefaultValue());
            }
        }
        String[] options = new String[optionList.size()];
        for (int i=0;i<optionList.size();i=i+2){
            options[i] =optionList.get(i);
            options[i+1] = optionList.get(i+1);
        }
        return options;
    }


}
