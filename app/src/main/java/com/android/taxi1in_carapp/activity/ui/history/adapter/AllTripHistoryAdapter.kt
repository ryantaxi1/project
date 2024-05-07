package com.android.taxi1in_carapp.activity.ui.history.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.android.taxi1in_carapp.R
import com.android.taxi1in_carapp.databinding.ItemTripHistoryBinding
import com.taxi1.Retrofit.Response.AllTripRecordsResponse


class AllTripHistoryAdapter(val context: Context, private val allTripList: ArrayList<AllTripRecordsResponse.Result>, val allTripListener: AllTripHistoryListener) : RecyclerView.Adapter<AllTripHistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemTripHistoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemTripHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(!allTripList[position].continueTripArray.isNullOrEmpty()){
            holder.binding.tvDate.text = allTripList[position].continueTripArray!![0].tripDateTime
        }

        when (position % 4) {
            0 -> holder.binding.root.setBackgroundColor(context.getColor(R.color.white))
            1 -> holder.binding.root.setBackgroundColor(context.getColor(R.color.gray_new))
            2 -> holder.binding.root.setBackgroundColor(context.getColor(R.color.gray_new3))
            3 -> holder.binding.root.setBackgroundColor(context.getColor(R.color.gray_new))
        }

        holder.binding.cbCheckbox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                allTripListener.checkedTrip(allTripList[position], position)
            }else{
                allTripListener.unCheckedTrip(allTripList[position], position)
            }
        })

        holder.binding.tvPickupTime.text = allTripList[position].tripStartTime
        holder.binding.tvDropTime.text = allTripList[position].tripStopTime
        holder.binding.tvPickupLoc.text = allTripList[position].startLocation
        holder.binding.tvDropLoc.text = allTripList[position].stopLocation
        holder.binding.tvFair.text = "$${allTripList[position].finalChage}"

        holder.binding.ivDelete.setOnClickListener {
            allTripListener.deleteTrip(allTripList[position], position)
        }

        holder.binding.root.setOnClickListener {
            allTripListener.tripClick(allTripList[position])
        }

    }

    override fun getItemCount(): Int {
        return allTripList.size
    }

    interface AllTripHistoryListener{
        fun tripClick(data: AllTripRecordsResponse.Result)
        fun deleteTrip(data: AllTripRecordsResponse.Result, position: Int)
        fun checkedTrip(data: AllTripRecordsResponse.Result, position: Int)
        fun unCheckedTrip(data: AllTripRecordsResponse.Result, position: Int)
    }
}