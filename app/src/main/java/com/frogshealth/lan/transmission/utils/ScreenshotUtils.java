package com.frogshealth.lan.transmission.utils;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

/**
 * 缩略图工具类
 *
 * Created by mayubao on 2016/11/14.
 * Contact me 345269374@qq.com
 */
public class ScreenshotUtils {

    /**
     * 创建缩略图
     *
     * @param filePath 文件地址
     * @return Bitmap
     */
    public static Bitmap createVideoThumbnail(String filePath){
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MICRO_KIND);
        return bitmap;
    }


    /**
     * 将图片转换成指定宽高
     *
     * @param source Bitmap
     * @param width 宽
     * @param height 高
     * @return Bitmap
     */
    public static Bitmap extractThumbnail(Bitmap source, int width, int height){
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(source, width, height);
        return bitmap;
    }


}
