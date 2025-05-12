package com.example.travelmate

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class EditTripActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var tripId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_trip)

        firestore = FirebaseFirestore.getInstance()

        val etDestination: EditText = findViewById(R.id.etDestination)
        val etDescription: EditText = findViewById(R.id.etDescription)
        val btnSave: Button = findViewById(R.id.btnSaveTrip)

        tripId = intent.getStringExtra("tripId") ?: ""
        val destination = intent.getStringExtra("destination") ?: ""
        val description = intent.getStringExtra("description") ?: ""

        etDestination.setText(destination)
        etDescription.setText(description)

        btnSave.setOnClickListener {
            val updatedDestination = etDestination.text.toString().trim()
            val updatedDescription = etDescription.text.toString().trim()

            if (updatedDestination.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val updatedTrip = hashMapOf<String, Any>(
                    "destination" to updatedDestination,
                    "description" to updatedDescription
                )

                firestore.collection("trips")
                    .document(tripId)
                    .set(updatedTrip)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Trip updated successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error updating trip: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
