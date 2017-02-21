package com.example.sokol.mcprices;


import android.os.AsyncTask;
import android.util.Log;

import com.example.sokol.mcprices.api.McApiInterface;
import com.example.sokol.mcprices.data.ProductListAdapter;
import com.example.sokol.mcprices.data.ProductsRepositoryInterface;
import com.example.sokol.mcprices.entities.Product;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by sokol on 17.02.2017.
 */

public class ProductsUpdater extends AsyncTask<Void, Void, Boolean> {

    private McApiInterface api;
    private ProductsRepositoryInterface productsRepository;
    private ProductListAdapter productListAdapter;

    public ProductsUpdater(McApiInterface api, ProductsRepositoryInterface productsRepository, ProductListAdapter productListAdapter){
        this.api = api;
        this.productsRepository = productsRepository;
        this.productListAdapter = productListAdapter;
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
        super.onPostExecute(isUpdated);
    }
}
