package com.example.sokol.mcprices.menu;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sokol.mcprices.R;
import com.example.sokol.mcprices.api.McApi;
import com.example.sokol.mcprices.api.McApiImpl;
import com.example.sokol.mcprices.background_tasks.CheckLastUpdateTask;
import com.example.sokol.mcprices.background_tasks.DownloadProductsTask;
import com.example.sokol.mcprices.cart.CartHandler;
import com.example.sokol.mcprices.data.ProductsDbHelper;
import com.example.sokol.mcprices.data.ProductsRepositoryImpl;
import com.example.sokol.mcprices.entities.Cart;

public class MenuFragment extends Fragment implements ProductsDisplayer {

    CartHandler cartHandler;
    Cart cart;
    McApi mcApi;
    ProductsRepositoryImpl productsRepository;
    RecyclerView productListRecyclerView;
    ProductListAdapter productListAdapter;
    ProgressBar pbUpdating;
    TextView itemPrice;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing objects for data manipulations
        mcApi = new McApiImpl();
        ProductsDbHelper dbHelper = new ProductsDbHelper(getContext());
        productsRepository = new ProductsRepositoryImpl(dbHelper.getWritableDatabase());
        productListAdapter = new ProductListAdapter(productsRepository, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);

        pbUpdating = (ProgressBar) rootView.findViewById(R.id.pb_updating);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startUpdate();
            }
        });
        itemPrice = (TextView) rootView.findViewById(R.id.item_price);

        productListRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_product_list);
        //TODO: count number of column regarding to screen width
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        productListRecyclerView.setLayoutManager(layoutManager);
        //TODO: BUG! Sometimes No adapter attached; skipping layout
        productListRecyclerView.setAdapter(productListAdapter);

        /*setHasOptionsMenu(true);*/

        if (isNetworkOnline()) {
            startUpdate();
        } else {
            loadError();
        }

        return rootView;
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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            cartHandler = (CartHandler) context;
            cart = cartHandler.getCart();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement CartHandler interface");
        }
    }

/*    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }*/

/*        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == (R.id.acton_refresh)) {
            startUpdate();
        }
        return super.onOptionsItemSelected(item);
    }*/

    public void startUpdate() {
        setLoadingVisible();
        if (isNetworkOnline()) {
            new CheckLastUpdateTask(mcApi, productsRepository.getTimestamp(), this).execute();
        } else {
            loadError();
        }
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

    @Override
    public void load() {
        productListAdapter.loadProducts();
        setDataVisible();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void loadError() {
        setDataVisible();
        Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_LONG).show();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClicked(int position) {
        cart.add(productListAdapter.getProduct(position));
        cartHandler.getCartAdapter().loadData();
    }
}
