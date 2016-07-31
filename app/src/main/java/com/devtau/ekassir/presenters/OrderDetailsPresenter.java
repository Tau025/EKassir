package com.devtau.ekassir.presenters;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import com.devtau.ekassir.R;
import com.devtau.ekassir.database.tables.OrdersTable;
import com.devtau.ekassir.model.Order;
import com.devtau.ekassir.util.Constants;
import com.devtau.ekassir.util.FileManager;
import com.devtau.ekassir.util.Logger;
import com.devtau.ekassir.view.OrderDetailsViewInterface;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * Презентер подробностей по выбранному заказу
 */
public class OrderDetailsPresenter {
    private static final String LOG_TAG = OrderDetailsPresenter.class.getSimpleName();
    private OrderDetailsViewInterface view;
    private Order order;

    public OrderDetailsPresenter(OrderDetailsViewInterface view) {
        this.view = view;
    }

    public void getOrderById(LoaderManager manager, long orderId) {
        //подготовим Uri
        Logger.v(LOG_TAG, "In getOrderById(). orderId: " + String.valueOf(orderId));
        final Uri uri = OrdersTable.buildOrderWithVehicleUri(orderId);
        Logger.v(LOG_TAG, "In getOrderById(). uri: " + String.valueOf(uri));

        //отправим запрос контент-ресолверу за нужным курсором
        manager.initLoader((int) orderId, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            String sortOrder = OrdersTable.COLUMN_ORDER_TIME + " DESC";

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(view.getContext(), uri, null, null, null, sortOrder);
            }
            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                //попробуем сформировать заказ из курсора
//                DatabaseUtils.dumpCursor(data);
                if(data != null) {
                    data.moveToFirst();
                    order = new Order(data);
                }
                Logger.v(LOG_TAG, "In getOrderById(). order is " + ((order != null) ? "valid" : "null"));

                //обрабатывать вью-элементы этой ативности нужно только если получен валидный заказ
                if (order != null) {
                    view.initControls(order);
                } else {
                    view.showMessage(view.getContext().getString(R.string.order_load_failed_msg));
                }
            }
            @Override
            public void onLoaderReset(Loader<Cursor> loader) {/*NOP*/}
        });
    }

    public File getStoredVehiclePhoto() {
        return FileManager.getStoredVehiclePhoto(view.getContext(), order);
    }

    public void loadImageFromServer() {
        new LoadImageTask().execute(order.getVehicle().getPhoto());
    }



    class LoadImageTask extends AsyncTask<String, Void, Boolean> {
        private Bitmap bitmap;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Logger.d(LOG_TAG, "Started LoadImageTask");
            InputStream inputStream = null;
            try {
                URL url = getUrlFromImageName(params[0]);
                if(url != null) {
                    inputStream = url.openConnection().getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
            } catch (IOException e) {
                Logger.e(LOG_TAG, "Error opening url connection", e);
                return false;
            } finally {
                try {
                    if(inputStream != null) {
                        Logger.v(LOG_TAG, "Closing inputStream");
                        inputStream.close();
                    }
                } catch (IOException e) {
                    Logger.e(LOG_TAG, "Error while closing inputStream", e);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success && bitmap != null){
                Logger.v(LOG_TAG, "Picture load success");

                //получим размеры загруженной картинки
                int imageWidth = bitmap.getWidth();
                int imageHeight = bitmap.getHeight();
                Logger.v(LOG_TAG, "Image from server w*h: " + String.valueOf(imageWidth) + "*" + String.valueOf(imageHeight));

                view.setImageBitmap(bitmap);
                FileManager.saveImageLocally(view.getContext(), order, bitmap);
            } else {
                Logger.e(LOG_TAG, "Picture load failed");
                view.showMessage(view.getContext().getString(R.string.picture_load_failed_msg));
                view.setImageResource(R.drawable.no_image);
            }
        }

        @Nullable
        private URL getUrlFromImageName(String imageName) {
            Uri imageUri = Constants.IMAGES_URI.buildUpon().appendPath(imageName).build();
            try {
                return new URL(imageUri.toString());
            } catch (MalformedURLException e) {
                Logger.e(LOG_TAG, "Couldn't transform Uri to URL", e);
                view.showMessage(view.getContext().getString(R.string.picture_load_failed_msg));
                return null;
            }
        }
    }
}
