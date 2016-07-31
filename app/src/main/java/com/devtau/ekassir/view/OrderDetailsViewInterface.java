package com.devtau.ekassir.view;

import android.graphics.Bitmap;
import com.devtau.ekassir.model.Order;
/**
 * Вью-интерфейс, обеспечивающий общение презентера с пользователем
 */
public interface OrderDetailsViewInterface extends View {
    void showMessage(String msg);
    void initControls(Order order);
    void setImageBitmap(Bitmap bitmap);
    void setImageResource(int no_image);
}
