package com.google.firebaseengage.menu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebaseengage.R;
import com.google.firebaseengage.data.ProductsRepository;
import com.google.firebaseengage.entities.Product;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

/**
 * Created by sokol on 20.02.2017.
 */

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListAdapterViewHolder> {

    public static final String PRICE_COLOR_KEY = "bg_price";
    //TODO: sorting by price/category/name
    private ProductsRepository repository;
    private ProductsDisplayer productsDisplayer;
    private SortedList<Product> productSortedListByName;
    private String priceColor;

    //TODO: add categories

    public ProductListAdapter(ProductsRepository repository, ProductsDisplayer productsDisplayer) {
        this.repository = repository;
        this.productsDisplayer = productsDisplayer;
        this.productSortedListByName = new SortedList<>(Product.class, new SortedList.Callback<Product>() {
            @Override
            public void onInserted(int position, int count) {

            }

            @Override
            public void onRemoved(int position, int count) {

            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {

            }

            @Override
            public int compare(Product o1, Product o2) {
                return o1.getName().compareTo(o2.getName());
            }

            @Override
            public void onChanged(int position, int count) {

            }

            @Override
            public boolean areContentsTheSame(Product oldItem, Product newItem) {
                return (oldItem.getName().equals(newItem.getName()) &&
                        oldItem.getId() == newItem.getId() &&
                        oldItem.getPic().equals(newItem.getPic()) &&
                        oldItem.getPrice() == newItem.getPrice()
                );
            }

            @Override
            public boolean areItemsTheSame(Product item1, Product item2) {
                return areContentsTheSame(item1, item2);
            }
        });
    }

    public void loadProducts() {
        priceColor = FirebaseRemoteConfig.getInstance().getString(PRICE_COLOR_KEY);
        Log.d("ENGAGE-DEBUG", "Using bg_price of " + priceColor + " from Remote Config");
        List<Product> products = repository.getProducts();
        productSortedListByName.addAll(products);
        notifyDataSetChanged();
    }

    @Override
    public ProductListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.menu_item, parent, false);
        return new ProductListAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductListAdapterViewHolder holder, int position) {
        GradientDrawable dr = (GradientDrawable) holder.textPrice.getBackground();
        dr.setColor(Color.parseColor(priceColor));

        Product p = productSortedListByName.get(position);
        holder.textView.setText(p.getName());
        holder.textPrice.setText(String.valueOf(p.getPrice()));
        holder.imageView.setImageURI(Uri.parse(p.getPic()));
    }

    @Override
    public int getItemCount() {
        int size = productSortedListByName.size();
        if (size == 0) {
            return 0;
        } else {
            return size;
        }
    }

    public Product getProduct(int position) {
        return productSortedListByName.get(position);
    }

    class ProductListAdapterViewHolder extends RecyclerView.ViewHolder {

        final TextView textView;
        final TextView textPrice;
        final ImageView imageView;

        ProductListAdapterViewHolder(final View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.item_text);
            this.textPrice = itemView.findViewById(R.id.item_price);
            this.imageView = itemView.findViewById(R.id.item_image);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                productsDisplayer.onItemClicked(position);
            });
        }
    }
}
