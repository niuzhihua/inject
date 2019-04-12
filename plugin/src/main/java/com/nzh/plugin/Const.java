package com.nzh.plugin;

/**
 * Created by 31414 on 2019/4/11.
 */

public class Const {

    public static final String METHOD_OnCreate = "onCreate";
    public static final String METHOD_OnClick = "onClick";
    public static final String METHOD_SetContentView = "setContentView";

    // @OnClick 注解 名
    public static final String ONCLICK_ANNOTATION = "lsn.javassit.nzh.com.javassit.OnClick";
    // @BindView 注解名
    public static final String BINDVIEW_ANNOTATION = "lsn.javassit.nzh.com.javassit.BindView";

    // 初始化View 时生成的方法名。
    public static final String METHOD_NAME = "nzhInitView";

    // 设置onclickListener 时生成的方法名
    public static final String CALl_SET_LISTENER = "nzhSetListener()";

}
