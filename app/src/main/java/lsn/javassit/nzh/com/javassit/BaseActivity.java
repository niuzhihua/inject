package lsn.javassit.nzh.com.javassit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public abstract class BaseActivity extends BaseActivity2 {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//
//        super.onCreate(savedInstanceState);
//        setContentView(getLayoutId());
//        test1();
//        test2(1);
//        test3("000");
//    }


    abstract int getLayoutId();

    public void test1() {
    }

    public void test2(int a) {
    }

    public void test3(String s) {
    }

}
