package com.ningso.ningsodemo.utils;

import java.io.Serializable;

/**
 * Created by NingSo on 16/4/17.下午6:42
 *
 * @author: NingSo
 * @Email: ningso.ping@gmail.com
 */
public class ExifInfo implements Serializable {
    private static final long serialVersionUID = 7071870181136461462L;
    public String datetime;
    public String imageLength;
    public String imageWidth;
    public String latitude;
    public String latitudeRef;
    public String longitude;
    public String longitudeRef;
    public String make;
    public String model;
    public String orientation;
}