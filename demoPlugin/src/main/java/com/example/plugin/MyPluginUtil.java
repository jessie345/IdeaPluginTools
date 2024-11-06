package com.example.plugin;

import com.example.plugin.beans.BuildProfile;
import com.example.plugin.beans.ProductBean;
import com.example.plugin.interfaces.OnBatExecutedListener;
import com.google.gson.Gson;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class MyPluginUtil {

    /**
     * 选择文件
     *
     * @param project project项目
     * @return 文件路径
     */
    public static String selectFilePath(@NotNull Project project) {
        // 创建一个文件选择器描述符，允许选择文件，不允许选择目录
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
        // 创建文件选择器对话框
        FileChooserDialog dialog = FileChooserFactory.getInstance().createFileChooser(descriptor, project, null);
        // 显示对话框并等待用户选择
        List<VirtualFile> selectedFiles = List.of(dialog.choose(project));
        if (!selectedFiles.isEmpty()) {
            // 用户选择了文件，处理选择结果
            for (VirtualFile file : selectedFiles) {
                // 这里可以添加对选中文件的处理逻辑
                // 例如：System.out.println(file.getPath());
                // 为了演示，我们简单地在消息框中显示文件路径
                // Messages.showMessageDialog(project, "Selected file: " + file.getPath(), "File Selected", Messages.getInformationIcon());
                return file.getPath();
            }
        } else {
            // 用户取消了选择或关闭了对话框
            Messages.showMessageDialog(project, "No file selected.", "File Selection", Messages.getWarningIcon());
        }
        return "";
    }


    /**
     * 弹出输入框
     *
     * @param project project内容
     * @return 文件内容字符换
     */
    public static String showInputDialog(@NotNull Project project) {
        return Messages.showInputDialog(project,
                "Input some words please",
                "Test Input Dialog",
                Messages.getQuestionIcon());
    }

    /**
     * 读取文件
     *
     * @param file file文件
     * @return 文件内容字符换
     */
    public static String readStringFromFile(File file) {
        try (InputStream inputStream = new FileInputStream(file);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            StringWriter stringWriter = new StringWriter();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringWriter.append(line);
                stringWriter.append(System.lineSeparator()); // 保留原始文件的换行符，或者使用 "\n"
            }
            return stringWriter.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 读取product_list.json获取产品列表
     *
     * @param project project内容
     */
    public static ArrayList<ProductBean> getProductList(@Nullable Project project) {
        String projectPath = Objects.requireNonNull(project).getBasePath();
        if (projectPath == null) {
            return null;
        }
        projectPath = Constant.ConvertPathForTestMode(projectPath);
        String jsonString = MyPluginUtil.readStringFromFile(new File(projectPath + "\\build-profile.json5"));

        Gson gson = new Gson();
        ArrayList<ProductBean> yourClassList = null;
        try {

//            Json5 json5 = new Json5();
//            Json5Element json5Element = json5.parse(Objects.requireNonNull(jsonString));
//
//            Json5Object rootObject = json5Element.getAsJson5Object();
//            Json5Element appElement = rootObject.get("app");


            yourClassList = (gson.fromJson(jsonString, BuildProfile.class)).getApp().getProducts();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return yourClassList;
    }

    /**
     * 弹出消息框
     *
     * @param project project内容
     * @param msg     msg内容
     */
    public static void showAlert(@NotNull Project project, String msg) {
        if (msg != null && !msg.trim().isEmpty()) {
            Messages.showMessageDialog(project, msg,
                    "提示：",
                    Messages.getInformationIcon());
        }
    }

    /**
     * 弹出消息气泡
     *
     * @param project project内容
     * @param msg     msg内容
     */
    public static void showNotification(@NotNull Project project, String msg) {
        if (msg != null && !msg.trim().isEmpty()) {
            Notification notification = new Notification("MyPackageNotify", msg, NotificationType.INFORMATION);
            Notifications.Bus.notify(notification, project);
        }
    }

    /**
     * 执行bat
     *
     * @param batCommand            bat命令
     * @param onBatExecutedListener bat命令完成回调
     */
    public static void executeBat(String batCommand, OnBatExecutedListener onBatExecutedListener) {

        try {
            // 创建Runtime实例并执行.bat文件
            Process process = Runtime.getRuntime().exec(batCommand);
            // 等待进程完成
            int exitCode = process.waitFor();
            // 打印进程的退出代码
            System.out.println("Batch file exited with code: " + exitCode);
            // 读取并打印进程的输出（标准输出流）
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                sb.append(line);
            }
            reader.close();
            onBatExecutedListener.onBatExecuted(exitCode, sb.toString());
            //读取并处理错误输出流（标准错误流） BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        } catch (IOException e) {
            onBatExecutedListener.onBatExecuted(1, e.getMessage());
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            onBatExecutedListener.onBatExecuted(1, e.getMessage());
            System.out.println(e.getMessage());
            // 注意：如果线程被中断，可能需要重新设置中断状态
            Thread.currentThread().interrupt();
        }
    }

    public static void unzipFile(String filePath) throws Exception {
        // 解压缩ZIP文件
        FileInputStream fis = new FileInputStream(filePath);
        ZipInputStream zis = new ZipInputStream(fis);
        ZipEntry zipEntry;
        while ((zipEntry = zis.getNextEntry()) != null) {
            String fileName = zipEntry.getName();
            File newFile = new File(fileName);
            if (zipEntry.isDirectory()) {
                newFile.mkdirs();
            } else {
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = zis.read(bytes)) >= 0) {
                    fos.write(bytes, 0, length);
                }
                fos.close();
            }
            zis.closeEntry();
        }
        zis.close();
    }

}