package com.taxi1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taxi1.Retrofit.Response.AllTripRecordsResponse
import com.android.taxi1in_carapp.databinding.ItemNewTripDataViewBinding

class TripDataAdapter(val context: Context, private val tripList: ArrayList<AllTripRecordsResponse.Result.ContinueTripArray>, private val listener: TripListClickListener) : RecyclerView.Adapter<TripDataAdapter.ViewHolder>() {


    class ViewHolder(val binding: ItemNewTripDataViewBinding) : RecyclerView.ViewHolder(binding.root)

//    class TripIdViewHolder(val binding : ItemTripIdBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder  {
        return ViewHolder(ItemNewTripDataViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))


        /*return if (viewType == 0){
            ViewHolder(ItemNewTripDataViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }else {
            TripIdViewHolder(ItemTripIdBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }*/
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.txtTitle.text = tripList[position].triplocation
        holder.binding.txtDate.text = tripList[position].tripDateTime
        holder.binding.rlTripItemData.setOnClickListener {
            listener.tripItemClick(tripList[position])
        }

        /*when(holder){
            is ViewHolder -> {
                holder.binding.txtTitle.text = tripList[position].currentLocation.toString()
                holder.binding.txtDate.text = tripList[position].dateTime.toString()
                holder.binding.root.setOnClickListener {
                    listener.tripItemClick(tripList[position])
                }
            }
            is TripIdViewHolder -> {
                holder.binding.txtTripId.text = tripList[position].tripId.toString()
            }
        }*/
    }

    override fun getItemCount(): Int {
        return tripList.size
    }

    /*override fun getItemViewType(position: Int): Int {
        return if(!tripList[position].isTripIdHeadingShow){
            0
        }else{
            1
        }
    }*/

    interface TripListClickListener{
        fun tripItemClick(tripData: AllTripRecordsResponse.Result.ContinueTripArray)
    }
}