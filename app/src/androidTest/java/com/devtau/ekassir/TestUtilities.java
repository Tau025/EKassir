package com.devtau.ekassir;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;
import com.devtau.ekassir.database.tables.OrdersTable;
import com.devtau.ekassir.database.tables.VehiclesTable;
import java.util.Map;
import java.util.Set;
/**
 * Вспомогательные методы для тестирования
 */
public class TestUtilities extends AndroidTestCase {
    static ContentValues createWeatherValues(long vehicleRegNumber) {
        ContentValues values = new ContentValues();

        values.put(OrdersTable.COLUMN_START_ADDRESS_CITY, "Санкт-Петербург");
        values.put(OrdersTable.COLUMN_START_ADDRESS_ADDRESS, "Пр. Кантемировская, д. 28");
        values.put(OrdersTable.COLUMN_END_ADDRESS_CITY, "Санкт-Петербург");
        values.put(OrdersTable.COLUMN_END_ADDRESS_ADDRESS, "Пр. Стачек, д. 50");
        values.put(OrdersTable.COLUMN_PRICE_AMOUNT, "73100");
        values.put(OrdersTable.COLUMN_PRICE_CURRENCY, "RUB");
        values.put(OrdersTable.COLUMN_ORDER_TIME, "1464367800000");
        values.put(OrdersTable.COLUMN_VEHICLE_REG_NUMBER, vehicleRegNumber);

        return values;
    }

    static ContentValues createVehicleValues() {
        ContentValues values = new ContentValues();

        values.put(VehiclesTable.COLUMN_REG_NUMBER, "к345тт25");
        values.put(VehiclesTable.COLUMN_MODEL_NAME, "Toyota Camry");
        values.put(VehiclesTable.COLUMN_PHOTO, "02.jpg");
        values.put(VehiclesTable.COLUMN_DRIVER_NAME, "Петров Петр Петрович");

        return values;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }
}
