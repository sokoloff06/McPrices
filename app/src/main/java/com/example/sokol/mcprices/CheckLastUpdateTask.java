package com.example.sokol.mcprices;

import android.os.AsyncTask;

import com.example.sokol.mcprices.api.McApi;

import java.sql.Timestamp;

/**
 * Created by sokol on 08.03.2017.
 */
class CheckLastUpdateTask extends AsyncTask<Void, Void, Boolean> {

    private McApi mcApi;
    private Timestamp localTimestamp;
    private ProductsDownLoader productsDownLoader;

    CheckLastUpdateTask(McApi mcApi, Timestamp localTimestamp, ProductsDownLoader productsDownLoader) {
        this.mcApi = mcApi;
        this.localTimestamp = localTimestamp;
        this.productsDownLoader = productsDownLoader;
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
        productsDownLoader.loadOrDownload(isOutOfDate);
    }
}
