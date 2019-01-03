package com.koala.learn.controller;

import com.google.gson.Gson;
import com.koala.learn.component.JedisAdapter;
import com.koala.learn.dao.DividerMapper;
import com.koala.learn.dao.LabGroupMapper;
import com.koala.learn.dao.LabMapper;
import com.koala.learn.entity.*;
import com.koala.learn.service.LabService;
import com.koala.learn.service.LabDesignerService;
import com.koala.learn.service.LabLearnService;
import com.koala.learn.utils.*;
import com.koala.learn.vo.FeatureVo;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * Created by koala on 2017/12/28.
 */
@Controller
public class LabDesignController {

    @Autowired
    LabDesignerService mLabDesignerService;
    @Autowired
    LabMapper mLabMapper;


    @Autowired
    LabService mLabService;

    @Autowired
    JedisAdapter mAdapter;

    @Autowired
    Gson mGson;

    @Autowired
    DividerMapper mDividerMapper;

    @Autowired
    LabLearnService mLabLearnService;

    @Autowired
    ThreadPoolTaskExecutor mExecutor;

    @Autowired
    LabGroupMapper mLabGroupMapper;

    @Autowired
    JedisAdapter mJedisAdapter;

    private static Logger logger = LoggerFactory.getLogger(LabDesignController.class);

    @RequestMapping("/design")
    public String design(Model model) {
        model.addAttribute("files", mLabDesignerService.getBuildinFileList());
        return "design/createGroup";
    }

    @RequestMapping("/design/group")
    public String createGroup(@RequestParam(name = "fileOption") Integer fileId,
                              @RequestParam("title") String title,
                              @RequestParam("des") String des,
                              @RequestParam("aim") String aim,
                              @RequestParam("type") final Integer type,
                              @RequestParam(name = "file", required = false) MultipartFile file, Model model, HttpSession session) throws IOException {
        System.out.println("----------------------" + fileId + "-------------------");
        LogUtils.log(Long.toString(file.getSize()));
        if (file.getSize() == 0) {
            LabGroup lab = mLabDesignerService.createLabGroup(title, des, aim, type);
            mLabDesignerService.addFile(lab, fileId, type);
            session.setAttribute("labGroup", lab);
            return "redirect:page?labGroup=" + lab.getId();
        } else if (file.getSize() > 104857600l) {
            model.addAttribute("error", "文件大小不能超过100M");
            return "common/error";
        } else {
            LabGroup lab = mLabDesignerService.createLabGroup(title, des, aim, type);
            mLabDesignerService.addFile(lab, file, type);
            session.setAttribute("labGroup", lab);
            return "redirect:page?labGroup=" + lab.getId();
        }

    }

    @RequestMapping("/design/page")
    public String goLabPage(Model model, HttpSession session, @RequestParam(value = "labGroup") Integer groupId) {
        LabGroup group = mLabDesignerService.selectByGroupId(groupId);
        session.setAttribute("labGroup", group);
        model.addAttribute("labGroup", group);
        List<Lab> labs = mLabDesignerService.selectLabsByGroup(group.getId());
        model.addAttribute("labs", labs);
        return "design/labpage";
    }

    @RequestMapping("/design/lab/{groupId}")
    public String createLab(@PathVariable("groupId") Integer groupId, @RequestParam("labName") String labName) {

        try {
            Lab lab = mLabDesignerService.createLab(groupId, labName);
            return "redirect:/design/" + lab.getId() + "/lab_1";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "common/error";
    }

    @RequestMapping("/design/{labId}/lab_1")
    public String goLab2(@PathVariable("labId") Integer labId, Model model) {
        List<FeatureVo> voList = mLabDesignerService.selectPreFeature();
        model.addAttribute("vos", voList);
        model.addAttribute("labId", labId);
        return "design/lab_1";
    }
    @RequestMapping("/design/{labId}/lab_2")
    public String goLab3(@PathVariable("labId") Integer labId, Model model) {
        List<FeatureVo> voList = mLabDesignerService.selectAllFeature();
        model.addAttribute("vos", voList);
        model.addAttribute("labId", labId);
        return "design/lab_2";
    }
    @RequestMapping("/design/{labId}/lab_3")
    public String goLab1(@PathVariable("labId") Integer labId, Model model, HttpSession session) throws IOException {
        Lab lab = mLabMapper.selectByPrimaryKey(new Integer(labId));
        model.addAttribute("lab", lab);
        session.setAttribute("lab", lab);

        String fileKey = RedisKeyUtil.getFileKey(lab.getId());
        File input = null;
        if (mJedisAdapter.llen(fileKey) >0){
            input = new File(mJedisAdapter.lrange(fileKey,0,1).get(0));
        }else {
            input = new File(lab.getFile().replace("csv","arff"));
        }
        List<String> attributeList = mLabDesignerService.resolveAttribute(input);

        model.addAttribute("attributes", attributeList);
        System.out.println(attributeList.toString());
        System.out.println(attributeList.size());
        if(lab.getLableType()==1) {
            return "design/lab_3";
        }else{
            return "design/lab_3_reg";
        }
    }
    @RequestMapping("/design/{labId}/lab_3_reg")
    public String goLab1Reg(@PathVariable("labId") Integer labId, Model model, HttpSession session) throws IOException {
        Lab lab = mLabMapper.selectByPrimaryKey(new Integer(labId));
        model.addAttribute("lab", lab);
        session.setAttribute("lab", lab);

        String fileKey = RedisKeyUtil.getFileKey(lab.getId());
        File input = null;
        if (mJedisAdapter.llen(fileKey) >0){
            input = new File(mJedisAdapter.lrange(fileKey,0,1).get(0));
        }else {
            input = new File(lab.getFile().replace("csv","arff"));
        }
        List<String> attributeList = mLabDesignerService.resolveAttribute(input);
        model.addAttribute("attributes", attributeList);
        System.out.println(attributeList.toString());
        System.out.println(attributeList.size());
        return "design/lab_3_reg";
    }



    @RequestMapping("/design/{labId}/lab_4")
    public String goLab4(String des, Model model, HttpSession session, @PathVariable("labId") Integer labId) {
        Lab lab = mLabMapper.selectByPrimaryKey(labId);
        if (StringUtils.isNotBlank(des)) {
            String key = RedisKeyUtil.getFeatureDesKey(lab.getId());
            mAdapter.set(key, des);
        }
        if (lab.getLableType()==1) {
            List<Classifier> classifierList = mLabService.getClassifier(-1);
            for (Classifier classifier : classifierList) {
                List<ClassifierParam> paramList = mLabService.getParamByClassifierId(classifier.getId());
                classifier.setParams(paramList);
            }
            model.addAttribute("classifierList", classifierList);
        }else if(lab.getLableType()==0){
            List<Classifier> classifierList = mLabService.getClassifier(1);
            for (Classifier classifier : classifierList) {
                List<ClassifierParam> paramList = mLabService.getParamByClassifierId(classifier.getId());
                classifier.setParams(paramList);
            }
            model.addAttribute("classifierList", classifierList);
        }

        model.addAttribute("lab", lab);
        model.addAttribute("selectedClassifiers", mLabService.getSelectedClassifier(labId));
        return "design/lab_4";
    }

    @RequestMapping("/design/{labId}/lab_5")
    public String goLab5(String des, HttpSession session, Model model, @PathVariable("labId") Integer labId) {
        Lab lab = mLabMapper.selectByPrimaryKey(labId);
        if (StringUtils.isNotBlank(des)) {
            String key = RedisKeyUtil.getClassifierDesKey(lab.getId());
            mAdapter.set(key, des);
        }
        List<Divider> dividers = mDividerMapper.selectAll();
        model.addAttribute("dividers", dividers);
        model.addAttribute("lab", lab);
        return "design/lab_5";
    }

    @RequestMapping("/design/{labId}/lab_6")
    public String goLab6(String des, HttpSession session, Model model, @PathVariable("labId") Integer labId) throws IOException {
        Lab lab = mLabMapper.selectByPrimaryKey(labId);
        model.addAttribute("lab", lab);
        if (StringUtils.isNotBlank(des)) {
            String key = RedisKeyUtil.getDividerDesKey(lab.getId());
            mAdapter.set(key, des);
        }

        List<String> echatsOptions = new ArrayList<>();
        List<String> classifierNameList = new ArrayList<>();

        String key = RedisKeyUtil.getFileKey(lab.getId());
        long length = mAdapter.llen(key);
        System.out.println(labId);
        String dividerOutKey = RedisKeyUtil.getDividerOutKey(labId, -1);
        System.out.println(dividerOutKey);
        File train = new File(mAdapter.hget(dividerOutKey, "train"));
        File test = new File(mAdapter.hget(dividerOutKey, "test"));
        System.out.println(train.toString());
        System.out.println(test.toString());
        String classifierKey = RedisKeyUtil.getClassifierKey(lab.getId());
        List<String> classifierList = mAdapter.lrange(classifierKey, 0, mAdapter.llen(classifierKey));
        List<List<?>> res = new ArrayList<>();

        if (lab.getLableType() == 1) {
            res.add(Arrays.asList("算法名", "召回率", "准确率", "精确率", "F-Measure", "ROC-Area"));
        } else {
            res.add(Arrays.asList("算法名", "可释方差值", "平均绝对误差", "均方根误差", "中值绝对误差", "R方值"));
        }
        for (String value : classifierList) {
            Classifier classifier = mGson.fromJson(value, Classifier.class);
            classifierNameList.add(classifier.getName());
            String[] options = mLabLearnService.resolveOptions(classifier);
            if (classifier.getPath().endsWith("py")) {
                System.out.println(train.getAbsolutePath());
                System.out.println(test.getAbsolutePath());
                File csvTrain = WekaUtils.arff2csv(train);
                File csvTest = WekaUtils.arff2csv(test);
                StringBuilder sb = new StringBuilder("python ");
                sb.append(classifier.getPath());
                for (int i = 0; i < options.length; i = i + 2) {
                    sb.append(" ").append(options[i].trim()).append("=").append(options[i + 1].trim());
                }
                sb.append(" train=").append(csvTrain.getAbsolutePath().trim()).append(" test=").append(csvTest.getAbsolutePath().trim());
                logger.info(sb.toString());
                String resParam = PythonUtils.execPy(sb.toString());
                mAdapter.lpush(RedisKeyUtil.getPyKey(labId), sb.toString() + "---" + resParam);
                String labResKey = RedisKeyUtil.getResLabKey(labId, classifier);
                mAdapter.set(labResKey, resParam);
                if (lab.getLableType() == 1) {
                    Result result = mGson.fromJson(resParam, Result.class);
                    if (result.getFeatureImportances() != null) {
                        EchatsOptions eo = mLabDesignerService.getEchartsOptions(lab, result.getFeatureImportances(), classifier);
                        echatsOptions.add(mGson.toJson(eo));
                    }
                    System.out.println(result);
                    res.add(Arrays.asList(classifier.getName(), result.getRecall(), result.getAccuracy(), result.getPrecision(),
                            result.getfMeasure(), result.getRocArea()));
                } else if (lab.getLableType() == 0) {
                    RegResult regResult = mGson.fromJson(resParam, RegResult.class);
                    if (regResult.getFeatureImportances()!=null ) {
                        EchatsOptions eo = mLabDesignerService.getEchartsOptions(lab, regResult.getFeatureImportances(), classifier);
                        echatsOptions.add(mGson.toJson(eo));
                    }
                    System.out.println(regResult);
                    res.add(Arrays.asList(classifier.getName(), regResult.getVarianceScore(), regResult.getAbsoluteError(), regResult.getSquaredError(),
                            regResult.getMedianSquaredError(), regResult.getR2Score()));
                } else {
                    ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
                    AbstractClassifier classify = (AbstractClassifier) ac.getBean(classifier.getPath());

                    ArffLoader loader = new ArffLoader();
                    loader.setFile(train);
                    Instances itrain = loader.getDataSet();

                    itrain.setClassIndex(itrain.numAttributes() - 1);
                    loader.setFile(test);
                    Instances itest = loader.getDataSet();
                    itest.setClassIndex(itest.numAttributes() - 1);

                    try {
                        classify.buildClassifier(itrain);
                        Evaluation evaluation = new Evaluation(itrain);
                        evaluation.evaluateModel(classify, itest);
                        res.add(Arrays.asList(classifier.getName(), evaluation.recall(1) / 100, evaluation.correct() / 100, evaluation.pctCorrect() / 100,
                                evaluation.fMeasure(1), evaluation.weightedAreaUnderROC()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        model.addAttribute("res", res);
        model.addAttribute("options", echatsOptions);
        model.addAttribute("classNames", classifierNameList);
        lab.setPublish(1);
        mLabMapper.updateByPrimaryKeySelective(lab);
        return "design/lab_6";
    }

    @RequestMapping("/design/upload/classifier")
    public String uploadClassifier() {
        return "design/updateClassifier";
    }
}
// 数据集  综述
