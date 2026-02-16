package com.example.mobilecomputing

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class RotationSensor(private val sensorManager: SensorManager, context: Context) : SensorEventListener {
    private var mRotation: Sensor? = null

    fun start() {
        mRotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (mRotation == null) {
            println("Rotation vector not found on this device!")
            return
        }
        println("Have gyro")
        mRotation?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val threshold = 1.0f
        println("Rotation triggered: x=$x, y=$y, z=$z")
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}