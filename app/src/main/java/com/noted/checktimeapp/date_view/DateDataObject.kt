package com.noted.checktimeapp.date_view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import com.noted.checktimeapp.MainAdapter
import com.noted.checktimeapp.R
import com.noted.checktimeapp.add_activity.EventObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DateDataObject() {
    private val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN).format(Date(System.currentTimeMillis()))
    val dateLiveData = ObservableField<String>()
    val itemColor = ObservableField<Drawable>()
    val isShowFirstEventPic = ObservableField<Boolean>(false)
    val isShowSecondEventPic = ObservableField<Boolean>(false)

    private var date:String = ""
    private var currentYear = ""
    private var currentMonth = ""
    fun showView(
        date: String,
        currentYear: String,
        currentMonth: String,
        context: Context,
        userDataArray: ArrayList<EventObject>
    ) {
        if (date.isEmpty()){
            dateLiveData.set(date)
            isShowSecondEventPic.set(false)
            isShowFirstEventPic.set(false)
            this.date = date
            itemColor.set(ContextCompat.getDrawable(context,
                R.drawable.date_shape_no_change
            ))
            return
        }
        this.date = date
        this.currentYear = currentYear
        this.currentMonth = currentMonth
        val dateInt:Int = date.toInt()
        var dateString = date
        if (dateInt < 10){
            dateString = "0$dateInt"
        }
        if (currentDate == "${currentYear}/${currentMonth}/$dateString"){
            dateLiveData.set(date)
            itemColor.set(ContextCompat.getDrawable(context,
                R.drawable.date_shape_change
            ))
        }else{
            dateLiveData.set(date)
            itemColor.set(ContextCompat.getDrawable(context,
                R.drawable.date_shape_no_change
            ))
        }
        if (userDataArray.isNullOrEmpty()){
            Log.i("Michael","userDataArray is null")
            isShowFirstEventPic.set(false)
            isShowSecondEventPic.set(false)
            return
        }
        var isFoundData = false
        for (data in userDataArray){
            if (data.totalDate == "${currentYear}/${currentMonth}/$dateString"){
                when {
                    data.eventArray.size > 1 -> {
                        isShowFirstEventPic.set(true)
                        isShowSecondEventPic.set(true)
                    }
                    data.eventArray.size == 1 -> {
                        isShowFirstEventPic.set(true)
                        isShowSecondEventPic.set(false)
                    }
                    else -> {
                        isShowSecondEventPic.set(false)
                        isShowSecondEventPic.set(false)
                    }
                }
                isFoundData = true
                break
            }
        }
        if (!isFoundData){
            isShowFirstEventPic.set(false)
            isShowSecondEventPic.set(false)
        }

    }

    fun setOnSetDateItemClickListener(listener: MainAdapter.OnSetDateItemClickListener) {
        if (date.isEmpty()){
            return
        }
        val dateStr = if (date.toInt() < 10) "0$date" else date

        listener.onItemClick("$currentYear/$currentMonth/$dateStr")
    }


}