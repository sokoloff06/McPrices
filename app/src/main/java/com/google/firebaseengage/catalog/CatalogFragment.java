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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebaseengage.R;
import com.google.firebaseengage.api.McApi;
import com.google.firebaseengage.api.McApiLocal;
import com.google.firebaseengage.background_tasks.CheckLastUpdateTask;
import com.google.firebaseengage.background_tasks.DownloadProductsTask;
import com.google.firebaseengage.cart.CartHandler;
import com.google.firebaseengage.data.ProductsDbHelper;
import com.google.firebaseengage.data.ProductsRepositoryImpl;
import com.google.firebaseengage.entities.Cart;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.concurrent.CountDownLatch;

public class CatalogFragment extends Fragment implements ProductsDisplayer {

    public static final String BG_COLOR_KEY = "bg_color";
    public static final String PRICE_COLOR_KEY = "bg_price";
    String priceColor;
    CountDownLatch waitingForRc = null;

    CartHandler cartHandler;
    Cart cart;
    McApi mcApi;
    ProductsRepositoryImpl productsRepository;
    RecyclerView productListRecyclerView;
    ProductListAdapter productListAdapter;
    ProgressBar pbUpdating;
    TextView itemPrice;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing objects for data manipulations
//        mcApi = new McApiImpl();
        mcApi = new McApiLocal(getContext().getApplicationContext());
        ProductsDbHelper dbHelper = new ProductsDbHelper(getContext());
        productsRepository = new ProductsRepositoryImpl(dbHelper.getWritableDatabase());
        productListAdapter = new ProductListAdapter(productsRepository, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_catalog, container, false);
        pbUpdating = rootView.findViewById(R.id.pb_updating);
        swipeRefreshLayout = rootView.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this::onSwipeUpdate);
//        itemPrice = rootView.findViewById(R.id.item_price);

        productListRecyclerView = rootView.findViewById(R.id.rv_product_list);
        //TODO: count number of column regarding to screen width
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        productListRecyclerView.setLayoutManager(layoutManager);
        //TODO: BUG! Sometimes No adapter attached; skipping layout
        productListRecyclerView.setAdapter(productListAdapter);

        /*setHasOptionsMenu(true);*/

        if (isNetworkOnline()) {
            loadProducts();
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

    public void onSwipeUpdate() {
        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(task -> {
                    Log.d(
                            "ENGAGE-DEBUG",
                            "Remote Control fetched and active"
                    );
                    // RC Demo 3: Background color on refresh (A/B testing)
                    String color = remoteConfig.getString(BG_COLOR_KEY);
                    this.getView().setBackgroundColor(Color.parseColor(color));
                    Log.d(LOG_TAG, "Applied bg_color of " + color + " from Remote Config");
                    // RC Demo 2: Updating price tag background color
                    priceColor = remoteConfig.getString(PRICE_COLOR_KEY);
                    loadProducts();
                })
                .addOnFailureListener(exception -> {
                            Log.d(
                                    "ENGAGE-DEBUG",
                                    "Remote Control FAILED to be fetched: " + exception.getLocalizedMessage());
                            loadProducts();
                        }
                );
    }

    private void loadProducts() {
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
        new DownloadProductsTask(getContext().getApplicationContext(), mcApi, productsRepository, filesDir, this).execute();
    }

    @Override
    public void load() {
        productListAdapter.loadProducts(priceColor);
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
