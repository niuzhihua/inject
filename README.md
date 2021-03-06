# inject

概述： 这是一个在android 原生开发中 初始化View的 的gradle 插件。 类似  ButterKnife 框架 的   @BindView 功能。只不过用操作字节码的方式来实现。  目前支持 在Activity 和 Fragment 中使用。   支持的功能如下：

         Activity ： 
                  支持@BindView 注解来初始化View。
                  支持@OnClick注解来给View设置onClickListener
         Fragment :  
                  支持@BindView 注解来初始化View。
                  @OnClick 功能还没写。


## 使用条件
     Gradle version 4.1+ | Gradle 4.1 以上版本
     Android Gradle Plugin version 3.0+ | Android Gradle 插件 3.0 以上版本


## Installation

首先在application 插件所在module 的build.gradle 中引入插件：

```
     buildscript {

		repositories {
			google()
			jcenter()
		}
		dependencies {
			classpath 'com.android.tools.build:gradle:3.0.1'
			classpath 'com.nzh.plugin:inject:1.1.6'
			// NOTE: Do not place your application dependencies here; they belong
			// in the individual module build.gradle files
		}
	}
	   
```
然后在application module 中使用插件：

```
	apply plugin: 'com.android.application'
	apply plugin: 'com.nzh.plugin'    

```

最后加入注解api的依赖：
```
        dependencies {
            implementation fileTree(dir: 'libs', include: ['*.jar'])
            implementation 'com.android.support:support-v4:26.1.0'
            compile 'com.android.support:appcompat-v7:26.1.0'
            testImplementation 'junit:junit:4.12'

            // 引入依赖
            provided 'com.nzh.plugin:inject:1.1.6'   //
        }
```


## Usage

使用方式和ButterKnife一样，见 app module 下例子。

```
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
```

 其他使用场景见：
 MainActivity extends Activity
 Test3Activity extends AppCompatActivity
 Test4Activity extends FragmentActivity
 Test29Activity extends BaseActivity
 
 BlankFragment extends Fragment
 AFragment extends BaseFragment

## 其他说明

/Tutorial_demo 目录 :   javassist 一些基本使用的例子。
 更多例子见 
 https://github.com/jboss-javassist/javassist/tree/master/sample
