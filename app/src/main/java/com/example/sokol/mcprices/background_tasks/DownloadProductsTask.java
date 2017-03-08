package com.example.sokol.mcprices.background_tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.sokol.mcprices.api.McApi;
import com.example.sokol.mcprices.data.ProductsRepository;
import com.example.sokol.mcprices.entities.Product;
import com.example.sokol.mcprices.fragments.ProductsLoader;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by sokol on 08.03.2017.
 */
public class DownloadProductsTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "DownloadProductsTask";
    private static final String BASE_URL = "https://mcdonalds.ru";

    private McApi mcApi;
    private ProductsRepository repository;
    private String filesDir;
    private ProductsLoader productsLoader;

    public DownloadProductsTask(McApi mcApi, ProductsRepository repository, String filesDir, ProductsLoader productsLoader) {
        this.mcApi = mcApi;
        this.repository = repository;
        this.filesDir = filesDir;
        this.productsLoader = productsLoader;
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
                URL url = new URL(BASE_URL + p.getPic());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream input = connection.getInputStream();

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
                connection.disconnect();

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
            productsLoader.load();
        } else {
            productsLoader.loadError();
        }
    }
}
