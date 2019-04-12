package lsn.javassit.nzh.com.javassit;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Test29Activity extends BaseActivity {

    @BindView(R.id.tv_test)
    TextView tv_test;

    @Override
    int getLayoutId() {
        return R.layout.activity_test2;
    }
    @Override
    protected void onResume() {
        super.onResume();
        tv_test.setText("----onResume-----");
    }

}
