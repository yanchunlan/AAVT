package com.wuwang.aavt.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
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
    private static final String TAG = "DrawUtils";
    public static Context context;

    public static List<Bitmap> createTextImage(int BitmapNumber, int drawable, int maxWidth, int maxHeight, float realTextHeight, float padding_5,
                                               CallBack callBack) {

        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> desc = new ArrayList<>();
//
        title.add("徐实打实大苏打实打实实打实大苏打是寅尹");
        title.add("徐实打实大苏打实打实实打实大苏打是寅尹");
//        title.add("徐实打实大苏打实打实实打实大苏打是寅尹");
//        title.add("徐实打实大苏打实打实实打实大苏打是寅尹");

        desc.add("徐实打实大苏打实打实实打实大苏打是寅尹");
        desc.add("徐实打实大苏打实打实实打实大苏打是寅尹");
//        desc.add("徐实打实大苏打实打实实打实大苏打是寅尹");
//        desc.add("徐实打实大苏打实打实实打实大苏打是寅尹");
//
//        desc.add("徐实打实大苏打实打实实打实大苏打是寅尹");
//        desc.add("徐实打实大苏打实打实实打实大苏打是寅尹");
//        desc.add("徐实打实大苏打实打实实打实大苏打是寅尹");
//        desc.add("徐实打实大苏打实打实实打实大苏打是寅尹");

        return createTextImage(context.getResources(), drawable, BitmapNumber, title, desc, maxWidth, maxHeight, realTextHeight, padding_5, callBack);
    }

    public static List<Bitmap> createTextImage(Resources res, @DrawableRes int drawable, int bitmapNumber,
                                               List<String> title, List<String> desc,
                                               int maxWidth, int maxHeight, // 图片宽高
                                               float realTextHeight, float padding_5,
                                               CallBack callBack) {
        if ((title == null || title.isEmpty()) && (desc == null || desc.isEmpty())) {
            return null;
        }
        Paint mPaintTitle = new Paint();
        Paint mPaintDesc = new Paint();
        Paint mPaintLogo = new Paint();
        Paint mPaintBg = new Paint();

        mPaintTitle.setColor(Color.WHITE);
        mPaintTitle.setTextSize(17);
        mPaintTitle.setStyle(Paint.Style.FILL);
        mPaintTitle.setAntiAlias(true);
        mPaintTitle.setTypeface(Typeface.DEFAULT_BOLD);

        mPaintDesc.setColor(Color.WHITE);
        mPaintDesc.setTextSize(13);
        mPaintDesc.setStyle(Paint.Style.FILL);
        mPaintDesc.setAntiAlias(true);

        mPaintLogo.setAntiAlias(true);

        mPaintBg.setStyle(Paint.Style.FILL);
        mPaintBg.setColor(Color.argb(51, 0, 0, 0));// 20%透明度
        mPaintBg.setAntiAlias(true);


        float topTitle = mPaintTitle.getFontMetrics().top;
        float bottomTitle = mPaintTitle.getFontMetrics().bottom;
        float topDesc = mPaintDesc.getFontMetrics().top;
        float bottomDesc = mPaintDesc.getFontMetrics().bottom;


        // 缩放比例
        float scale = realTextHeight / (bottomTitle - topTitle);

        //  ------- img start ----------
        Bitmap bitmap = BitmapFactory.decodeResource(res, drawable);
        int bw = bitmap.getWidth();
        int bh = bitmap.getHeight();

        float imgScale = scale * ((bottomTitle - topTitle) * 63 / 24) / bh;

        Matrix matrix = new Matrix();
        matrix.postScale(imgScale, imgScale);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bw, bh, matrix, true);

        // 图片的真实宽高
        bw = newBitmap.getWidth();
        bh = newBitmap.getHeight();
        //  ------- img end ----------

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


        float maxW = maxWidth * 1.0f / scale;
//        float maxH = maxHeight / scale;
        float[] tempArr = new float[1];


        // final w,h
        float sh = 0;
        float sw = bw / scale;

        sh += bh / scale;
        sh += padding_5 * 4.2 / scale;


        // title
        if (title != null && !title.isEmpty()) {
            tw = mPaintTitle.measureText("导演", 0, "导演".length());
            th = -topTitle + sh;

            sh += bottomTitle - topTitle;
            sh += padding_5 / scale;

            // 导演
            StringBuilder b = new StringBuilder();
            int len = title.size();
            // 暂时限定title2个，一个最大满屏，2个最大就控制显示一半，
            float maxLength = maxW /len  + "  ".length();
            for (int i = 0; i < len; i++) {
                String tempStr = title.get(i);
                float tempW = mPaintDesc.measureText(tempStr, 0, tempStr.length());
                Log.d(TAG, "createTextImage: maxLength "+maxLength+" maxW "+maxW);
                if (tempW > maxLength) {
                    int index = mPaintDesc.breakText(tempStr, false, maxLength, tempArr);
                    tempStr = tempStr.substring(0, index) + "...";
                }
                b.append(tempStr);
                if (i != len - 1) {
                    b.append("   ");
                }
            }
            tText = b.toString();
            ttw = mPaintDesc.measureText(tText, 0, tText.length());
            tth = -topDesc + sh;

            sh += bottomDesc - topDesc;
            sh += padding_5 * 2.2 / scale;

            // calcute sw
            if (tw > sw) {
                sw = tw;
            }
            if (ttw > sw) {
                sw = ttw;
            }
        } else { // 没有导演信息 ,控制演员显示居中
            sh += bottomTitle - topTitle;    // 在高度上加一个标题的高度
        }

        // desc
        if (desc != null && !desc.isEmpty()) {
            dw = mPaintTitle.measureText("演员", 0, "演员".length());
            dh = -topTitle + sh;

            sh += bottomTitle - topTitle;
            sh += padding_5 / scale;

            // 导演
            StringBuilder b = new StringBuilder();
            StringBuilder b2 = new StringBuilder();
            int len = desc.size();
            for (int i = 0; i < len; i++) {
                if (i >= 4) { // 4567
                    String tempStr = desc.get(i);
                    float tempW = mPaintDesc.measureText(tempStr, 0, tempStr.length());
                    float maxLength = maxW / (len - 4) + " ".length();
                    if (tempW > maxLength) {
                        int index = mPaintDesc.breakText(tempStr, false, maxLength, tempArr);
                        tempStr = tempStr.substring(0, index) + "...";
                    }
                    b2.append(tempStr);
                    if (i != len - 1) {
                        b2.append("  ");
                    }
                } else { // 0123
                    String tempStr = desc.get(i);
                    float tempW = mPaintDesc.measureText(tempStr, 0, tempStr.length());
                    int maxLen = len > 4 ? 4 : len;
                    float maxLength = maxW / maxLen + " ".length();
                    if (tempW > maxLength) {
                        int index = mPaintDesc.breakText(tempStr, false, maxLength, tempArr);
                        tempStr = tempStr.substring(0, index) + "...";
                    }
                    b.append(tempStr);
                    if (i != 3) {
                        b.append("  ");
                    }
                }
            }

            // 第一行
            dText = b.toString();
            ddw = mPaintDesc.measureText(dText, 0, dText.length());
            ddh = -topDesc + sh;


            // calcute maxw
            if (dw > sw) {
                sw = dw;
            }
            if (ddw > sw) {
                sw = ddw;
            }

            // d2 第二行
            if (b2.length() > 0) {
                sh += bottomDesc - topDesc;
                sh += padding_5 / scale;

                dText2 = b2.toString();
                ddw2 = mPaintDesc.measureText(dText2, 0, dText2.length());
                ddh2 = -topDesc + sh;

                if (ddw2 > sw) {
                    sw = ddw2;
                }
            }
            sh += bottomDesc - topDesc;
            sh += padding_5 * 3 / scale;
        } else {// 没有演员信息 ,控制导演显示居中   在高度上加一个标题的高度
            th += bottomTitle - topTitle;
            tth += bottomTitle - topTitle;
        }

        // 底部边距
//        sh += padding_5  / scale;
        sw += padding_5 * 4 / scale;

        // 防止越界  在宽高逗逼预设的都大的时候，按照最大的处理
        float finalH = sh * scale;
        float finalW = sw * scale;

        if (finalW > maxWidth) {
            maxWidth = (int) Math.ceil(finalW);
            maxW = finalW / scale;
        }
        Log.d("123", "createTextImage: maxW " + maxW + " sw " + sw);
        // 异常情况，异常处理，偶尔会出现maxW=0情况
        if (maxW < sw) {
            maxW = sw;
        }

        if (finalH > maxHeight) {
            maxHeight = (int) Math.ceil(finalH);
        }
        if (callBack != null) {
            callBack.callBackWH(maxWidth, maxHeight);
        }

        List<Bitmap> data = new ArrayList<Bitmap>();
        for (int i = bitmapNumber; i > 0; i--) {
            // alpha
            int alpha = 255 / i;
            mPaintLogo.setAlpha(alpha);
            mPaintTitle.setAlpha(alpha);
            mPaintDesc.setAlpha(alpha);
            mPaintBg.setAlpha(alpha / 5); // 透明度初始值是1/5 所以需要/5

//            mPaintLogo.setARGB(alpha, 255, 255, 255);
//            mPaintTitle.setARGB(alpha, 255, 255, 255);
//            mPaintDesc.setARGB(alpha, 255, 255, 255);
//            mPaintBg.setARGB(alpha/5, 0, 0, 0); // 透明度初始值是1/5 所以需要/5


            // draw
            Bitmap b = Bitmap.createBitmap((int) maxWidth, (int) maxHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(b);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(0, bh / 2, maxWidth, maxHeight, padding_5 * 2, padding_5 * 2, mPaintBg);
            } else {
                canvas.drawRect(0, bh / 2, maxWidth, maxHeight, mPaintBg);
            }
            // 后面绘制的内容都需要高度减去平移的高度
            canvas.drawBitmap(newBitmap, (maxWidth - bw) / 2, 0, mPaintLogo);

            canvas.scale(scale, scale);
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
        if (newBitmap != null && !newBitmap.isRecycled()) {
            newBitmap.recycle();
            newBitmap = null;
        }
        return data;
    }


    public interface CallBack {
        void callBackWH(int width, int height);
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

    public static Bitmap textToBitmap(String text, int screenWidth, int realHeight, float padding_5) {
        if (text == null || TextUtils.isEmpty(text)) {
            return null;
        }
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(dp2px(14));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        float top = paint.getFontMetrics().top;
        float bottom = paint.getFontMetrics().bottom;
        float h = bottom - top;

        // 缩放比例
        float scale = realHeight / h;

        float realWidth = paint.measureText(text, 0, text.length()) * scale;
        float paddingLeft = padding_5 * 3; // 左右边距 15

        realWidth = (realWidth + paddingLeft * 2);
        if (realWidth <= screenWidth) {
            realWidth = screenWidth;
        } else {
            // 大于一个屏幕多加一个屏幕
            realWidth += screenWidth; // 多添加一个内容，为了防止滚动没了，就不显示黑边
        }

        Bitmap b = Bitmap.createBitmap((int) Math.ceil(realWidth), realHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        canvas.drawColor(Color.argb(51, 0, 0, 0)); // 20%透明度
        canvas.scale(scale, scale);

        Log.d("123", "textToBitmap: " + paddingLeft / scale);
        Log.d("123", "textToBitmap: " + paddingLeft);
        canvas.drawText(text, paddingLeft / scale, -top, paint); // 此处放置是未缩放的left ,top
        return b;
    }

    public static Bitmap textToBitmap(String title, String location, @DrawableRes int drawable,
                                      int screenWidth, int realHeight, float padding_5) {
        return textToBitmap(context.getResources(), title, location, drawable,
                screenWidth, realHeight, padding_5);
    }

    public static Bitmap textToBitmap(Resources res, String title, String location, @DrawableRes int drawable,
                                      int screenWidth, int realHeight, float padding_5) {
        if ((title == null || TextUtils.isEmpty(title)) && (location == null || TextUtils.isEmpty(location))) {
            return null;
        }
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(17);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        // 未缩放的宽高
        float maxW = 0;
        float maxH = 0;
        float paddingLeft = padding_5 * 3; // 左右边距 15


        float top = paint.getFontMetrics().top;
        float bottom = paint.getFontMetrics().bottom;
        float h = (bottom - top); // 控件高度

        maxH += h;

        // 缩放比例
        float scale = realHeight / h;

        // 未缩放的最大宽度
        float halfWidth = screenWidth / scale / 2;

        // title
        float titleHeight = 0f; // 标题文字绘制起始点高度
        // location img
        Bitmap cb = null;
        float cbLeft = 0f;
        float cbTop = 0f;
        // location text
        float locationW = 0f;
        float locationH = 0f;


        if (title != null && !TextUtils.isEmpty(title)) {
            float w = paint.measureText(title, 0, title.length());
            // 文字不能太长，超过一半屏幕截取
            if (w > halfWidth) {
                w = halfWidth;
                float[] arr = new float[1];
                int index = paint.breakText(title, false, halfWidth, arr);
                title = title.substring(0, index);
                title += "...";
            }

            // 计算文字绘制起点
            titleHeight = -top;
            maxW += w;
            maxW += padding_5 * 4 / scale; // 间距 20
        }
        if (location != null && !TextUtils.isEmpty(location)) {
            // text
            paint.setTextSize(14);

            // -----  img start ----
            // 计算图片的真实缩放
            Bitmap b = BitmapFactory.decodeResource(res, drawable);
            int bw = b.getWidth();
            int bh = b.getHeight();
            // 缩放比例  尽可能小取h值，本来是bottom-top 改为descent - ascent
            float imgScale = (paint.getFontMetrics().descent - paint.getFontMetrics().ascent - paint.getFontMetrics().bottom) * scale / bh;

            Matrix matrix = new Matrix();
            matrix.postScale(imgScale, imgScale);
            cb = Bitmap.createBitmap(b, 0, 0, bw, bh, matrix, true);

            // 此处为何是加了2次一半，是为了调图片为证
            float halfCbW = cb.getWidth() / scale / 3;
            maxW += halfCbW * 2;

            // 图片的真实left , top
            cbLeft = maxW * scale;
            cbTop = realHeight - cb.getHeight() - bottom * scale;

            maxW += halfCbW * 1;
            maxW += padding_5 * 2 / scale;
            // -----  img end ----


            // draw location
            float w = paint.measureText(location, 0, location.length());
            // 文字不能太长，超过一半屏幕截取
            if (w > halfWidth) {
                w = halfWidth;
                float[] arr = new float[1];
                int index = paint.breakText(title, false, halfWidth, arr);
                location = location.substring(0, index);
                location += "...";
            }

            locationW = maxW;
            locationH = maxH - bottom;

            maxW += w;

            if (!b.isRecycled()) {
                b.recycle();
                b = null;
            }
        }
        Bitmap b = Bitmap.createBitmap((int) Math.ceil(maxW * scale), realHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
//        canvas.save();
        if (cb != null) {
            Paint pLocation = new Paint();
            pLocation.setAntiAlias(true);
            canvas.drawBitmap(cb, cbLeft, cbTop, pLocation);
            if (!cb.isRecycled()) {
                cb.recycle();
                cb = null;
            }
        }
        canvas.scale(scale, scale);
        if (0 != titleHeight) {
            paint.setTextSize(17);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            canvas.drawText(title, paddingLeft / scale, titleHeight, paint);
        }
        if (locationW != 0f && locationH != 0f) {
            paint.setTextSize(14);
            paint.setTypeface(Typeface.DEFAULT);
            canvas.drawText(location, locationW, locationH, paint);
        }
//        canvas.restore();
        Log.d("123", "textToBitmap: " + paddingLeft / scale);
        Log.d("123", "textToBitmap: " + paddingLeft);

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
        return (int) dp;
//        float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
//        return (int) (dp * scale + 0.5f);
    }
}
