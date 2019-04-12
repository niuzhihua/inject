package lsn.javassit.nzh.com.javassit;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity2 extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        aaa(1142);
    }

    public void aaa(int aa) {
    }

    abstract int getLayoutId();
}
