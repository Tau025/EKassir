package com.devtau.ekassir.model;
/**
 * Класс для подробностей по стоимости поездки
 */
public class Price {
    private int amount;
    private String currency;

    public Price(int amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public int getAmount() {
        return amount;
    }
    public String getCurrency() {
        return currency;
    }
}