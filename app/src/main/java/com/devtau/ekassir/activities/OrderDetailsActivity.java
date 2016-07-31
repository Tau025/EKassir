package com.devtau.ekassir.activities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.devtau.ekassir.R;
import com.devtau.ekassir.model.Order;
import com.devtau.ekassir.presenters.OrderDetailsPresenter;
import com.devtau.ekassir.util.Constants;
import com.devtau.ekassir.util.Logger;
import com.devtau.ekassir.view.OrderDetailsViewInterface;
import java.io.File;
import java.util.Locale;
import java.util.TimeZone;
/**
 * Активность подробностей по выбранному заказу
 */
public class OrderDetailsActivity extends AppCompatActivity implements
        OrderDetailsViewInterface {
    public static final String EXTRA_ORDER_ID = "extra_order_id";
    private static final String LOG_TAG = OrderDetailsActivity.class.getSimpleName();

    private TextView tv_start_address;
    private TextView tv_end_address;
    private TextView tv_order_time;
    private TextView tv_price;

    private ImageView iv_vehicle_photo;
    private TextView tv_vehicle_reg_number;
    private TextView tv_vehicle_model_name;
    private TextView tv_vehicle_driver_name;

    private OrderDetailsPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        long orderId = getIntent().getLongExtra(EXTRA_ORDER_ID, 0);
        presenter = new OrderDetailsPresenter(this);
        presenter.getOrderById(getSupportLoaderManager(), orderId);
    }

    @Override
    public void initControls(Order order) {
        tv_start_address = (TextView) findViewById(R.id.tv_start_address);
        tv_end_address = (TextView) findViewById(R.id.tv_end_address);
        tv_order_time = (TextView) findViewById(R.id.tv_order_time);
        tv_price = (TextView) findViewById(R.id.tv_order_price);

        iv_vehicle_photo = (ImageView) findViewById(R.id.iv_vehicle_photo);
        tv_vehicle_model_name = (TextView) findViewById(R.id.tv_vehicle_model_name);
        tv_vehicle_reg_number = (TextView) findViewById(R.id.tv_vehicle_reg_number);
        tv_vehicle_driver_name = (TextView) findViewById(R.id.tv_vehicle_driver_name);

        populateUI(order);
    }

    private void populateUI(Order order) {
        Resources res = getResources();
        Locale locale = res.getConfiguration().locale;

        tv_start_address.setText(order.getStartAddress().toString());
        tv_end_address.setText(order.getEndAddress().toString());

        //если пользователь находится в другом часовом поясе, то он увидит время подачи в его поясе
        Constants.dateFormat.setTimeZone(TimeZone.getDefault());
        tv_order_time.setText(Constants.dateFormat.format(order.getOrderTime()));

        tv_price.setText(String.format(locale,
                res.getString(R.string.order_price_formatter),
                order.getPrice().getAmount() / 100,
                order.getPrice().getAmount() % 100,
                order.getPrice().getCurrency()));

        //проверим, нет ли сохраненной фотографии машины на устройстве
        File vehiclePhoto = presenter.getStoredVehiclePhoto();
        Logger.v(LOG_TAG, "In populateUI(). vehiclePhoto is " + ((vehiclePhoto != null) ? "valid" : "null"));

        if(vehiclePhoto != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(vehiclePhoto.getAbsolutePath());
            iv_vehicle_photo.setImageBitmap(bitmap);
        } else {
            //запустим запрос к серверу только если на устройстве нужной фотографии нет
            presenter.loadImageFromServer();
        }

        tv_vehicle_model_name.setText(order.getVehicle().getModelName());
        tv_vehicle_reg_number.setText(order.getVehicle().getRegNumber());
        tv_vehicle_driver_name.setText(order.getVehicle().getDriverName());
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        if(iv_vehicle_photo != null) {
            iv_vehicle_photo.setImageBitmap(bitmap);
        } else {
            Logger.e(LOG_TAG, "iv_vehicle_photo is null");
        }
    }

    @Override
    public void setImageResource(int no_image) {
        if(iv_vehicle_photo != null) {
            iv_vehicle_photo.setImageResource(no_image);
        } else {
            Logger.e(LOG_TAG, "iv_vehicle_photo is null");
        }
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
