package com.noted.checktimeapp.to_do_view

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.noted.checktimeapp.add_activity.EventObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ToDoViewModel {

    val isShowSearchNoData = ObservableField<Boolean>(false)

    val recyclerViewData = MutableLiveData<ToDoDataObject>()

    val isShowRemoveButton = ObservableField<Boolean>(false)

    val isShowNoticeDialog = ObservableField<Boolean>(false)

    private lateinit var userDataArray: ArrayList<EventObject>

    private lateinit var currentYear: String

    private lateinit var currentMonth: String

    val refreshRecyclerViewData = MutableLiveData<ToDoDataObject>()
    val saveUserData = MutableLiveData<String>()

    fun setOnRemoveButtonClickListener(){
        isShowNoticeDialog.set(true)
        isShowNoticeDialog.set(false)
    }

    fun setData(
        userDataArray: ArrayList<EventObject>,
        currentMonth: String,
        currentYear: String
    ) {
        this.userDataArray = userDataArray
        this.currentMonth = currentMonth
        this.currentYear = currentYear

        if (userDataArray.isNullOrEmpty()){
            isShowSearchNoData.set(true)
            return
        }


        //test
        val gson = Gson()
        Log.i("Michael","傳進來的JSON : ${gson.toJson(userDataArray)}")

        var isShowNoData = true
        var itemCount = 0
        var timeItemCount = 0
        for (data in userDataArray){
            Log.i("Michael","目前日期 : $currentYear/$currentMonth")
            if (data.totalDate.contains("$currentYear/$currentMonth")){
                itemCount += data.eventArray.size
                timeItemCount += data.totalTime.size
                isShowNoData = false
                if (data.totalTime.isNullOrEmpty() && timeItemCount == 0){
                    Log.i("Michael","沒有 timeArray")
                    isShowNoData = true
                }
                if (data.eventArray.isNullOrEmpty() && itemCount == 0){
                    Log.i("Michael","沒有eventArray")
                    isShowNoData = true
                }
            }
        }
        Log.i("Michael","itemCount = $itemCount")
        if (isShowNoData || itemCount == 0){
            isShowSearchNoData.set(true)
            isShowRemoveButton.set(false)
            recyclerViewData.value = ToDoDataObject(ArrayList(),"","")
            return
        }



        isShowSearchNoData.set(false)
        isShowRemoveButton.set(true)
        val dataObject = ToDoDataObject(userDataArray,currentYear,currentMonth)
        recyclerViewData.value = dataObject

    }

    fun onDialogConfirmClickListener() {
        val sdf = SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.TAIWAN)
        val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN).format(Date(System.currentTimeMillis()))
        val currentHour = SimpleDateFormat("hh", Locale.TAIWAN).format(Date(System.currentTimeMillis()))
        val currentMinute = SimpleDateFormat("mm", Locale.TAIWAN).format(Date(System.currentTimeMillis()))
        val currentTimeType = SimpleDateFormat("a", Locale.TAIWAN).format(Date(System.currentTimeMillis()))
        val totalCurrentTime = "$currentDate $currentHour:$currentMinute $currentTimeType"
        val curDate  = sdf.parse(totalCurrentTime) ?: return
        var dataDate : Date?
        for (data in userDataArray){
            Log.i("Michael","${data.totalDate} 比對 $currentYear/$currentMonth")
            if (data.totalDate.contains("$currentYear/$currentMonth")){
                val loopTime  = data.totalTime.iterator()
                val loopEvent = data.eventArray.iterator()
                val notificationRequestCodeLoop = data.notificationRequestCodeArray.iterator()
                while (loopTime.hasNext() && loopEvent.hasNext() && notificationRequestCodeLoop.hasNext()){
                    loopEvent.next()
                    notificationRequestCodeLoop.next()
                    dataDate = sdf.parse("${data.totalDate} ${loopTime.next()}")
                    if (curDate.after(dataDate) || curDate.time >= dataDate.time){
                        Log.i("Michael","符合條件 開始進行刪除")
                        loopTime.remove()
                        loopEvent.remove()
                        notificationRequestCodeLoop.remove()
                    }
                }
            }
        }
        val gson = Gson()
        val eventJson = gson.toJson(userDataArray)
        saveUserData.value = eventJson
        Log.i("Michael","變更後的資料為 $eventJson")
        val dataObject = ToDoDataObject(userDataArray,currentYear,currentMonth)
        refreshRecyclerViewData.value = dataObject
    }
}