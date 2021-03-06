package com.base.library.photopicker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.base.library.application.BaseApplication;
import com.base.library.photopicker.PhotoPickerActivity;
import com.base.library.util.CCPAppManager;
import com.base.library.util.DemoUtils;
import com.base.library.util.ECPreferenceSettings;
import com.base.library.util.ECPreferences;
import com.base.library.util.FileAccessor;
import com.base.library.util.FileUtils;
import com.base.library.util.ToastUtil;
import com.base.library.view.upPhotoView.DoPicCapUtil;

import java.io.File;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;

/**
 * 作者：王东一
 * 创建时间：2017/4/25.
 * 图片工具类
 */

public class PhotoUtils {
    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FORE_WARD_SLASH = "/";
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x3;
    public static final int REQUEST_CODE_TAKE_LOCATION = 0x11;
    public static final int REQUEST_CODE_LOAD_IMAGE = 0x4;
    public static final int REQUEST_CODE_IMAGE_CROP = 0x5;

    /**
     * 判断外部存储卡是否可用
     *
     * @return 是否可用
     */
    public static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static int getHeightInPx(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getWidthInPx(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getHeightInDp(Context context) {
        final float height = context.getResources().getDisplayMetrics().heightPixels;
        return px2dip(context, height);
    }

    public static int getWidthInDp(Context context) {
        final float width = context.getResources().getDisplayMetrics().widthPixels;
        return px2dip(context, width);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 资源格式化字符串
     *
     * @param context  上下文
     * @param resource 资源
     * @param args     参数
     * @return 字符串
     */
    public static String formatResourceString(Context context, int resource, Object... args) {
        String str = context.getResources().getString(resource);
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return String.format(str, args);
    }

    /**
     * 获取拍照相片存储文件
     *
     * @param context 上下文
     * @return 文件对象
     */
    public static File createFile(Context context) {
        File file;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = Environment.getExternalStorageDirectory();
            String rootDir = "/" + "cache";
            file = new File(external, rootDir + "/picturePhotos");
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            File cacheDir = context.getCacheDir();
            String rootDir = "/" + "cache";
            file = new File(cacheDir, rootDir + "/picturePhotos");
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        return new File(file, "temp.png");
    }

    public static boolean saveImage(String url) {
        return saveImage(url, ".jpg");
    }

    //资源 id 转换成 Uri
    private static Uri resourceIdToUri(Context context, int resourceId) {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FORE_WARD_SLASH + resourceId);
    }

    public static boolean saveImage(String url, String ext) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        String filePath = url;
        File dir = new File(FileAccessor.APPS_ROOT_DIR, "save");
        if (!dir.exists())
            dir.mkdirs();
        long timeMillis = System.currentTimeMillis();
        int result = FileUtils.copyFile(dir.getAbsolutePath(), "ecexport" + timeMillis, ext, FileUtils.readFlieToByte(filePath, 0, FileUtils.decodeFileLength(filePath)));
        if (result == 0) {
            ExportImgUtil.refreshingMediaScanner(BaseApplication.getInstance(), "ecexport" + timeMillis + ext);
            BaseApplication.getToastUtil().showMiddleToast("图片已保存至" + dir.getAbsolutePath());
            return false;
        }
        BaseApplication.getToastUtil().showMiddleToast("图片保存失败");
        return true;
    }

    //唤醒图片选择
    public static void showPhotoIntent(Context mContext, boolean isShowCamera, int MODE, int maxNum) {
        Intent intent = new Intent(mContext, PhotoPickerActivity.class);
        intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, isShowCamera);//是否显示相机
        intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, MODE);//单选多选
        intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, maxNum);//最多张数
        ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_LOAD_IMAGE);
    }

    //图片选取返回
    public static void onPhotoResult(int requestCode, Intent data, onPhotoBack onPhotoBack) {
        if (requestCode == REQUEST_CODE_TAKE_PICTURE || requestCode == REQUEST_CODE_LOAD_IMAGE) {
            if (requestCode == REQUEST_CODE_LOAD_IMAGE) {
                if (data == null) {
                    onPhotoBack.onBack(new ArrayList<String>());
                    return;
                }
                ArrayList<String> result = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);//返回的相片路径
                onPhotoBack.onBack(result);
            }
        }
    }

    public interface onPhotoBack {
        void onBack(ArrayList<String> result);
    }
}
