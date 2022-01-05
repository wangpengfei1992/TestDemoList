//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not limited to reproduction, retransmission, communication, display, mirror, download, modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.

//
package com.wpf.common_ui.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by MD01 on 2017/12/21.
 */

public class JsonUtil {

    private static Gson gson = new Gson();

    private JsonUtil() {
    }

    /**
     * toJson
     *
     * @param src
     * @return
     */
    public static String toJson(Object src) {

        if(src==null){
            return null;
        }
        return gson.toJson(src);
    }

    /**
     * fromJson
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromJsonToObject(String json, Class<T> clazz) throws JsonSyntaxException {
        if(json ==null){
            return  null;
        }
        return gson.fromJson(json, clazz);
    }

    /**
     * fromJsonToList
     *
     * @param json
     * @param type // Type type = new TypeToken<ArrayList<City>>() {}.getType();
     * @return
     */
    public static <T> List<T> fromJsonToList(String json, Type type) {
        return gson.fromJson(json, type);
    }

}
