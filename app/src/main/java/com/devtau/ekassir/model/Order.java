package com.devtau.ekassir.model;

import android.database.Cursor;
import android.provider.BaseColumns;
import java.text.ParseException;
import java.util.Date;
import com.devtau.ekassir.model.Address.StartAddress;
import com.devtau.ekassir.model.Address.EndAddress;
import com.devtau.ekassir.util.Constants;
import com.devtau.ekassir.util.Logger;
import static com.devtau.ekassir.database.tables.OrdersTable.*;
/**
 * Класс собирает в себе подробности по конкретному заказу
 * http://www.jsonschema2pojo.org/
 */
public class Order {
    private static final String LOG_TAG = Order.class.getSimpleName();

    private long id;
    private StartAddress startAddress;
    private EndAddress endAddress;
    private Price price;
    private Date orderTime;
    private Vehicle vehicle;

    public Order(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        startAddress = new StartAddress(
                cursor.getString(cursor.getColumnIndex(COLUMN_START_ADDRESS_CITY)),
                cursor.getString(cursor.getColumnIndex(COLUMN_START_ADDRESS_ADDRESS))
        );
        endAddress = new EndAddress(
                cursor.getString(cursor.getColumnIndex(COLUMN_END_ADDRESS_CITY)),
                cursor.getString(cursor.getColumnIndex(COLUMN_END_ADDRESS_ADDRESS))
        );
        price = new Price(
                cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE_AMOUNT)),
                cursor.getString(cursor.getColumnIndex(COLUMN_PRICE_CURRENCY))
        );
        orderTime = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_ORDER_TIME)));

        vehicle = new Vehicle(cursor);
    }

    public long getId() {
        return id;
    }
    public StartAddress getStartAddress() {
        return startAddress;
    }
    public EndAddress getEndAddress() {
        return endAddress;
    }
    public Price getPrice() {
        return price;
    }
    public Date getOrderTime() {
        return orderTime;
    }
    public Vehicle getVehicle() {
        return vehicle;
    }
}