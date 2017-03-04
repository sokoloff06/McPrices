package com.example.sokol.mcprices;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.sokol.mcprices.entities.Product;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by sokol on 04.03.2017.
 */

public class SavePicsTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private List<Product> products;

    public SavePicsTask(Context context, List<Product> products){

        this.context = context;
        this.products = products;
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (Product p : products){
            try {
                //Output
                String filename = p.getName() + ".png";
                FileOutputStream output = context.openFileOutput(filename, Context.MODE_PRIVATE);

                //Input
                URL url = new URL("https://mcdonalds" + p.getPic());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream input = connection.getInputStream();

                //Buffer
                int bytesRead = -1;
                byte[] buffer = new byte[4096];
                while((bytesRead = input.read(buffer)) != -1){
                    output.write(buffer, 0, bytesRead);
                }

                //Closing references
                p.setPic(filename);
                input.close();
                output.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
