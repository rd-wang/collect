package com.master.app.tools;


import com.google.gson.Gson;


/**
 * @param
 * @author Litao-pc on 2016/11/30.
 *         ~
 */

public class JsonUtils {
    private static Gson g = new Gson();

    public static <T> T toObject(String json, Class<T> clazz) {
        return g.fromJson(json, clazz);
    }

    public static String toJson(Object o) {
        return g.toJson(o);
    }
}
