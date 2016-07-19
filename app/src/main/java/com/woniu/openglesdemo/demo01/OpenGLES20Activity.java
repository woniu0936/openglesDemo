package com.woniu.openglesdemo.demo01;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

/**
 * @author woniu
 * @title OpenGLES20Activity
 * @description 它使用OpenGL让屏幕呈现为黑色
 * @modifier
 * @date
 * @since 16/7/19 下午7:30
 */
public class OpenGLES20Activity extends Activity {
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
