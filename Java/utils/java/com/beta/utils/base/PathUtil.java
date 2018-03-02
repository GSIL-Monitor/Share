package com.beta.utils.base;

import java.io.File;

/**
 * 说明：路径工具类 创建人：FH Q313596790 修改时间：2014年9月20日
 * 
 * @version
 */
public class PathUtil {

    /**
     * 获取classpath1
     * 
     * @return
     */
    public static String getClasspath() {
        String path = (String.valueOf(Thread.currentThread().getContextClassLoader().getResource("")) + "../../")
                .replaceAll("file:/", "").replaceAll("%20", " ").trim();
        if (path.indexOf(":") != 1) {
            path = File.separator + path;
        }
        return path;
    }

    /**
     * 获取classpath2
     * 
     * @return
     */
    public static String getClassResources() {
        String path = (String.valueOf(Thread.currentThread().getContextClassLoader().getResource("")))
                .replaceAll("file:/", "").replaceAll("%20", " ").trim();
        if (path.indexOf(":") != 1) {
            path = File.separator + path;
        }
        return path;
    }

}
