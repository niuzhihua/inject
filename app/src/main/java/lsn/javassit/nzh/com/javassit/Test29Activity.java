package lsn.javassit.nzh.com.javassit;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.TextView;

import com.nzh.plugin.api.BindView;


import lsn.javassit.nzh.com.javassit.fragment.AFragment;
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

        BlankFragment blankFragment = BlankFragment.newInstance("aaa", "bbb");
        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, blankFragment);

        AFragment aFragment = AFragment.newInstance();
        transaction.replace(R.id.fragment_container2, aFragment);

        transaction.commit();

    }


}
