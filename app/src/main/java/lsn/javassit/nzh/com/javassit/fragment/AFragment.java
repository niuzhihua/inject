package lsn.javassit.nzh.com.javassit.fragment;

import android.widget.TextView;

import com.nzh.plugin.api.BindView;


import lsn.javassit.nzh.com.javassit.R;

/**
 * Created by 31414 on 2019/4/28.
 */

public class AFragment extends BaseFragment {

    @BindView(R.id.tv)
    TextView textView;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_blank;
    }

    @Override
    public void onResume() {
        super.onResume();

        textView.setText("-AFragment--test ok---");
    }

    public static AFragment newInstance() {

        AFragment fragment = new AFragment();
        return fragment;
    }
}
