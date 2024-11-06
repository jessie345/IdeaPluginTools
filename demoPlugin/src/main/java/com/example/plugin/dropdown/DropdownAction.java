package com.example.plugin.dropdown;

import com.example.plugin.MyPluginUtil;
import com.example.plugin.beans.ProductBean;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Objects;

public class DropdownAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Component invoker = e.getInputEvent() instanceof MouseEvent ? e.getInputEvent().getComponent() : null;
        if (invoker == null) {
            MyPluginUtil.showAlert(Objects.requireNonNull(e.getProject()), "请点击工具栏按钮并在弹产品列表中进行选择");
            return;
        }

        //后台任务读取产品配置文件
        ProgressManager.getInstance().run(new Task.Backgroundable(e.getProject(), "Loading product list") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ArrayList<ProductBean> productList = MyPluginUtil.getProductList(e.getProject());
                if (productList == null || productList.isEmpty()) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        MyPluginUtil.showAlert(Objects.requireNonNull(e.getProject()), "产品列表解析失败，请检查您的配置文件");
                    });
                } else {
                    //产品配置文件读取成功切回前台显示下拉列表框
                    ApplicationManager.getApplication().invokeLater(() -> {
                        DropdownPopup popup = new DropdownPopup();
                        popup.showPopup(invoker, e, productList);
                    });
                }
            }
        });
    }
}