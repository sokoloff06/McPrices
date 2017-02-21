package com.example.sokol.mcprices.data;

import android.provider.BaseColumns;

/**
 * Created by sokol on 17.02.2017.
 */

public class ProductsContract {

    ProductsContract() {

    }

    class ProductsEntries implements BaseColumns {

        public static final String TABLE_NAME = "products";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " +
                        TABLE_NAME + "(" +
                        _ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME     + " TEXT, " +
                        COLUMN_PRICE    + " INTEGER);";
        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " +
                        TABLE_NAME;
    }

    public class TimestampEntries implements BaseColumns{

        public static final String TABLE_NAME = "last_update";
        public static final String COLUMN_TIMESTAMP = "timestamp";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " +
                        TABLE_NAME          + "(" +
                        _ID                 + " INTEGER PRIMARY KEY, " +
                        COLUMN_TIMESTAMP    + " TEXT);";

        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " +
                        TABLE_NAME;

    }

}
