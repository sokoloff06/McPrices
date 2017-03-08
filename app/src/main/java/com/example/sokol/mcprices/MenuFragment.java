package com.example.sokol.mcprices;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sokol.mcprices.api.McApi;
import com.example.sokol.mcprices.api.McApiImpl;
import com.example.sokol.mcprices.data.ProductListAdapter;
import com.example.sokol.mcprices.data.ProductsDbHelper;
import com.example.sokol.mcprices.data.ProductsRepositoryImpl;

public class MenuFragment extends Fragment implements ProductsLoader, ProductsDownLoader {

    McApi mcApi;
    ProductsRepositoryImpl productsRepository;
    RecyclerView productListRecyclerView;
    ProductListAdapter productListAdapter;
    ProgressBar pbUpdating;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing objects for data manipulations
        mcApi = new McApiImpl();
        ProductsDbHelper dbHelper = new ProductsDbHelper(getContext());
        productsRepository = new ProductsRepositoryImpl(dbHelper.getWritableDatabase());
        if (isNetworkOnline()) {
            startUpdate();
        } else {
            loadError();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);

        pbUpdating = (ProgressBar) rootView.findViewById(R.id.pb_updating);

        productListRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_product_list);
        productListAdapter = new ProductListAdapter(getContext(), productsRepository);

        //TODO: count number of column regarding to screen width
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        productListRecyclerView.setLayoutManager(layoutManager);
        productListRecyclerView.setAdapter(productListAdapter);


        return rootView;
    }

    @Override
    public void load() {
        for (String f : getContext().fileList()) {
            System.out.println(f);
        }
        productListAdapter.loadProducts();
    }

    @Override
    public void loadError() {
        Toast.makeText(getContext(), "Error during downloading data! Check network connection", Toast.LENGTH_LONG).show();
    }


    private void setDataVisible() {
        pbUpdating.setVisibility(View.INVISIBLE);
        productListRecyclerView.setVisibility(View.VISIBLE);
    }

    void setLoadingVisible() {
        productListRecyclerView.setVisibility(View.INVISIBLE);
        pbUpdating.setVisibility(View.VISIBLE);
    }

    public boolean isNetworkOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == (R.id.acton_refresh)) {
            startUpdate();
        }
        return super.onOptionsItemSelected(item);
    }

    void startUpdate() {
        new CheckLastUpdateTask(mcApi, productsRepository.getTimestamp(), this).execute();
    }

    public void loadOrDownload(boolean isOutOfDate) {
        if (isOutOfDate) {
            download();
        } else {
            load();
        }
    }

    private void download() {
        String filesDir = getContext().getFilesDir().getAbsolutePath();
        new DownloadProductsTask(mcApi, productsRepository, filesDir, this).execute();
    }
}
