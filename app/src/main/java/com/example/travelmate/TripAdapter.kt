package com.example.travelmate

import Trip
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class TripAdapter(
    private val trips: List<Trip>,
    private val onItemClick: (Trip) -> Unit
) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        holder.bind(trip)
    }

    override fun getItemCount(): Int = trips.size

    inner class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDestination: TextView = itemView.findViewById(R.id.tvDestination)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvLatitude: TextView = itemView.findViewById(R.id.tvLatitude)
        private val tvLongitude: TextView = itemView.findViewById(R.id.tvLongitude)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val ivMap: ImageView = itemView.findViewById(R.id.map)  // Match the ID here

        fun bind(trip: Trip) {
            tvDestination.text = trip.destination
            tvDescription.text = trip.description
            tvLatitude.text = "Latitude: ${trip.latitude}"
            tvLongitude.text = "Longitude: ${trip.longitude}"
            tvDate.text = "Planned Date: ${trip.tripDate}"

            // Build the Google Static Map URL with the latitude and longitude
            val mapUrl = "https://maps.googleapis.com/maps/api/staticmap?center=${trip.latitude},${trip.longitude}&zoom=14&size=600x300&markers=color:red%7Clabel:S%7C${trip.latitude},${trip.longitude}&key=AIzaSyD9hq-XPw1SNs89apAYJtDBEL_qbM4P5jE"

            Log.d("TripAdapter", "Map URL: $mapUrl")  // Log the URL for debugging

            // Use Glide to load the map image into the ivMap ImageView
            Glide.with(itemView.context)
                .load(mapUrl)
                .apply(RequestOptions().error(R.drawable.mountain_image))
                .into(ivMap)

            itemView.setOnClickListener {
                onItemClick(trip)
            }
        }
    }
}
