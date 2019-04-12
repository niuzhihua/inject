package com.nzh.plugin;

/**
 * Created by 31414 on 2019/4/3.
 */

public class MyFieldInfo {

    String fieldViewType; // 类型
    String fieldViewName; // 变量名称
    int fieldViewId = 0;   // id 值

    public MyFieldInfo(String fieldViewType, String fieldViewName, int fieldViewId) {
        this.fieldViewType = fieldViewType;
        this.fieldViewName = fieldViewName;
        this.fieldViewId = fieldViewId;
    }

    public MyFieldInfo(String fieldViewType, String fieldViewName) {
        this.fieldViewType = fieldViewType;
        this.fieldViewName = fieldViewName;
    }

    public String getFieldViewType() {
        return fieldViewType;
    }

    public String getFieldViewName() {
        return fieldViewName;
    }

    public int getFieldViewId() {
        return fieldViewId;
    }
}
