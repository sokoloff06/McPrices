package com.google.firebaseengage.menu;

/**
 * Created by sokol on 08.03.2017.
 */

public interface ProductsDisplayer {
    void load();

    void loadOrDownload(boolean isOutOfDate);

    void loadError();

    void onItemClicked(int position);
}
