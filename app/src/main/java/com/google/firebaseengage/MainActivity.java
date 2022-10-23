package com.google.firebaseengage;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebaseengage.cart.CartAdapter;
import com.google.firebaseengage.cart.CartHandler;
import com.google.firebaseengage.entities.Cart;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements CartHandler {

    public static final String BG_COLOR_KEY = "bg_color";
    NavigationView navigationView;
    private Cart cart;
    private CartAdapter cartAdapter;
    private DrawerLayout navDrawer;
    ViewPager viewPager;
    FirebaseRemoteConfig remoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Firebase Remote Config
        setUpAndApplyRemoteConfig();
        navDrawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    // close drawer when item is tapped
                    navDrawer.closeDrawers();
                    int itemId = menuItem.getItemId();
                    if (itemId == R.id.nav_sale) {
                        return true;
                    }
                    // Add code here to update the UI based on the item selected
                    // For example, swap UI fragments here
                    return true;
                });

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        cart = new Cart();
        cartAdapter = new CartAdapter(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        MenuCartPagerAdapter pagerAdapter = new MenuCartPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        /**
         * The {@link ViewPager} that will host the section contents.
         */
        viewPager = findViewById(R.id.container);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs_selector);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setUpAndApplyRemoteConfig() {
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(5)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(task -> {
                    Log.d("ENGAGE-DEBUG", "Remote Control fetched and active");
                })
                .addOnFailureListener(exception -> Log.d("ENGAGE-DEBUG",
                        "Remote Control FAILED to be fetched: " + exception.getLocalizedMessage()));
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
        String color = remoteConfig.getString(BG_COLOR_KEY);
        Log.d("ENGAGE-DEBUG", "Using bg_color of " + color + " from Remote Config");
        viewPager.setBackgroundColor(Color.parseColor(color));
    }


    @Override
    public Cart getCart() {
        return cart;
    }

    @Override
    public void onDataHasChanged() {
        TextView sumTextView = findViewById(R.id.sum_text_view);
        sumTextView.setText(cart.getSum() + "€");
    }

    @Override
    public CartAdapter getCartAdapter() {
        return cartAdapter;
    }
}
