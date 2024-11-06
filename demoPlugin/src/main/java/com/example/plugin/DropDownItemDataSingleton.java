package com.example.plugin;

import com.example.plugin.beans.DropdownItem;
import com.example.plugin.beans.ProductBean;

import java.util.ArrayList;
import java.util.List;

public class DropDownItemDataSingleton {

    private DropDownItemDataSingleton() {
    }

    private final static DropDownItemDataSingleton instance = new DropDownItemDataSingleton();
    private static List<DropdownItem> items = null;

    public static DropDownItemDataSingleton getInstance() {
        return instance;
    }

    public List<DropdownItem> getItems(ArrayList<ProductBean> productList) {
        if (items == null || hasNoRunningItem()) {
            items = new ArrayList<>();
            //items.add(new DropdownItem("同步", false));
            for (ProductBean bean : productList) {
                if (!bean.getName().equals("default")) {
                    items.add(new DropdownItem(bean.getName(), false));
                }
            }
        }
        return items;
    }

    public boolean hasNoRunningItem() {
        if (items == null) {
            return false;
        }
        return items.stream().noneMatch(DropdownItem::isRunning);
    }

}