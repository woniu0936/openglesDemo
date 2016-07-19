package com.woniu.openglesdemo.demo02;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

/**
 * @author woniu
 * @title OpenGLES2Activity02
 * @description
 * @modifier
 * @date
 * @since 16/7/19 下午8:03
 */
public class OpenGLES2Activity02 extends Activity {
    private GLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }
}
