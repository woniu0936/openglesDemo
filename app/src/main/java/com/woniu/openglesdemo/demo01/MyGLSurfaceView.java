package com.woniu.openglesdemo.demo01;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * @author woniu
 * @title MyGLSurfaceView
 * @description
 * @modifier
 * @date
 * @since 16/7/19 下午7:32
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        mRenderer = new MyGLRenderer(context);
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
    }

}
