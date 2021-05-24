package com.example.blackholessurfaceview

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener, SurfaceHolder.Callback {


    private val tag = "Black holes"
    private val matrixSize = 16
    private val radius = 30f
    private var mgValues = FloatArray(3)
    private var acValues = FloatArray(3)
    private var startTime:Long = 0
    private var surfaceWidth = 0
    private var surfaceHeight = 0
    private var ballX = 0f
    private var ballY = 0f
    private var isGoal = false

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val inR = FloatArray(matrixSize)
        val outR = FloatArray(matrixSize)
        val I = FloatArray(matrixSize)
        val orValues = FloatArray(3)

        if(event == null) return

        when(event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> acValues = event.values.clone()
            Sensor.TYPE_MAGNETIC_FIELD -> acValues = event.values.clone()
        }
        SensorManager.getRotationMatrix(inR, I, acValues, mgValues)

        SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR)

        SensorManager.getOrientation(outR,orValues)

        val pitch = rad2Deg(orValues[1])
        val roll = rad2Deg(orValues[2])

        if(!isGoal){
            drawGameBoad(pitch,roll)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        surfaceWidth = width
        surfaceHeight = height
        ballX = (width / 2).toFloat()
        ballY = (height / radius).toFloat()
        startTime = System.currentTimeMillis()
    }

    private fun rad2Deg(rad: Float): Int {
        return Math.floor(Math.toDegrees(rad.toDouble())).toInt()
    }

    private fun drawGameBoad(pitch: Int, roll: Int) {

        ballX += roll
        ballY -= pitch

        if(ballX < 0){
            ballX = surfaceWidth -radius
        } else if (ballX > surfaceWidth){
            ballX =radius
        }
        if(ballY + radius < 0){
            isGoal = true
        } else if (ballX > surfaceWidth){
            ballY = surfaceHeight -radius
        }

        val canvas = surfaceView.holder.lockCanvas()
        val paint = Paint()
        paint.color = Color.YELLOW
        canvas.drawColor(Color.BLUE)
        canvas.drawCircle(ballX,ballY,radius,paint)

        if(isGoal) {
            paint.textSize = 80f
            canvas.drawText(goaled(),10f,(surfaceHeight - 60).toFloat(),paint)
        }
        surfaceView.holder.unlockCanvasAndPost(canvas)
    }

    private fun goaled(): String {
    val elapsedTime = System.currentTimeMillis()- startTime
        val secTime = (elapsedTime / 1000).toInt()
        return "Goal! ${secTime}"
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magField, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val holder = surfaceView.holder
        holder.addCallback(this)

    }
}