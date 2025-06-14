# 📱 Pocket Mode - Proximity Sensor Detection in Flutter

This Flutter project demonstrates how to use the **proximity sensor** on Android devices via a **MethodChannel** to detect whether a phone is in a pocket or near an object. It also leverages Android’s native `PROXIMITY_SCREEN_OFF_WAKE_LOCK` feature to control the screen based on proximity.

---

## 🧠 Features

- ✅ Detects proximity (e.g., phone in pocket, face-down, or near object)
- 🔁 Real-time sensor feedback sent from Android to Flutter
- 💤 Automatically manages screen state via wake lock
- 📞 Communication between Flutter and native Android using MethodChannel
- 📱 Clean, modular Flutter structure with platform integration

---

## 🏗 Project Structure

lib/
├── services/
│ └── proximity_service.dart # Handles platform channel communication
├── views/
│ └── home_screen.dart # Simple UI to demonstrate proximity changes
└── main.dart # App entry and lifecycle observer

android/
└── MainActivity.kt # Kotlin native code to manage sensor + wake lock


---

## 🔧 Setup Instructions

### ✅ Requirements

- Flutter SDK
- Android device (with proximity sensor)
- Kotlin setup in your Android module

---

## 🚀 How It Works

### 🟩 `main.dart`

Initializes the app and manages app lifecycle. Proximity listener is stopped when app is detached.

```dart
import 'package:flutter/material.dart';
import 'package:pocket_mode/services/proximity_service.dart';
import 'package:pocket_mode/views/home_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget with WidgetsBindingObserver {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Pocket Mode',
      home: const HomeScreen(),
    );
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.detached) {
      ProximityService.stopListening();
    }
  }
}
🟦 home_screen.dart
The main UI. Starts proximity sensor listening in initState() and logs changes.

import 'package:flutter/material.dart';
import 'package:pocket_mode/services/proximity_service.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  @override
  void initState() {
    super.initState();
    ProximityService.startListening((isNear) {
      debugPrint('isNear: $isNear');
    });
  }

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      body: Center(child: Text('Pocket Mode (Proximity Sensor)')),
    );
  }
}

🟨 proximity_service.dart
Handles communication with the Android native side using MethodChannel.

import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';

class ProximityService {
  static const _channel = MethodChannel('pocket_mode');

  static Future<void> startListening(Function(bool isNear) onChange) async {
    _channel.setMethodCallHandler((call) async {
      if (call.method == 'onProximityChanged') {
        final bool isNear = call.arguments == true;
        onChange(isNear);
      }
    });
    try {
      await _channel.invokeMethod('startProximity');
    } catch (error) {
      debugPrint('Failed to start proximity sensor: $error');
    }
  }

  static Future<void> stopListening() async {
    try {
      await _channel.invokeMethod('stopProximity');
    } catch (error) {
      debugPrint('Failed to stop proximity sensor: $error');
    }
  }
}


🟥 MainActivity.kt (Android Native)
Implements sensor listener and sends proximity data to Flutter.

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

    private fun setupSensors() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    }

    private fun setupPowerManager() {
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager?.newWakeLock(
            PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
            WAKE_LOCK_TAG
        )
    }

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

    private fun startProximity(result: MethodChannel.Result) {
        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)

            if (wakeLock?.isHeld == false) {
                wakeLock?.acquire()
            }

            result.success(null)
        } ?: result.error("UNAVAILABLE", "Proximity sensor not available", null)
    }

    private fun stopProximity(result: MethodChannel.Result) {
        sensorManager.unregisterListener(this)
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
        result.success(null)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_PROXIMITY) return

        val isNear = event.values[0] < (proximitySensor?.maximumRange ?: 0f)
        methodChannel?.invokeMethod("onProximityChanged", isNear)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

🔐 Permissions
Add this to your AndroidManifest.xml:

<uses-permission android:name="android.permission.WAKE_LOCK"/>

face

🧪 Testing
1. Run the app on a real Android device with a proximity sensor.

2. Cover the top part of the phone where the proximity sensor is located.

3. Check the debug console for output like:

isNear: true

⚠️ Notes
- This feature works only on Android

- PROXIMITY_SCREEN_OFF_WAKE_LOCK is deprecated in newer Android versions, but still works

```
## 🙋‍♂️ Author

Made with ❤️ by **Md. Raju Islam**  
📧 Email: [rajuislam.dev@gmail.com](mailto:rajuislam.dev@gmail.com)  
🔗 [LinkedIn](https://www.linkedin.com/in/rajuislamdev/)  
🌐 [rajuislam.com](https://rajuislam.com)
