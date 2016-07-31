package com.devtau.ekassir;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import com.devtau.ekassir.database.MySQLHelper;
import com.devtau.ekassir.database.tables.OrdersTable;
import com.devtau.ekassir.database.tables.VehiclesTable;
import java.util.HashSet;

public class TestDb extends AndroidTestCase {
    void deleteTheDatabase() {
        mContext.deleteDatabase(MySQLHelper.DB_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(OrdersTable.TABLE_NAME);
        tableNameHashSet.add(VehiclesTable.TABLE_NAME);

        mContext.deleteDatabase(MySQLHelper.DB_NAME);
        SQLiteDatabase db = MySQLHelper.getInstance(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: Database has not been created correctly", cursor.moveToFirst());

        do {
            tableNameHashSet.remove(cursor.getString(0));
        } while(cursor.moveToNext());
        assertTrue("Error: Database was created without both the OrdersTable and VehiclesTable",
                tableNameHashSet.isEmpty());

        cursor = db.rawQuery("PRAGMA table_info(" + OrdersTable.TABLE_NAME + ")", null);
        assertTrue("Error: We were unable to query the database for table information.", cursor.moveToFirst());

        final HashSet<String> orderColumnsHashSet = new HashSet<>();
        orderColumnsHashSet.add(OrdersTable.COLUMN_START_ADDRESS_CITY);
        orderColumnsHashSet.add(OrdersTable.COLUMN_START_ADDRESS_ADDRESS);
        orderColumnsHashSet.add(OrdersTable.COLUMN_END_ADDRESS_CITY);
        orderColumnsHashSet.add(OrdersTable.COLUMN_END_ADDRESS_ADDRESS);
        orderColumnsHashSet.add(OrdersTable.COLUMN_PRICE_AMOUNT);
        orderColumnsHashSet.add(OrdersTable.COLUMN_PRICE_CURRENCY);
        orderColumnsHashSet.add(OrdersTable.COLUMN_ORDER_TIME);
        orderColumnsHashSet.add(OrdersTable.COLUMN_VEHICLE_REG_NUMBER);

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            orderColumnsHashSet.remove(columnName);
        } while(cursor.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                orderColumnsHashSet.isEmpty());
        db.close();
    }

    public void testLocationTable() {
        insertLocation();
    }

    public void testWeatherTable() {
        long locationRowId = insertLocation();
        assertFalse("Error: Location Not Inserted Correctly", locationRowId == -1L);

        MySQLHelper dbHelper = MySQLHelper.getInstance(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues orderValues = TestUtilities.createWeatherValues(locationRowId);
        long orderRowId = db.insert(OrdersTable.TABLE_NAME, null, orderValues);
        assertTrue(orderRowId != -1);

        Cursor ordersCursor = db.query(OrdersTable.TABLE_NAME, null, null, null, null, null, null);
        assertTrue( "Error: No Records returned from location query", ordersCursor.moveToFirst());

        TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate",
                ordersCursor, orderValues);
        assertFalse( "Error: More than one record returned from weather query",
                ordersCursor.moveToNext() );

        ordersCursor.close();
        dbHelper.close();
    }


    public long insertLocation() {
        MySQLHelper dbHelper = MySQLHelper.getInstance(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createVehicleValues();
        long locationRowId;
        locationRowId = db.insert(VehiclesTable.TABLE_NAME, null, testValues);
        assertTrue(locationRowId != -1);

        Cursor vehiclesCursor = db.query(VehiclesTable.TABLE_NAME, null, null, null, null, null, null);
        assertTrue( "Error: No Records returned from location query", vehiclesCursor.moveToFirst() );

        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                vehiclesCursor, testValues);
        assertFalse( "Error: More than one record returned from location query",
                vehiclesCursor.moveToNext() );

        vehiclesCursor.close();
        db.close();
        return locationRowId;
    }
}
