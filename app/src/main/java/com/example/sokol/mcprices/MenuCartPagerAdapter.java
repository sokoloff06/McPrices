package com.example.sokol.mcprices;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by sokol on 07.03.2017.
 */

class MenuCartPagerAdapter extends FragmentPagerAdapter {

    private MenuFragment menuFragment;
    private CartFragment cartFragment;

    MenuCartPagerAdapter(FragmentManager fm) {
        super(fm);
        menuFragment = new MenuFragment();
        cartFragment = new CartFragment();
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return cartFragment;
            default:
                return menuFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
