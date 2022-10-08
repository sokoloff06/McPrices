package com.google.firebaseengage;

import com.google.firebaseengage.cart.CartFragment;
import com.google.firebaseengage.menu.MenuFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * Created by sokol on 07.03.2017.
 */

public class MenuCartPagerAdapter extends FragmentStatePagerAdapter {

    private static final CharSequence CART_TITLE = "CART";
    private static final CharSequence MENU_TITLE = "MENU";

    public MenuCartPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 1) {
            return CART_TITLE;
        }
        return MENU_TITLE;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new CartFragment();
        }
        return new MenuFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }
}
