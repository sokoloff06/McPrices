package com.example.sokol.mcprices.data;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sokol.mcprices.R;
import com.example.sokol.mcprices.entities.Product;

import java.util.List;

/**
 * Created by sokol on 20.02.2017.
 */

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListAdapterViewHolder> {
    private final Context context;


    //TODO: sorting by price/category/name
    //TODO: add categories

    private List<Product> products;

    public ProductListAdapter(Context context) {
        this.context = context;
    }

    public class ProductListAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView textView;
        public final TextView textPrice;
        public final ImageView imageView;

        public ProductListAdapterViewHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.item_text);
            this.textPrice = (TextView) itemView.findViewById(R.id.item_price);
            this.imageView = (ImageView) itemView.findViewById(R.id.item_image);
        }
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public ProductListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);
        return new ProductListAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductListAdapterViewHolder holder, int position) {
        Product p = products.get(position);
        holder.textView.setText(p.getName());
        holder.textPrice.setText(String.valueOf(p.getPrice()) + "\u20BD");
        //holder.imageView.setImageURI(Uri.parse(p.getPic()));
        //holder.imageView.setImageResource(R.drawable.big_mac_big);

        String filesdir = context.getFilesDir().getAbsolutePath();
        String filename = filesdir + "/" + p.getName() + ".png";
        holder.imageView.setImageURI(Uri.parse(filename));

/*        try {
            String filename = p.getName() + ".png";
            FileInputStream input = context.openFileInput(filename);
            int bytesRead = -1;
            byte[] buffer = new byte[4096];

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            while ((bytesRead = input.read(buffer)) != -1){
                output.write(buffer, 0, bytesRead);
            }
            byte[] data = output.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            holder.imageView.setImageBitmap(bitmap);


        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public int getItemCount() {
        if (products == null) {
            return 0;
        } else {
            return products.size();
        }
    }
}
