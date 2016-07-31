package com.devtau.ekassir.model;

import java.util.Comparator;
/**
 * Компараторы необходимы, если вы собираетесь сортировать лист объектов этого класса
 */
public class OrdersComparators {
    public static Comparator<Order> FIRST_FRESH = new Comparator<Order>() {
        @Override
        public int compare(Order first, Order second) {
            if (first.getOrderTime().after(second.getOrderTime())) return -1;
            if (first.getOrderTime().before(second.getOrderTime())) return 1;
            return 0;
        }
    };

    public static Comparator<Order> FIRST_OLD = new Comparator<Order>() {
        @Override
        public int compare(Order first, Order second) {
            if (first.getOrderTime().after(second.getOrderTime())) return 1;
            if (first.getOrderTime().before(second.getOrderTime())) return -1;
            return 0;
        }
    };

    public static Comparator<Order> FIRST_LOWER_PRICE = new Comparator<Order>() {
        @Override
        public int compare(Order first, Order second) {
            if(!first.getPrice().getCurrency().equals(second.getPrice().getCurrency())) return 0;
            return first.getPrice().getAmount() - second.getPrice().getAmount();
        }
    };

    public static Comparator<Order> FIRST_HIGHER_PRICE = new Comparator<Order>() {
        @Override
        public int compare(Order first, Order second) {
            if(!first.getPrice().getCurrency().equals(second.getPrice().getCurrency())) return 0;
            return second.getPrice().getAmount() - first.getPrice().getAmount();
        }
    };
}
