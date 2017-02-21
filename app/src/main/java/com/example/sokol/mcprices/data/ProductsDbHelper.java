package com.example.sokol.mcprices.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.sokol.mcprices.data.ProductsContract.*;
/**
 * Created by sokol on 17.02.2017.
 */

public class ProductsDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "products.db";

    public ProductsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ProductsContract.ProductsEntries.SQL_CREATE_TABLE);
        db.execSQL(ProductsContract.TimestampEntries.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ProductsContract.ProductsEntries.SQL_DROP_TABLE);
        db.execSQL(ProductsContract.TimestampEntries.SQL_DROP_TABLE);
        onCreate(db);
    }
}
