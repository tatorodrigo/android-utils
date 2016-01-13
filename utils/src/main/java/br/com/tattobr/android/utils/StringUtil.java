package br.com.tattobr.android.utils;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class StringUtil {
    public static String base64Encode(String data) {
        return base64Encode(data.getBytes(Charset.forName("UTF-8")));
    }

    public static String base64Encode(byte[] data) {
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static String base64Decode(String data) {
        try {
            return new String(base64DecodeByte(data), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] base64DecodeByte(String data) {
        return Base64.decode(data, Base64.DEFAULT);
    }

    public static String compress(String data) {
        byte[] compressed = StringCompressor.compress(data);
        return base64Encode(compressed);
    }

    public static String decompress(String data) {
        byte[] base64decoded = null;
        try {
            base64decoded = base64DecodeByte(data);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        String decompressed = null;
        if (base64decoded != null && base64decoded.length > 0) {
            decompressed = StringCompressor.decompress(base64decoded);
        }
        return decompressed;
    }

    enum StringCompressor {
        ;

        public static byte[] compress(String text) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                OutputStream out = new DeflaterOutputStream(baos);
                out.write(text.getBytes("UTF-8"));
                out.close();
            } catch (IOException e) {
                throw new AssertionError(e);
            }
            return baos.toByteArray();
        }

        public static String decompress(byte[] bytes) {
            InputStream in = new InflaterInputStream(new ByteArrayInputStream(bytes));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) > 0)
                    baos.write(buffer, 0, len);
                return new String(baos.toByteArray(), "UTF-8");
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }
    }
}
