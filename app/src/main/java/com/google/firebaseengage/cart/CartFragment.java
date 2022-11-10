package com.google.firebaseengage.cart;


import static com.google.firebaseengage.MainActivity.LOG_TAG;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebaseengage.R;
import com.google.firebaseengage.entities.Cart;

/**
 * Created by sokol on 07.03.2017.
 */

public class CartFragment extends Fragment {

    public static final String PURCHASE_BTN_COLOR = "btn_buy_color";
    RecyclerView cartRecyclerView;
    TextView sumTextView;
    CartAdapter cartAdapter;
    CartHandler cartHandler;
    Cart cart;
    Button sendPurchaseButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartAdapter = cartHandler.getCartAdapter();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            cartHandler = (CartHandler) context;
            cart = cartHandler.getCart();
        } catch (ClassCastException e) {
            throw new ClassCastException(context +
                    " must implement CartHandler interface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        sumTextView = rootView.findViewById(R.id.sum_text_view);
        cartRecyclerView = rootView.findViewById(R.id.rv_cart);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        cartRecyclerView.setLayoutManager(layoutManager);
        cartRecyclerView.setAdapter(cartAdapter);
        sendPurchaseButton = rootView.findViewById(R.id.sendPurchaseButton);
        // RC Demo 4: Setting default/last-fetched purchase button color - personalization
        String color = FirebaseRemoteConfig.getInstance().getString(PURCHASE_BTN_COLOR);
        sendPurchaseButton.setBackgroundColor(Color.parseColor(color));
        Log.d("ENGAGE-DEBUG", "Applied btn_buy_color of " + color + " from Remote Config");
        sendPurchaseButton.setOnClickListener(view -> {
            Context ctx = getContext();
            if (ctx != null) {
                Bundle eventParams = new Bundle();
                double sum = cart.getSum();
                EditText editText = rootView.findViewById(R.id.transaction_id_field);
                eventParams.putString(FirebaseAnalytics.Param.CURRENCY, "EUR");
                eventParams.putDouble(FirebaseAnalytics.Param.VALUE, sum);
                eventParams.putString(FirebaseAnalytics.Param.TRANSACTION_ID, editText.getText().toString());
                Log.d(LOG_TAG, "Logging event");
                FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.PURCHASE, eventParams);
                Toast.makeText(getContext(), eventParams.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    public void onSwipeUpdate() {
        // RC Demo 4: Purchase button color - personalization
        String color = FirebaseRemoteConfig.getInstance().getString(PURCHASE_BTN_COLOR);
        sendPurchaseButton.setBackgroundColor(Color.parseColor(color));
        Log.d("ENGAGE-DEBUG", "Applied btn_buy_color of " + color + " from Remote Config");
    }
}
