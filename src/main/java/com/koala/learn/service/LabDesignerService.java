package com.koala.learn.service;

import com.google.gson.Gson;
import com.koala.learn.Const;
import com.koala.learn.component.HostHolder;
import com.koala.learn.component.JedisAdapter;
import com.koala.learn.dao.*;
import com.koala.learn.entity.*;
import com.koala.learn.utils.CSVUtils;
import com.koala.learn.utils.LogUtils;
import com.koala.learn.utils.RedisKeyUtil;
import com.koala.learn.utils.WekaUtils;
import com.koala.learn.vo.FeatureVo;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import weka.core.Instances;

/**
 * Created by koala on 2018/1/2.
 */

@Service
public class LabDesignerService {

    @Autowired
    ThreadPoolTaskExecutor mExecutor;

    @Autowired
    FeatureMapper mFeatureMapper;

    @Autowired
    FeatureParamMapper mFeatureParamMapper;

    @Autowired
    ClassifierMapper mClassifierMapper;

    @Autowired
    JedisAdapter mJedisAdapter;

    @Autowired
    LabMapper mLabMapper;
    @Autowired
    HostHolder mHolder;

    @Autowired
    LabGroupMapper mLabGroupMapper;

    @Autowired
    BuildinFileMapper fileMapper;

    @Autowired
    Gson gson;


    public List<BuildinFile> getBuildinFileList(){
        return fileMapper.selectAllFile();
    }

    public LabGroup createLabGroup(String title, String des,String aim,final Integer type) throws IOException {
        LabGroup lab = new LabGroup();
        lab.setName(title);
        lab.setDes(des);
        lab.setPublish(0);
        lab.setOwner(mHolder.getUser().getId());
        lab.setLabType(new Integer(type));
        lab.setCreateTime(new Date());
        lab.setAim(aim);
        mLabGroupMapper.insert(lab);
        return lab;
    }
    public Lab createLab(Integer groupId, String name) throws IOException {
        final LabGroup group = mLabGroupMapper.selectByPrimaryKey(groupId);
        Lab lab = new Lab();
        lab.setPublish(0);
        lab.setUserId(mHolder.getUser().getId());
        lab.setLableType(group.getLabType());
        lab.setCover(Const.IMAGE);
        lab.setFile(group.getFile());
        lab.setGroupId(groupId);
        lab.setTitle(name);
        mLabMapper.insert(lab);
        if (group.getFile().endsWith("csv")){
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        CSVUtils.csv2Arff(new File(group.getFile()),group.getLabType());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        return lab;
    }

    public void addFile(LabGroup group, MultipartFile file, final Integer type) throws IOException {
        int id = group.getId();
        File root = new File(Const.ROOT+id+"/");
        if (!root.exists()){
            root.mkdirs();
        }
        System.out.println(root.getAbsolutePath());

        final File data = new File(root,file.getOriginalFilename());
        file.transferTo(data);
        group.setFile(data.getAbsolutePath());
        mLabGroupMapper.updateByPrimaryKeySelective(group);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    CSVUtils.csv2Arff(data,type);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addFile(LabGroup group, Integer fileId, final Integer type) throws IOException {
        int id = group.getId();
        File root = new File(Const.ROOT+id+"/");
        if (!root.exists()){
            root.mkdirs();
        }
        BuildinFile buildinFile = fileMapper.selectByPrimaryKey(fileId);
        LogUtils.log(buildinFile.getFile());
        File file = new File(buildinFile.getFile());
        final File copy = new File(root,"data.csv");
        LogUtils.log(copy.getAbsolutePath());
        FileUtils.copyFile(file,copy);
        group.setFile(copy.getAbsolutePath());
        System.out.println(copy.getAbsolutePath());
        mLabGroupMapper.updateByPrimaryKeySelective(group);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    CSVUtils.csv2Arff(copy,type);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public List<String> resolveAttribute(File file) throws IOException {
        List<String> attributeList = new ArrayList<>();
        Instances instances = WekaUtils.readFromFile(file.getAbsolutePath());
        for (int i=0;i<instances.numAttributes()-1;i++){
            attributeList.add(instances.attribute(i).name());
        }
        return attributeList;
    }
    public List<FeatureVo> selectPreFeature(){
        List<Feature> features = mFeatureMapper.selectAllPre();
        List<FeatureVo> voList = new ArrayList<>();
        for (Feature feature:features){
            FeatureVo vo = new FeatureVo();
            System.out.println();
            vo.setFeature(feature);
            List<FeatureParam> paramList = mFeatureParamMapper.selectAllByFeatureId(feature.getId());
            vo.setParamList(paramList);
            voList.add(vo);
        }
        return voList;
    }

    public List<FeatureVo> selectAllFeature(){
        List<Feature> features = mFeatureMapper.selectAll();
        List<FeatureVo> voList = new ArrayList<>();
        for (Feature feature:features){
            FeatureVo vo = new FeatureVo();
            vo.setFeature(feature);
            List<FeatureParam> paramList = mFeatureParamMapper.selectAllByFeatureId(feature.getId());
            vo.setParamList(paramList);
            voList.add(vo);
        }
        return voList;
    }

    public LabGroup selectByGroupId(Integer groupId){
        return mLabGroupMapper.selectByPrimaryKey(groupId);
    }

    public List<Lab> selectLabsByGroup(Integer groupId){
        return mLabMapper.selectByGroup(groupId);
    }


    public String getCacheKey(Lab lab){
        StringBuilder sb = new StringBuilder(lab.getGroupId());
        String treatKey = RedisKeyUtil.getFeatureKey(lab.getId());
        return sb.toString();
    }

    public EchatsOptions getEchartsOptions(Lab lab,List<Double> importance,Classifier classifier) throws IOException {

        String attributeListKey = RedisKeyUtil.getAttributeListKey(lab.getId());
        List<String> atrributes = null;
        if (mJedisAdapter.get(attributeListKey) == null){
            String fileKey = RedisKeyUtil.getFileKey(lab.getId());
            File input = null;
            if (mJedisAdapter.llen(fileKey) >0){
                input = new File(mJedisAdapter.lrange(fileKey,0,1).get(0));
            }else {
                input = new File(lab.getFile().replace("csv","arff"));
            }
            atrributes = WekaUtils.getAttributeList(input);
            mJedisAdapter.set(attributeListKey,gson.toJson(atrributes));
        }else {
            atrributes = gson.fromJson(mJedisAdapter.get(attributeListKey),List.class);
        }
        EchatsOptions options = new EchatsOptions();
        options.setTitle(new EchatsOptions.TitleBean("相关性分析","表征特征的重要程度"));
        options.setTooltip(new EchatsOptions.TooltipBean());
        EchatsOptions.XAxisBean xAxisBean = new EchatsOptions.XAxisBean("value",true,new EchatsOptions.XAxisBean.AxisLabelBean());
        options.setXAxis(Arrays.asList(xAxisBean));
        EchatsOptions.YAxisBean yAxisBean = new EchatsOptions.YAxisBean("category",true,new EchatsOptions.YAxisBean.AxisLabelBeanX());
        yAxisBean.setData(atrributes);
        options.setYAxis(Arrays.asList(yAxisBean));
        options.setLegend(new EchatsOptions.LegendBean(Arrays.asList("快速特征选择")));
        EchatsOptions.SeriesBean seriesBean = new EchatsOptions.SeriesBean();

        seriesBean.setType("bar");
        seriesBean.setName("快速特征选择");
        options.setSeries(Arrays.asList(seriesBean));
        if (atrributes.size()==importance.size()){
            seriesBean.setData(importance);
            return options;
        }else {
            String key = RedisKeyUtil.getFeatureKey(lab.getId());
            if (mJedisAdapter.llen(key)>0){
                List<String> features = mJedisAdapter.lrange(key,0,mJedisAdapter.llen(key));
                for (String str:features){
                    FeatureVo vo = gson.fromJson(str,FeatureVo.class);
                    if (vo.getFeature().getName().equals("Window")){
                        for (FeatureParam param:vo.getParamList()){
                            if (param.getName().equals("windowLength") &&
                                    Integer.valueOf(param.getDefaultValue())*atrributes.size() == importance.size()){
                                int length = Integer.valueOf(param.getDefaultValue());
                                List<Double> datas = new ArrayList<>();
                                for (int i=0;i<atrributes.size();i++){
                                    double sum = 0d;
                                    for (int j=0;j<length;j++){
                                        sum = sum+importance.get(j*atrributes.size()+i);
                                    }
                                    datas.add(sum);
                                }
                                seriesBean.setData(datas);
                                return options;
                            }
                        }
                    }
                }
            }
        }
        return null;

    }
//    public static void main(String[] args) throws IOException {
//        File file = new File("H:/tem/learn/14/21_data.csv");
//        CSVLoader csvLoader = new CSVLoader();
//        csvLoader.setFile(file);
//        Instances instances = csvLoader.getDataSet();
//    }
}
