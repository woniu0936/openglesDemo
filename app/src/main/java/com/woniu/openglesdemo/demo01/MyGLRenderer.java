package com.woniu.openglesdemo.demo01;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import com.woniu.openglesdemo.R;

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

    private int[] mTexNames;
    private Resources mResources;

    public MyGLRenderer(Context context) {
        mResources = context.getResources();
    }

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
        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
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

        mTexNames = new int[1];
        GLES20.glGenTextures(1, mTexNames, 0);

        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.mipmap.ic_launcher);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexNames[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
    }
}
