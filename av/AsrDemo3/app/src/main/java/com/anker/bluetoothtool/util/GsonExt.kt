package com.anker.bluetoothtool.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ParameterizedTypeImpl(val clz: Class<*>) : ParameterizedType {
    override fun getRawType(): Type = List::class.java

    override fun getOwnerType(): Type? = null

    override fun getActualTypeArguments(): Array<Type> = arrayOf(clz)
}

inline fun <T> Gson.listToJson(list: List<T>): String = toJson(list)

//必须加inline修饰，不然会报错
inline fun <reified T> String.jsonToList(): List<T> = Gson().fromJson<List<T>>(this, ParameterizedTypeImpl(T::class.java))



