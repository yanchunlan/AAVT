package com.wuwang.aavt.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * author:  ycl
 * date:  2018/12/24 13:55
 * desc:
 */
public class DrawUtils {

    public static Context context;

    public static List<Bitmap> createTextImage(int BitmapNumber, int drawable) {
        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> desc = new ArrayList<>();

        title.add("张东雨曦");
        title.add("张东雨曦张东雨曦");

        desc.add("徐寅尹");
        desc.add("徐寅尹徐寅尹");
        desc.add("徐寅尹徐寅尹");
        desc.add("徐寅尹徐寅尹");
        desc.add("徐寅尹徐寅尹");
        desc.add("徐寅尹徐寅尹");
        desc.add("徐寅尹徐寅尹");
        desc.add("徐寅尹徐寅尹");

        return createTextImage(context.getResources(), drawable, BitmapNumber, title, desc);
    }

    public static List<Bitmap> createTextImage(Resources res, @DrawableRes int drawable, int bitmapNumber,
                                               List<String> title, List<String> desc) {
        if ((title == null || title.isEmpty()) && (desc == null || desc.isEmpty())) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(res, drawable);

        Paint mPaintTitle = new Paint();
        Paint mPaintDesc = new Paint();
        Paint mPaintLogo = new Paint();
        Paint mPaintBg = new Paint();

//        mPaintTitle.setColor(Color.WHITE);
        mPaintTitle.setTextSize(dp2px(17));
        mPaintTitle.setStyle(Paint.Style.FILL);
        mPaintTitle.setAntiAlias(true);


//        mPaintDesc.setColor(Color.WHITE);
        mPaintDesc.setTextSize(dp2px(13));
        mPaintDesc.setStyle(Paint.Style.FILL);
        mPaintDesc.setAntiAlias(true);

        mPaintLogo.setAntiAlias(true);

        mPaintBg.setStyle(Paint.Style.FILL);
        mPaintBg.setColor(Color.argb(51, 0, 0, 0));// 20%透明度
       /* mPaintBg.setColor(Color.parseColor("#000000"));
        mPaintBg.setAlpha(255 * 20 / 100);*/
        mPaintBg.setAntiAlias(true);


        // final w,h
        int sh = 0;
        float maxW = 0f;

        // logo
        int bw = bitmap.getWidth();
        int bh = bitmap.getHeight();

        // 导演宽高
        float tw = 0f;
        float th = 0f;
        float ttw = 0f;
        float tth = 0f;
        String tText = null;

        // 演员
        float dw = 0f;
        float dh = 0f;
        float ddw = 0f;
        float ddh = 0f;
        String dText = null;
        // d2 第二行
        float ddw2 = 0f;
        float ddh2 = 0f;
        String dText2 = null;

        sh += bh;
        sh += dp2px(20);
        maxW = bh;

        float topTitle = mPaintTitle.getFontMetrics().top;
        float bottomTitle = mPaintTitle.getFontMetrics().bottom;
        float topDesc = mPaintDesc.getFontMetrics().top;
        float bottomDesc = mPaintDesc.getFontMetrics().bottom;

        // title
        if (title != null && !title.isEmpty()) {
            tw = mPaintTitle.measureText("导演", 0, "导演".length());
            th = -topTitle + sh;

            sh += bottomTitle - topTitle;
            sh += dp2px(5);

            // 导演
            StringBuilder b = new StringBuilder();
            int len = title.size();
            for (int i = 0; i < len; i++) {
                b.append(title.get(i));
                if (i != len - 1) {
                    b.append("  ");
                }
            }
            tText = b.toString();
            ttw = mPaintDesc.measureText(tText, 0, tText.length());
            tth = -topDesc + sh;

            sh += bottomDesc - topDesc;
            sh += dp2px(15);

            // calcute maxw
            if (tw > maxW) {
                maxW = tw;
            }
            if (ttw > maxW) {
                maxW = ttw;
            }
        }

        // desc
        if (desc != null && !desc.isEmpty()) {
            dw = mPaintTitle.measureText("演员", 0, "演员".length());
            dh = -topTitle + sh;

            sh += bottomTitle - topTitle;
            sh += dp2px(5);

            // 导演
            StringBuilder b = new StringBuilder();
            StringBuilder b2 = new StringBuilder();
            int len = desc.size();
            for (int i = 0; i < len; i++) {
                if (i >= 4) { // 4567
                    b2.append(desc.get(i));
                    if (i != len - 1) {
                        b2.append("  ");
                    }
                } else { // 0123
                    b.append(desc.get(i));
                    if (i != 3) {
                        b.append("  ");
                    }
                }
            }
            dText = b.toString();
            ddw = mPaintDesc.measureText(dText, 0, dText.length());
            ddh = -topDesc + sh;

            // calcute maxw
            if (dw > maxW) {
                maxW = dw;
            }
            if (ddw > maxW) {
                maxW = ddw;
            }

            // d2 第二行
            if (b2.length() > 0) {
                sh += bottomDesc - topDesc;
                sh += dp2px(5);

                dText2 = b2.toString();
                ddw2 = mPaintDesc.measureText(dText2, 0, dText2.length());
                ddh2 = -topDesc + sh;

                if (ddw2 > maxW) {
                    maxW = ddw2;
                }
            }
            sh += bottomDesc - topDesc;
            sh += dp2px(15);
        }

        // 底部边距
        sh += dp2px(10);
        maxW += dp2px(80);

        List<Bitmap> data = new ArrayList<Bitmap>();
        for (int i = bitmapNumber; i > 0; i--) {
            // alpha
            int alpha = 255 / i;
            mPaintLogo.setAlpha(alpha);
            mPaintTitle.setAlpha(alpha);
            mPaintDesc.setAlpha(alpha);
            mPaintBg.setAlpha(alpha/5); // 透明度初始值是1/5 所以需要/5

//            mPaintLogo.setARGB(alpha, 255, 255, 255);
//            mPaintTitle.setARGB(alpha, 255, 255, 255);
//            mPaintDesc.setARGB(alpha, 255, 255, 255);
//            mPaintBg.setARGB(alpha/5, 0, 0, 0); // 透明度初始值是1/5 所以需要/5



            // draw
            Bitmap b = Bitmap.createBitmap((int) maxW, (int) (sh + bh / 2), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(b);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(0, bh / 2, maxW, sh, dp2px(8), dp2px(8), mPaintBg);
            } else {
                canvas.drawRect(0, bh / 2, maxW, sh, mPaintBg);
            }

            // 后面绘制的内容都需要高度减去平移的高度
            canvas.drawBitmap(bitmap, (maxW - bw) / 2, 0, mPaintLogo);
            if (tText != null) {
                canvas.drawText("导演", (maxW - tw) / 2, th, mPaintTitle);
                canvas.drawText(tText, (maxW - ttw) / 2, tth, mPaintDesc);
            }
            if (dText != null) {
                canvas.drawText("演员", (maxW - dw) / 2, dh, mPaintTitle);
                canvas.drawText(dText, (maxW - ddw) / 2, ddh, mPaintDesc);
                if (dText2 != null) {
                    canvas.drawText(dText2, (maxW - ddw2) / 2, ddh2, mPaintDesc);
                }
            }
            data.add(b);
        }

        // recycle
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return data;
    }

    //如果超过宽度就返回多个bitmap
    public static List<Bitmap> createTextImage(String text, int maxWidth) {
        if (text == null || TextUtils.isEmpty(text)) {
            return null;
        }
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(dp2px(18));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        float top = paint.getFontMetrics().top;
        float bottom = paint.getFontMetrics().bottom;

        int padding = 10; // 内边距
        int height = (int) ((bottom - top) + padding * 2); // 控件高度
        float ty = -top + padding; // 文字绘制位置

        float width = paint.measureText(text, 0, text.length());

        List<Bitmap> data = new ArrayList<>();
        if (width <= maxWidth) { // 返回单个bitmap
            Bitmap b = Bitmap.createBitmap(maxWidth, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(b);
            canvas.drawColor(Color.argb(51, 0, 0, 0)); // 20%透明度
            canvas.drawText(text, padding, ty, paint);
            data.add(b);
        } else {// 返回多个bitmap
            float[] arr = new float[1];

            int lastIndex;
            int firstIndex = 0;
            int totalLength = text.length();
            int currentLength = 0;

            int num = (int) Math.ceil(width / maxWidth);

            for (int i = 0; i < num; i++) {
                // 不断重复剪切文字
                if (i != 0) {
                    text = text.substring(firstIndex);
                    firstIndex = 0;
                }
                lastIndex = paint.breakText(text, false, maxWidth, arr);
                // 检测是否到了最后 ,如果没到最后就自动添加 num ，让其多加一个
                currentLength += lastIndex;
                if (i == num - 1 && currentLength < totalLength) {
                    num += 1;
                }
                // create bitmap
                Bitmap b = Bitmap.createBitmap(maxWidth, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(b);
                canvas.drawColor(Color.argb(51, 0, 0, 0)); // 20%透明度
                canvas.drawText(text.substring(firstIndex, lastIndex), padding, ty, paint);
                firstIndex = lastIndex;
                data.add(b);
            }
        }
        return data;
    }

    public static Bitmap textToBitmap(String text,int maxWidth) {
        if (text == null || TextUtils.isEmpty(text)) {
            return null;
        }
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(dp2px(18));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        float top = paint.getFontMetrics().top;
        float bottom = paint.getFontMetrics().bottom;

        float width = paint.measureText(text, 0, text.length());
        int padding = 10; // 内边距

        int w = (int) (width + padding * 2);
        if (w < maxWidth) {
            w = maxWidth;
        }
        w += maxWidth; // 多添加一个内容，为了防止滚动没了，就不显示黑边

        Bitmap b = Bitmap.createBitmap(w, (int) ((bottom - top) + padding * 2), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        canvas.drawColor(Color.argb(51, 0, 0, 0)); // 20%透明度
        canvas.drawText(text, padding, -top + padding, paint);
        return b;
    }

    public Bitmap getAlplaBitmap(Bitmap sourceImg, int number) {
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg.getWidth(), sourceImg.getHeight());
        number = number * 255 / 100;
        for (int i = 0; i < argb.length; i++) {
            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
        }
        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg.getHeight(), Bitmap.Config.ARGB_8888);
        return sourceImg;
    }

    public static int dp2px(float dp) {
        float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
