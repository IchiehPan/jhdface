package cn.jhd.face.client.bean;

import java.io.Serializable;

public class ResponseBean implements Serializable {

    public static final int SUCCESS = 1;
    public static final int NETWORK_ERROR = 0;

    private int code;
    private String msg;
    private int pages;
    private String file_name;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

}
