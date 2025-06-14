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
    return MaterialApp(title: 'Pocket Mode', home: HomeScreen());
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.paused) {
    } else if (state == AppLifecycleState.resumed) {
    } else if (state == AppLifecycleState.detached) {
      ProximityService.stopListening();
    } else if (state == AppLifecycleState.inactive) {}
  }
}
