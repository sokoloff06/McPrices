package com.example.sokol.mcprices.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.sokol.mcprices.data.ProductsContract.ProductsEntries;
import com.example.sokol.mcprices.data.ProductsContract.TimestampEntries;
import com.example.sokol.mcprices.entities.Product;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by sokol on 17.02.2017.
 */

public class ProductsRepository implements ProductsRepositoryInterface {

    private SQLiteDatabase db;

    public ProductsRepository(SQLiteDatabase db){
        this.db = db;
    }

    @Override
    public Timestamp getTimestamp() {
        Cursor timestampCursor = db.query(
                TimestampEntries.TABLE_NAME,
                new String[]{TimestampEntries.COLUMN_TIMESTAMP},
                null,
                null,
                null,
                null,
                null,
                "1"
        );
        if(!timestampCursor.moveToFirst()){
            return null;
        }
        int timestampCursorIndex = timestampCursor.getColumnIndexOrThrow(TimestampEntries.COLUMN_TIMESTAMP);
        return Timestamp.valueOf(timestampCursor.getString(timestampCursorIndex));
    }

    private void setTimestamp(Timestamp timestamp) {
        ContentValues values = new ContentValues();
        values.put(TimestampEntries.COLUMN_TIMESTAMP, timestamp.toString());
        db.update(
                TimestampEntries.TABLE_NAME,
                values,
                null,
                null
        );
    }

    @Override
    public List<Product> getProducts(){
        List<Product> products = new ArrayList<>();
        Cursor productsCursor = db.query(
                ProductsEntries.TABLE_NAME,
                new String[]{ProductsEntries.COLUMN_NAME, ProductsEntries.COLUMN_PRICE},
                null,
                null,
                null,
                null,
                null);
        if(!productsCursor.moveToFirst()){
            return null;
        }
        int productsNameCursorIndex = productsCursor.getColumnIndexOrThrow(ProductsEntries.COLUMN_NAME);
        int productsPriceCursorIndex = productsCursor.getColumnIndexOrThrow(ProductsEntries.COLUMN_PRICE);
        int productsPicCursorIndex = productsCursor.getColumnIndexOrThrow(ProductsEntries.COLUMN_PIC);
        while (!productsCursor.isAfterLast()){
            products.add(
                    new Product(
                            productsCursor.getString(productsNameCursorIndex),
                            productsCursor.getInt(productsPriceCursorIndex),
                            productsCursor.getString(productsPicCursorIndex)
                    )
            );
            productsCursor.moveToNext();
        }
        return products;
    }

    @Override
    public void update(List<Product> products, Timestamp currentTimestamp){

        Log.i("Products Repository", "Starting transaction");
        db.beginTransaction();
        try {
            db.delete(
                    ProductsEntries.TABLE_NAME,
                    null,
                    null
            );
            ContentValues row = new ContentValues();
            for (Product p : products) {
                row.put(ProductsEntries.COLUMN_NAME, p.getName());
                row.put(ProductsEntries.COLUMN_PRICE, p.getPrice());
                db.insert(
                        ProductsEntries.TABLE_NAME,
                        null,
                        row
                );
            }
            setTimestamp(currentTimestamp);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            Log.i("Products Repository", "Transaction has ended");
        }
    }
}
