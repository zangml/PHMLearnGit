package com.koala.learn.utils.feature;

import com.koala.learn.utils.WekaUtils;
import org.apache.commons.lang.StringUtils;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TimeFeature implements IFeature{

//    private boolean avg;
//    private boolean std;
//    private boolean max;
//    private boolean min;
//    private boolean rms;  //信号有效值,用于表征信号中的能量大小，  计算所有幅值的平方和再除以总样本数目，再开方；
    private String indexName; //提取特征依据的索引，每个索引号代表实际的一个csv文件，每个csv文件提取出一组特征；
    @Override
    public void setOptions(String[] options) {

        indexName=options[1];
    }

    @Override
    public File filter(Instances input, File file, File out) throws IOException {
        List<String> attributeList = new ArrayList<>();
        for (int i=0;i<input.numAttributes()-1;i++){
            attributeList.add(input.attribute(i).name());
        }
        System.out.println("原始文件(除最后一列)");
        for(String name : attributeList) {
            System.out.println(name);
        }
        System.out.println("--------------");

        int attr1Max=0;//记录索引列的最大数目
        Attribute attribute =input.attribute(indexName);
        for(int i=0;i<input.size();i++){
            Instance instance = input.get(i);
            if( instance.value(attribute)>attr1Max){
                attr1Max=(int)instance.value(attribute);
            }
        }
        System.out.println(attr1Max);
        ArffLoader loader = new ArffLoader();
        loader.setFile(new File("/Users/zangmenglei/test/tool.arff"));
        Instances input1 = loader.getDataSet();
        Instances instances= new Instances(input1);

        for(int i=0;i<attributeList.size()+1;i++) {
            instances.  deleteAttributeAt(0);
        }
        List<String> attributeList2 = new ArrayList<>();
        for (int i=0;i<instances.numAttributes()-1;i++){
            attributeList2.add(instances.attribute(i).name());
        }
        System.out.println("删除后");
        for(String Aname : attributeList2) {
            System.out.println(Aname);
        }
        System.out.println("------");
        Attribute attributeInputLast=input.attribute(input.attribute(input.numAttributes()-1).name());
        System.out.println(input.attribute(input.numAttributes()-1).name());
        Set<Double> set =new HashSet<>();
        for(int i=0;i<input.size();i++){
            Instance instance =input.get(i);
            set.add(instance.value(attributeInputLast));
        }
        System.out.println(set.size());
        double[] last=new double[attr1Max];
        int dex=0;
        for(double lastNum : set){
            last[dex]=lastNum;
            dex++;
            if(dex==attr1Max){
                break;
            }
        }
        Arrays.sort(last);

        Attribute attributeLastNew=new Attribute(input.attribute(input.numAttributes()-1).name());
        instances.insertAttributeAt(attributeLastNew,0);

        Attribute attributeLast=instances.attribute(input.attribute(input.numAttributes()-1).name());
        for(int i=0;i<attr1Max;i++){
            Instance instance=instances.get(i);
            instance.setValue(attributeLast,last[attr1Max-1-i]);
        }
        for(String name : attributeList){
            if(name.equals(indexName)){
                continue;
            }
            String name1="avg_"+name;
            String name2="std_"+name;
            String name3="max_"+name;
            String name4="min_"+name;
            String name5="RMS_"+name;

            Attribute attributeAvg=new Attribute(name1);
            Attribute attributeStd=new Attribute(name2);
            Attribute attributeMax=new Attribute(name3);
            Attribute attributeMin=new Attribute(name4);
            Attribute attributeRMS=new Attribute(name5);
            instances.insertAttributeAt(attributeAvg,instances.numAttributes()-1);
            instances.insertAttributeAt(attributeStd,instances.numAttributes()-1);
            instances.insertAttributeAt(attributeMax,instances.numAttributes()-1);
            instances.insertAttributeAt(attributeMin,instances.numAttributes()-1);
            instances.insertAttributeAt(attributeRMS,instances.numAttributes()-1);
            Attribute attributeA=instances.attribute(name1);
            Attribute attributeB=instances.attribute(name2);
            Attribute attributeC=instances.attribute(name3);
            Attribute attributeD=instances.attribute(name4);
            Attribute attributeE=instances.attribute(name5);

            Attribute attribute1=input.attribute(name);
            Attribute attribute2=input.attribute(indexName);

            for(int k=1;k<=attr1Max;k++){
                int sum=0;
                List<Double> list=new ArrayList<>();
                for(int i=0;i<input.size();i++){
                    Instance instance=input.get(i);
                    if(instance.value(attribute2)==k){
                        sum=sum+1;
                        list.add(instance.value(attribute1));
                    }
                }
                double[] nums=new double[sum];
                int index=0;
                for(double num :list){
                    nums[index]=num;
                    index++;
                }
                double avg=this.avg(nums);

                double std=this.std(nums);

                double max=this.max(nums);

                double min=this.min(nums);

                double RMS=this.RMS(nums);

                Instance instance =instances.get(k-1);
                instance.setValue(attributeA,avg);
                instance.setValue(attributeB,std);
                instance.setValue(attributeC,max);
                instance.setValue(attributeD,min);
                instance.setValue(attributeE,RMS);

            }
        }

        System.out.println(instances.size());


        System.out.println(instances.size());
        ArffSaver saver = new ArffSaver();
        saver.setFile(out);
        saver.setInstances(instances);
        saver.writeBatch();
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeFeature that = (TimeFeature) o;
        return Objects.equals(indexName, that.indexName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indexName);
    }
    //计算均值
    public double avg(double[] nums){
        double sum=0;
        for(int i=0;i<nums.length;i++){
            sum=sum+nums[i];
        }
        double avg=sum/nums.length;
        return avg;
    }
    //计算标准差
    public double std(double[] nums){
        double sum=0;
        double avg=this.avg(nums);
        for(int i=0;i<nums.length;i++){
            sum=sum+(nums[i]-avg)*(nums[i]-avg);
        }
        double sd=sum/nums.length;
        double std=Math.sqrt(sd);
        return std;
    }
    //计算最大值
    public double max(double[] nums){
        double max=nums[0];
        for(int i=1;i<nums.length;i++){
            if(nums[i]>max){
                max=nums[i];
            }
        }
        return max;
    }
    //计算最大值
    public double min(double[] nums){
        double min=nums[0];
        for(int i=1;i<nums.length;i++){
            if(nums[i]<min){
                min=nums[i];
            }
        }
        return min;
    }
    //计算RMS值
    public double RMS(double[] nums){
        double sum=0;
        for(int i=0;i<nums.length;i++){
            sum=sum+nums[i]*nums[i];
        }
        double rms=sum/nums.length;
        return rms;
    }

    public static void main(String[] args) throws IOException {
        String oPath="/Users/zangmenglei/test/djmsNewTest.arff";
        String iPath="/Users/zangmenglei/test/djmsNew.arff";
        String[] options = {"-t","csv_no"};
        TimeFeature feature = new TimeFeature();
        feature.setOptions(options);
        ArffLoader loader = new ArffLoader();
        loader.setFile(new File(iPath));
        Instances input = loader.getDataSet();
        feature.filter(input,null,new File(oPath));
    }
}
