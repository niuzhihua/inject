package lsn.javassit.nzh.com.javassit;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nzh.plugin.api.BindView;

import org.w3c.dom.Text;

import java.util.List;

import lsn.javassit.nzh.com.javassit.fragment.BlankFragment;

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

        BlankFragment blankFragment = BlankFragment.newInstance("aaa","bbb");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container,blankFragment);
        transaction.commit();

    }


}
