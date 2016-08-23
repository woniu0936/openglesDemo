package com.woniu.openglesdemo.demo07;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Bundle;

import com.woniu.openglesdemo.GlUtil;
import com.woniu.openglesdemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

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
public class OpenGLES2Activity07 extends Activity {
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
        mGLSurfaceView.setRenderer(new MyRenderer(this));
        //RenderMode 有两种
        // RENDERMODE_WHEN_DIRTY:懒惰渲染，需要手动调用 glSurfaceView.requestRender() 才会进行更新
        // RENDERMODE_CONTINUOUSLY : 连续渲染
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    static class MyRenderer implements GLSurfaceView.Renderer {
        private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "attribute vec2 a_texCoord;" +
                "varying vec2 v_texCoord;" +
                "void main() {" +
                "  gl_Position = uMVPMatrix * vPosition;" +
                "  v_texCoord = a_texCoord;" +
                "}";
        private static final String FRAGMENT_SHADER = "precision mediump float;" +
                "varying vec2 v_texCoord;" +
                "uniform sampler2D s_texture;" +
                "void main() {" +
                "  gl_FragColor = texture2D( s_texture, v_texCoord );" +
                "}";

        private static final float[] VERTEX = {   // in counterclockwise order:
                1, 1, 0,   // top right
                -1, 1, 0,  // top left
                -1, -1, 0, // bottom left
                1, -1, 0,  // bottom right
        };

        private static final short[] VERTEX_INDEX = {0, 1, 2, 0, 2, 3};

        private final FloatBuffer mVertexBuffer;
        private final FloatBuffer mUvTexVertexBuffer;
        private final ShortBuffer mVertexIndexBuffer;
        private final float[] mProjectionMatrix = new float[16];
        private final float[] mCameraMatrix = new float[16];
        private final float[] mMVPMatrix = new float[16];

        private int mProgram;
        private int mPositionHandle;
        private int mMatrixHandle;
        private int mTexCoordHandle;
        private int mTexSamplerHandle;
        int[] mTexNames;
        Resources mResources;

        private int waterMarkId;

        //显示整个图片
        private static final float[] UV_TEX_VERTEX = {   // in clockwise order:
                1, 0,  // bottom right
                0, 0,  // bottom left
                0, 1,  // top left
                1, 1,  // top right
        };

        //只显示了图片的左上角部分
//        private static final float[] UV_TEX_VERTEX = {   // in clockwise order:
//                0.5f, 0,  // bottom right
//                0, 0,  // bottom left
//                0, 0.5f,  // top left
//                0.5f, 0.5f,  // top right
//        };

        MyRenderer(Context context) {
            mResources = context.getResources();
            mVertexBuffer = ByteBuffer.allocateDirect(VERTEX.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(VERTEX);
            mVertexBuffer.position(0);

            mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
                    .order(ByteOrder.nativeOrder())
                    .asShortBuffer()
                    .put(VERTEX_INDEX);
            mVertexIndexBuffer.position(0);

            mUvTexVertexBuffer = ByteBuffer.allocateDirect(UV_TEX_VERTEX.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(UV_TEX_VERTEX);
            mUvTexVertexBuffer.position(0);

            waterMarkId = GlUtil.createTextureFromImage(context, R.mipmap.watermark);
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
            mProgram = GLES20.glCreateProgram();
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);

            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_texCoord");
            mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            mTexSamplerHandle = GLES20.glGetUniformLocation(mProgram, "s_texture");

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
         *
         * @param unused
         */
        @Override
        public void onDrawFrame(GL10 unused) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            GLES20.glUseProgram(mProgram);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, waterMarkId);

            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0,
                    mVertexBuffer);

            GLES20.glEnableVertexAttribArray(mTexCoordHandle);
            GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0,
                    mUvTexVertexBuffer);

            GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
            GLES20.glUniform1i(mTexSamplerHandle, 0);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                    GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

            GLES20.glDisableVertexAttribArray(mPositionHandle);
            GLES20.glDisableVertexAttribArray(mTexCoordHandle);

        }

        static int loadShader(int type, String shaderCode) {
            int shader = GLES20.glCreateShader(type);
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);
            return shader;
        }
    }

}
