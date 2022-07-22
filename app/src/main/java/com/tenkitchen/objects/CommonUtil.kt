package com.tenkitchen.objects

import android.content.Context
import android.util.TypedValue
import java.text.DecimalFormat


object CommonUtil {
    fun digit2Comma( value: Int ): String{
        return DecimalFormat("#,###").format(value).toString()
    }

    fun intToDp(context: Context, value: Int ): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            context.getResources().getDisplayMetrics()
        ).toInt()
    }

}