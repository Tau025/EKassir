package com.devtau.ekassir.database.tables;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import com.devtau.ekassir.database.MySQLHelper;
import com.devtau.ekassir.model.Vehicle;
/**
 * Класс описывет список полей таблицы в бд по конкретной машине
 */
public abstract class VehiclesTable {
    public static final String TABLE_NAME = "Vehicles";

    public static final String COLUMN_REG_NUMBER = "regNumber";
    public static final String COLUMN_MODEL_NAME = "modelName";
    public static final String COLUMN_PHOTO = "photo";
    public static final String COLUMN_DRIVER_NAME = "driverName";

    //здесь делается допущение того, что гос.знак однозначно идентифицирует машину, что не совсем точно
    //для увеличения точности id машины необходимо так же хранить на сервере
    public static final String FIELDS =
            COLUMN_REG_NUMBER + " TEXT NOT NULL, "
            + COLUMN_MODEL_NAME + " TEXT NOT NULL, "
            + COLUMN_PHOTO + " TEXT NOT NULL, "
            + COLUMN_DRIVER_NAME + " TEXT NOT NULL, "
            + "UNIQUE (" + COLUMN_REG_NUMBER + ") ON CONFLICT REPLACE";


    public static final Uri CONTENT_URI = MySQLHelper.BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MySQLHelper.CONTENT_AUTHORITY + "/" + TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + MySQLHelper.CONTENT_AUTHORITY + "/" + TABLE_NAME;

    public static Uri buildVehicleUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static ContentValues getContentValues(Vehicle vehicle) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_REG_NUMBER, vehicle.getRegNumber());
        cv.put(COLUMN_MODEL_NAME, vehicle.getModelName());
        cv.put(COLUMN_PHOTO, vehicle.getPhoto());
        cv.put(COLUMN_DRIVER_NAME, vehicle.getDriverName());
        return cv;
    }
}