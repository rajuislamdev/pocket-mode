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
