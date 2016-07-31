package com.devtau.ekassir.model;
/**
 * Класс для подробностей по адресу подачи и адресу назначения
 */
public abstract class Address {
    private String city;
    private String address;

    public String getCity() {
        return city;
    }
    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return city + ", " + address;
    }


    public static class StartAddress extends Address{
        public StartAddress(String city, String address) {
            super.city = city;
            super.address = address;
        }
    }

    public static class EndAddress extends Address{
        public EndAddress(String city, String address) {
            super.city = city;
            super.address = address;
        }
    }
}
