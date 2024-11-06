package com.example.plugin;

public class Constant {
    //为了便于在IDEA测试项目untitled1中进行测试，设置测试开关
    private static final boolean TestMode = true;
    private static final String TestDemoProjectPath = "D:\\harmony_workspace\\TestDemo";

    public static String ConvertPathForTestMode(String projectPath) {
        if (Constant.TestMode && projectPath.contains("issuser")) {
            return TestDemoProjectPath;
        } else {
            return projectPath;
        }
    }
}
