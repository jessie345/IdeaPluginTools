package com.example.plugin.abandon;

import com.example.plugin.MyPluginUtil;
import com.example.plugin.beans.BuildProfile;
import com.example.plugin.beans.ProductBean;
import com.google.gson.Gson;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ReadProductAction extends AnAction {

    List<ProductBean> yourClassList;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String path = MyPluginUtil.selectFilePath(Objects.requireNonNull(getEventProject(e)));
        String jsonString = MyPluginUtil.readStringFromFile(new File(path));
        Gson gson = new Gson();
        try {
            yourClassList = gson.fromJson(jsonString, BuildProfile.class).getApp().getProducts();
            if (yourClassList != null) {
                String[] namesArray = yourClassList.stream()
                        .map(ProductBean::getName) // 将每个Person对象映射到它的名字
                        .toArray(String[]::new);
                MyPluginUtil.showAlert(Objects.requireNonNull(e.getProject()), Arrays.toString(namesArray));
            } else {
                MyPluginUtil.showAlert(Objects.requireNonNull(e.getProject()), "产品列表为空");
            }
        } catch (Exception ex) {
            MyPluginUtil.showAlert(Objects.requireNonNull(e.getProject()), "json解析失败");
        }
    }
}
