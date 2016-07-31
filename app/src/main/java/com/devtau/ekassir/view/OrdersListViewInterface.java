package com.devtau.ekassir.view;

import android.database.Cursor;
/**
 * Вью-интерфейс, обеспечивающий общение презентера с пользователем
 */
public interface OrdersListViewInterface extends View {
    void showNoInternet();
    void showProgressBar();
    boolean dismissProgressBar();
    void initListView();
    void showMessage(String msg);
    void swapCursor(Cursor cursor);
}
