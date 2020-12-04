package com.springboot.provider.entity;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author XuZhenkui
 * @since 2020-10-15
 */
public class Role{

    private Integer id;

    private String title;

    public Role() {
    }

    public Role(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Role{" +
            "id=" + id +
            ", title=" + title +
        "}";
    }
}
