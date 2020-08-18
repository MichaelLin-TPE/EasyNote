package com.noted.checktimeapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.noted.checktimeapp.add_activity.EventObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel : ViewModel() {

    private var currentYear:Int = SimpleDateFormat("yyyy", Locale.TAIWAN).format(Date(System.currentTimeMillis())).toInt()

    val monthTextLiveData = MutableLiveData<String>(currentYear.toString()+"/"+SimpleDateFormat("MM", Locale.TAIWAN).format(Date(System.currentTimeMillis()))+"月")

    val recyclerViewWeekViewLiveData = MutableLiveData<ArrayList<String>>()

    val recyclerViewDateViewLiveData = MutableLiveData<RecyclerViewData>()

    val refreshRecyclerViewLiveData = MutableLiveData<RecyclerViewData>()

    val intentToAddEventActivityData = MutableLiveData<String>()

    val showToDoView = MutableLiveData<Boolean>(false)

    val refreshEvent = MutableLiveData<Boolean>(false)

    val isShowSelectMonthView = MutableLiveData<Boolean>(false)

    private val TAG = MainViewModel::class.java.simpleName

    private val monthArray = ArrayList<String>()



    private var currentMonthIndex :Int = 0
    override fun onCleared() {
        super.onCleared()
        Log.i(TAG,"onCleared MainViewModel")
    }

    fun setOnSelectMonthButtonClickListener(){
        isShowSelectMonthView.value = true
    }

    fun setOnNextButtonClickListener(textMonth : String){

        val month = textMonth.substring(5,textMonth.length)
        Log.i("Michael","textMonth : $month")
        for (i in 0 until monthArray.size){
            if (monthArray[i]+"月" == month){
                currentMonthIndex = i
                break
            }
        }
        currentMonthIndex += 1
        if (currentMonthIndex >= monthArray.size){
            currentMonthIndex = 0
            currentYear++
        }

        monthTextLiveData.value = "$currentYear/${monthArray[currentMonthIndex]}月"
        refreshRecyclerView(currentYear, monthArray[currentMonthIndex])
    }

    private fun getWeekDay(totalDate: String): Int {
        val simpleDate = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
        val currentDate :Date = simpleDate.parse(totalDate)
        return SimpleDateFormat("u", Locale.TAIWAN).format(currentDate).toInt()
    }

    //取得全天數
    private fun getTotalDate(currentYear: Int, month: Int): Any {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR,currentYear)
        calendar.set(Calendar.MONTH,month - 1)
        calendar.set(Calendar.DATE,1)
        calendar.roll(Calendar.DATE,-1)
        return calendar.get(Calendar.DATE)
    }

    fun setOnForwardButtonClickListener(textMonth : String){

        val month = textMonth.substring(5,textMonth.length)
        Log.i("Michael","textMonth : $month")
        for (i in 0 until monthArray.size){
            if (monthArray[i]+"月" == month){
                currentMonthIndex = i
                break
            }
        }
        currentMonthIndex -= 1
        if (currentMonthIndex < 0){
            currentMonthIndex = monthArray.size - 1
            currentYear--
        }

        monthTextLiveData.value = "$currentYear/${monthArray[currentMonthIndex]}月"

        refreshRecyclerView(currentYear,monthArray[currentMonthIndex])
    }

    private fun refreshRecyclerView(currentYear: Int, month: String) {

        val maxMonthDate:Int = getTotalDate(currentYear,month.toInt()) as Int
        val totalDate = "${currentYear}/${month}/01"
        val weekDay:Int = getWeekDay(totalDate)


        val dateArray = ArrayList<String>()
        for (i in 0 until (maxMonthDate+weekDay-1)){
            if (i < (weekDay-1)){
                dateArray.add("")
            }else{
                dateArray.add((i - (weekDay-2)).toString())
            }
        }
        if (dateArray.isNullOrEmpty()){
            return
        }
        val recyclerViewData = RecyclerViewData(currentYear.toString(),month,dateArray)
        refreshRecyclerViewLiveData.value = recyclerViewData
    }

    fun onActivityCreate() {
        monthArray.add("01")
        monthArray.add("02")
        monthArray.add("03")
        monthArray.add("04")
        monthArray.add("05")
        monthArray.add("06")
        monthArray.add("07")
        monthArray.add("08")
        monthArray.add("09")
        monthArray.add("10")
        monthArray.add("11")
        monthArray.add("12")
        showRecyclerView()
    }

    private fun showRecyclerView() {
        val weekArray = ArrayList<String>()
        weekArray.add("一")
        weekArray.add("二")
        weekArray.add("三")
        weekArray.add("四")
        weekArray.add("五")
        weekArray.add("六")
        weekArray.add("日")
        recyclerViewWeekViewLiveData.value = weekArray
        val monthMaxDate = getCurrentMonthMaxDate()
        Log.i("Michael","取得當月份天數 : $monthMaxDate")
        val currentMonth = SimpleDateFormat("MM", Locale.TAIWAN).format(Date(System.currentTimeMillis()))
        val monthFirstDate = "$currentYear/$currentMonth/01"
        val simpleDate = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
        val currentDate :Date = simpleDate.parse(monthFirstDate)
        val firstWeekDay :Int = SimpleDateFormat("u", Locale.TAIWAN).format(currentDate).toInt()
        Log.i("Michael","星期$firstWeekDay")


        val dateArray = ArrayList<String>()
        for (i in 0 until (monthMaxDate+firstWeekDay-1)){
            if (i < (firstWeekDay-1)){
                dateArray.add("")
            }else{
                dateArray.add((i - (firstWeekDay-2)).toString())
            }

        }
        if (dateArray.isNullOrEmpty()){
            return
        }
        val recyclerViewData = RecyclerViewData(currentYear.toString(),currentMonth,dateArray)
        showToDoView.value = true
        recyclerViewDateViewLiveData.value = recyclerViewData
    }

    private fun getCurrentMonthMaxDate(): Int {
        val calendar = Calendar.getInstance()
        //把日期設定為當月第一天
        calendar.set(Calendar.DATE,1)
        //日期回滾一天就是最後一天
        calendar.roll(Calendar.DATE,-1)
        return calendar.get(Calendar.DATE)
    }

    fun onSetDateItemClickListener(totalDate: String) {
        intentToAddEventActivityData.value = totalDate
    }

    fun onCatchNewEvent() {
        refreshEvent.value = true
    }

    fun onItemClickListener(month: String) {

        monthTextLiveData.value = "$currentYear/${month}月"

        refreshRecyclerView(currentYear,month)

    }
}