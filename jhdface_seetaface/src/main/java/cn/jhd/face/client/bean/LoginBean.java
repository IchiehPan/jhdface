package cn.jhd.face.client.bean;

import cn.jhd.face.client.bean.base.Entity;

public class LoginBean extends Entity {

    /**
     *
     */
    private static final long serialVersionUID = -1330645987120968555L;
    private String token;
    private long expire_time;
    private String account;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(long expire_time) {
        this.expire_time = expire_time;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String name) {
        this.account = name;
    }
}