package com.devtau.ekassir.presenters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import com.devtau.ekassir.activities.OrderDetailsActivity;
import com.devtau.ekassir.database.tables.OrdersTable;
import com.devtau.ekassir.database.tables.VehiclesTable;
import com.devtau.ekassir.model.Order;
import com.devtau.ekassir.util.Constants;
import com.devtau.ekassir.util.EKassirAPI;
import com.devtau.ekassir.util.Logger;
import com.devtau.ekassir.view.OrdersListViewInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Презентер, показывающий список заказов с общей информацией по каждому из них
 */
public class OrdersListPresenter {
    private static final String LOG_TAG = OrdersListPresenter.class.getSimpleName();
    private OrdersListViewInterface view;
    //счетчик попыток переподключения для метода retryConnection()
    private int counter;
    private static final int ORDERS_LIST_LOADER = 0;


    public OrdersListPresenter(OrdersListViewInterface view) {
        this.view = view;
    }

    public void sendRequestToServer() {
        if (checkIsOnline()){
            sendRequest();
        } else {
            view.showNoInternet();
        }
    }

    public boolean checkIsOnline() {
        ConnectivityManager cm = (ConnectivityManager) view.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void sendRequest() {
        Logger.v(LOG_TAG, "Entered retrofit sendRequest() method");
        view.showProgressBar();

        Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMATTER_ON_SERVER).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        EKassirAPI client = retrofit.create(EKassirAPI.class);

        Callback<List<Order>> callback = new Callback<List<Order>>() {
            @Override
            public void onResponse (Call<List<Order>> call, Response<List<Order>> response){
                if (response.isSuccessful()) {
                    //получен ответ от сервера об успехе. обработаем полученный результат
                    Logger.v(LOG_TAG, "Retrofit response isSuccessful");
                    List<Order> ordersResponse = response.body();
                    view.dismissProgressBar();
                    handleSuccess(ordersResponse);
                } else {
                    //ответ пришел, но говорит об ошибке
                    int errorCode = response.code();
                    Logger.e(LOG_TAG, "Retrofit response is not successful. ErrorCode: " + String.valueOf(errorCode));
                    Logger.e(LOG_TAG, "Check URL_ENDPOINT and executeRequest parameters if any");
                    ResponseBody errorBody = response.errorBody();
                    view.dismissProgressBar();
                    handleError(errorBody);
                }
            }

            @Override
            public void onFailure (Call<List<Order>> call, Throwable t){
                Logger.e(LOG_TAG, "Retrofit failure. Check API_BASE_URL and internet connection");
                view.dismissProgressBar();
                handleFailure(t.getLocalizedMessage());
            }
        };
        Call<List<Order>> call = client.executeRequest();
        //запрос можно выполнять синхронно методом execute(), или асинхронно методом enqueue()
        call.enqueue(callback);
    }

    private void handleSuccess(List<Order> ordersResponse) {
        Logger.v(LOG_TAG, "handleSuccess()");
        //переберем полученный лист и вставим/обновим записи в бд
        for(final Order order: ordersResponse) {
            view.getContext().getContentResolver().insert(
                    VehiclesTable.CONTENT_URI, VehiclesTable.getContentValues(order.getVehicle()));
            view.getContext().getContentResolver().insert(
                    OrdersTable.CONTENT_URI, OrdersTable.getContentValues(order));
        }
        view.initListView();
    }

    private void handleError(ResponseBody errorBody) {
        try {
            Logger.e(LOG_TAG, "ErrorBody: " + errorBody.string());
        } catch (IOException e) {
            Logger.e(LOG_TAG, "Error while getting string from errorBody", e);
        }
    }

    private void handleFailure(String failureMessage) {
        view.showMessage(failureMessage);
    }

    public void retryConnection(){
        //делаем несколько попыток повторного подключения с некоторым интервалом (см. Constants)
        view.showProgressBar();
        counter = 0;
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (checkIsOnline()) {
                    Logger.v(LOG_TAG, "Online!");
                    view.dismissProgressBar();
                    sendRequest();
                } else if (counter < Constants.RETRY_COUNT) {
                    Logger.v(LOG_TAG, "Retrying connection. Counter: " + String.valueOf(counter));
                    counter++;
                    handler.postDelayed(this, Constants.RETRY_LAG);
                } else if (view.dismissProgressBar()){
                    //если все попытки не увенчались успехом, показываем диалог еще раз
                    view.showNoInternet();
                }
            }
        });
    }

    public void initLoaderManager(LoaderManager manager) {
        //отправим запрос контент-ресолверу за нужным курсором
        manager.initLoader(ORDERS_LIST_LOADER, null, new LoaderManager.LoaderCallbacks<Cursor>(){
            String sortOrder = OrdersTable.COLUMN_ORDER_TIME + " DESC";
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(view.getContext(), OrdersTable.CONTENT_WITH_VEHICLES_URI,
                        null, null, null, sortOrder);
            }
            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                view.swapCursor(data);
            }
            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                view.swapCursor(null);
            }
        });
    }

    public void onListItemClick(Cursor cursor) {
        //getItemAtPosition вернет полный курсор всех строк списка, но выставленный в нужную позицию
        //таким образом, вызывать cursor.moveToFirst не требуется
        //и можно сразу работать с активной строкой курсора
//        DatabaseUtils.dumpCursor(cursor);
        if(cursor != null) {
            Intent intent = new Intent(view.getContext(), OrderDetailsActivity.class);
            intent.putExtra(OrderDetailsActivity.EXTRA_ORDER_ID,
                    cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
            view.getContext().startActivity(intent);
        }
    }
}
