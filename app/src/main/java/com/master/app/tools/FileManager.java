/*Copyright ©2015 TommyLemon(https://github.com/TommyLemon)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package com.master.app.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import static com.master.app.tools.CommonDateParseUtils.YYYY_MM_DD_HH_MM;

/**
 * 数据存储工具类
 *
 * @must 1.将fileRootPath中的包名（这里是zblibrary.demo）改为你的应用包名
 * 2.在Application中调用init方法
 */
public class FileManager {
    private static final String TAG = "DataKeeper";

    public static final String SAVE_SUCCEED = "保存成功";
    public static final String SAVE_FAILED = "保存失败";
    public static final String DELETE_SUCCEED = "删除成功";
    public static final String DELETE_FAILED = "删除失败";

    public static final String ROOT_SHARE_PREFS_ = "gold_SHARE_PREFS_";


    public static final String fileRootPath = getSDPath() != null ? (getSDPath() + "/Collect_for_ArcGIS/") : null;
    public static final String exChangePath = fileRootPath + "exChange/";
    public static final String roadPath = fileRootPath + "road/";
    public static final String regionPath = fileRootPath + "region/";

    //存储文件的类型<<<<<<<<<<<<<<<<<<<<<<<<<
    public static final int TYPE_FILE_EXCHANGE = 0;                                //保存保存临时文件
    public static final int TYPE_FILE_RAOD = 1;                            //保存路网地图
    public static final int TYPE_FILE_REGION = 2;                            //保存区划地图

    //存储文件的类型>>>>>>>>>>>>>>>>>>>>>>>>>

    //不能实例化
    private FileManager() {
    }

    private static Context context;

    //获取context，获取存档数据库引用
    public static void init(Context context_) {
        context = context_;

        Log.i(TAG, "init fileRootPath = " + fileRootPath);

        //判断SD卡存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (fileRootPath != null) {
                File file = new File(exChangePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                file = new File(roadPath);
                if (!file.exists()) {
                    file.mkdir();
                }
                file = new File(regionPath);
                if (!file.exists()) {
                    file.mkdir();
                }
            }
        }
    }


    public static SharedPreferences getRootSharedPreferences() {
        return context.getSharedPreferences(ROOT_SHARE_PREFS_, Context.MODE_PRIVATE);
    }

    //**********外部存储缓存***************

    /**
     * 存储缓存文件 返回文件绝对路径
     *
     * @param file 要存储的文件
     * @param type 文件的类型
     *             Json = "json";							//交换文件
     *             roadmap = "";							//离线地图
     *             regionmap = "";							//离线地图
     * @return 存储文件的绝对路径名 若SDCard不存在返回null
     */
    public static String storeFile(File file, int type) {

        if (!hasSDCard()) {
            return null;
        }
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        byte[] data = null;
        try {
            FileInputStream in = new FileInputStream(file);
            data = new byte[in.available()];
            in.read(data, 0, data.length);
            in.close();
        } catch (IOException e) {
            Log.e(TAG, "storeFile  try { FileInputStream in = new FileInputStream(file); ... >>" +
                    " } catch (IOException e) {\n" + e.getMessage());
        }
        return storeFile(data, suffix, type);
    }

    /**
     * @return 存储文件的绝对路径名 若SDCard不存在返回null
     */
    @SuppressLint("DefaultLocale")
    public static String storeFile(byte[] data, String suffix, int type) {

        if (!hasSDCard()) {
            return null;
        }
        String path = null;
        if (type == (TYPE_FILE_RAOD)) {
            path = roadPath + "road_" + Long.toHexString(System.currentTimeMillis()).toUpperCase()
                    + "." + suffix;
        } else if (type == (TYPE_FILE_REGION)) {
            path = regionPath + "regin_" + Long.toHexString(System.currentTimeMillis()).toUpperCase()
                    + "." + suffix;
        } else if (type == (TYPE_FILE_EXCHANGE)) {
            path = exChangePath + "json_" + Long.toHexString(System.currentTimeMillis()).toUpperCase()
                    + "." + suffix;
        }
        try {

            FileOutputStream out = new FileOutputStream(path);
            out.write(data, 0, data.length);
            out.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "storeFile  try { FileInputStream in = new FileInputStream(file); ... >>" +
                    " } catch (FileNotFoundException e) {\n" + e.getMessage() + "\n\n >> path = null;");
            path = null;
        } catch (IOException e) {
            Log.e(TAG, "storeFile  try { FileInputStream in = new FileInputStream(file); ... >>" +
                    " } catch (IOException e) {\n" + e.getMessage() + "\n\n >> path = null;");
            path = null;
        }
        return path;
    }

    /**
     * @param fileName
     * @return
     */
    public static String getJsonFileCachePath(String fileName) {
        return getFileCachePath(TYPE_FILE_RAOD, fileName, "json");
    }


    /**
     * 获取一个文件缓存的路径
     */
    public static String getFileCachePath(int fileType, String fileName, String formSuffix) {

        switch (fileType) {
            case TYPE_FILE_RAOD:
                return roadPath + fileName + "." + formSuffix;
            case TYPE_FILE_REGION:
                return regionPath + fileName + "." + formSuffix;
            case TYPE_FILE_EXCHANGE:
                return exChangePath + fileName + "." + formSuffix;
            default:
                return null;
        }
    }

    /**
     * 若存在SD 则获取SD卡的路径 不存在则返回null
     */
    public static String getSDPath() {
        File sdDir = null;
        String path = null;
        //判断sd卡是否存在
        boolean sdCardExist = hasSDCard();
        if (sdCardExist) {
            //获取跟目录
            sdDir = Environment.getExternalStorageDirectory();
            path = sdDir.toString();
        }
        return path;
    }

    /**
     * 判断是否有SD卡
     */
    public static boolean hasSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    //  保存文件到缓存目录
    public static boolean saveIntoDirs(String jsonContext) {
        String filename = CommonDateParseUtils.date2string(new Date(), YYYY_MM_DD_HH_MM);
        String path = getFileCachePath(TYPE_FILE_EXCHANGE, filename, "json");

        return FileUtils.saveStrToFile(jsonContext, path, null);

    }


}