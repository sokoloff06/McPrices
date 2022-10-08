package com.google.firebaseengage.cart;

import com.google.firebaseengage.entities.Cart;

/**
 * Created by sokol on 08.03.2017.
 */
public interface CartHandler {

    Cart getCart();

    void onDataHasChanged();

    CartAdapter getCartAdapter();
}
