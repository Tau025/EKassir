package com.devtau.ekassir.util;

import android.net.Uri;
import java.text.SimpleDateFormat;
import java.util.Locale;

public abstract class Constants {
    //API_BASE_URL всегда завершается /
    public static final String API_BASE_URL = "http://careers.ekassir.com/test/";
    //URL_ENDPOINT не начинается с /
    public static final String URL_ENDPOINT = "orders.json";

    //максимальное количество попыток переподключения и лаг между ними при отсутствующем интернете
    public static final int RETRY_COUNT = 6;
    public static final int RETRY_LAG = 500;//ms

    public static final Uri IMAGES_URI =
            Uri.parse(API_BASE_URL).buildUpon().appendPath("images").build();

//    /data/data/com.devtau.ekassir/EKassirVehiclePhotos/VehiclePhoto01.jpg
    public static final String BASE_PHOTOS_DIR_NAME = "EKassirVehiclePhotos";
    public static final String VEHICLE_PHOTO_PREFIX = "VehiclePhoto";
    public static final String VEHICLE_PHOTO_FORMAT = ".jpg";
    public static final int CASHED_PHOTO_LIFETIME = 10 * 60 * 1000;//10 минут

    public static final String DATE_FORMATTER_ON_SERVER = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
}