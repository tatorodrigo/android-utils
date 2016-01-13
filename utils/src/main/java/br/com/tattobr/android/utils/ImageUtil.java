package br.com.tattobr.android.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.IOException;

public class ImageUtil {
    public static String getPath(Context context, Uri uri) {
        String path = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        path = cursor.getString(column_index);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }

        return path;
    }

    public static Bitmap getResizedImage(Uri uriFile, int width, int height) {
        return getResizedImage(uriFile, width, height, false);
    }

    public static Bitmap getResizedImage(Uri uriFile, int width, int height, boolean fixMatrix) {
        try {
            // Configura o BitmapFactory para apenas ler o tamanho da imagem (sem carregá-la em memória)
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            // Faz o decode da imagem
            BitmapFactory.decodeFile(uriFile.getPath(), opts);
            // Lê a largura e altura do arquivo
            int w = opts.outWidth;
            int h = opts.outHeight;

            if (width == 0 || height == 0) {
                width = w / 2;
                height = h / 2;
            }

            // Fator de escala
            int scaleFactor = Math.min(w / width, h / height);
            opts.inSampleSize = scaleFactor;
            // Agora deixa carregar o bitmap completo
            opts.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(uriFile.getPath(), opts);

            if (fixMatrix) {
                Bitmap newBitmap = fixMatrix(uriFile, bitmap);

                /*
                if(!bitmap.isRecycled()){
                    bitmap.recycle();
                }
                - Não posso reciclar esse Bitmap. Pois, o newBitmap possui a mesma referência que o bitmap
                */
                return newBitmap;
            } else {
                return bitmap;
            }

        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap fixMatrix(Uri uriFile, Bitmap bitmap) throws IOException {
        Matrix matrix = new Matrix();

        /**
         * Classe para ler tags escritas no JPEG
         * Para utilizar esta classe precisa de Android 2.2 ou superior
         */
        ExifInterface exif = new ExifInterface(uriFile.getPath());

        // Lê a orientação que foi salva a foto
        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        boolean fix = false;

        // Rotate bitmap
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                fix = true;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                fix = true;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                fix = true;
                break;
            default:
                // ORIENTATION_ROTATE_0
                fix = false;
                break;
        }

        if (!fix) {
            return bitmap;
        }

        // Corrige a orientação (passa a matrix)
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return newBitmap;
    }
}