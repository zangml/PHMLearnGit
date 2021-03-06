package com.koala.learn.entity;

public class CourseType {
    private Integer id;

    private String name;

    private String cover;

    private Integer hasOj;

    public CourseType(Integer id, String name, String cover, Integer hasOj) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.hasOj = hasOj;
    }

    public CourseType() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover == null ? null : cover.trim();
    }

    public Integer getHasOj() {
        return hasOj;
    }

    public void setHasOj(Integer hasOj) {
        this.hasOj = hasOj;
    }
}