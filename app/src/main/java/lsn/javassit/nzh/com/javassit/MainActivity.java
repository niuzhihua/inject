package lsn.javassit.nzh.com.javassit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.tv)
    TextView tv;

    Button btn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv.setText("hello javassist!");
        tv.setText("2hello javassist!");


//        Debug.startMethodTracing();
//        Debug.startMethodTracing("");
//        Debug.stopMethodTracing();
    }


    public void to_Activity_Test2(View view) {
        startActivity(new Intent(this, Test29Activity.class));
    }

    public void to_Activity_Test3(View view) {

        startActivity(new Intent(this, Test3Activity.class));

    }

    public void to_AppCompatActivity_Test4(View view) {
        startActivity(new Intent(this, Test4Activity.class));
    }


}
