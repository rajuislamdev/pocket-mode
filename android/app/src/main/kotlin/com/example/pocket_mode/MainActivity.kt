package com.example.pocket_mode

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.PowerManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity(), SensorEventListener {

    companion object {
        private const val CHANNEL_NAME = "pocket_mode"
        private const val WAKE_LOCK_TAG = "pocket_mode:WakeLock"
    }

    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private var methodChannel: MethodChannel? = null

    private var powerManager: PowerManager? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        setupSensors()
        setupPowerManager()
        setupMethodChannel(flutterEngine)
    }

    /**
     * Initialize proximity sensor.
     */
    private fun setupSensors() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    }

    /**
     * Initialize PowerManager and WakeLock.
     */
    private fun setupPowerManager() {
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager?.newWakeLock(
            PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
            WAKE_LOCK_TAG
        )
    }

    /**
     * Set up communication between Flutter and Android using MethodChannel.
     */
    private fun setupMethodChannel(flutterEngine: FlutterEngine) {
        methodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL_NAME)

        methodChannel?.setMethodCallHandler { call, result ->
            when (call.method) {
                "startProximity" -> startProximity(result)
                "stopProximity" -> stopProximity(result)
                else -> result.notImplemented()
            }
        }
    }

    /**
     * Start listening to the proximity sensor and acquire WakeLock.
     */
    private fun startProximity(result: MethodChannel.Result) {
        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)

            if (wakeLock?.isHeld == false) {
                wakeLock?.acquire()
            }

            result.success(null)
        } ?: result.error("UNAVAILABLE", "Proximity sensor not available", null)
    }

    /**
     * Stop listening to the proximity sensor and release WakeLock.
     */
    private fun stopProximity(result: MethodChannel.Result) {
        sensorManager.unregisterListener(this)

        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }

        result.success(null)
    }

    /**
     * Called when the proximity sensor state changes.
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_PROXIMITY) return

        val isNear = event.values[0] < (proximitySensor?.maximumRange ?: 0f)
        methodChannel?.invokeMethod("onProximityChanged", isNear)
        // WakeLock behavior is automatically handled.
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}
