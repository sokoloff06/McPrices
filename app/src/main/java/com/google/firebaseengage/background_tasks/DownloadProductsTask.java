package com.google.firebaseengage.background_tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebaseengage.api.McApi;
import com.google.firebaseengage.data.ProductsRepository;
import com.google.firebaseengage.entities.Product;
import com.google.firebaseengage.menu.ProductsDisplayer;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by sokol on 08.03.2017.
 *
 *
 * Data model:
 * [
 *   {
 *     "id": 1,
 *     "name": "Apples",
 *     "price": 100,
 *     "pic": "product_images/1"
 *   },
 *   {
 *     "id": 1,
 *     "name": "Bananas",
 *     "price": 100,
 *     "pic": "product_images/1"
 *   }
 * ]
 */
public class DownloadProductsTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "DownloadProductsTask";

    private final McApi mcApi;
    private final ProductsRepository repository;
    private final String filesDir;
    private final ProductsDisplayer productsDisplayer;
    private final Context context;

    public DownloadProductsTask(Context context, McApi mcApi, ProductsRepository repository, String filesDir, ProductsDisplayer productsDisplayer) {
        this.context = context;
        this.mcApi = mcApi;
        this.repository = repository;
        this.filesDir = filesDir;
        this.productsDisplayer = productsDisplayer;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.i(TAG, "Timestamp is out of date");
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Log.i(TAG, "Transferring task to Repository");
        List<Product> products = mcApi.getProducts();
        if (products == null) {
            return false;
        }
        if (!savePics(products)) {
            return false;
        }
        System.out.println("Updating DB...");
        repository.update(products, currentTimestamp);
        return true;
    }

    private boolean savePics(List<Product> products) {
        for (Product p : products) {
            try {
                //Output
                String filename = p.getName() + ".png";
                String filepath = filesDir + "/" + filename;
                FileOutputStream output = new FileOutputStream(filepath);

                //Input
                InputStream input = context.getAssets().open(p.getId() + ".png");

                //Buffer
                int bytesRead;
                byte[] buffer = new byte[4096];
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                Log.i(TAG, filename + " is downloaded");

                //Closing references
                p.setPic(filepath);
                System.out.println(p.getPic());
                input.close();
                output.close();

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        System.out.println("Pictures successfully saved");
        return true;
    }

    @Override
    protected void onPostExecute(Boolean IsLoadSucessful) {
        if (IsLoadSucessful) {
            productsDisplayer.load();
        } else {
            productsDisplayer.loadError();
        }
    }
}
