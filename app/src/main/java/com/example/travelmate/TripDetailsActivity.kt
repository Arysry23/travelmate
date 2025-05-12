package com.example.travelmate

import Trip
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class TripDetailsActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var tvDestination: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var ivTripMountain: ImageView  // ImageView for the mountain image
    private lateinit var btnEditTrip: Button
    private lateinit var btnDeleteTrip: Button
    private var tripId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_details)

        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        tvDestination = findViewById(R.id.tvDestination)
        tvDescription = findViewById(R.id.tvDescription)
        tvLatitude = findViewById(R.id.tvLatitude)
        tvLongitude = findViewById(R.id.tvLongitude)
        ivTripMountain = findViewById(R.id.ivTripMountain)  // Mountain ImageView
        btnEditTrip = findViewById(R.id.btnEditTrip)
        btnDeleteTrip = findViewById(R.id.btnDeleteTrip)

        // Get the tripId passed from the previous activity
        tripId = intent.getStringExtra("tripId")

        if (tripId != null) {
            loadTripDetails(tripId!!)
        }

        btnEditTrip.setOnClickListener {
            val intent = Intent(this, EditTripActivity::class.java)
            intent.putExtra("tripId", tripId)
            intent.putExtra("destination", tvDestination.text.toString())
            intent.putExtra("description", tvDescription.text.toString())
            startActivity(intent)
        }

        btnDeleteTrip.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun loadTripDetails(tripId: String) {
        firestore.collection("trips")
            .document(tripId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val trip = document.toObject(Trip::class.java)
                    if (trip != null) {
                        // Set the destination, description, latitude, and longitude
                        tvDestination.text = "Destination: ${trip.destination}"
                        tvDescription.text = "Description: ${trip.description}"
                        tvLatitude.text = "Latitude: ${trip.latitude}"
                        tvLongitude.text = "Longitude: ${trip.longitude}"

                        // Load the mountain image into the ImageView
                        Glide.with(this)
                            .load(R.drawable.mountain_image)  // Your mountain image
                            .into(ivTripMountain)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading trip details", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to delete this trip?")
            .setPositiveButton("Yes") { _, _ ->
                deleteTrip()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteTrip() {
        tripId?.let {
            firestore.collection("trips")
                .document(it)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Trip deleted successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error deleting trip", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
