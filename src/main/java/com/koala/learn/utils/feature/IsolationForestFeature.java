package com.koala.learn.utils.feature;

import com.koala.learn.Const;
import com.koala.learn.utils.PythonUtils;
import com.koala.learn.utils.WekaUtils;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.io.IOException;

public class IsolationForestFeature implements IFeature{
    private String contamination; //异常点的比列

    @Override
    public void setOptions(String[] options) {
        contamination=options[1];

    }

    @Override
    public File filter(Instances input, File file, File out) throws IOException {
        if(file.getAbsolutePath().endsWith("arff")) {
            file = WekaUtils.arff2csv(file);
        }
        try{
            if (file.getAbsolutePath().endsWith("arff")) {
                out = new File(out.getAbsolutePath().replace("arff","csv"));
            }
            String dec = "python "+ Const.ISOLATION_FOREST_FEATURE+ " contamination="+contamination
                    +" path="+file.getAbsolutePath()+" opath="+out;
            System.out.println(dec);
            PythonUtils.execPy(dec);
            out=WekaUtils.csv2arff(out);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        String path = "/Users/zangmenglei/mylib/diabetes.arff";
        String opath = "/Users/zangmenglei/mylib/diabetesIso.csv";
        String[] options = {"-i", "0.01"};
        IsolationForestFeature isolationForestFeature = new IsolationForestFeature();
        isolationForestFeature.setOptions(options);
        ArffLoader loader = new ArffLoader();
        loader.setFile(new File(path));
        isolationForestFeature.filter(null,new File(path),new File(opath));
    }
}
