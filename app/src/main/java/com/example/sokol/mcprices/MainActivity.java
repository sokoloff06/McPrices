package com.example.sokol.mcprices;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.sokol.mcprices.cart.CartAdapter;
import com.example.sokol.mcprices.cart.CartHandler;
import com.example.sokol.mcprices.entities.Cart;

public class MainActivity extends AppCompatActivity implements CartHandler {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private MenuCartPagerAdapter pagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;
    private Cart cart;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cart = new Cart();
        cartAdapter = new CartAdapter(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pagerAdapter = new MenuCartPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_selector);
        tabLayout.setupWithViewPager(viewPager);
    }


/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == (R.id.acton_refresh)) {
            MenuFragment mf = (MenuFragment) pagerAdapter.getItem(0);
            mf.startUpdate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/


    @Override
    public Cart getCart() {
        return cart;
    }

    @Override
    public void onDataHasChanged() {
        TextView sumTextView = (TextView) findViewById(R.id.sum_text_view);
        sumTextView.setText(String.valueOf(cart.getSum()) + " \u20BD");
    }

    @Override
    public CartAdapter getCartAdapter() {
        return cartAdapter;
    }
}
