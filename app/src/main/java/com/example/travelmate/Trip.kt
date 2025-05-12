data class Trip(
    val destination: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val tripDate: String = "", // New field
    val id: String = ""
)
