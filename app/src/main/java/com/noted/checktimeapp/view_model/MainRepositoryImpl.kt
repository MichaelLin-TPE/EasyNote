package com.noted.checktimeapp.view_model

import android.util.Log
import com.noted.checktimeapp.RecyclerViewData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainRepositoryImpl : MainRepository {

    private var currentMonthIndex :Int = 0

    private var currentYear = SimpleDateFormat("yyyy", Locale.TAIWAN).format(Date(System.currentTimeMillis())).toInt()

    override fun getMonthArray(): ArrayList<String> {
        val monthArray = ArrayList<String>()
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
        return monthArray
    }

    override fun getWeekArray(): ArrayList<String> {
        val weekArray = ArrayList<String>()
        weekArray.add("一")
        weekArray.add("二")
        weekArray.add("三")
        weekArray.add("四")
        weekArray.add("五")
        weekArray.add("六")
        weekArray.add("日")
        return weekArray
    }

    override fun onGetRecyclerViewData(onGetRecyclerViewDataListener: MainRepository.OnGetRecyclerViewDataListener) {

        val monthMaxDate = getCurrentMonthMaxDate()
        val currentMonth = SimpleDateFormat("MM", Locale.TAIWAN).format(Date(System.currentTimeMillis()))
        val monthFirstDate = "$currentYear/$currentMonth/01"
        val simpleDate = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
        val currentDate : Date = simpleDate.parse(monthFirstDate) ?: return
        val firstWeekDay :Int = SimpleDateFormat("u", Locale.TAIWAN).format(currentDate).toInt()
        val dateArray = ArrayList<String>()
        for (i in 0 until (monthMaxDate+firstWeekDay-1)){
            if (i < (firstWeekDay-1)){
                dateArray.add("")
            }else{
                dateArray.add((i - (firstWeekDay-2)).toString())
            }

        }
        if (dateArray.isNullOrEmpty()){
            onGetRecyclerViewDataListener.onFail("資料結構錯誤 : dateArray is null")
            return
        }
        val recyclerViewData = RecyclerViewData(currentYear.toString(),currentMonth,dateArray)
        onGetRecyclerViewDataListener.onSuccess(recyclerViewData)

    }

    override fun getCurrentMonthMaxDate(): Int {
        val calendar = Calendar.getInstance()
        //把日期設定為當月第一天
        calendar.set(Calendar.DATE,1)
        //日期回滾一天就是最後一天
        calendar.roll(Calendar.DATE,-1)
        return calendar.get(Calendar.DATE)
    }

    override fun setOnNextButtonListener(
        textMonth: String,
        onNextButtonClickListener: MainRepository.OnNextButtonClickListener
    ) {
        val month = textMonth.substring(5,textMonth.length)
        Log.i("Michael","textMonth : $month")
        for (i in 0 until getMonthArray().size){
            if (getMonthArray()[i]+"月" == month){
                currentMonthIndex = i
                break
            }
        }
        currentMonthIndex += 1
        if (currentMonthIndex >= getMonthArray().size){
            currentMonthIndex = 0
            currentYear++
        }
        val monthTextLiveDataString = "$currentYear/${getMonthArray()[currentMonthIndex]}月"
        onNextButtonClickListener.onSuccess(monthTextLiveDataString,getRefreshData(currentYear,getMonthArray()[currentMonthIndex]))

    }

    override fun setOnForwardButtonClickListener(
        textMonth: String,
        onForwardButtonClickListener: MainRepository.OnForwardButtonClickListener
    ) {

    }

    private fun getRefreshData(currentYear: Int, month: String): RecyclerViewData? {
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
            return null
        }
        return RecyclerViewData(currentYear.toString(),month,dateArray)
    }

    //取得全天數
    override fun getTotalDate(currentYear: Int, month: Int): Any {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR,currentYear)
        calendar.set(Calendar.MONTH,month - 1)
        calendar.set(Calendar.DATE,1)
        calendar.roll(Calendar.DATE,-1)
        return calendar.get(Calendar.DATE)
    }

    override fun getWeekDay(totalDate: String): Int {
        val simpleDate = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
        val currentDate : Date = simpleDate.parse(totalDate) ?: return 0
        return SimpleDateFormat("u", Locale.TAIWAN).format(currentDate).toInt()
    }


}