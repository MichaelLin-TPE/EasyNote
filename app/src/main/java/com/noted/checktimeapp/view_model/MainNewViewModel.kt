package com.noted.checktimeapp.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.noted.checktimeapp.RecyclerViewData

class MainNewViewModel(private val mainRepository: MainRepository) : ViewModel() {

    val recyclerViewWeekViewLiveData = MutableLiveData<ArrayList<String>>()

    val errorDialog = MutableLiveData<String>()

    val recyclerViewDataViewLiveData = MutableLiveData<RecyclerViewData>()

    val intentToAddEventActivityData = MutableLiveData<String>()

    val showToDoView = MutableLiveData<Boolean>(false)

    val refreshRecyclerViewLiveData = MutableLiveData<RecyclerViewData>()

    val monthTextLiveData = MutableLiveData<String>()


    fun setOnNextButtonClickListener(textMonth :String){
        mainRepository.setOnNextButtonListener(textMonth,onNextButtonClickListener)
    }

    fun setOnForwardButtonClickListener(textMonth: String){
        mainRepository.setOnForwardButtonClickListener(textMonth,onForwardButtonClickListener)
    }

    private var onForwardButtonClickListener = object  : MainRepository.OnForwardButtonClickListener{
        override fun onSuccess(date: String, recyclerViewData: RecyclerViewData?) {
            //做到這邊還沒做完
        }

        override fun onFail(errorCode: String) {

        }

    }

    private var onNextButtonClickListener = object :MainRepository.OnNextButtonClickListener{
        override fun onSuccess(date: String, recyclerViewData: RecyclerViewData?) {
            if (recyclerViewData == null){
                errorDialog.value = "資料結構錯誤 : RecyclerViewData is null"
                return
            }
            monthTextLiveData.value = date
            refreshRecyclerViewLiveData.value = recyclerViewData

        }

        override fun onFail(errorCode: String) {
            errorDialog.value = errorCode
        }

    }


    override fun onCleared() {
        super.onCleared()
        Log.i("Michael","onCleared MainNewViewModel")
    }

    fun onActivityCreate() {
        recyclerViewWeekViewLiveData.value = mainRepository.getWeekArray()
        mainRepository.onGetRecyclerViewData(onGetRecyclerViewDataListener)
    }

    fun onSetDateItemClickListener(totalDate: String) {
        intentToAddEventActivityData.value = totalDate
    }

    private var onGetRecyclerViewDataListener = object :MainRepository.OnGetRecyclerViewDataListener{
        override fun onSuccess(recyclerViewData: RecyclerViewData) {
            showToDoView.value = true
            recyclerViewDataViewLiveData.value = recyclerViewData
        }

        override fun onFail(errorCode: String) {
            errorDialog.value = errorCode
        }

    }

    open class Factory(private val mainRepository: MainRepository): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainNewViewModel(mainRepository) as T
        }
    }
}