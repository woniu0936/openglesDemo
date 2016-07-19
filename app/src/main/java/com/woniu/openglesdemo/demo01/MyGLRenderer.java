package com.woniu.openglesdemo.demo01;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author woniu
 * @title MyGLRenderer
 * @description
 * @modifier
 * @date
 * @since 16/7/19 下午7:32
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {
    /**
     * 调用一次，用来配置View的OpenGL ES环境
     * 你可能想知道，自己明明使用的是OpenGL ES 2.0接口，为什么这些方法会有一个GL10的参数。
     * 这是因为这些方法的签名（Method Signature）在2.0接口中被简单地重用了，以此来保持Android框架的代码尽量简单。
     * @param gl10
     * @param eglConfig
     */
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * 如果View的几何形态发生变化时会被调用，例如当设备的屏幕方向发生改变时
     *
     * @param gl10
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    /**
     * 每次重新绘制View时被调用
     *
     * @param gl10
     */
    @Override
    public void onDrawFrame(GL10 gl10) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}
