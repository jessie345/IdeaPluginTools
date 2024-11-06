package com.example.plugin.abandon;

import com.example.plugin.MyPluginUtil;
import com.example.plugin.beans.ProductBean;
import com.example.plugin.interfaces.OnBatExecutedListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBList;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyToolWindow implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Loading product list") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ArrayList<ProductBean> productList = MyPluginUtil.getProductList(project);
                if (productList == null || productList.isEmpty()) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, "产品列表解析失败，请检查您的配置文件", "异常", JOptionPane.ERROR_MESSAGE);
                    });
                } else {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        buildPanel(project, toolWindow, productList);
                    });
                }
            }
        });
    }

    private static void buildPanel(@NotNull Project project, ToolWindow toolWindow, ArrayList<ProductBean> productList) {
        // Create a list of items to display
        List<String> items = new ArrayList<>();
        for (ProductBean bean : productList) {
            items.add(bean.getName());
        }
        // Create a JList from the items
        JList<String> list = new JBList<>(items.toArray(new String[0]));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // 获取当前选中的项
                    ListSelectionModel selectionModel = list.getSelectionModel();
                    int selectedIndex = selectionModel.getMinSelectionIndex();
                    if (selectedIndex != -1) {
                        onProductSelected(project, items, list);
                    }
                }
            }
        });
        // Create a JPanel to hold the JList
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        // Create a content for the tool window
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(panel, "产品列表", false);
        // Add the content to the tool window
        toolWindow.getContentManager().addContent(content);
    }

    private static void onProductSelected(@NotNull Project project, List<String> items, JList<String> list) {

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Packaging " + items.get(list.getSelectedIndex())) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ApplicationManager.getApplication().invokeLater(list::updateUI);
                MyPluginUtil.executeBat(items.get(list.getSelectedIndex()), project, new OnBatExecutedListener() {
                    @Override
                    public void onBatExecuted(int exitCode, String exitMsg) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            MyPluginUtil.showAlert(project, exitMsg);
                        });
                    }
                });
            }
        });
    }
}