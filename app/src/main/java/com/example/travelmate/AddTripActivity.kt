package com.example.travelmate

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddTripActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var etDestination: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnSave: Button
    private lateinit var ivTripImage: ImageView
    private lateinit var tvDatePicker: TextView
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap

    private var selectedLatLng: LatLng? = null
    private lateinit var firestore: FirebaseFirestore
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_trip)

        etDestination = findViewById(R.id.etDestination)
        etDescription = findViewById(R.id.etDescription)
        btnSave = findViewById(R.id.btnSave)
        ivTripImage = findViewById(R.id.ivTripImage)
        tvDatePicker = findViewById(R.id.tvDatePicker)

        firestore = FirebaseFirestore.getInstance()

        ivTripImage.setImageResource(R.drawable.ic_add_photo)

        mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        tvDatePicker.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(this,
                { _, year, month, dayOfMonth ->
                    selectedDate = "$dayOfMonth/${month + 1}/$year"
                    tvDatePicker.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        btnSave.setOnClickListener {
            val destination = etDestination.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (destination.isEmpty() || description.isEmpty() || selectedLatLng == null || selectedDate.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields and select location and date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tripData = hashMapOf<String, Any>(
                "destination" to destination,
                "description" to description,
                "imageUrl" to "https://i.imgur.com/2yaf2wb.jpg",
                "latitude" to selectedLatLng!!.latitude,
                "longitude" to selectedLatLng!!.longitude,
                "timestamp" to System.currentTimeMillis(),
                "tripDate" to selectedDate
            )

            saveTripToFirestore(tripData)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val defaultLocation = LatLng(20.5937, 78.9629)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 4f))

        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            selectedLatLng = latLng
        }
    }

    private fun saveTripToFirestore(tripData: HashMap<String, Any>) {
        firestore.collection("trips")
            .add(tripData)
            .addOnSuccessListener {
                Toast.makeText(this, "Trip saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving trip: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
