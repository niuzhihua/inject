1: 概述： 这是一个在android 原生开发中 初始化View的 的gradle 插件。 类似  ButterKnife 框架 的@BindView 功能。只不过用操作字节码的
         方式来实现。  目前支持 在Activity 和 Fragment 中使用。其中：

         Activity ： 
                  支持@BindView 注解来初始化View。
                  支持@OnClick注解来给View设置onClickListener
         Fragment :  
                  支持@BindView 注解来初始化View。
                  @OnClick 功能还没写。

2： 使用条件：

     Gradle version 4.1+ | Gradle 4.1 以上版本
     Android Gradle Plugin version 3.0+ | Android Gradle 插件 3.0 以上版本


3：使用介绍：

    在 工程的build.gradle 文件中的 buildscript 的 classpath 中引入 插件：

       classpath 'com.nzh.plugin:inject:1.1.6'

    然后在application 插件所在module 的build.gradle 中使用插件：

        apply plugin: 'com.android.application'
        apply plugin: 'com.nzh.plugin'    //

        dependencies {
            implementation fileTree(dir: 'libs', include: ['*.jar'])
            implementation 'com.android.support:support-v4:26.1.0'
            compile 'com.android.support:appcompat-v7:26.1.0'
            testImplementation 'junit:junit:4.12'

            // 引入依赖
            provided 'com.nzh.plugin:inject:1.1.6'   //
        }

4： 使用见 app module 下例子。

        public class Test3Activity extends AppCompatActivity {

            @BindView(R.id.name)
            TextView mName;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_test3);

                mName.setText("--Test3Activity extends Activity--");
            }

            @OnClick({R.id.btn,
                    R.id.btn2})
            public void test(View view) {

            }

        }

       // 其他见：
        MainActivity extends Activity
        Test3Activity extends AppCompatActivity
        Test4Activity extends FragmentActivity
        Test29Activity extends BaseActivity

        BlankFragment extends Fragment
        AFragment extends BaseFragment


 5：   /Tutorial_demo : javassist 一些基本使用的例子。
        更多例子见 https://github.com/jboss-javassist/javassist/tree/master/sample
