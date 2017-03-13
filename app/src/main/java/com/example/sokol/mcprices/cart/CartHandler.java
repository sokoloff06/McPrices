package com.example.sokol.mcprices.cart;

import com.example.sokol.mcprices.entities.Cart;

/**
 * Created by sokol on 08.03.2017.
 */
public interface CartHandler {

    Cart getCart();

    void onDataHasChanged();

    CartAdapter getCartAdapter();
}
