package com.example.plugin.generate;

import com.example.plugin.Constant;
import com.example.plugin.MyPluginUtil;
import com.example.plugin.ZipFileUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class CreateResStackAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        //后台任务读取产品配置文件
        ProgressManager.getInstance().run(new Task.Backgroundable(anActionEvent.getProject(), "Generate files") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                String projectPath = Objects.requireNonNull(anActionEvent.getProject()).getBasePath();
                if (projectPath == null) {
                    return;
                }
                projectPath = Constant.ConvertPathForTestMode(projectPath);

                if (ZipFileUtil.unzip(projectPath + "\\resources_copy.zip", projectPath + "\\entry\\src\\main\\resources\\")) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        MyPluginUtil.showNotification(Objects.requireNonNull(anActionEvent.getProject()), "资源文件已生成");
                        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
                    });
                } else {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        MyPluginUtil.showNotification(Objects.requireNonNull(anActionEvent.getProject()), "资源文件生成失败，请重试");
                    });
                }
            }
        });
    }

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        // 获取当前项目
        Project project = anActionEvent.getProject();
        if (project == null) {
            // 如果没有项目，隐藏动作
            anActionEvent.getPresentation().setEnabledAndVisible(false);
            return;
        }
        // 获取当前选中的文件
        VirtualFile[] selectedFiles = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        if (selectedFiles == null || selectedFiles.length != 1) {
            // 如果没有选中的文件，隐藏动作
            anActionEvent.getPresentation().setEnabledAndVisible(false);
            return;
        }
        //通过文件的子文件中是否含有build-profile.json5文件判断是否是module，以此决定要不要显示功能按钮
        boolean hasBuildProfile = Arrays.stream(selectedFiles[0].getChildren()).anyMatch(virtualFile -> virtualFile.getName().equals("build-profile.json5"));
        anActionEvent.getPresentation().setEnabledAndVisible(hasBuildProfile && !selectedFiles[0].getPath().equals(project.getBasePath()));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        // 指定使用 BGT/EDT,根据是否有耗时操作选择
        return ActionUpdateThread.BGT;
    }
}
