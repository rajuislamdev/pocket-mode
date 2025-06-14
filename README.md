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
- Android device (physical or emulator with proximity sensor)
- Kotlin support in Android project

---

## 🚀 How It Works

### 🟩 `main.dart`

- Initializes the app.
- Registers `WidgetsBindingObserver` to manage sensor lifecycle (stopping on app `detached`).

```dart
class MyApp extends StatelessWidget with WidgetsBindingObserver {
  ...
  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.detached) {
      ProximityService.stopListening();
    }
  }
}


🟦 home_screen.dart

* UI to observe proximity detection.

* Starts proximity listener in initState.

🟨 proximity_service.dart

* Uses a MethodChannel to communicate with Android native code.

* Listens for proximity updates via onProximityChanged.

static Future<void> startListening(Function(bool isNear) onChange) async {
  _channel.setMethodCallHandler((call) async {
    if (call.method == 'onProximityChanged') {
      final bool isNear = call.arguments == true;
      onChange(isNear);
    }
  });
  await _channel.invokeMethod('startProximity');
}


🟥 MainActivity.kt

* Implements SensorEventListener

* Initializes SensorManager, PowerManager, and WakeLock

* Sends proximity state to Flutter via MethodChannel

<uses-permission android:name="android.permission.WAKE_LOCK"/>

🔐 Permissions
Ensure you add the required permission in AndroidManifest.xml:

<uses-permission android:name="android.permission.WAKE_LOCK"/>


💡 Use Cases
* Lock screen automatically when phone is in pocket

* Save power by turning off screen when face-down

* Block unwanted touch inputs during calls or when pocketed

🧪 Testing
To test:

* Run the app on a real Android device with a proximity sensor.

* Cover the top front part of the device (where the sensor is).

* Observe logs for isNear: true/false.

⚠️ Notes
* This implementation works on Android only.

📃 License
* MIT License. See the LICENSE file for details.

🙋‍♂️ Author
Made with ❤️ by Md. Raju Islam
📧 Email: rajuislam.dev@gmail.com
🐦 Linkedin: https://www.linkedin.com/in/rajuislamdev/
🌐 Website: rajuislam.com
```
