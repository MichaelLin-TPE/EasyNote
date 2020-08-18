package com.noted.checktimeapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noted.checktimeapp.databinding.SelectMonthItemBinding

class SelectMonthAdapter : RecyclerView.Adapter<SelectMonthAdapter.ViewHolder>() {

    private lateinit var listener:OnItemClickListener

    fun setOnItemClickListener(listener:OnItemClickListener){
        this.listener = listener
    }


    class ViewHolder(private val dataBinding: SelectMonthItemBinding) : RecyclerView.ViewHolder(dataBinding.root) {
        fun setData(
            month: String,
            listener: OnItemClickListener
        ) {
            val viewModel = SelectMonthViewModel(month)
            dataBinding.vm = viewModel
            dataBinding.listener = listener
            dataBinding.executePendingBindings()
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataBinding = SelectMonthItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(dataBinding)
    }


    override fun getItemCount(): Int {
        return 12
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val monthArray = ArrayList<String>()
        for (i in 1 until 13){
            if (i<10){
                monthArray.add("0$i")
            }else{
                monthArray.add(i.toString())
            }

        }
        val month = monthArray[position]
        holder.setData(month,listener)
    }

    interface OnItemClickListener{
        fun onClick(month: String)
    }
}