package com.base.library.photopicker.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.base.library.util.FileAccessor;
import com.base.library.util.WDYLog;

import java.io.File;

/**
 * 作者：王东一
 * 创建时间：2017/4/26.
 */

public class ExportImgUtil {
    private static final String TAG = "ExportImgUtil";

    public static void refreshingMediaScanner(Context context, String pathName) {
        if (TextUtils.isEmpty(pathName)) {
            return;
        }
        File dir = new File(FileAccessor.EXPORT_DIR, pathName);
        // exportToGallery(context,dir.getAbsolutePath());
        Uri uri = Uri.fromFile(dir);
        Intent action = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        context.sendBroadcast(action);
        WDYLog.d(TAG, String.format("refreshing media scanner on path=%s", new Object[]{pathName}));
    }

    private static Uri exportToGallery(Context context, String filename) {
        // Save the name and description of a video in a ContentValues map.
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Video.Media.DATA, filename);
        // Add a new record (identified by uri)
        final Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return uri;
    }
}
