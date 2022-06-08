/// 经纬度
class LatLng {
  final double latitude;
  final double longitude;

  LatLng({required this.latitude, required this.longitude})
      : assert(latitude >= -90 && latitude <= 90, '纬度范围为[-90, 90]!'),
        assert(longitude >= -180 && longitude <= 180, '经度范围为[-180, 180]!');

  static LatLng? fromJson(Map<String, dynamic?> json) {
    if (json.isEmpty) return null;
    return LatLng(latitude: json['latitude'] ?? 0, longitude: json['longitude'] ?? 0);
  }

  Map<String, dynamic> toJson() {
    return {
      'latitude': latitude,
      'longitude': longitude,
    };
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
          other is LatLng &&
              runtimeType == other.runtimeType &&
              latitude == other.latitude &&
              longitude == other.longitude;

  @override
  int get hashCode => latitude.hashCode ^ longitude.hashCode;

  @override
  String toString() {
    return 'LatLng{latitude: $latitude, longitude: $longitude}';
  }
}
