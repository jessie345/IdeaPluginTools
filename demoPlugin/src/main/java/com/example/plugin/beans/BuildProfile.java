package com.example.plugin.beans;

import java.util.ArrayList;

public class BuildProfile {

    private AppBean app;

    public static class AppBean {
        ArrayList<ProductBean> products;

        public ArrayList<ProductBean> getProducts() {
            return products;
        }
    }

    public AppBean getApp() {
        return app;
    }

    public void setApp(AppBean app) {
        this.app = app;
    }
}
