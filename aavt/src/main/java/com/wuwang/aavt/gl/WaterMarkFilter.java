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
    public static final int bmpNumber = 1;  // 控制动画的帧数

    // Overview
    private int[] port_overview = new int[2];
    private LazyFilter filter_overview = null;
    private int textureId_overview = -1;
    private float ratio_overView = 1;  //  ratio=w/h
    private boolean isCreateOverview = false;
    private final Object overview_Lock = new Object();
    // Overview动画
    private int lastX = 0; // 不断累加x，平移图片达到动画效果
    private boolean canWaterMark = false;
    private final int speed = 3; //水印滚动速度

    // title location
    private int[] port_title = new int[2];
    private LazyFilter filter_title = null;
    private int textureId_title = -1; // textureId
    private float ratio_title = 1;  //  ratio=w/h 宽高比
    private boolean isCreateTitle = false;


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
        if (isCreateTitle) {
            filter_title = new LazyFilter() {
                @Override
                protected void onClear() {
                }
            };
            filter_title.create();
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


            // 绘制导演信息
            if (textureId_director != null && textureId_director.length != 0) {
                if (time < 100000000L) { // 100ms内不添加动画

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
                    long interval = 2000000000L / bmpNumber;
                    int n = (int) ((5000000000L - time) / interval);
//                    Log.d("123", "onDrawChild:n  "+n);
                    if (n >= textureId_director.length) {
                        n = textureId_director.length - 1;
                    }
                    filter_director.draw(textureId_director[n]);
                } else {
                    deleteDirectorTexture();
                }
            }

            // 绘制概述信息
            if (textureId_overview != -1) {
                if (canWaterMark && time > 3000000000L) { // 大于1s才开始动画
                    lastX -= speed;
                    // 重复动画
                    if (lastX <= (-port_overview[0] + mWidth)) {
                        lastX = speed * 3;
                    }
                }
                GLES20.glViewport(lastX, 0, port_overview[0], port_overview[1]);
                filter_overview.draw(textureId_overview);
            }

            // 绘制标题信息
            if (textureId_title != -1) {
                // 概述有无决定其高度
                if (textureId_overview != -1) {
                    GLES20.glViewport(0, port_overview[1], port_title[0], port_title[1]);
                } else {
                    GLES20.glViewport(0, 0, port_title[0], port_title[1]);
                }
                filter_title.draw(textureId_title);
            }

            GLES20.glDisable(GLES20.GL_BLEND);

            GLES20.glViewport(viewPort[0], viewPort[1], viewPort[2], viewPort[3]);
        }
    }

    // -----------------------------------  set logo  start   ----------------------------------------
    public WaterMarkFilter setPositionLogo(final int x, final int y) {
        port_Logo[0] = x;
        port_Logo[1] = y;
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                if (mWidth > mHeight) {  // 横屏 依据横屏适配
                    port_Logo[2] = (int) (mWidth * 90f / 667);
                    port_Logo[3] = (int) (port_Logo[2] / ratio_Logo);
                } else { // 竖屏
                    port_Logo[2] = (int) (mWidth * 90f / 375);
                    port_Logo[3] = (int) (port_Logo[2] / ratio_Logo);
                }
                filter_Logo.sizeChanged(port_Logo[2], port_Logo[3]);
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
                        textureId_Logo = -1;
                    }
                }
            }
        });
        return this;
    }
    // -----------------------------------  set  logo  end   ----------------------------------------


    // -----------------------------------  set director  start   ----------------------------------------
    // 导演
    public WaterMarkFilter setBitmapDirector(final int Drawable) {
        if (Drawable == 0) {
            isCreateDirector = false;
            return this;
        }
        isCreateDirector = true;
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                if (Drawable != 0) {
                    float padding_5 = 5f; // 只计算5距离，进去界面单独计算间隔
                    float textHeight = 0;
                    if (mWidth > mHeight) { // 高度适配
                        port_director[1] = mHeight * 231 / 375;
                        port_director[0] = mHeight * 294 / 375; // port_director[1] * 294 / 231;
                        textHeight = (mHeight * 24f / 375);
                        padding_5 = mWidth * 5f / 667;
                    } else { // 宽度适配
//                        port_director[0] = mWidth * 294 / 667;
//                        port_director[1] = mWidth * 231 / 667; // port_director[0]*231/294;
                        // 竖屏 宽度加大100 ，294+100=394
                       port_director[0] = mWidth * 394 / 667;
                        port_director[1] = mWidth * 231 / 667; // port_director[0]*231/294;


                        textHeight = (mHeight * 24f / 667);
                        padding_5 = mWidth * 5f / 375;
                    }
                    List<Bitmap> bmpData = DrawUtils.createTextImage(bmpNumber, Drawable, port_director[0], port_director[1], textHeight, padding_5, new DrawUtils.CallBack() {
                        @Override
                        public void callBackWH(int width, int height) {
                            port_director[0] = width;
                            port_director[1] = height;
                        }
                    });
                    if (bmpData != null && !bmpData.isEmpty()) {
                        int len = bmpData.size();
                        if (textureId_director == null) {
                            textureId_director = new int[len];
                        }

                        for (int i = 0; i < len; i++) {
                            Bitmap bmp = bmpData.get(i);
                            // 只设置一次
//                            if (ratio_director == 1) {
//                                ratio_director = bmp.getWidth() * 1.0f / bmp.getHeight();
//                            }

                            int textureId = GpuUtils.createTextureID(false);
                            textureId_director[i] = textureId;
                            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
                            bmp.recycle();
                        }
                    }
                } else {
                    deleteDirectorTexture();
                }
            }
        });
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
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

                    // 计算真实高度，传入进去，创建与真实高度一致的图片
                    float padding_5 = 5f; // 只计算5距离，进去界面单独计算间隔
                    if (mWidth > mHeight) {  // 横屏 依据横屏适配
                        port_overview[1] = (int) (mHeight * 20f / 375);
                        padding_5 = mWidth * 5f / 667;
                    } else { // 竖屏
                        port_overview[1] = (int) (mHeight * 20f / 667);
                        padding_5 = mWidth * 5f / 375;
                    }
                    Bitmap bmp = DrawUtils.textToBitmap(text, mWidth, port_overview[1], padding_5);

                    port_overview[0] = bmp.getWidth();
                    // 判断文字是否超过一屏幕，决定动画启动与否
                    if (port_overview[0] > mWidth) {
                        canWaterMark = true;
                    }

                    Log.d("123", "run: bmpW " + bmp.getWidth() + " bmpH " + bmp.getHeight());
                    Log.d("123", "run: canWaterMark " + canWaterMark + " " + (port_overview[0] - mWidth));
                    Log.d("123", "run: mWidth " + mWidth + " mHeight " + mHeight);
                    Log.d("123", "run: port_overview " + port_overview[0] + " port_overview " + port_overview[1]);

                    if (textureId_overview == -1) {
                        textureId_overview = GpuUtils.createTextureID(false);
                    } else {
                        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_overview);
                    }
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
                    bmp.recycle();
                } else {
                    deleteOverViewTexture();
                }
            }
        });
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                filter_overview.sizeChanged(port_overview[0], port_overview[1]);
            }
        });
        return this;
    }

    private void deleteOverViewTexture() {
        synchronized (overview_Lock) {
            if (textureId_overview != -1) {
                GLES20.glDeleteTextures(1, new int[]{textureId_overview}, 0);
                textureId_overview = -1;
            }
        }
    }
    // -----------------------------------  set  director  end   ----------------------------------------


    // -----------------------------------  set title and location  start   ----------------------------------------

    // 标题
    public WaterMarkFilter setTextTitleAndLocation(final String title, final String location, final int drawable) {
        if ((title == null || TextUtils.isEmpty(title)) && (location == null || TextUtils.isEmpty(location))) {
            isCreateTitle = false;
            return this;
        }
        isCreateTitle = true;
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                if ((title != null && !TextUtils.isEmpty(title)) || (location != null && !TextUtils.isEmpty(location))) {


                    // 计算真实高度，传入进去，创建与真实高度一致的图片
                    float padding_5 = 5f; // 只计算5距离，进去界面单独计算间隔
                    if (mWidth > mHeight) {  // 横屏 依据横屏适配
                        port_title[1] = (int) (mHeight * 24f / 375);
                        padding_5 = mWidth * 5f / 667;
                    } else { // 竖屏
                        port_title[1] = (int) (mHeight * 24f / 667);
                        padding_5 = mWidth * 5f / 375;
                    }

                    Bitmap bmp = DrawUtils.textToBitmap(title, location, drawable, mWidth, port_title[1], padding_5);
                    ratio_title = bmp.getWidth() * 1.0f / bmp.getHeight();
                    port_title[0] = (int) (port_title[1] * ratio_title);


                    Log.d("123", "run: bmpW " + bmp.getWidth() + " bmpH " + bmp.getHeight());
                    Log.d("123", "run: mWidth " + mWidth + " mHeight " + mHeight);
                    Log.d("123", "run: port_title " + port_title[0] + " port_title " + port_title[1]);


                    if (textureId_title == -1) {
                        textureId_title = GpuUtils.createTextureID(false);
                    } else {
                        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_title);
                    }
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
                    bmp.recycle();
                } else {
                    deleteTitleAndLocationTexture();
                }
            }
        });
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                filter_title.sizeChanged(port_title[0], port_title[1]);
            }
        });
        return this;
    }

    private void deleteTitleAndLocationTexture() {
        if (textureId_title != -1) {
            GLES20.glDeleteTextures(1, new int[]{textureId_title}, 0);
            textureId_title = -1;
        }
    }
    // -----------------------------------  set  title and location  end   ----------------------------------------


    @Override
    public void destroy() {
        super.destroy();
        // logo
        if (textureId_Logo != -1) {
            GLES20.glDeleteTextures(1, new int[]{textureId_Logo}, 0);
            textureId_Logo = -1;
        }
        deleteDirectorTexture();
        deleteOverViewTexture();
        deleteTitleAndLocationTexture();
    }
}
