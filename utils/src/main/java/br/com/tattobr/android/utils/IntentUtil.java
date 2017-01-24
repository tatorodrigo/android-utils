package br.com.tattobr.android.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

public class IntentUtil {
    public static Intent getActionViewIntent(Context context, String providerPackage, File file, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        setDataAndType(context, providerPackage, intent, file, mimeType);
        return intent;
    }

    public static void setDataAndType(Context context, String providerPackage, Intent intent, File file, String mimeType) {
        if (context == null) {
            throw new IllegalArgumentException("Context can not be null");
        }
        if (providerPackage == null) {
            throw new IllegalArgumentException("Provider package can not be null");
        }
        if (intent == null) {
            throw new IllegalArgumentException("Intent can not be null");
        }
        if (file == null) {
            throw new IllegalArgumentException("File can not be null");
        }
        if (mimeType == null) {
            throw new IllegalArgumentException("MimeType can not be null");
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.setDataAndType(
                    Uri.fromFile(file),
                    mimeType
            );
        } else {
            intent.setDataAndType(
                    FileProvider.getUriForFile(context, providerPackage, file),
                    mimeType
            );
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }
}
