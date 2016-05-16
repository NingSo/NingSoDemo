package com.ningso.ningsodemo.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore.Images.Media;
import android.support.design.BuildConfig;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by NingSo on 16/4/17.下午6:38
 *
 * @author: NingSo
 * @Email: ningso.ping@gmail.com
 */
public class ImageUtils {
    private static final String[] ACCEPTABLE_IMAGE_TYPES;
    private static final String[] IMAGE_PROJECTION = new String[]{"_id", "datetaken", "date_added", "orientation", "_data"};
    private static final int INDEX_ORIENTATION = 3;
    public static final int JPEG_QUALITY = 80;
    public static final long MIN_SD_CARD_SPACE = 5242880;
    private static final String TAG = "ImageUtil";
    public static final int TYPE_JPG = 0;
    public static final int TYPE_PNG = 1;
    private static final String WHERE_CLAUSE = "(mime_type in (?, ?, ?))";

    static {
        String[] strArr = new String[INDEX_ORIENTATION];
        strArr[TYPE_JPG] = "image/jpeg";
        strArr[TYPE_PNG] = "image/png";
        strArr[2] = "image/gif";
        ACCEPTABLE_IMAGE_TYPES = strArr;
    }

    public static Bitmap loadDisplaySample(Context cx, Uri uri, int maxLength) throws FileNotFoundException {
        InputStream is = cx.getContentResolver().openInputStream(uri);
        Options opts = new Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, opts);
        int length = Math.max(opts.outWidth, opts.outHeight);
        int n = TYPE_PNG;
        while (length > maxLength) {
            maxLength /= 2;
            n += TYPE_PNG;
        }
        return loadDisplay(cx, cx.getContentResolver().openInputStream(uri), n, getImageOrientation(cx, uri));
    }

    public static Bitmap loadDisplaySample(Context cx, Uri uri) throws FileNotFoundException {
        InputStream is = cx.getContentResolver().openInputStream(uri);
        return loadDisplay(cx, cx.getContentResolver().openInputStream(uri), TYPE_PNG, getImageOrientation(cx, uri));
    }

    public static Bitmap loadDisplayAsset(Context cx, String assetPath) throws IOException {
        return loadDisplay(cx, cx.getAssets().open(assetPath), TYPE_PNG);
    }

    public static Bitmap loadDisplay(Context cx, InputStream is, int sample) {
        Options opts = new Options();
        opts.inJustDecodeBounds = false;
        opts.inDither = true;
        opts.inSampleSize = sample;
        return BitmapFactory.decodeStream(is, null, opts);
    }

    private static Bitmap loadDisplay(Context cx, InputStream is, int sample, int orientation) {
        Bitmap bm = loadDisplay(cx, is, sample);
        if (orientation == 0) {
            return bm;
        }
        Matrix mtx = new Matrix();
        mtx.setRotate((float) orientation);
        return Bitmap.createBitmap(bm, TYPE_JPG, TYPE_JPG, bm.getWidth(), bm.getHeight(), mtx, true);
    }

    public static int getImageOrientation(Context cx, Uri imageUri) {
        int orientation = getOrientationFromMedia(cx, imageUri);
        if (orientation == 0) {
            return getOrientationFromExif(imageUri);
        }
        return orientation;
    }

    private static int getOrientationFromMedia(Context context, Uri imageUri) {
        ContentResolver mContentResolver = context.getContentResolver();
        int orientation = TYPE_JPG;
        Cursor c = createCursor(mContentResolver, imageUri);
        if (c != null) {
            if (c.moveToFirst()) {
                orientation = c.getInt(INDEX_ORIENTATION);
            }
            c.close();
        }
        return orientation;
    }

    private static int getOrientationFromExif(Uri uri) {
        int orientation = TYPE_JPG;
        try {
            if (uri.getScheme().equals("file")) {
                orientation = exifOrientationToDegrees(new ExifInterface(uri.getPath()).getAttributeInt("Orientation", TYPE_PNG));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orientation;
    }

    private static Cursor createCursor(ContentResolver mContentResolver, Uri mBaseUri) {
        try {
            Cursor c;
            if (mBaseUri.getScheme().startsWith("file")) {
                String[] args = new String[TYPE_PNG];
                args[TYPE_JPG] = BuildConfig.FLAVOR;
                args[TYPE_JPG] = mBaseUri.getPath();
                c = Media.query(mContentResolver, Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, "(_data=?)", args, sortOrder());
            } else {
                c = Media.query(mContentResolver, mBaseUri, IMAGE_PROJECTION, WHERE_CLAUSE, ACCEPTABLE_IMAGE_TYPES, sortOrder());
            }
            return c;
        } catch (Exception e) {
            return null;
        }
    }

    private static String sortOrder() {
        String ascending = " DESC";
        return "case ifnull(datetaken,0) when 0 then date_modified*1000 else datetaken end" + ascending + ", _id" + ascending;
    }

    private static int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == 6) {
            return 90;
        }
        if (exifOrientation == INDEX_ORIENTATION) {
            return 180;
        }
        if (exifOrientation == 8) {
            return 270;
        }
        return TYPE_JPG;
    }

    public static int degreesToExifOrientation(int normalizedAngle) {
        if (normalizedAngle == 0) {
            return TYPE_PNG;
        }
        if (normalizedAngle == 90) {
            return 6;
        }
        if (normalizedAngle == 180) {
            return INDEX_ORIENTATION;
        }
        if (normalizedAngle == 270) {
            return 8;
        }
        return TYPE_PNG;
    }

    public static boolean isSdcardFull() {
        StatFs statFs = new StatFs(new File(Environment.getExternalStorageDirectory().getPath()).getPath());
        return ((long) statFs.getBlockSize()) * (((long) statFs.getAvailableBlocks()) - 4) < MIN_SD_CARD_SPACE;
    }

    public static Uri saveImage(Context context, Bitmap bitmap, String path, int type, int quality) throws SDCardException, IOException {
        return saveImage(context, bitmap, path, type, quality, null, true);
    }

    public static Uri saveImage(Context context, Bitmap bitmap, String path, int type, int quality, boolean needScan) throws SDCardException, IOException {
        return saveImage(context, bitmap, path, type, quality, null, needScan);
    }

    public static Uri saveImage(Context context, Bitmap bitmap, String path, int type, int quality, ExifInfo exif, boolean needScan) throws SDCardException, IOException {
        int pos = path.lastIndexOf(47);
        if (pos <= 0) {
            return null;
        }
        String dir = path.substring(TYPE_JPG, pos + TYPE_PNG);
        String name = path.substring(pos + TYPE_PNG, path.length());
        if (type == TYPE_PNG) {
            return saveImage(context, bitmap, dir, name, BuildConfig.FLAVOR, TYPE_PNG, 100, exif, needScan);
        } else if (type == 0) {
            return saveImage(context, bitmap, dir, name, BuildConfig.FLAVOR, TYPE_JPG, quality, exif, needScan);
        } else {
            throw new RuntimeException("type error");
        }
    }

    private static Uri saveImage(Context context, Bitmap bitmap, String dir, String name, String suffix, int type, int quality, ExifInfo exif, boolean needScan) throws SDCardException, IOException {
        if (isSdcardFull()) {
            throw new SDCardException();
        }
        String newPath;
        if (quality <= 0) {
            quality = JPEG_QUALITY;
        }
        String[] nameArr = name.split("\\.");
        if (nameArr.length < 2 || !(nameArr[nameArr.length - 1].equals("jpg") || nameArr[nameArr.length - 1].equals("png") || nameArr[nameArr.length - 1].equals("tmp"))) {
            newPath = dir + name + suffix;
        } else {
            newPath = dir + name;
            if (nameArr[nameArr.length - 1].equals("tmp")) {
                needScan = false;
            }
        }
        new File(dir).mkdirs();
        File f = new File(newPath);
        f.createNewFile();
        FileOutputStream fileOS = new FileOutputStream(f);
        bitmap.compress(type == 0 ? CompressFormat.JPEG : CompressFormat.PNG, quality, fileOS);
        fileOS.close();
        if (needScan) {
            fileScan(context, newPath, type);
        }
        Log.d(TAG, "save ok at:" + newPath);
        if (exif != null) {
            try {
                ExifUtils.saveExifToFile(newPath, exif);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Uri.fromFile(new File(newPath));
    }

    @SuppressLint({"DefaultLocale"})
    @TargetApi(14)
    public static void fileScan(Context context, String path, int type, int orientation) {
        File file = new File(path);
        if (!file.exists() || file.length() == 0) {
            Log.d(TAG, "File Scan failed: file doesn't exist or is an empty file.");
            return;
        }
        Uri uri = null;
        try {
            String filename = path.substring(path.lastIndexOf("/") + TYPE_PNG);
            String filetitle = filename.substring(TYPE_JPG, filename.lastIndexOf("."));
            File parentFile = new File(path).getParentFile();
            if (parentFile == null) {
                parentFile = new File("/");
            }
            String parentPath = parentFile.toString().toLowerCase();
            String parentName = parentFile.getName();
            ContentValues values = new ContentValues(8);
            values.put("title", filetitle);
            values.put("_display_name", filename);
            values.put("description", context.getString(context.getApplicationInfo().labelRes));
            values.put("datetaken", System.currentTimeMillis());
            values.put("date_added", System.currentTimeMillis());
            values.put("date_modified", System.currentTimeMillis());
            values.put("mime_type", type == 0 ? "image/jpeg" : "image/png");
            values.put("orientation", orientation);
            values.put("bucket_id", parentPath.hashCode());
            values.put("bucket_display_name", parentName);
            values.put("_data", path);
            uri = context.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (uri == null || uri.equals(BuildConfig.FLAVOR)) {
            Log.d(TAG, "Insertion into database failed! Now send the broadcast.");
            context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse(("file://" + path).replaceAll("%", "%25").replaceAll("#", "%23").replaceAll(" ", "%20"))));
        }
    }

    public static void fileScan(Context context, String path, int type) {
        fileScan(context, path, type, TYPE_JPG);
    }
}