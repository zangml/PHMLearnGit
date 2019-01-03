package com.koala.learn.controller;

import com.google.gson.Gson;
import com.koala.learn.commen.ServerResponse;
import com.koala.learn.component.HostHolder;
import com.koala.learn.component.JedisAdapter;
import com.koala.learn.dao.LabMapper;
import com.koala.learn.dao.FeatureMapper;
import com.koala.learn.entity.*;
import com.koala.learn.service.LabDesignBGService;
import com.koala.learn.utils.CSVUtils;
import com.koala.learn.utils.RedisKeyUtil;
import com.koala.learn.utils.WekaUtils;
import com.koala.learn.utils.treat.ViewUtils;
import com.koala.learn.vo.PointVo;
import com.koala.learn.vo.ViewType;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.multipart.MultipartFile;
import weka.classifiers.evaluation.output.prediction.CSV;
import weka.core.Instances;

/**
 * Created by koala on 2018/1/9.
 */
@Controller
public class LabDesignBGController {

    @Autowired
    LabMapper mLabMapper;

    @Autowired
    ThreadPoolTaskExecutor mExecutor;

    @Autowired
    JedisAdapter mJedisAdapter;

    @Autowired
    FeatureMapper mFeatureMapper;

    @Autowired
    HostHolder mHolder;

    @Autowired
    LabDesignBGService mDesignBGService;

    @Autowired
    Gson gson;

    private static Logger logger = LoggerFactory.getLogger(LabDesignBGController.class);

    @RequestMapping(path = "/file/{id}")
    @ResponseBody
    ResponseEntity downloadFile(@PathVariable("id") int id, HttpServletResponse response) throws IOException {
        BuildinFile file = mDesignBGService.getFileById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment","data.csv");
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;fileName="
                + "data.csv");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(file.getFile())),
                headers, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/design/remove/lab/{labId}")
    @ResponseBody
    public ServerResponse<Object> removeLab(@PathVariable("labId") int id){
        Lab lab = mLabMapper.selectByPrimaryKey(id);
        if (mHolder.getUser() != null && mHolder.getUser().getId()==lab.getUserId()){
            mLabMapper.deleteByPrimaryKey(lab.getId());
            return ServerResponse.createBySuccess();
        }else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }

    @RequestMapping(path = "/design/{id}/attribute/{type}")
    @ResponseBody
    public String getAttributeView(@PathVariable("id") final int id, @RequestParam final Map<String,Object> param, @PathVariable("type") final Integer type) throws Exception {
        Lab lab = mLabMapper.selectByPrimaryKey(id);
        String fileKey = RedisKeyUtil.getFileKey(lab.getId());
        File input = null;
        if (mJedisAdapter.llen(fileKey) >0){
            input = new File(mJedisAdapter.lrange(fileKey,0,1).get(0));
        }else {
            input = new File(lab.getFile().replace("csv","arff"));
        }

        Instances instances = WekaUtils.readFromFile(input.getAbsolutePath());

        String value = mJedisAdapter.get(RedisKeyUtil.getAttributeKey(param.toString(),type,id));
        if ( value != null){
            return value;
        }
        EchatsOptions options = null;
        if (type ==ViewUtils.VIEW_PCA){
            options = ViewUtils.reslovePCA(instances);
        }else if (type == ViewUtils.VIEW_ATTRI){
            options = ViewUtils.resloveAttribute(instances,param.get("attribute1").toString());
        }else if(type ==ViewUtils.VIEW_REG_PCA2){
            options = ViewUtils.resloveRegPCA(instances);
        }else if(type ==ViewUtils.VIEW_REG_PCA3){
            EchatsOptions3D options3D =null;
            options3D=ViewUtils.resloveRegPCA3D(instances);
            String key = RedisKeyUtil.getAttributeKey(param.toString(),type,id);
            Gson gson = new Gson();
            String json = gson.toJson(options3D);
            mJedisAdapter.set(key,json);
            return json;
        }
        else if (type == ViewUtils.VIEW_MULATTRI){
            options = ViewUtils.resloveMulAttribute(instances,param);
        }else if (type == ViewUtils.VIEW_RELATIVE){
            input =WekaUtils.arff2csv(input);
            options = ViewUtils.resloveRelative(input.getAbsolutePath());
        }else if (type == ViewUtils.VIEW_REG_ATTRI){
            options = ViewUtils.resloveRegAttribute(instances,param.get("attribute1").toString());
        }else if (type == ViewUtils.VIEW_REG_RELATIVE){
            input =WekaUtils.arff2csv(input);
            options = ViewUtils.resloveRegRelative(input.getAbsolutePath());
        }


        String key = RedisKeyUtil.getAttributeKey(param.toString(),type,id);
        Gson gson = new Gson();
        String json = gson.toJson(options);
        mJedisAdapter.set(key,json);
        return json;
    }


    @RequestMapping("/design/{id}/part1/view/list")
    @ResponseBody
    public ServerResponse getViewTypeList(@PathVariable("id") int id){
        String key = RedisKeyUtil.getLabViewKey(id);
        Map<String,String> map = mJedisAdapter.hget(key);
        return ServerResponse.createBySuccess(reslove(map));
    }

    @RequestMapping(path = "/design/{id}/part1/view/add/{type}")
    @ResponseBody
    public ServerResponse addViewType(@PathVariable("id")int id,@PathVariable("type") int type,String des){
        String key = RedisKeyUtil.getLabViewKey(id);
        mJedisAdapter.hset(key,type+"",des);
        Map<String,String> map = mJedisAdapter.hget(key);
        System.out.println(key);
        System.out.println(map);
        return ServerResponse.createBySuccess(reslove(map));
    }


    @RequestMapping(path = "/design/{labId}/part2/pre/add/{featureId}")
    @ResponseBody
    public ServerResponse addPre(@RequestParam Map<String,String> param,
                                     @PathVariable("labId") Integer labId,
                                     @PathVariable("featureId") Integer featureId,
                                     HttpSession session) throws Exception {

        Lab lab = mLabMapper.selectByPrimaryKey(labId);
        System.out.println(featureId);
        Feature feature = mFeatureMapper.selectByPrimaryKey(featureId);
        String fileKey = RedisKeyUtil.getFileKey(labId);

        File out = mDesignBGService.addFeature(session,feature,param,lab);
        mDesignBGService.savePre(lab,feature,param);

        mJedisAdapter.lpush(fileKey,out.getAbsolutePath());

        System.out.println(out.getName());
        return ServerResponse.createBySuccess(out.getName());
    }


    @RequestMapping(path = "/design/{labId}/part2/feature/add/{featureId}")
    @ResponseBody
    public ServerResponse addFeature(@RequestParam Map<String,String> param,
                                     @PathVariable("labId") Integer labId,
                                     @PathVariable("featureId") Integer featureId,
                                     HttpSession session) throws Exception {

        Lab lab = mLabMapper.selectByPrimaryKey(labId);
        Feature feature = mFeatureMapper.selectByPrimaryKey(featureId);
        String fileKey = RedisKeyUtil.getFileKey(labId);

        File out = mDesignBGService.addFeature(session,feature,param,lab);
        mDesignBGService.saveFeature(lab,feature,param);

        mJedisAdapter.lpush(fileKey,out.getAbsolutePath());
        System.out.println(out);
        return ServerResponse.createBySuccess(out.getName());
    }

    @RequestMapping(path = "/design/{labId}/classify/")
    @ResponseBody
    public ServerResponse addClassifier(@RequestParam Map<String,String> param,@PathVariable("labId") Integer labId){
        System.out.println(param);
        try {
            mDesignBGService.saveClassifier(param,labId);
            return ServerResponse.createBySuccess();
        }catch (Exception e){
            return ServerResponse.createByError();
        }

    }

    @RequestMapping(path = "/design/{labId}/divider/")
    @ResponseBody
    public ServerResponse setDivider(@RequestParam Map<String,String> param, @PathVariable("labId") Integer labId,HttpSession session){
        System.out.println(param);
        mDesignBGService.saveDivider(param,labId,session);
        return ServerResponse.createBySuccess();
    }

    List<ViewType> reslove(Map<String,String> cache){
        Map<Integer,String> map = ViewUtils.getTypeMap();
        List<ViewType> types = new ArrayList<>();
        for (String key:cache.keySet()){
            ViewType type = new ViewType(new Integer(key),map.get(new Integer(key)));
            types.add(type);
        }
        return types;
    }

    @RequestMapping(path = "/design/{labId}/finish")
    @ResponseBody
    public ServerResponse finish(@PathVariable("labId") Integer labId){
        return mDesignBGService.finish(labId);
    }

    @RequestMapping(path = "/design/doUpload/classifier")
    public String doUpload(@RequestParam(name = "classifierFile") MultipartFile classifierFile,
                           @RequestParam(name = "testFile") MultipartFile testFile,
                           @RequestParam Map<String,Object> params, Model model,
                           HttpSession session){

        ServerResponse response = null;
        try {
            response = mDesignBGService.uploadClassifier(session,classifierFile,testFile,params);
            if (response.isSuccess()){
                return "redirect:/design";
            }else {
                model.addAttribute("error",response.getMsg());
                return "common/error";
            }
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error",e.getMessage());
            return "common/error";
        }

    }

}
