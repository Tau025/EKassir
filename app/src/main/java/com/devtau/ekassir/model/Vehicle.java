package com.devtau.ekassir.model;

import android.database.Cursor;
import static com.devtau.ekassir.database.tables.VehiclesTable.*;
/**
 * Класс для подробностей по автомобилю
 */
public class Vehicle {
    private String regNumber;
    private String modelName;
    private String photo;
    private String driverName;

    public Vehicle(Cursor cursor) {
        regNumber = cursor.getString(cursor.getColumnIndex(COLUMN_REG_NUMBER));
        modelName = cursor.getString(cursor.getColumnIndex(COLUMN_MODEL_NAME));
        photo = cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO));
        driverName = cursor.getString(cursor.getColumnIndex(COLUMN_DRIVER_NAME));
    }

    public String getRegNumber() {
        return regNumber;
    }
    public String getModelName() {
        return modelName;
    }
    public String getPhoto() {
        return photo;
    }
    public String getDriverName() {
        return driverName;
    }
}