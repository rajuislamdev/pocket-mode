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
    return Scaffold(
      body: Center(child: Text('Pocket Mode (Proximity Sensor)')),
    );
  }
}
