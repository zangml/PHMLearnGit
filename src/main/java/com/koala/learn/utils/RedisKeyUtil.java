package com.koala.learn.utils;

import com.google.gson.Gson;
import com.koala.learn.entity.Classifier;

/**
 * Created by nowcoder on 2016/7/30.
 */
public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";
    // 获取粉丝
    private static String BIZ_FOLLOWER = "FOLLOWER";
    // 关注对象
    private static String BIZ_FOLLOWEE = "FOLLOWEE";
    private static String BIZ_TIMELINE = "TIMELINE";


    private static String BIZ_ATTRIBUTE = "ATTRIBUTE";

    private static String BIZ_ATTRIBUTEVIEW = "ATTRIBUTEVIEW";


    private static String BIZ_ATTRIBUTELIST = "ATTRIBUTELIST";

    private static String BIZ_LABVIEW = "LABVIEW";
    private static String BIZ_FEATURE = "FEATURE";
    private static String BIZ_PRE ="PRE";
    private static String BIZ_CLASSIFIER = "CLASSIFIER";
    private static String BIZ_FEATURE_DES = "FEATURE_DES";
    private static String BIZ_CLASSIFIER_DES = "CLASSIFIER_DES";
    private static String BIZ_DIVIDER = "DIVIDER";
    private static String BIZ_DIVIDER_DES = "DIVIDER_DES";
    private static String BIZ_FILE = "FILE";


    private static String BIZ_FEATURE_INSTANCE = "FEATURE_INSTANCE";
    private static String BIZ_FILE_INSTANCE = "FILE_INSTANCE";

    private static String BIZ_DIVIDER_INSTANCE = "DIVIDER_INSTANCE";

    private static String BIZ_RES_INSTANCE = "RES_INSTANCE";

    private static String BIZ_RES_LAB = "RES_LAB";

    private static String BIZ_DIVIDER_OUT = "DIVIDER_OUT";

    private static String BIZ_CACHE_KEY = "CACHE";

    private static Gson gson = new Gson();

    public static String getLikeKey(int entityType, int entityId) {
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType, int entityId) {
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getEventQueueKey() {
        return BIZ_EVENTQUEUE;
    }

    // 某个实体的粉丝key
    public static String getFollowerKey(int entityType, int entityId) {
        return BIZ_FOLLOWER + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    // 每个用户对某类实体的关注key
    public static String getFolloweeKey(int userId, int entityType) {
        return BIZ_FOLLOWEE + SPLIT + String.valueOf(userId) + SPLIT + String.valueOf(entityType);
    }

    public static String getTimelineKey(int userId) {
        return BIZ_TIMELINE + SPLIT + String.valueOf(userId);
    }

    public static String getAttributeKey(String attribute,String labId){
        return BIZ_ATTRIBUTE + SPLIT + labId+SPLIT+attribute;
    }

    public static String getAttributeKey(String attribute,Integer viewType,Integer labId){
        if (viewType==0 || viewType == 3){
            return BIZ_ATTRIBUTEVIEW + SPLIT + labId+SPLIT+viewType;
        }
        return BIZ_ATTRIBUTEVIEW + SPLIT + labId+SPLIT+attribute+SPLIT+viewType;
    }

    public static String getLabViewKey(Integer labId){
        return BIZ_LABVIEW+SPLIT+labId;
    }

    public static String getFeatureKey(Integer labId){
        return BIZ_FEATURE+SPLIT+labId;
    }

    public static String getPreKey(Integer labId){
        return BIZ_PRE+SPLIT+labId;
    }

    public static String getClassifierKey(Integer labId){
        return BIZ_CLASSIFIER+SPLIT+labId;
    }

    public static String getFeatureDesKey(Integer labId){
        return BIZ_FEATURE_DES+SPLIT+labId;
    }

    public static String getClassifierDesKey(Integer labId){
        return BIZ_CLASSIFIER_DES+SPLIT+labId;
    }

    public static String getDividerKey(Integer labId){
        return BIZ_DIVIDER+SPLIT+labId;
    }

    public static String getDividerDesKey(Integer labId){
        return BIZ_DIVIDER_DES+SPLIT+labId;
    }

    public static String getAttributeListKey(Integer labId){
        return BIZ_ATTRIBUTELIST+SPLIT+labId;
    }


    public static String getFeatureInstanceKey(Integer labId,Integer instanceId){
        return BIZ_FEATURE_INSTANCE+SPLIT+labId+SPLIT+instanceId;
    }

    private final static String BIZ_CLASSIFIER_INSTANCE = "CLASSIFIER_INSTANCE";
    public static String getClassifierInstanceKey(Integer labId,Integer instanceId){
        return BIZ_CLASSIFIER_INSTANCE+SPLIT+labId+SPLIT+instanceId;
    }

    public static String getFileKey(Integer labId){
        return BIZ_FILE+SPLIT+labId;
    }

    public static String getFileInstanceKey(Integer labId,Integer instance){
        return BIZ_FILE_INSTANCE+SPLIT+labId+SPLIT+instance;
    }

    public static String getResLabKey(Integer labId,Classifier classifier){
        return BIZ_RES_LAB+SPLIT+labId+SPLIT+classifier.getName()+gson.toJson(classifier).hashCode();
    }

    public static String getResInstanceKey(Integer labId, Integer instance){

        return BIZ_RES_INSTANCE+SPLIT+labId+SPLIT+instance;
    }

    public static String getResInstanceKey(Integer labId, Integer instance, Classifier classifier){
        if (classifier==null){
            return BIZ_RES_INSTANCE+SPLIT+labId+SPLIT+instance;
        }else {
            return BIZ_RES_INSTANCE+SPLIT+labId+SPLIT+instance+SPLIT+classifier.getName()+gson.toJson(classifier).hashCode();
        }

    }

    public static String getDividerInstanceKey(Integer labId,Integer instance){
        return BIZ_DIVIDER_INSTANCE+SPLIT+labId+SPLIT+instance;
    }

    public static String getDividerOutKey(Integer labId,Integer instance){
        return BIZ_DIVIDER_OUT+SPLIT+labId+SPLIT+instance;
    }

    public static String getCacheKey(Integer labId,String file,String classifier){
        return BIZ_CACHE_KEY+SPLIT+labId+SPLIT+file+SPLIT+classifier;
    }

    public static String getPyKey(Integer labId){
        return "PY"+SPLIT+ labId;
    }

    public static String getUpClassKey(Integer classId){
        return "upClass"+SPLIT+classId;
    }
}
