package com.noted.checktimeapp.week_view

import android.graphics.Color
import androidx.databinding.ObservableField

data class WeekDayObject(val weekDay:String) {
    val weekLiveData = ObservableField<String>(weekDay)
    val weekStringColor = ObservableField<Int>(
        if (weekDay == "六" || weekDay == "日") Color.GREEN else Color.WHITE
    )
}