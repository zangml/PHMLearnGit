package com.koala.learn.entity;

public class Feature {
    private Integer id;

    private String name;

    private String label;

    private String des;

    private Integer featureType;

    public Feature(Integer id, String name, String label, String des,Integer featureType) {
        this.id = id;
        this.name = name;
        this.label = label;
        this.des = des;
        this.featureType=featureType;
    }

    public Feature() {
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label == null ? null : label.trim();
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des == null ? null : des.trim();
    }

    public Integer getType() {
        return featureType;
    }

    public void setType(Integer type) {
        this.featureType = type;
    }

    @Override
    public String toString() {
        return "Feature{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", des='" + des + '\'' +
                ", type=" + featureType +
                '}';
    }
}