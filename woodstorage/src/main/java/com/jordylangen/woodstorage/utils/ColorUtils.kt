package com.jordylangen.woodstorage.utils

import android.graphics.Color

import java.util.Random

object ColorUtils {

    private const val MAX_RGB_VALUE = 256
    private const val MAX_COLOR_VALUE = 255

    fun randomColor(): Int {
        val random = Random()
        var red = random.nextInt(MAX_RGB_VALUE)
        var green = random.nextInt(MAX_RGB_VALUE)
        var blue = random.nextInt(MAX_RGB_VALUE)

        red = (red + Color.red(MAX_COLOR_VALUE)) / 2
        green = (green + Color.green(MAX_COLOR_VALUE)) / 2
        blue = (blue + Color.blue(MAX_COLOR_VALUE)) / 2

        return Color.rgb(red, green, blue)
    }
}
