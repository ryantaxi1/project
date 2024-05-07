package com.taxi1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.taxi1.Bean.SOSHistoryBean
import com.android.taxi1in_carapp.R
import com.taxi1.Retrofit.Response.SosHistoryResponse

class SOSHistoryAdapter() : RecyclerView.Adapter<SOSHistoryAdapter.ViewHolder>() {

    var list: List<SosHistoryResponse.Result?> = ArrayList()
    lateinit var context: Context

    private var itemClickListener: ItemClickListener? = null

    fun setClicklistner(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    constructor(context: Context, soslist: List<SosHistoryResponse.Result?>) : this() {

        this.context=context
        this.list = soslist
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view: View

        view = LayoutInflater.from(parent.context).inflate(R.layout.item_sos_history, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = list.get(position)

        holder.tvNumber.setText(data?.number)
        holder.tvStartTime.setText(data?.startDateTime)
        holder.tvEndTime.setText(data?.endDateTime)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ItemClickListener {
        fun itemclick(bean: SOSHistoryBean,position: Int,type : String)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal var tvNumber: TextView
        internal var tvStartTime: TextView
        internal var tvEndTime: TextView

        init {

            tvNumber = view.findViewById(R.id.tvNumber)
            tvStartTime = view.findViewById(R.id.tvstime)
            tvEndTime = view.findViewById(R.id.tvetime)

        }
    }
}