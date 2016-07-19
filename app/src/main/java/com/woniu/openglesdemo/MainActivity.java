package com.woniu.openglesdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.woniu.openglesdemo.demo01.OpenGLES20Activity;
import com.woniu.openglesdemo.demo02.OpenGLES2Activity02;
import com.woniu.openglesdemo.demo03.OpenGLES2Activity03;
import com.woniu.openglesdemo.demo04.OpenGLES2Activity04;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_01)
    Button btn01;
    @BindView(R.id.btn_02)
    Button btn02;
    @BindView(R.id.btn_03)
    Button btn03;
    @BindView(R.id.btn_04)
    Button btn04;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btn01.setOnClickListener(this);
        btn02.setOnClickListener(this);
        btn03.setOnClickListener(this);
        btn04.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_01:
                startActivity(new Intent(this, OpenGLES20Activity.class));
                break;
            case R.id.btn_02:
                startActivity(new Intent(this, OpenGLES2Activity02.class));
                break;
            case R.id.btn_03:
                startActivity(new Intent(this, OpenGLES2Activity03.class));
                break;
            case R.id.btn_04:
                startActivity(new Intent(this, OpenGLES2Activity04.class));
                break;
        }
    }
}
