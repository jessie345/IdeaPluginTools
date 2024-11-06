package com.example.plugin;

public class Constant {

    public static final int COMMAND_TYPE_BUILD = 0;
    public static final int COMMAND_TYPE_SYNC = 1;

    public static final String COMMAND_TYPE_SYNC_TEXT = "执行同步命令";

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
