package com.taxi1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taxi1.Bean.AllTripModel
import com.taxi1.Retrofit.Response.AllTripRecordsResponse
import com.android.taxi1in_carapp.databinding.ItemAllTripdataBinding


class AllTripDataAdapter(val context: Context, private val allTripList: ArrayList<AllTripRecordsResponse.Result>, val allTripListener: AllTripListener) : RecyclerView.Adapter<AllTripDataAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAllTripdataBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
     return ViewHolder(ItemAllTripdataBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtTripId.text = "TripId: ${allTripList[position].tripId}"
        holder.binding.txtStartLocation.text = allTripList[position].startLocation
        holder.binding.txtEndLocation.text = allTripList[position].stopLocation
        holder.binding.txtStartDate.text = allTripList[position].tripStartTime
        holder.binding.txtEndDate.text = allTripList[position].tripStopTime

        holder.binding.root.setOnClickListener {
            allTripListener.tripClick(allTripList[position])
        }

    }

    override fun getItemCount(): Int {
        return allTripList.size
    }

    interface AllTripListener{
        fun tripClick(data: AllTripRecordsResponse.Result)
    }
}