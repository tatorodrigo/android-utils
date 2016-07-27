package br.com.tattobr.android.utils;

import android.webkit.MimeTypeMap;

import java.io.File;

public class MimeTypeUtil {
    public static String getMimeType(String url) {
        String type = null;
        String extension = null;
        int index = url.lastIndexOf(".");
        if (index > 0) {
            extension = url.substring(index + 1);
        }
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type != null ? type.toLowerCase() : null;
    }

    public static String getMimeType(File file) {
        return getMimeType(file.getAbsolutePath());
    }

    public static boolean isImage(String url) {
        String mimeType = getMimeType(url);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isImage(File file) {
        return isImage(file.getAbsolutePath());
    }

    public static boolean isVideo(String url) {
        String mimeType = getMimeType(url);
        return mimeType != null && mimeType.startsWith("video");
    }

    public static boolean isVideo(File file) {
        return isVideo(file.getAbsolutePath());
    }

    public static boolean isAudio(String url) {
        String mimeType = getMimeType(url);
        return mimeType != null && mimeType.startsWith("audio");
    }

    public static boolean isAudio(File file) {
        return isAudio(file.getAbsolutePath());
    }
}
