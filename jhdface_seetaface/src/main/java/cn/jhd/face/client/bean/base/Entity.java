package cn.jhd.face.client.bean.base;

import java.io.Serializable;

/**
 * 瀹炰綋绫�
 */
public abstract class Entity implements Serializable {

    public static final String ID = "id";

    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
