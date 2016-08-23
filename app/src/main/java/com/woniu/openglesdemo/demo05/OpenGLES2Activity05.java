package com.woniu.openglesdemo.demo05;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;

import com.woniu.openglesdemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author woniu
 * @title OpenGLES2Activity05
 * @description 渲染一个三角形
 * @modifier
 * @date
 * @since 16/7/19 下午8:03
 */
public class OpenGLES2Activity05 extends Activity {
    @BindView(R.id.mGLSurfaceView)
    GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opengl_05);
        ButterKnife.bind(this);

        //设置 OpenGL ES 版本
        mGLSurfaceView.setEGLContextClientVersion(2);
        //往画布(GLSurfaceView)中添加渲染(Renderer)
        mGLSurfaceView.setRenderer(new MyRenderer());
        //RenderMode 有两种
        // RENDERMODE_WHEN_DIRTY:懒惰渲染，需要手动调用 glSurfaceView.requestRender() 才会进行更新
        // RENDERMODE_CONTINUOUSLY : 连续渲染
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    static class MyRenderer implements GLSurfaceView.Renderer {
        private static final String VERTEX_SHADER = "attribute vec4 vPosition;\n"
                + "uniform mat4 uMVPMatrix;\n"
                + "void main() {\n"
                + "  gl_Position = uMVPMatrix * vPosition;\n"
                + "}";
//        private static final String VERTEX_SHADER = "attribute vec4 vPosition;\n"
//                + "void main() {\n"
//                + "  gl_Position = vPosition;\n"
//                + "}";
        private static final String FRAGMENT_SHADER = "precision mediump float;\n"
                + "void main() {\n"
                + "  gl_FragColor = vec4(0.5,0,0,1);\n"
                + "}";
        private static final float[] VERTEX = {   // in counterclockwise order:
                0, 1, 0.0f, // top
                -0.5f, -1, 0.0f, // bottom left
                1f, -1, 0.0f,  // bottom right
        };

        private final FloatBuffer mVertexBuffer;
        private final float[] mProjectionMatrix = new float[16];
        private final float[] mCameraMatrix = new float[16];
        private final float[] mMVPMatrix = new float[16];

        private int mProgram;
        private int mPositionHandle;
        private int mMatrixHandle;


        MyRenderer() {
            mVertexBuffer = ByteBuffer.allocateDirect(VERTEX.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(VERTEX);
            mVertexBuffer.position(0);
        }

        /**
         * 在 surface 创建时被回调，通常用于进行初始化工作，只会被回调一次
         *
         * @param unused
         * @param config
         */
        @Override
        public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        }

        /**
         * 在每次 surface 尺寸变化时被回调，
         * 注意，第一次得知 surface 的尺寸时也会回调
         *
         * @param unused
         * @param width
         * @param height
         */
        @Override
        public void onSurfaceChanged(GL10 unused, int width, int height) {
            /**有的时候，我们的初始化工作可能需要依赖 surface 的尺寸，所以这里我们把初始化工作放到了 onSurfaceChanged 方法中。*/
            //创建 GLSL 程序
            mProgram = GLES20.glCreateProgram();
            //加载 shader 代码
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
            //加载 shader 代码
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
            //attatch shader 代码
            GLES20.glAttachShader(mProgram, vertexShader);
            //attatch shader 代码
            GLES20.glAttachShader(mProgram, fragmentShader);
            //链接 shader 代码
            GLES20.glLinkProgram(mProgram);

            //获取 shader 代码中的变量索引
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            /**需要指出的是，shader 代码中的变量索引，在 GLSL 程序的生命周期内（两次链接之间），都是固定的，只需要获取一次*/

            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

//            public static void frustumM(float[] m,    //保存变换矩阵的数组
//            int offset,   //开始保存的下标偏移量
//            float left,
//            float right,
//            float bottom,
//            float top,
//            float near,   //注意 0 < near < far
//            float far)
//            float ratio = (float) width / height;
//            Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
            float ratio = (float) height / width;
            Matrix.frustumM(mProjectionMatrix, 0, -1, 1, -ratio, ratio, 3, 7);
//            public static void setLookAtM ( float[] rm,
//            int rmOffset,
//            float eyeX, float eyeY, float eyeZ,
//            float centerX, float centerY, float centerZ,
//            float upX, float upY, float upZ)
//            我们需要传入 9 个坐标值，(eyeX, eyeY, eyeX)，(centerX, centerY, centerZ)，(upX, upY, upZ)。
//            eye 表示相机的坐标点，center 表示物体（目标，或者图形）的中心坐标点，up 表示方向向量。
            Matrix.setLookAtM(mCameraMatrix, 0, 0, 0, 3, 0, 0, 0, 0, 1, 0);
            //矩阵乘法的封装函数。
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mCameraMatrix, 0);
        }

        /**
         * 在绘制每一帧的时候回调
         * 绘制的过程其实就是为 shader 代码变量赋值，并调用绘制命令的过程
         * @param unused
         */
        @Override
        public void onDrawFrame(GL10 unused) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            GLES20.glUseProgram(mProgram);

            // 启用一个指向三角形的顶点数组的handle
            GLES20.glEnableVertexAttribArray(mPositionHandle);

            // 准备三角形的坐标数据
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                    12, mVertexBuffer);

            //开始绘制
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

            GLES20.glDisableVertexAttribArray(mPositionHandle);

            GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        }

        static int loadShader(int type, String shaderCode) {
            int shader = GLES20.glCreateShader(type);
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);
            return shader;
        }
    }

}
