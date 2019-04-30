package lsn.javassit.nzh.com.javassit;

import android.app.Activity;
import android.os.Trace;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nzh.plugin.api.BindView;
import com.nzh.plugin.api.OnClick;

public class Test4Activity extends FragmentActivity {

    @BindView(R.id.text)
    TextView textView;
    @BindView(R.id.text2)
    TextView textView2;
    @BindView(R.id.text3)
    TextView textView3;
    @BindView(R.id.text4)
    TextView textView4;
    TextView text5;
    @BindView(R.id.text6)
    TextView textView6;
    @BindView(R.id.text7)
    TextView textView7;
    @BindView(R.id.text8)
    TextView textView8;
    @BindView(R.id.text9)
    TextView textView9;
    @BindView(R.id.btn10)
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 用systrace 工具分析时 ，生成的trace 文件就包含我们自定义的 tag.
        Trace.beginSection("niuzhihua");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test4);

        text5 = findViewById(R.id.text5);
        textView.setText("Test4Activity extends AppCompatActivity");
        textView.setText("---Test4Activity extends AppCompatActivity---");
        textView4.setText("---点击textView4---");
        textView8.setText("---点击textView8---");
        button.setText("---button---");
        text5.setText("---id 和 view 名称一样---");


        Trace.endSection();
    }

    @OnClick({R.id.btn10, R.id.text4
    })
    public void def(View view) {
        Toast.makeText(this, "view" + view.getId(), Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.text8
    })
    public void abc(View view) {
        Toast.makeText(this, "view" + view.getId(), Toast.LENGTH_SHORT).show();
    }

}
