package com.ningso.ningsodemo.utils;

import android.annotation.SuppressLint;
import android.media.ExifInterface;
import android.text.TextUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by NingSo on 16/4/17.下午6:41
 *
 * @author: NingSo
 * @Email: ningso.ping@gmail.com
 */
public class ExifUtils {

    public static boolean saveExifToFile(String path, ExifInfo exif) {
        try {
            ExifInterface newExif = new ExifInterface(path);
            if (!(exif == null)) {
                setAttribute(newExif, "ImageLength", exif.imageLength);
                setAttribute(newExif, "ImageWidth", exif.imageWidth);
                setAttribute(newExif, "Make", exif.make);
                setAttribute(newExif, "Orientation", exif.orientation);
                setAttribute(newExif, "Model", exif.model);
                setAttribute(newExif, "GPSLatitude", exif.latitude);
                setAttribute(newExif, "GPSLatitudeRef", exif.latitudeRef);
                setAttribute(newExif, "GPSLongitude", exif.longitude);
                setAttribute(newExif, "GPSLongitudeRef", exif.longitudeRef);
            }
            newExif.setAttribute("DateTime", new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
            newExif.saveAttributes();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void setAttribute(ExifInterface exif, String attr, String value) {
        if (!TextUtils.isEmpty(value)) {
            exif.setAttribute(attr, value);
        }
    }

    @SuppressLint({"InlinedApi"})
    public static ExifInfo getFileExifInfo(String path) {
        ExifInfo exifInfo = new ExifInfo();
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (exif != null) {
            exifInfo.imageLength = exif.getAttribute("ImageLength");
            exifInfo.imageWidth = exif.getAttribute("ImageWidth");
            exifInfo.make = exif.getAttribute("Make");
            exifInfo.orientation = exif.getAttribute("Orientation");
            exifInfo.model = exif.getAttribute("Model");
            exifInfo.datetime = exif.getAttribute("DateTime");
            exifInfo.latitude = exif.getAttribute("GPSLatitude");
            exifInfo.latitudeRef = exif.getAttribute("GPSLatitudeRef");
            exifInfo.longitude = exif.getAttribute("GPSLongitude");
            exifInfo.longitudeRef = exif.getAttribute("GPSLongitudeRef");
        }
        return exifInfo;
    }

    public static void setExifOrientation(String path, int orientation) {
        try {
            ExifInterface exif = new ExifInterface(path);
            int o = exif.getAttributeInt("Orientation", 0);
            orientation = ImageUtils.degreesToExifOrientation(orientation);
            if (o != orientation) {
                exif.setAttribute("Orientation", orientation + "");
                exif.saveAttributes();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
