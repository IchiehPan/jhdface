package cn.jhd.face.client.bean;

import cn.jhd.face.client.bean.base.Entity;

public class UserBean extends Entity {
    /**
     *
     */
    private static final long serialVersionUID = 827393864070878964L;
    public static int MALE = 1;
    public static int FEMALE = 0;
    public static int SECRET = -1;

    private String uid;
    private String realname;
    private int gender;
    private String url;
    private String avatar;
    private String unit;
    private String company;
    private String position;
    private String department;
    private int index;
    private int configTime;
    private int on;
    private long lastTime;
    private int show;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realName) {
        this.realname = realName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getConfigTime() {
        return configTime;
    }

    public void setConfigTime(int configTime) {
        this.configTime = configTime;
    }

    public int getOn() {
        return on;
    }

    public void setOn(int on) {
        this.on = on;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public int getShow() {
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }

}
