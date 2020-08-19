package com.noted.checktimeapp.view_model

import com.noted.checktimeapp.RecyclerViewData

interface MainRepository {

    fun getMonthArray():ArrayList<String>

    fun getWeekArray():ArrayList<String>
    fun onGetRecyclerViewData(onGetRecyclerViewDataListener: OnGetRecyclerViewDataListener)
    fun getTotalDate(currentYear: Int, month: Int): Any

    fun getWeekDay(totalDate: String): Int

    fun getCurrentMonthMaxDate() : Int
    fun setOnNextButtonListener(textMonth: String, onNextButtonClickListener: OnNextButtonClickListener)
    fun setOnForwardButtonClickListener(textMonth: String, onForwardButtonClickListener: MainRepository.OnForwardButtonClickListener)

    interface OnGetRecyclerViewDataListener{
        fun onSuccess(recyclerViewData : RecyclerViewData)
        fun onFail(errorCode:String)
    }

    interface OnNextButtonClickListener{
        fun onSuccess(date:String,recyclerViewData: RecyclerViewData?)
        fun onFail(errorCode: String)
    }
    interface OnForwardButtonClickListener{
        fun onSuccess(date:String,recyclerViewData: RecyclerViewData?)
        fun onFail(errorCode: String)
    }
}