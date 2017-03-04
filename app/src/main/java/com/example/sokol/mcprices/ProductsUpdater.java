package com.example.sokol.mcprices;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.sokol.mcprices.api.McApiInterface;
import com.example.sokol.mcprices.data.ProductListAdapter;
import com.example.sokol.mcprices.data.ProductsRepositoryInterface;
import com.example.sokol.mcprices.entities.Product;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by sokol on 17.02.2017.
 */
//TODO: Rewrite this class!
public class ProductsUpdater extends AsyncTask<Void, Void, Boolean> {

    private McApiInterface api;
    private ProductsRepositoryInterface productsRepository;
    private ProductListAdapter productListAdapter;
    private OnUpdateTaskCompleted listener;
    private Context context;

    public ProductsUpdater(McApiInterface api,
                           ProductsRepositoryInterface productsRepository,
                           ProductListAdapter productListAdapter,
                           OnUpdateTaskCompleted listener,
                           Context context){
        this.api = api;
        this.productsRepository = productsRepository;
        this.productListAdapter = productListAdapter;
        this.listener = listener;
        this.context = context;
    }



    private boolean isOutOfDate(){

        Timestamp lastUpdateLocal = productsRepository.getTimestamp();
        if(lastUpdateLocal == null){
            return true;
        }
        Timestamp lastUpdateWeb = api.getLastUpdatedTimestamp();

        return lastUpdateLocal.before(lastUpdateWeb);
    }

    public boolean update(){
        if(isOutOfDate()){
            Log.i("ProductsUpdater", "Timestamp is out of date");
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            Log.i("ProductsUpdater", "Transferring task to Repository");
            List<Product> products = api.getProducts();
            if (products == null){
                return false;
            }
            else{
                savePics(products);
                productsRepository.update(products, currentTimestamp);
                productListAdapter.setProducts(products);
                return true;
            }
        }
        return true;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.i("ProductsUpdater", "Starting Update...");
        if(update()){
            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean isUpdated) {
        Log.i("ProductsUpdater", "Update finished");
        productListAdapter.notifyDataSetChanged();
        listener.onUpdateCompleted();
        super.onPostExecute(isUpdated);
    }

    protected void savePics(List<Product> products) {
        for (Product p : products){
            try {
                //Output
                String filename = p.getName() + ".png";
                FileOutputStream output = context.openFileOutput(filename, Context.MODE_PRIVATE);

                //Input
                URL url = new URL("https://mcdonalds.ru" + p.getPic());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream input = connection.getInputStream();

                //Buffer
                int bytesRead = -1;
                byte[] buffer = new byte[4096];
                while((bytesRead = input.read(buffer)) != -1){
                    output.write(buffer, 0, bytesRead);
                }
                Log.i("ProductsUpdater", filename + " pic is downloaded");

                //Closing references
                p.setPic(filename);
                input.close();
                output.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
