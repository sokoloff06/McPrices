package com.google.firebaseengage.catalog;

import static com.google.firebaseengage.MainActivity.LOG_TAG;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebaseengage.R;
import com.google.firebaseengage.api.ProductsApi;
import com.google.firebaseengage.api.ProductsApiLocal;
import com.google.firebaseengage.background_tasks.CheckLastUpdateTask;
import com.google.firebaseengage.background_tasks.DownloadProductsTask;
import com.google.firebaseengage.cart.CartHandler;
import com.google.firebaseengage.data.ProductsDbHelper;
import com.google.firebaseengage.data.ProductsRepositoryImpl;
import com.google.firebaseengage.entities.Cart;

public class CatalogFragment extends Fragment implements ProductsDisplayer {

    public static final String KEY_BG_COLOR = "bg_color";
    public static final String KEY_PRICE_TAG_COLOR = "price_tag";
    String priceColor;

    CartHandler cartHandler;
    Cart cart;
    ProductsApi productsApi;
    ProductsRepositoryImpl productsRepository;
    RecyclerView productListRecyclerView;
    ProductListAdapter productListAdapter;
    FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initializing objects for data manipulations
        productsApi = new ProductsApiLocal(getContext().getApplicationContext());
        ProductsDbHelper dbHelper = new ProductsDbHelper(getContext());
        productsRepository = new ProductsRepositoryImpl(dbHelper.getWritableDatabase());
        productListAdapter = new ProductListAdapter(productsRepository, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_catalog, container, false);
        productListRecyclerView = rootView.findViewById(R.id.rv_product_list);
        //TODO: count number of column regarding to screen width
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        productListRecyclerView.setLayoutManager(layoutManager);
        //TODO: BUG! Sometimes No adapter attached; skipping layout
        productListRecyclerView.setAdapter(productListAdapter);
        String color = remoteConfig.getString(KEY_BG_COLOR);
        rootView.setBackgroundColor(Color.parseColor(color));
        Log.d(LOG_TAG, "Applied bg_color of " + color + " from Remote Config");
        if (isNetworkOnline()) {
            loadProducts();
        } else {
            loadError();
        }
        return rootView;
    }

    private void setDataVisible() {
        productListRecyclerView.setVisibility(View.VISIBLE);
    }


    void setLoadingVisible() {
        productListRecyclerView.setVisibility(View.INVISIBLE);
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
            throw new ClassCastException(context
                    + " must implement CartHandler interface");
        }
    }


    public void onSwipeUpdate() {
        String color = remoteConfig.getString(KEY_BG_COLOR);
        this.getView().setBackgroundColor(Color.parseColor(color));
        Log.d(LOG_TAG, "Applied bg_color of " + color + " from Remote Config");
        loadProducts();
    }

    private void loadProducts() {
        setLoadingVisible();
        if (isNetworkOnline()) {
            new CheckLastUpdateTask(productsApi, productsRepository.getTimestamp(), this).execute();
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
        new DownloadProductsTask(getContext().getApplicationContext(), productsApi, productsRepository, filesDir, this).execute();
    }

    @Override
    public void load() {
        // RC Demo 2: Applying price tag color
        priceColor = remoteConfig.getString(KEY_PRICE_TAG_COLOR);
//        Map allParams = remoteConfig.getAll();
        productListAdapter.loadProducts(priceColor);
        setDataVisible();
    }

    @Override
    public void loadError() {
        setDataVisible();
        Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClicked(int position) {
        cart.add(productListAdapter.getProduct(position));
        cartHandler.getCartAdapter().loadData();
    }
}
