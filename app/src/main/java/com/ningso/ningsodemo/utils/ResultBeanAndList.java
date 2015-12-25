package com.ningso.ningsodemo.utils;

import java.util.ArrayList;

/**
 * Created by NingSo on 15/12/18.下午10:12
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 */
public class ResultBeanAndList<T> {

    public Object bean;

    public ArrayList<T> list;

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public ArrayList<T> getList() {
        return list;
    }

    public void setList(ArrayList<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "ResultBeanAndList{" +
                "bean=" + bean +
                ", list=" + list +
                '}';
    }
}
