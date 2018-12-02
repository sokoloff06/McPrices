package com.example.sokol.mcprices;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.example.sokol.mcprices.cart.CartAdapter;
import com.example.sokol.mcprices.cart.CartHandler;
import com.example.sokol.mcprices.entities.Cart;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Map;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements CartHandler {

    private static final String AF_DEV_KEY = "4UGrDF4vFvPLbHq5bXtCza";
    NavigationView navigationView;
    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private MenuCartPagerAdapter pagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;
    private Cart cart;
    private CartAdapter cartAdapter;
    private DrawerLayout navDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navDrawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped

                        navDrawer.closeDrawers();

                        int itemId = menuItem.getItemId();
                        switch (itemId) {
                            case R.id.nav_sale:
                                Intent intent = new Intent(getApplicationContext(), SaleActivity.class);
                                startActivity(intent);
                                return true;
                        }
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //TODO: understand why the code below is being executed after I snoozed the app (switched to another app for example)

        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
            @Override
            public void onInstallConversionDataLoaded(Map<String, String> map) {
                for (String attrName : map.keySet()) {
                    Log.d(AppsFlyerLib.LOG_TAG, "onInstallConversionDataLoaded attribute: " + attrName + " = " + map.get(attrName));
                }
                Log.d(AppsFlyerLib.LOG_TAG, "AppsflyerID:" + AppsFlyerLib.getInstance().getAppsFlyerUID(getApplicationContext()));
                String deepLink = map.get("af_web_dp");
                boolean is_first_launch = Boolean.parseBoolean(map.get("is_first_launch"));
                if (is_first_launch) {
                    switch (deepLink) {
                        case "http://mcprices.com/sale":
                            Intent intent = new Intent(getApplicationContext(), SaleActivity.class);
                            startActivity(intent);
                    }
                }
            }

            @Override
            public void onInstallConversionFailure(String s) {

            }

            @Override
            public void onAppOpenAttribution(Map<String, String> map) {

            }

            @Override
            public void onAttributionFailure(String s) {

            }
        };
        String senderId = "145246440594"; /* A.K.A Project Number */
        AppsFlyerLib.getInstance().enableUninstallTracking(senderId);
        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionListener, getApplicationContext());
        AppsFlyerLib.getInstance().startTracking(getApplication());

        cart = new Cart();
        cartAdapter = new CartAdapter(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pagerAdapter = new MenuCartPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = findViewById(R.id.container);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs_selector);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_main);
    }


    @Override
    public Cart getCart() {
        return cart;
    }

    @Override
    public void onDataHasChanged() {
        TextView sumTextView = findViewById(R.id.sum_text_view);
        sumTextView.setText(String.valueOf(cart.getSum()) + " \u20BD");
    }

    @Override
    public CartAdapter getCartAdapter() {
        return cartAdapter;
    }
}
