package com.android.taxi1in_carapp.activity.ui.account.changeVehicle.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.Retrofit.newresponse.GetProfileResponse
import com.android.taxi1in_carapp.databinding.ItemViewVehicleEditBinding

class ChangeVehicleListAdapter(val context: Context, private var list: ArrayList<GetProfileResponse.Result.VehicalData>, var userData: GetProfileResponse.Result?, private val listener: VehicleListAdapterListener) : RecyclerView.Adapter<ChangeVehicleListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemViewVehicleEditBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemViewVehicleEditBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvPlateNo.text = list[position].vehicalNumber

        if(userData?.plateNumber == list[position].vehicalNumber){
            holder.binding.llPlatNumber.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green))
        }else{
            holder.binding.llPlatNumber.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
        }

        holder.binding.root.setOnClickListener {
            listener.changeVehicle(list[position])
        }

        holder.binding.ivDelete.setOnClickListener {
            listener.deleteVehicle(list[position])
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateListData(list: ArrayList<GetProfileResponse.Result.VehicalData>, userData: GetProfileResponse.Result?){
        this.list = list
        this.userData = userData
        notifyDataSetChanged()
    }

    interface VehicleListAdapterListener{
        fun deleteVehicle(data: GetProfileResponse.Result.VehicalData)
        fun changeVehicle(data: GetProfileResponse.Result.VehicalData)
    }
}