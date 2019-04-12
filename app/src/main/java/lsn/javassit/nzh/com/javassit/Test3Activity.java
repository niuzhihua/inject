package lsn.javassit.nzh.com.javassit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Test3Activity extends Activity {

    @BindView(R.id.name)
    TextView mName;

    TextView test;

    String a = "test";
    int test2 = 10;


    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);
        mName.setText("Test3Activity extends Activity");
        mName.setText("我用了@BindView");

    }


    @OnClick({R.id.btn,
            R.id.btn2})
    public void test(View view) {


    }


}
