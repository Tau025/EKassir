package com.devtau.ekassir.database.tables;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import com.devtau.ekassir.database.MySQLHelper;
import com.devtau.ekassir.model.Order;
import com.devtau.ekassir.util.Constants;
import com.devtau.ekassir.util.Logger;
/**
 * Класс описывет список полей таблицы в бд по конкретному заказу
 */
public abstract class OrdersTable {
    public static final String TABLE_NAME = "Orders";

    public static final String COLUMN_START_ADDRESS_CITY = "startAddressCity";
    public static final String COLUMN_START_ADDRESS_ADDRESS = "startAddressAddress";
    public static final String COLUMN_END_ADDRESS_CITY = "endAddressCity";
    public static final String COLUMN_END_ADDRESS_ADDRESS = "endAddressAddress";
    public static final String COLUMN_PRICE_AMOUNT = "priceAmount";
    public static final String COLUMN_PRICE_CURRENCY = "priceCurrency";
    public static final String COLUMN_ORDER_TIME = "orderTime";
    public static final String COLUMN_VEHICLE_REG_NUMBER = "vehicleRegNumber";

    public static final String FIELDS = MySQLHelper.PRIMARY_KEY
            + COLUMN_START_ADDRESS_CITY + " TEXT NOT NULL, "
            + COLUMN_START_ADDRESS_ADDRESS + " TEXT NOT NULL, "
            + COLUMN_END_ADDRESS_CITY + " TEXT NOT NULL, "
            + COLUMN_END_ADDRESS_ADDRESS + " TEXT NOT NULL, "
            + COLUMN_PRICE_AMOUNT + " INTEGER NOT NULL, "
            + COLUMN_PRICE_CURRENCY + " TEXT NOT NULL, "
            + COLUMN_ORDER_TIME + " INTEGER NOT NULL, "
            + COLUMN_VEHICLE_REG_NUMBER + " TEXT NOT NULL, "
            + "FOREIGN KEY (" + COLUMN_VEHICLE_REG_NUMBER + ") REFERENCES "
            + VehiclesTable.TABLE_NAME + " (" + VehiclesTable.COLUMN_REG_NUMBER + ")";

    public static final Uri CONTENT_URI =
            MySQLHelper.BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
    public static final Uri CONTENT_WITH_VEHICLES_URI =
            Uri.parse(CONTENT_URI + "-" + VehiclesTable.TABLE_NAME);

    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MySQLHelper.CONTENT_AUTHORITY + "/" + TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + MySQLHelper.CONTENT_AUTHORITY + "/" + TABLE_NAME;
    private static final String LOG_TAG = OrdersTable.class.getSimpleName();

    public static Uri buildOrderUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static Uri buildOrderWithVehicleUri(long id) {
        return ContentUris.withAppendedId(CONTENT_WITH_VEHICLES_URI, id);
    }

    public static String getOrderIdFromUri(Uri uri) {
        Logger.v(LOG_TAG, "getOrderIdFromUri(). segment 1: " + String.valueOf(uri.getPathSegments().get(1)));
        return uri.getPathSegments().get(1);
    }

    public static ContentValues getContentValues(Order order) {
        ContentValues cv = new ContentValues();
        if (order.getId() != 0) {
            cv.put(BaseColumns._ID, order.getId());
        }
        cv.put(COLUMN_START_ADDRESS_CITY, order.getStartAddress().getCity());
        cv.put(COLUMN_START_ADDRESS_ADDRESS, order.getStartAddress().getAddress());
        cv.put(COLUMN_END_ADDRESS_CITY, order.getEndAddress().getCity());
        cv.put(COLUMN_END_ADDRESS_ADDRESS, order.getEndAddress().getAddress());

        cv.put(COLUMN_PRICE_AMOUNT, order.getPrice().getAmount());
        cv.put(COLUMN_PRICE_CURRENCY, order.getPrice().getCurrency());

        cv.put(COLUMN_ORDER_TIME, order.getOrderTime().getTime());
        cv.put(COLUMN_VEHICLE_REG_NUMBER, order.getVehicle().getRegNumber());
        return cv;
    }
}

/*
http://www.sqlite.org/datatype3.html
INTEGER целое число, дата-время как long
TEXT    символьные данные, дата-время как ISO8601 строки ("YYYY-MM-DD HH:MM:SS.SSS")
REAL    вещественное число
NUMERIC логическое значение
BLOB    двоичные большие объекты
*/