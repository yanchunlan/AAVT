/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wuwang.aavt.gl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;

import com.wuwang.aavt.utils.DrawUtils;
import com.wuwang.aavt.utils.GpuUtils;

import java.util.List;

/**
 * WaterMarkFilter
 *
 * @author wuwang
 * @version v1.0 2017:11:06 14:10
 */
public class WaterMarkFilter extends LazyFilter {

    private int[] viewPort = new int[4]; // 视频图像的原坐标

    // logo
    private int[] port_Logo = new int[4];  // 坐标点
    private final LazyFilter filter_Logo = new LazyFilter() { // 封装filter
        @Override
        protected void onClear() {
        }
    };
    private int textureId_Logo = -1; // textureId
    private float ratio_Logo = 1;  //  ratio=w/h 宽高比
    private int gravity_Logo = Gravity.NO_GRAVITY; // 对齐方式

    // director
    private int[] port_director = new int[2];
    private LazyFilter filter_director = null;
    private int[] textureId_director = null;
    private float ratio_director = 1;  //  ratio=w/h
    private boolean isCreateDirector = false;
    private final Object director_Lock = new Object();
    public static final int bmpNumber = 5;  // 控制动画的帧数

    // Overview
    private int[] port_overview = new int[2];
    private LazyFilter filter_overview = null;
    private int[] textureId_overview = null;
    private float ratio_overView = 1;  //  ratio=w/h
    private boolean isCreateOverview = false;
    private final Object overview_Lock = new Object();
    private int lastTime = -1;


    @Override
    protected void onCreate() {
        super.onCreate();
        filter_Logo.create();
        if (isCreateDirector) {
            filter_director = new LazyFilter() {
                @Override
                protected void onClear() {
                }
            };
            filter_director.create();
        }
        if (isCreateOverview) {
            filter_overview = new LazyFilter() {
                @Override
                protected void onClear() {
                }
            };
            filter_overview.create();
        }
    }

    @Override
    protected void onDrawChild(long time) {
        if (textureId_Logo != -1) {
            GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewPort, 0);

            // 添加混合模式 凸显水印
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);

            // logo
            switch (gravity_Logo) {
                case Gravity.RIGHT:
                    GLES20.glViewport(mWidth - port_Logo[0] - port_Logo[2], mHeight - port_Logo[3] - port_Logo[1], port_Logo[2], port_Logo[3]);
                    break;
                default:
                    GLES20.glViewport(port_Logo[0], mHeight - port_Logo[3] - port_Logo[1], port_Logo[2], port_Logo[3]);
                    break;
            }
            filter_Logo.draw(textureId_Logo);


//            Log.d("123", "onDrawChild: "+time);
            // 绘制导演信息
            if (textureId_director != null && textureId_director.length != 0) {
                if (time < 100000000L) {

                }
//                else if (time < 1000000000L) { // 500ms 做完 显示动画
//                    GLES20.glViewport((mWidth - port_director[0]) / 2, (mHeight - port_director[1]) / 2, port_director[0], port_director[1]);
//                    long interval = 500000000L / bmpNumber;
//                    int n = (int) ((time - 500000000L) / interval);
//                    Log.d("123", "onDrawChild:n  "+n);
//                    filter_director.draw(textureId_director[n]);
//                }
                else if (time < 3000000000L) {
                    GLES20.glViewport((mWidth - port_director[0]) / 2, (mHeight - port_director[1]) / 2, port_director[0], port_director[1]);
                    filter_director.draw(textureId_director[textureId_director.length - 1]);
                } else if (time < 5000000000L) { //500ms 做完 退出动画
                    GLES20.glViewport((mWidth - port_director[0]) / 2, (mHeight - port_director[1]) / 2, port_director[0], port_director[1]);
                    long interval = 3000000000L / bmpNumber;
                    int n = (int) ((6000000000L - time) / interval);
//                    Log.d("123", "onDrawChild:n  "+n);
                    filter_director.draw(textureId_director[n]);
                } else {
                    deleteDirectorTexture();
                }
            }

            // 绘制概述信息
            Log.d("123", "onDrawChild:time  " + time);
            Log.d("123", "onDrawChild:time  length " + textureId_overview.length);
            Log.d("123", "onDrawChild:number  " + ((int) (time / 1000000000L)));
            if (textureId_overview != null && textureId_overview.length != 0) {
                int cTime = ((int) (time / 1000000000L)) % (textureId_overview.length);
                Log.d("123", "onDrawChild: cTime " + cTime + " lastTime " + lastTime);
                if (cTime != lastTime) {
                    lastTime = cTime;
                }
                GLES20.glViewport(0, 0, port_overview[0], port_overview[1]);
                filter_overview.draw(textureId_overview[lastTime]);
            }
            GLES20.glDisable(GLES20.GL_BLEND);

            GLES20.glViewport(viewPort[0], viewPort[1], viewPort[2], viewPort[3]);
        }
    }

    // -----------------------------------  set logo  start   ----------------------------------------
    public WaterMarkFilter setPositionLogo(final int x, final int y, final int width) {
        port_Logo[0] = x;
        port_Logo[1] = y;
        port_Logo[2] = width;
        port_Logo[3] = (int) (width / ratio_Logo);
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                filter_Logo.sizeChanged(width, (int) (width / ratio_Logo));
            }
        });
        return this;
    }

    public WaterMarkFilter setGravityLogo(int gravity_Logo) {
        this.gravity_Logo = gravity_Logo;
        return this;
    }

    public WaterMarkFilter setBitmapLogo(final Bitmap bmp) {
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                if (bmp != null) {
                    ratio_Logo = bmp.getWidth() * 1.0f / bmp.getHeight();
                    port_Logo[3] = (int) (port_Logo[2] / ratio_Logo); // 此处需要重置一下h

                    if (textureId_Logo == -1) {
                        textureId_Logo = GpuUtils.createTextureID(false);
                    } else {
                        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_Logo);
                    }
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
                    bmp.recycle();
                } else {
                    if (textureId_Logo != -1) {
                        GLES20.glDeleteTextures(1, new int[]{textureId_Logo}, 0);
                    }
                }
            }
        });
        return this;
    }
    // -----------------------------------  set  logo  end   ----------------------------------------


    // -----------------------------------  set director  start   ----------------------------------------
    // 导演
    public WaterMarkFilter setBitmapDirector(final List<Bitmap> bmpData) {
        if (bmpData == null || bmpData.isEmpty()) {
            isCreateDirector = false;
            return this;
        }
        isCreateDirector = true;
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                if (bmpData != null && !bmpData.isEmpty()) {
                    int len = bmpData.size();
                    if (textureId_director == null) {
                        textureId_director = new int[len];
                    }

                    for (int i = 0; i < len; i++) {
                        Bitmap bmp = bmpData.get(i);
                        // 只设置一次
                        if (ratio_director == 1) {
                            ratio_director = bmp.getWidth() * 1.0f / bmp.getHeight();
                        }

                        int textureId = GpuUtils.createTextureID(false);
                        textureId_director[i] = textureId;
                        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
                        bmp.recycle();
                    }
                } else {
                    deleteDirectorTexture();
                }
            }
        });
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                //  根据屏幕适配 横屏宽度显示一半适配，竖屏显示4/5
                if (mWidth > mHeight) {
                    port_director[0] = mWidth / 2;
                } else {
                    port_director[0] = mWidth * 4 / 5;
                }
                port_director[1] = (int) (port_director[0] / ratio_director);
                filter_director.sizeChanged(port_director[0], port_director[1]);
            }
        });
        return this;
    }

    private void deleteDirectorTexture() {
        synchronized (director_Lock) {
            if (textureId_director != null) {
                int len = textureId_director.length;
                if (len != 0) {
                    GLES20.glDeleteTextures(len, textureId_director, 0);
                    textureId_director = null;
                }
            }
        }
    }
    // -----------------------------------  set  director  end   ----------------------------------------


    // -----------------------------------  set overView  start   ----------------------------------------

    // 概述
    public WaterMarkFilter setTextOverView(final String text) {
        if (text == null || TextUtils.isEmpty(text)) {
            isCreateOverview = false;
            return this;
        }
        isCreateOverview = true;
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                if (text != null && !TextUtils.isEmpty(text)) {
                    // 横屏最大宽度是3/4，竖屏是全屏
                    List<Bitmap> bmpData = DrawUtils.createTextImage(text, mWidth > mHeight ? mWidth * 3 / 4 : mWidth);
                    int len = bmpData.size();
                    if (textureId_overview == null) {
                        textureId_overview = new int[len];
                    }

                    for (int i = 0; i < len; i++) {
                        Bitmap bmp = bmpData.get(i);
                        // 只设置一次
                        if (ratio_overView == 1) {
                            ratio_overView = bmp.getWidth() * 1.0f / bmp.getHeight();
                        }

                        int textureId = GpuUtils.createTextureID(false);
                        textureId_overview[i] = textureId;
                        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
                        bmp.recycle();
                    }
                } else {
                    deleteOverViewTexture();
                }
            }
        });
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                //  根据屏幕适配 根据屏幕适配 横屏宽度显示一半适配，竖屏满屏
                if (mWidth > mHeight) {
                    port_overview[0] = mWidth * 3 / 4;
                } else {
                    port_overview[0] = mWidth;
                }
                port_overview[1] = (int) (port_overview[0] / ratio_overView);
                filter_overview.sizeChanged(port_overview[0], port_overview[1]);
            }
        });
        return this;
    }

    private void deleteOverViewTexture() {
        synchronized (overview_Lock) {
            if (textureId_overview != null) {
                int len = textureId_overview.length;
                if (len != 0) {
                    GLES20.glDeleteTextures(len, textureId_overview, 0);
                    textureId_overview = null;
                }
            }
        }
    }
    // -----------------------------------  set  director  end   ----------------------------------------


    @Override
    public void destroy() {
        super.destroy();
        deleteDirectorTexture();
        deleteOverViewTexture();
    }
}
