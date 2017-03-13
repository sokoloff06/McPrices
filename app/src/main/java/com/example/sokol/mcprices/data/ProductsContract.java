package com.example.sokol.mcprices.data;

import android.provider.BaseColumns;

/**
 * Created by sokol on 17.02.2017.
 */

class ProductsContract {

    ProductsContract() {

    }

    class ProductsEntries implements BaseColumns {

        static final String TABLE_NAME = "products";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_PRICE = "price";
        static final String COLUMN_PIC = "pic";

        static final String SQL_CREATE_TABLE =
                "CREATE TABLE " +
                        TABLE_NAME + "(" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT, " +
                        COLUMN_PRICE + " INTEGER, " +
                        COLUMN_PIC + " TEXT);";
        static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " +
                        TABLE_NAME;
    }

    class TimestampEntries implements BaseColumns {

        static final String TABLE_NAME = "last_update";
        static final String COLUMN_TIMESTAMP = "timestamp";

        static final String SQL_CREATE_TABLE =
                "CREATE TABLE " +
                        TABLE_NAME + "(" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_TIMESTAMP + " TEXT);";

        static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " +
                        TABLE_NAME;

    }

}
