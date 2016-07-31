package com.devtau.ekassir.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.devtau.ekassir.database.tables.OrdersTable;
import com.devtau.ekassir.database.tables.VehiclesTable;
import com.devtau.ekassir.util.Logger;
/**
 * Не забудьте прописать MyContentProvider в манифесте
 */
public class MyContentProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String LOG_TAG = MyContentProvider.class.getSimpleName();
    private MySQLHelper mOpenHelper;

    public static final int ORDER = 100;
    public static final int ORDER_WITH_VEHICLE_BY_ID = 101;
    public static final int VEHICLE = 200;
    public static final int ALL_ORDERS_WITH_VEHICLES = 300;

    //задача QUERY_BUILDER в том чтобы сцепить все связанные таблицы воедино,
    //с тем чтобы дальше выполнять запросы к этой большой таблице
    public static final SQLiteQueryBuilder QUERY_BUILDER;
    static{
        QUERY_BUILDER = new SQLiteQueryBuilder();
        //здесь применено внутреннее связывание двух таблиц по ключевому полю
        //Orders INNER JOIN Vehicles ON Orders.vehicleRegNumber = regNumber
        QUERY_BUILDER.setTables(OrdersTable.TABLE_NAME + " INNER JOIN " + VehiclesTable.TABLE_NAME +
                        " ON " + OrdersTable.TABLE_NAME + "." + OrdersTable.COLUMN_VEHICLE_REG_NUMBER +
                        " = " + VehiclesTable.COLUMN_REG_NUMBER);
    }


    @Override
    public boolean onCreate() {
        Logger.d(LOG_TAG, "onCreate()");
        mOpenHelper = MySQLHelper.getInstance(getContext());
        return true;
    }

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MySQLHelper.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, OrdersTable.TABLE_NAME, ORDER);
        matcher.addURI(authority, VehiclesTable.TABLE_NAME, VEHICLE);
        matcher.addURI(authority, OrdersTable.TABLE_NAME + "-" + VehiclesTable.TABLE_NAME + "/*", ORDER_WITH_VEHICLE_BY_ID);
        matcher.addURI(authority, OrdersTable.TABLE_NAME + "-" + VehiclesTable.TABLE_NAME, ALL_ORDERS_WITH_VEHICLES);
        return matcher;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ORDER:
                return OrdersTable.CONTENT_TYPE;
            case VEHICLE:
                return VehiclesTable.CONTENT_TYPE;
            case ORDER_WITH_VEHICLE_BY_ID:
                return OrdersTable.CONTENT_ITEM_TYPE;
            case ALL_ORDERS_WITH_VEHICLES:
                return OrdersTable.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        switch(match) {
            case ORDER: {
                //перехватываем ошибку вставки и пробуем обработать ее как обновление уже существующей записи
                try {
                    long _id = db.insertOrThrow(OrdersTable.TABLE_NAME, null, values);
                    if(_id > 0) {
                        returnUri = OrdersTable.buildOrderUri(_id);
                    }
                } catch(SQLiteConstraintException e) {
                    int rowsUpdated = update(uri, values, BaseColumns._ID + " =? ",
                            new String[]{values.getAsString(BaseColumns._ID)});
                    if(rowsUpdated == 0) throw e;
                }
                break;
            }
            case VEHICLE: {
                //автообновление уже имеющихся записей настроено на уровне VehiclesTable.FIELDS
                long _id = db.insert(VehiclesTable.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = VehiclesTable.buildVehicleUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                } break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Logger.v(LOG_TAG, "query(). uri: " + String.valueOf(uri));
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            // "order"
            case ORDER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        OrdersTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            // "order-vehicle"
            case ALL_ORDERS_WITH_VEHICLES: {
                retCursor = QUERY_BUILDER.query(mOpenHelper.getReadableDatabase(),
                        projection, null, null, null, null, sortOrder
                );
                break;
            }

            // "vehicle"
            case VEHICLE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        VehiclesTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            // "order/*"
            case ORDER_WITH_VEHICLE_BY_ID: {
                retCursor = getOrderById(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ORDER: {
                rowsUpdated = db.update(OrdersTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case VEHICLE: {
                rowsUpdated = db.update(VehiclesTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case ORDER: {
                rowsDeleted = db.delete(OrdersTable.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case VEHICLE: {
                rowsDeleted = db.delete(VehiclesTable.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        //надо разобраться с пакетной вставкой
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ORDER:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value: values) {
                        long _id = db.insert(OrdersTable.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    public Cursor getOrderById(Uri uri, String[] projection, String sortOrder) {
        String orderIdString = OrdersTable.getOrderIdFromUri(uri);

        return QUERY_BUILDER.query(mOpenHelper.getReadableDatabase(),
                projection,
                OrdersTable.TABLE_NAME + "." + BaseColumns._ID + " = ? ",
                new String[]{orderIdString},
                null,
                null,
                sortOrder
        );
    }
}
