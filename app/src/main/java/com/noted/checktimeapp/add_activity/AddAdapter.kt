package com.noted.checktimeapp.add_activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noted.checktimeapp.databinding.AddEventItemBinding

class AddAdapter() : RecyclerView.Adapter<AddAdapter.ViewHolder>() {

    private var data: EventObject? = null

    private lateinit var listener:OnAddItemClickListener

    fun setOnAddItemClickListener(listener:OnAddItemClickListener){
        this.listener = listener
    }

    fun setData(data:EventObject?){
        this.data = data
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding = AddEventItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(dataBinding)
    }

    override fun getItemCount(): Int {
        return data?.eventArray?.size ?: 0
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (data == null){
            return
        }
        holder.setData(data!!.eventArray[position],data!!.totalTime[position],position,listener)
    }


    class ViewHolder(private val dataBinding: AddEventItemBinding) : RecyclerView.ViewHolder(dataBinding.root) {
        fun setData(
            eventContent: String,
            time: String,
            position: Int,
            listener: OnAddItemClickListener
        ) {
            val addItem = AddItemViewModel("${position+1}. $eventContent $time",eventContent)
            dataBinding.vm = addItem
            dataBinding.listener = listener
            dataBinding.executePendingBindings()
        }
    }

    interface OnAddItemClickListener{
        fun onClick(eventContent :String)
    }
}