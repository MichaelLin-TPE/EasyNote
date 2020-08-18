package com.noted.checktimeapp

import com.noted.checktimeapp.date_view.DateViewHolder
import com.noted.checktimeapp.to_do_view.ToDoViewHolder
import com.noted.checktimeapp.week_view.WeekViewHolder

interface MainViewPresenter {
    fun getItemViewType(position: Int) : Int
    fun setWeekData(it: ArrayList<String>)
    fun getItemCount(): Int
    fun onBindWeekViewHolder(weekViewHolder: WeekViewHolder, position: Int)
    fun setDateData(it: ArrayList<String>)
    fun onBindDateViewHolder(dateViewHolder: DateViewHolder, position: Int)
    fun setYearAndMonth(currentYear: String, currentMonth: String)
    fun setOnSetDateItemClickListener(listener: MainAdapter.OnSetDateItemClickListener, dateViewHolder: DateViewHolder)
    fun setUserData(eventJson: String?)
    fun setToDoView(eventJson: String?)
    fun onBindToDoViewHolder(toDoViewHolder: ToDoViewHolder, position: Int)
}