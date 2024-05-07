package com.android.taxi1in_carapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.Retrofit.newresponse.GetProfileResponse
import com.android.taxi1in_carapp.databinding.ItemviewVehiclelistBinding

class AllVehicleAdapter(
    val context: Context,
    private val list: ArrayList<GetProfileResponse.Result.VehicalData>,
    val userData: GetProfileResponse.Result?
) : RecyclerView.Adapter<AllVehicleAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemviewVehiclelistBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemviewVehiclelistBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvPlatNo.text = list[position].vehicalNumber

        if(userData?.plateNumber == list[position].vehicalNumber){
            holder.binding.root.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            holder.binding.ivCurrentCar.visibility = VISIBLE
        }else{
            holder.binding.root.background = null
            holder.binding.ivCurrentCar.visibility = GONE
        }

        holder.binding.root.setOnClickListener {

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}