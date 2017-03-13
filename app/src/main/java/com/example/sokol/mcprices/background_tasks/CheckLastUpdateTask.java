package com.example.sokol.mcprices.background_tasks;

import android.os.AsyncTask;

import com.example.sokol.mcprices.api.McApi;
import com.example.sokol.mcprices.menu.ProductsDisplayer;

import java.sql.Timestamp;

/**
 * Created by sokol on 08.03.2017.
 */
public class CheckLastUpdateTask extends AsyncTask<Void, Void, Boolean> {

    private McApi mcApi;
    private Timestamp localTimestamp;
    private ProductsDisplayer productsDisplayer;

    public CheckLastUpdateTask(McApi mcApi, Timestamp localTimestamp, ProductsDisplayer productsDisplayer) {
        this.mcApi = mcApi;
        this.localTimestamp = localTimestamp;
        this.productsDisplayer = productsDisplayer;
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
        productsDisplayer.loadOrDownload(isOutOfDate);
    }
}
