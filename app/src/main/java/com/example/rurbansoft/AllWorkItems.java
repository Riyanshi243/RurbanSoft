package com.example.rurbansoft;

public class AllWorkItems {

    public AllWorkItems(String state, String district, String cluster, String gp, String components, String subComponents, String phase, String status, String dateTime) {
        State = state;
        District = district;
        Cluster = cluster;
        Gp = gp;
        Components = components;
        SubComponents = subComponents;
        Phase = phase;
        Status = status;
        DateTime = dateTime;
        this.img = img;


    }

    private String State;
    private String District;
    private String Cluster;
    private String Gp;
    private String Components;
    private String SubComponents;
    private String Phase;
    private String Status;
    private String DateTime;


    byte[] img;

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getCluster() {
        return Cluster;
    }

    public void setCluster(String cluster) {
        Cluster = cluster;
    }

    public String getGp() {
        return Gp;
    }

    public void setGp(String gp) {
        Gp = gp;
    }

    public String getComponents() {
        return Components;
    }

    public void setComponents(String components) {
        Components = components;
    }

    public String getSubComponents() {
        return SubComponents;
    }

    public void setSubComponents(String subComponents) {
        SubComponents = subComponents;
    }

    public String getPhase() {
        return Phase;
    }

    public void setPhase(String phase) {
        Phase = phase;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }
}
