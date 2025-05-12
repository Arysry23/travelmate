package com.example.travelmate

import Trip
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var tripRecyclerView: RecyclerView
    private lateinit var tripAdapter: TripAdapter
    private lateinit var searchEditText: EditText
    private lateinit var sortByDateSwitch: Switch

    private val allTrips = mutableListOf<Trip>()
    private val filteredTrips = mutableListOf<Trip>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        firestore = FirebaseFirestore.getInstance()

        val btnAddTrip: Button = findViewById(R.id.btnAddTrip)
        searchEditText = findViewById(R.id.etSearch)
        sortByDateSwitch = findViewById(R.id.switchSortByDate)

        tripRecyclerView = findViewById(R.id.recyclerViewTrips)
        tripRecyclerView.layoutManager = LinearLayoutManager(this)

        tripAdapter = TripAdapter(filteredTrips) { selectedTrip ->
            val intent = Intent(this, TripDetailsActivity::class.java)
            intent.putExtra("tripId", selectedTrip.id)
            startActivity(intent)
        }
        tripRecyclerView.adapter = tripAdapter



        btnAddTrip.setOnClickListener {
            startActivity(Intent(this, AddTripActivity::class.java))
        }

        // Search functionality
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = updateTrips()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Sort switch functionality
        sortByDateSwitch.setOnCheckedChangeListener { _, _ ->
            updateTrips()
        }

        fetchTrips()
    }

    private fun fetchTrips() {
        firestore.collection("trips")
            .get()
            .addOnSuccessListener { result ->
                allTrips.clear()
                for (document in result) {
                    val trip = document.toObject(Trip::class.java).copy(id = document.id)
                    allTrips.add(trip)
                }
                updateTrips()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error getting trips: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateTrips() {
        val query = searchEditText.text.toString().lowercase(Locale.getDefault())
        val shouldSortByDate = sortByDateSwitch.isChecked

        val filtered = allTrips.filter {
            it.destination.lowercase(Locale.getDefault()).contains(query)
        }.let { list ->
            if (shouldSortByDate) list.sortedBy { it.timestamp } else list
        }

        filteredTrips.clear()
        filteredTrips.addAll(filtered)
        tripAdapter.notifyDataSetChanged()
    }
}
