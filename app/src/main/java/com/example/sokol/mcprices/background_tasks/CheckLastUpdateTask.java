package com.example.sokol.mcprices.background_tasks;

import android.os.AsyncTask;

import com.example.sokol.mcprices.api.McApi;
import com.example.sokol.mcprices.fragments.ProductsLoader;

import java.sql.Timestamp;

/**
 * Created by sokol on 08.03.2017.
 */
public class CheckLastUpdateTask extends AsyncTask<Void, Void, Boolean> {

    private McApi mcApi;
    private Timestamp localTimestamp;
    private ProductsLoader productsLoader;

    public CheckLastUpdateTask(McApi mcApi, Timestamp localTimestamp, ProductsLoader productsLoader) {
        this.mcApi = mcApi;
        this.localTimestamp = localTimestamp;
        this.productsLoader = productsLoader;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        System.out.println("Checking local timestamp");
        if (localTimestamp == null) {
            return true;
        }
        System.out.println("Getting timestamp from server");
        Timestamp serverTimestamp = mcApi.getLastUpdatedTimestamp();
        return localTimestamp.before(serverTimestamp);
    }

    @Override
    protected void onPostExecute(Boolean isOutOfDate) {
        productsLoader.loadOrDownload(isOutOfDate);
    }
}
