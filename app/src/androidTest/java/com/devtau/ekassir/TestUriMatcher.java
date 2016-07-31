package com.devtau.ekassir;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;
import com.devtau.ekassir.database.MyContentProvider;
import com.devtau.ekassir.database.tables.OrdersTable;
import com.devtau.ekassir.database.tables.VehiclesTable;

public class TestUriMatcher extends AndroidTestCase {
    private static final long TEST_ORDER_ID = 10L;

    private static final Uri TEST_ORDER_DIR = OrdersTable.CONTENT_URI;
    private static final Uri TEST_VEHICLE_DIR = VehiclesTable.CONTENT_URI;
    private static final Uri TEST_ORDER_WITH_VEHICLE_BY_ID = OrdersTable.buildOrderWithVehicleUri(TEST_ORDER_ID);
    private static final Uri TEST_ALL_ORDERS_WITH_VEHICLES = OrdersTable.CONTENT_WITH_VEHICLES_URI;

    public void testUriMatcher() {
        UriMatcher testMatcher = MyContentProvider.buildUriMatcher();

        assertEquals("Error: The ORDER_DIR URI was matched incorrectly.",
                testMatcher.match(TEST_ORDER_DIR), MyContentProvider.ORDER);
        assertEquals("Error: The VEHICLE_DIR URI was matched incorrectly.",
                testMatcher.match(TEST_VEHICLE_DIR), MyContentProvider.VEHICLE);
        assertEquals("Error: The ORDER_WITH_VEHICLE_BY_ID URI was matched incorrectly.",
                testMatcher.match(TEST_ORDER_WITH_VEHICLE_BY_ID), MyContentProvider.ORDER_WITH_VEHICLE_BY_ID);
        assertEquals("Error: The ALL_ORDERS_WITH_VEHICLES URI was matched incorrectly.",
                testMatcher.match(TEST_ALL_ORDERS_WITH_VEHICLES), MyContentProvider.ALL_ORDERS_WITH_VEHICLES);
    }
}
