package com.nzh.plugin;

/**
 * Created by 31414 on 2019/4/11.
 */

public class Const {

    public static final String METHOD_OnCreate = "onCreate";
    public static final String METHOD_OnCreateView = "onCreateView";
    public static final String METHOD_OnClick = "onClick";
    public static final String METHOD_SetContentView = "setContentView";

    // @OnClick 注解 名
    public static final String ONCLICK_ANNOTATION = "com.nzh.plugin.api.OnClick";
    // @BindView 注解名
    public static final String BINDVIEW_ANNOTATION = "com.nzh.plugin.api.BindView";

    // 初始化View 时生成的方法名。
    public static final String METHOD_NAME = "nzhInitView";
    // Fragment 中 生成方法用到的
    public static final String NZH_Gen_Method = "nzhInitView(android.view.View v)";
    public static final String NZH_Gen_Method_Body = "v.findViewById(";

    // 设置onclickListener 时生成的方法名
    public static final String CALl_SET_LISTENER = "nzhSetListener()";

}
