package com.google.firebaseengage;

import com.google.firebaseengage.cart.CartFragment;
import com.google.firebaseengage.catalog.CatalogFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * Created by sokol on 07.03.2017.
 */

public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private static final CharSequence CART_TITLE = "CART";
    private static final CharSequence CATALOG_TITLE = "CATALOG";

    private final CartFragment cart = new CartFragment();
    private final CatalogFragment catalog = new CatalogFragment();

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 1) {
            return CART_TITLE;
        }
        return CATALOG_TITLE;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return cart;
        }
        return catalog;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
