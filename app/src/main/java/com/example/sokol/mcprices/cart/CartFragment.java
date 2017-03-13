package com.example.sokol.mcprices.cart;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sokol.mcprices.R;
import com.example.sokol.mcprices.entities.Cart;

/**
 * Created by sokol on 07.03.2017.
 */

public class CartFragment extends Fragment {

    RecyclerView cartRecyclerView;
    TextView sumTextView;
    CartAdapter cartAdapter;
    CartHandler cartHandler;
    Cart cart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cartAdapter = cartHandler.getCartAdapter();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            cartHandler = (CartHandler) context;
            cart = cartHandler.getCart();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement CartHandler interface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        sumTextView = (TextView) rootView.findViewById(R.id.sum_text_view);
        cartRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_cart);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        cartRecyclerView.setLayoutManager(layoutManager);
        cartRecyclerView.setAdapter(cartAdapter);

        return rootView;
    }
}
