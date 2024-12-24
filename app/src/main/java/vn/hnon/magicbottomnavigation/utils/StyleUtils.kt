package vn.hnon.magicbottomnavigation.utils

import android.content.Context
import android.util.TypedValue

object StyleUtils {

    fun dip2Pixel(context: Context, dip: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip.toFloat(),
            context.resources.displayMetrics,
        ).toInt()
    }
}