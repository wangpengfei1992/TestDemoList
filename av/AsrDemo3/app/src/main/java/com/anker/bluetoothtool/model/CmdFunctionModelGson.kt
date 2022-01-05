package com.anker.bluetoothtool.model

import java.io.Serializable

/**
 *  Author: anker
 *  Time:   2021/8/17
 *  Description : This is description.
 */
data class CmdFunctionModelGson(
    var name: String = "",
    var cmd: String = "",
) : Serializable