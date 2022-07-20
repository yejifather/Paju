package com.tenkitchen.objects

import java.text.DecimalFormat

object CommonUtil {
    public fun digit2Comma( value: Int ): String{
        return DecimalFormat("#,###").format(value).toString()
    }
}