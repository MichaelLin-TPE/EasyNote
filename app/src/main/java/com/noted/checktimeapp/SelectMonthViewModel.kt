package com.noted.checktimeapp

import androidx.databinding.ObservableField

class SelectMonthViewModel(private val month:String) {

    val monthStringLiveData = ObservableField<String>(month+"月")

    fun setOnSelectMonthItemClickListener(listener: SelectMonthAdapter.OnItemClickListener) {
        listener.onClick(month)
    }
}