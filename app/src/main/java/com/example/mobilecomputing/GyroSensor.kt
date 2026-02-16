

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

import android.content.Context

class GyroSensor(private val sensorManager: SensorManager, context: Context) : SensorEventListener {
    private var mGyro: Sensor? = null
    private val notificationHelper = NotificationHelper(context)
    fun start() {
        mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        if (mGyro == null) {
            println("GyroSensor No gyroscope found on this device!")
            return
        }
        println("Have gyro")
        mGyro?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val threshold = 7.0f
        if (Math.abs(z) > threshold){
            notificationHelper.showNotification("Gyro", "Spin really fast gyro: ${Math.abs(z)}", null)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}
