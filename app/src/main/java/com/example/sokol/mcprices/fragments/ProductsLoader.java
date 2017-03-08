package com.example.sokol.mcprices.fragments;

/**
 * Created by sokol on 08.03.2017.
 */

public interface ProductsLoader {
    void load();

    void loadOrDownload(boolean isOutOfDate);
    void loadError();
}
