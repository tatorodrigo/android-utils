package br.com.tattobr.android.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.List;

public class MediaScannerUtil {
    public static void scanNewItemFile(Context context, File item) {
        String[] paths = {item.getAbsolutePath()};
        String[] mimeTypes = null;
        scanNewItems(context, paths, mimeTypes);
    }

    public static void scanNewItemsFile(Context context, List<File> items) {
        int size = items.size();
        String[] paths = new String[size];
        File item;
        for (int i = 0; i < size; i++) {
            item = items.get(i);
            paths[i] = item.getAbsolutePath();
        }

        scanNewItems(context, paths, null);
    }

    public static void scanDeletedItemsFile(Context context, List<File> items) {
        int size = items.size();
        if (size > 0) {
            ContentResolver contentResolver = context.getContentResolver();
            File item;
            String path;
            String mimeType;
            Uri uri;
            for (int i = 0; i < size; i++) {
                item = items.get(i);
                path = item.getAbsolutePath();
                mimeType = MimeTypeUtil.getMimeType(path);
                if (mimeType != null) {
                    if (mimeType.toLowerCase().startsWith("image")) {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if (mimeType.toLowerCase().startsWith("audio")) {
                        uri = MediaStore.Audio.Media.getContentUriForPath(path);
                    } else if (mimeType.toLowerCase().startsWith("video")) {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else {
                        uri = null;
                    }
                    if (uri != null) {
                        try {
                            contentResolver.delete(uri, MediaStore.MediaColumns.DATA + "=?", new String[]{path});
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static void scanNewItems(Context context, String[] paths, String[] mimeTypes) {
        MediaScannerConnection.scanFile(context, paths, mimeTypes, null);
    }
}
