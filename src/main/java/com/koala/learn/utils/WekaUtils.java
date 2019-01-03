package com.koala.learn.utils;

import com.google.gson.Gson;
import com.koala.learn.component.JedisAdapter;
import com.koala.learn.entity.AttributeEntity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;

/**
 * Created by koala on 2017/12/14.
 */
public class WekaUtils {

    public static Instances readFromFile(String path) throws IOException {
        File file = new File(path);
        Instances instances = null;
        if (file.getAbsolutePath().endsWith("arff")){
            ArffLoader arffLoader = new ArffLoader();
            arffLoader.setFile(file);
            instances = arffLoader.getDataSet();
        }else {
            CSVLoader csvLoader = new CSVLoader();
            csvLoader.setFile(file);
            instances = csvLoader.getDataSet();
        }
        return instances;
    }

    public static File arff2csv(File file) throws IOException {
        File out = new File(file.getAbsolutePath().replace("arff","csv"));
        if (out.exists()){
            return out;
        }
        Instances instances = readFromFile(file.getAbsolutePath());
        CSVSaver csvSaver = new CSVSaver();
        csvSaver.setInstances(instances);
        csvSaver.setFile(out);
        csvSaver.writeBatch();
        return out;
    }
    public static File csv2arff(File file) throws  IOException{
        File out = new File(file.getAbsolutePath().replace("csv","arff"));
        if (out.exists()){
            return out;
        }
        Instances instances = readFromFile(file.getAbsolutePath());
        instances.setClassIndex(instances.numAttributes()-1);
        ArffSaver arffSaver =new ArffSaver();
        arffSaver.setInstances(instances);
        arffSaver.setFile(out);
        arffSaver.writeBatch();
        return out;
    }

    public static List<String> getAttributeList(File file) throws IOException {
        Instances instances = readFromFile(file.getAbsolutePath());
        List<String> res = new ArrayList<>();
        for (int i=0;i<instances.numAttributes()-1;i++){
            res.add(instances.attribute(i).name());
        }
        return res;
    }

    public static List<AttributeEntity> resolveAttribute(File file) throws IOException {
        ArffLoader loader = new ArffLoader();
        loader.setFile(file);
        Instances instances = loader.getDataSet();
        instances.setClassIndex(0);
        List<AttributeEntity> entityList = new ArrayList<>();
        Jedis jedis = new Jedis("redis://localhost:6379/10");

        Attribute classattribute = instances.classAttribute();
        AttributeEntity attributeEntity = new AttributeEntity();
        double[] classxAxis = new double[]{0d,1d};
        int[] classyAxis = new int[2];
        attributeEntity.setName(classattribute.name());

        for (int j=0;j<instances.size();j++){
            Instance instance = instances.get(j);
            if (instance.classValue()==0){
                classyAxis[0]++;
            }else {
                classyAxis[1]++;
            }
        }
        attributeEntity.setxAxis(classxAxis);
        attributeEntity.setyAxis(classyAxis);
        Gson gson = new Gson();
        jedis.set(RedisKeyUtil.getAttributeKey(classattribute.name(),"1"),gson.toJson(attributeEntity));
        jedis.close();
        return entityList;
    }

    private static double[] getX(double lower,double upper){
        double[] x = new double[40];
        double d = (upper-lower)/40;
        for (int i=0;i<40;i++){
            x[i] = lower+d*(i+1);
        }

        return x;
    }
    public static void main(String[] args) throws IOException {
        File file = new File("/Users/zangmenglei/config/diabetes.csv");
        csv2arff(file);
    }
}
