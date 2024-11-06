package com.example.plugin.dropdown;

import com.example.plugin.Constant;
import com.example.plugin.DropDownItemDataSingleton;
import com.example.plugin.MyPluginUtil;
import com.example.plugin.beans.DropdownItem;
import com.example.plugin.beans.ProductBean;
import com.example.plugin.interfaces.OnBatExecutedListener;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class DropdownPopup {

    public void showPopup(Component invoker, AnActionEvent actionEvent, ArrayList<ProductBean> productList) {
        JBPopup popup = JBPopupFactory.getInstance().createPopupChooserBuilder(
                        DropDownItemDataSingleton.getInstance().getItems(productList)
                )
                .setMovable(false)
                .setRenderer(new DropdownItemRenderer())
                .setItemChosenCallback(selectedItem -> {
                    if (selectedItem != null && DropDownItemDataSingleton.getInstance().hasNoRunningItem()) {
                        // 处理选中项
                        System.out.println("Selected: " + selectedItem.getText());
                        selectedItem.setRunning(true);
                        ProgressManager.getInstance().run(new Task.Backgroundable(actionEvent.getProject(), "Packaging " + selectedItem.getText()) {
                            @Override
                            public void run(@NotNull ProgressIndicator indicator) {

                                String projectPath = Objects.requireNonNull(actionEvent.getProject()).getBasePath();
                                if (projectPath == null) {
                                    return;
                                }
                                projectPath = Constant.ConvertPathForTestMode(projectPath);
                                String batCommand = "";
                                if (selectedItem.getCommandType() == Constant.COMMAND_TYPE_SYNC) {
                                    batCommand = projectPath + "\\test_hvigorw.bat"
                                            //项目所在盘符
                                            + " " + (projectPath.length() >= 2 ? projectPath.substring(0, 2) : "")
                                            //项目根路径
                                            + " " + projectPath
                                            //产品名称参数
                                            + " " + selectedItem.getText();
                                } else if (selectedItem.getCommandType() == Constant.COMMAND_TYPE_BUILD) {
                                    batCommand = projectPath + "\\test_hvigorw.bat"
                                            //项目所在盘符
                                            + " " + (projectPath.length() >= 2 ? projectPath.substring(0, 2) : "")
                                            //项目根路径
                                            + " " + projectPath
                                            //产品名称参数
                                            + " " + selectedItem.getText();
                                }
                                MyPluginUtil.executeBat(batCommand, new OnBatExecutedListener() {
                                    @Override
                                    public void onBatExecuted(int exitCode, String exitMsg) {
                                        selectedItem.setRunning(false);
                                        ApplicationManager.getApplication().invokeLater(() -> {
                                            if (exitCode == 0) {
                                                MyPluginUtil.showNotification(Objects.requireNonNull(actionEvent.getProject()), "打包完成:" + selectedItem.getText());
                                            } else {
                                                MyPluginUtil.showAlert(Objects.requireNonNull(actionEvent.getProject()), exitMsg);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    } else {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            MyPluginUtil.showNotification(Objects.requireNonNull(actionEvent.getProject()), "有待完成任务，请稍候");
                        });
                    }
                })
                .setRequestFocus(true)
                .createPopup();
        popup.show(new RelativePoint(invoker, new Point(0, invoker.getHeight())));
    }


    private static class DropdownItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof DropdownItem item) {
                setText(item.getText());
                //TODO 修改图标
                if (item.getCommandType() == Constant.COMMAND_TYPE_SYNC) {
                    if (item.isRunning()) {
                        setIcon(IconLoader.getIcon("/icons/item_sync_running.svg", DropdownPopup.class));
                    } else {
                        setIcon(IconLoader.getIcon("/icons/item_sync.svg", DropdownPopup.class));
                    }
                } else if (item.getCommandType() == Constant.COMMAND_TYPE_BUILD) {
                    if (item.isRunning()) {
                        setIcon(IconLoader.getIcon("/icons/item_build_running.svg", DropdownPopup.class));
                    } else {
                        setIcon(IconLoader.getIcon("/icons/item_build.svg", DropdownPopup.class));
                    }
                }
            }
            return this;
        }
    }
}