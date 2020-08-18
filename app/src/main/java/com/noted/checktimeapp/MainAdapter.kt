package com.noted.checktimeapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noted.checktimeapp.databinding.DateViewBinding
import com.noted.checktimeapp.databinding.ToDoLayoutBinding
import com.noted.checktimeapp.databinding.WeekViewBinding
import com.noted.checktimeapp.date_view.DateViewHolder
import com.noted.checktimeapp.to_do_view.ToDoViewHolder
import com.noted.checktimeapp.week_view.WeekViewHolder

class MainAdapter(private val mainViewPresenter: MainViewPresenter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val WEEK_VIEW = 0

    private val DATE_VIEW = 1

    private val TO_DO = 2

    private lateinit var listener:OnSetDateItemClickListener

    fun setOnSetDateItemClickListener(listener:OnSetDateItemClickListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            WEEK_VIEW ->{
                val weekDataBinding = WeekViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return WeekViewHolder(
                    weekDataBinding,
                    parent.context
                )
            }
            DATE_VIEW ->{
                val dateDataBinding = DateViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return DateViewHolder(
                    dateDataBinding,
                    parent.context
                )
            }
            TO_DO ->{
                val toDoDataBinding = ToDoLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return ToDoViewHolder(
                    toDoDataBinding,
                    parent.context
                )
            }
        }
        return null!!
    }

    override fun getItemViewType(position: Int): Int {
        return mainViewPresenter.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return mainViewPresenter.getItemCount()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is WeekViewHolder){
            mainViewPresenter.onBindWeekViewHolder(holder as WeekViewHolder,position)
        }
        if (holder is DateViewHolder){
            mainViewPresenter.onBindDateViewHolder(holder as DateViewHolder, position)
            mainViewPresenter.setOnSetDateItemClickListener(listener,holder as DateViewHolder)
        }
        if (holder is ToDoViewHolder){
            mainViewPresenter.onBindToDoViewHolder(holder as ToDoViewHolder, position)
        }
    }
    interface OnSetDateItemClickListener{
        fun onItemClick(totalDate:String)
    }
}