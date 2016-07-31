package com.devtau.ekassir.activities;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.devtau.ekassir.MyCursorAdapter;
import com.devtau.ekassir.R;
import com.devtau.ekassir.presenters.OrdersListPresenter;
import com.devtau.ekassir.fragments.NoInternetDF;
import com.devtau.ekassir.fragments.ProgressBarDF;
import com.devtau.ekassir.view.OrdersListViewInterface;
/**
 * Главная активность приложения, показывающая список заказов с общей информацией по каждому из них
 */
public class OrdersListActivity extends AppCompatActivity implements
        OrdersListViewInterface,
        NoInternetDF.NoInternetDFListener{

    private MyCursorAdapter adapter;
    private OrdersListPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new OrdersListPresenter(this);
        presenter.sendRequestToServer();
    }

    @Override
    public void initListView() {
        ListView listView = (ListView) findViewById(R.id.listView);
        if(listView != null) {
            //инициализируем список пустым курсором
            adapter = new MyCursorAdapter(this, null, 0);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    presenter.onListItemClick((Cursor) adapterView.getItemAtPosition(position));
                }
            });
        }
        presenter.initLoaderManager(getSupportLoaderManager());
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void swapCursor(Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showNoInternet() {
        NoInternetDF.show(getSupportFragmentManager());
    }

    @Override
    public void showProgressBar() {
        ProgressBarDF.show(getSupportFragmentManager());
    }

    @Override
    public boolean dismissProgressBar() {
        return ProgressBarDF.dismiss(getSupportFragmentManager());
    }

    @Override
    public void retryConnection() {
        presenter.retryConnection();
    }
}
