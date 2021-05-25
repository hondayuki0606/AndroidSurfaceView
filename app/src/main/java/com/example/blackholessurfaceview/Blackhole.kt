package com.example.blackholessurfaceview

class Blackhole(val x: Float,val y:Float,val speed:Int) {
    val MAX = 110f
    val MIN = 30f
    var r = 30f
    var sign = 1

    fun grow() {
        if(r > MAX) {
            sign = -1
        } else if (r < MIN) {
            sign = 1
        }
        r += (speed*sign).toFloat()
    }
}