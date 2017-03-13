package com.example.sokol.mcprices.menu;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sokol.mcprices.R;
import com.example.sokol.mcprices.data.ProductsRepository;
import com.example.sokol.mcprices.entities.Product;

import java.util.List;

/**
 * Created by sokol on 20.02.2017.
 */

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListAdapterViewHolder> {

    //TODO: sorting by price/category/name
    private List<Product> products;
    private ProductsRepository repository;
    private ProductsDisplayer productsDisplayer;

    //TODO: add categories

    public ProductListAdapter(ProductsRepository repository, ProductsDisplayer productsDisplayer) {
        this.repository = repository;
        this.productsDisplayer = productsDisplayer;
    }

    public void loadProducts() {
        products = repository.getProducts();
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
        Product p = products.get(position);
        holder.textView.setText(p.getName());
        holder.textPrice.setText(String.valueOf(p.getPrice()) + "\u20BD");
        holder.imageView.setImageURI(Uri.parse(p.getPic()));
    }

    @Override
    public int getItemCount() {
        if (products == null) {
            return 0;
        } else {
            return products.size();
        }
    }

    public Product getProduct(int position) {
        return products.get(position);
    }

    class ProductListAdapterViewHolder extends RecyclerView.ViewHolder {

        final TextView textView;
        final TextView textPrice;
        final ImageView imageView;

        ProductListAdapterViewHolder(final View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.item_text);
            this.textPrice = (TextView) itemView.findViewById(R.id.item_price);
            this.imageView = (ImageView) itemView.findViewById(R.id.item_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    productsDisplayer.onItemClicked(position);
                }
            });
        }
    }
}