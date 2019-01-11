/*
 * Created by Wuwang on 2017/10/11
 * Copyright © 2017年 深圳哎吖科技. All rights reserved.
 */
package com.wuwang.aavt.examples;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;

import com.wuwang.aavt.av.Mp4Processor;
import com.wuwang.aavt.core.Renderer;
import com.wuwang.aavt.gl.BaseFilter;
import com.wuwang.aavt.gl.LazyFilter;
import com.wuwang.aavt.gl.WaterMarkFilter;
import com.wuwang.aavt.utils.DrawUtils;

import java.io.IOException;


public class VideoUtils {

    public static void transcodeVideoFile(String srcVideoFile, String dstVideoFile, int dstWidth, int dstHeight, final Context context, final Mp4Processor.OnProgressListener progressListener) throws IOException {
        final Mp4Processor processor = new Mp4Processor();
        processor.setOutputPath(dstVideoFile);
        processor.setInputPath(srcVideoFile);
        processor.setOutputSize(dstWidth, dstHeight);
        processor.setOnCompleteListener(new Mp4Processor.OnProgressListener() {
            @Override
            public void onProgress(long max, long current) {
                progressListener.onProgress(max, current);
            }

            @Override
            public void onComplete(String path) {
                Log.e("", "end:::::" + path);
                progressListener.onComplete(path);
            }
        });
        processor.setRenderer(new Renderer() {
            BaseFilter mFilter;

            @Override
            public void create() {
                mFilter = new WaterMarkFilter();
                DrawUtils.context = context;
                ((WaterMarkFilter) mFilter)
                        .setBitmapLogo(BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo))
                        .setPositionLogo(20, 20)
                        .setGravityLogo(Gravity.RIGHT)
                        .setBitmapDirector(R.mipmap.icon_movie_water_mark)
                        .setTextOverView("你地哦解散第 的所得税的实多岁的实打实的实打实的所得税实打实的收打实的实打实的实打实的所得税")
                        .setTextTitleAndLocation("这里是影片名", "深圳南山区",R.mipmap.icon_place_stroke);
                mFilter.create();
            }

            @Override
            public void sizeChanged(int width, int height) {
                mFilter.sizeChanged(width, height);
            }

            @Override
            public void draw(int texture) {
                mFilter.draw(texture, processor.getPresentationTime());
            }

            @Override
            public void destroy() {
                mFilter.destroy();
            }
        });
        processor.start();
    }

    interface OnProgress {
        void process(long time);
    }

}
