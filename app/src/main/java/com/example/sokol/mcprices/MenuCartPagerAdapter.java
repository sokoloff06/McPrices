package com.example.sokol.mcprices;

import com.example.sokol.mcprices.cart.CartFragment;
import com.example.sokol.mcprices.menu.MenuFragment;

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
        switch (position) {
            case 1:
                return CART_TITLE;
            default:
                return MENU_TITLE;
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MenuFragment();
            case 1:
                return new CartFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
