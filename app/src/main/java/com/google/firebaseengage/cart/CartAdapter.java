package com.google.firebaseengage.cart;

import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebaseengage.R;
import com.google.firebaseengage.entities.Cart;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by sokol on 08.03.2017.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartItemViewHolder> {
    private final Cart cart;

    //TODO: Swipe to delete whole ProductRecord from cart


    private final CartHandler cartHandler;
    private List<Cart.ProductRecord> records = new ArrayList<>();

    public CartAdapter(CartHandler cartHandler) {
        this.cartHandler = cartHandler;
        this.cart = cartHandler.getCart();
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartItemViewHolder holder, int position) {
        holder.countEditText.setText(String.valueOf(records.get(position).getCount()));
        holder.nameTextView.setText(records.get(position).getName());
        holder.pictureImageView.setImageURI(Uri.parse(records.get(position).getPic()));
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void loadData() {
        records = new ArrayList<>(cart.getProducts().values());
        notifyDataSetChanged();
        cartHandler.onDataHasChanged();
    }

    private Cart.ProductRecord getProductRecord(int position) {
        return records.get(position);
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {

        final ImageView pictureImageView;
        final TextView nameTextView;
        final EditText countEditText;
        final Button buttonRemove;
        final Button buttonAdd;


        public CartItemViewHolder(View itemView) {
            super(itemView);
            this.pictureImageView = itemView.findViewById(R.id.cart_item_picture_image_view);
            this.nameTextView = itemView.findViewById(R.id.cart_item_name_text_view);
            this.countEditText = itemView.findViewById(R.id.cart_item_count_edit_text);
            this.buttonRemove = itemView.findViewById(R.id.button_remove);
            this.buttonAdd = itemView.findViewById(R.id.button_add);

            buttonAdd.setOnClickListener(v -> {
                cart.add(getProductRecord(getAdapterPosition()));
                loadData();
            });

            buttonRemove.setOnClickListener(v -> {
                cart.remove(getProductRecord(getAdapterPosition()));
                loadData();
            });

            countEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    //Nothing to do here
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //And here
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (countEditText.hasFocus()) {
                        Cart.ProductRecord productRecord = getProductRecord(getAdapterPosition());
                        if (s.length() != 0) {
                            productRecord.setCount(Integer.parseInt(String.valueOf(s)));
                            //TODO Problem here. Selection resets to the beginning
                            countEditText.setSelection(s.length()); //doesn't help somewhy
                            cart.set(productRecord);
                            loadData();
                        }
                    }
                }
            });
        }
    }
}
