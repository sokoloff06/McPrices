package com.example.sokol.mcprices;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.sokol.mcprices.api.McApi;
import com.example.sokol.mcprices.api.McApiInterface;
import com.example.sokol.mcprices.data.ProductListAdapter;
import com.example.sokol.mcprices.data.ProductsDbHelper;
import com.example.sokol.mcprices.data.ProductsRepository;

public class ProductListActivity extends AppCompatActivity {

    RecyclerView productListRecyclerView;
    McApiInterface mcApi;
    ProductListAdapter productListAdapter;
    ProductsRepository productsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        mcApi = new McApi();
        ProductsDbHelper dbHelper = new ProductsDbHelper(this);
        productsRepository = new ProductsRepository(dbHelper.getWritableDatabase());
        productListAdapter = new ProductListAdapter();

        //TODO: try to update firstly
        if(isNetworkOnline()){
            new ProductsUpdater(mcApi, productsRepository, productListAdapter).execute();
        }
        else{
            productListAdapter.setProducts(productsRepository.getProducts());
            Toast updateError = Toast.makeText(this, "Error during update. Check network connection", Toast.LENGTH_LONG);
            updateError.show();
        }

        productListRecyclerView = (RecyclerView) findViewById(R.id.rv_product_list);
        productListRecyclerView.setAdapter(productListAdapter);
        //TODO: count number of column regarding to screen width
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        productListRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == (R.id.acton_refresh)){
            if(isNetworkOnline()){
                new ProductsUpdater(mcApi, productsRepository, productListAdapter).execute();
            }
            else{
                Toast updateError = Toast.makeText(this, "Error during update. Check network connection", Toast.LENGTH_LONG);
                updateError.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isNetworkOnline() {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;

    }
}
