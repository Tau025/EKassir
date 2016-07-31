package com.devtau.ekassir;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.devtau.ekassir.model.Order;
import com.devtau.ekassir.util.Constants;
import java.util.Locale;
import java.util.TimeZone;
/**
 * Адаптер списка главного экрана
 */
public class MyCursorAdapter extends CursorAdapter {
    public MyCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        //Заполняем ViewHolder данными
        Order order = new Order(cursor);
        Resources res = holder.view.getContext().getResources();
        Locale locale = res.getConfiguration().locale;

        holder.tv_start_address.setText(order.getStartAddress().toString());
        holder.tv_end_address.setText(order.getEndAddress().toString());

        //если пользователь находится в другом часовом поясе, то он увидит время подачи в его поясе
        Constants.dateFormat.setTimeZone(TimeZone.getDefault());
        holder.tv_order_time.setText(Constants.dateFormat.format(order.getOrderTime()));

        holder.tv_order_price.setText(String.format(locale,
                res.getString(R.string.order_price_formatter),
                order.getPrice().getAmount() / 100,
                order.getPrice().getAmount() % 100,
                order.getPrice().getCurrency()));
    }



    public static class ViewHolder {
        private View view;
        private TextView tv_start_address;
        private TextView tv_end_address;
        private TextView tv_order_time;
        private TextView tv_order_price;

        public ViewHolder(View view) {
            this.view = view;
            tv_start_address = (TextView) view.findViewById(R.id.tv_start_address);
            tv_end_address = (TextView) view.findViewById(R.id.tv_end_address);
            tv_order_time = (TextView) view.findViewById(R.id.tv_order_time);
            tv_order_price = (TextView) view.findViewById(R.id.tv_order_price);
        }
    }
}