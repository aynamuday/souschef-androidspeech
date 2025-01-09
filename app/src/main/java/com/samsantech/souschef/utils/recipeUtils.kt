package com.samsantech.souschef.utils

fun getRecipeTimeText(hr: String, min: String): String {
    var text = ""

    if (hr.trim() != "0") {
        text = hr + "h"
    }
    if (min.trim() != "0") {
        text += " $min" + "m"
    }

    return text
}