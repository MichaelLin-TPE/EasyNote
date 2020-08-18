package com.noted.checktimeapp

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.noted.checktimeapp.add_activity.EventObject
import com.noted.checktimeapp.date_view.DateViewHolder
import com.noted.checktimeapp.to_do_view.ToDoViewHolder
import com.noted.checktimeapp.week_view.WeekViewHolder
import kotlin.collections.ArrayList

class MainViewPresenterImpl : MainViewPresenter {

    private var weekArray = ArrayList<String>()

    private var dateArray = ArrayList<String>()

    private var currentYear:String = ""

    private var currentMonth:String = ""

    private var isShowWeek = false

    private var isShowDate = false

    private var isShowToDo = false

    private val WEEK_VIEW = 0

    private val DATE_VIEW = 1

    private val TO_DO = 2
    private var itemCount = 0

    private var userDataArray = ArrayList<EventObject>()

    override fun getItemViewType(position: Int) : Int {

        if (isShowWeek && position < weekArray.size){

            return WEEK_VIEW
        }
        if (isShowDate && position >= weekArray.size && position < (dateArray.size + weekArray.size)){

            return DATE_VIEW
        }
        if (isShowToDo && position == (dateArray.size + weekArray.size) && position >= (dateArray.size + weekArray.size)){
            return TO_DO
        }

        return 0
    }

    override fun setWeekData(it: ArrayList<String>) {
        this.weekArray = it
        isShowWeek = true
    }

    override fun getItemCount(): Int {
        if (isShowWeek){
            itemCount = weekArray.size
        }
        if (isShowDate){
            itemCount += dateArray.size
        }
        if (isShowToDo){
            itemCount +=1
        }
        return itemCount
    }

    override fun onBindWeekViewHolder(weekViewHolder: WeekViewHolder, position: Int) {
        if (!isShowWeek){
            return
        }
        weekViewHolder.setData(weekArray[position])
    }

    override fun setDateData(it: ArrayList<String>) {
        this.dateArray = it
        isShowDate = true
    }

    override fun onBindDateViewHolder(dateViewHolder: DateViewHolder, position: Int) {
        if (!isShowDate){
            return
        }
        val itemPosition = position - weekArray.size
        dateViewHolder.setData(dateArray[itemPosition],currentYear,currentMonth,userDataArray)
    }

    override fun setYearAndMonth(currentYear: String, currentMonth: String) {
        this.currentYear = currentYear
        this.currentMonth = currentMonth
    }

    override fun setOnSetDateItemClickListener(
        listener: MainAdapter.OnSetDateItemClickListener,
        dateViewHolder: DateViewHolder
    ) {
        dateViewHolder.setOnSetDateItemClickListener(listener)
    }

    override fun setUserData(eventJson: String?) {
        val gson = Gson()
        userDataArray = gson.fromJson(eventJson,object :TypeToken<List<EventObject>>(){}.type)
    }

    override fun setToDoView(eventJson: String?) {
//        if (eventJson.isNullOrEmpty()){
//            isShowToDo = false
//            return
//        }
        isShowToDo = true
    }

    override fun onBindToDoViewHolder(toDoViewHolder: ToDoViewHolder, position: Int) {
        toDoViewHolder.setData(userDataArray,currentMonth,currentYear)
    }

    override fun setOnRemoveButtonClickListener(
        removeListener: MainAdapter.OnSetRemoveButtonClickListener,
        holder: ToDoViewHolder
    ) {
        holder.setRemoveButtonClickListener(removeListener)
    }

}