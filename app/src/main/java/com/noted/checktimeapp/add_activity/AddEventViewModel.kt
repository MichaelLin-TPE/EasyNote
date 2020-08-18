package com.noted.checktimeapp.add_activity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddEventViewModel : ViewModel() {

    val pageTitle = MutableLiveData<String>()

    val isShowTimePicker = MutableLiveData<Boolean>(false)

    val totalTimeLiveData = MutableLiveData<String>()

    val isBeforeToday = MutableLiveData<Boolean>(true)

    val isShowNoData = MutableLiveData<Boolean>(false)

    val showToast = MutableLiveData<String>()

    val saveEventData = MutableLiveData<String>()

    val eventRecyclerViewData = MutableLiveData<EventObject>()

    val refreshRecyclerView = MutableLiveData<EventObject>()

    val clearTimeAndEditContentData = MutableLiveData<Boolean>(false)

    val isSetAlarmNotification = MutableLiveData<AlarmNotification>()

    val removeAlarmNotification = MutableLiveData<AlarmNotification>()

    val isFinishPage = MutableLiveData<Boolean>(false)

    val isShowSelectModeDialog = MutableLiveData<Boolean>(false)

    val isShowEditContentDialog = MutableLiveData<EditContentDialogDataObject>()

    private var totalTime = ""

    private var totalDate = ""

    private var userDataArray = ArrayList<EventObject>()

    private var eventContent = ""

    private var isYesChecked = false

    private var isNoChecked = true

    private lateinit var eventObject: EventObject

    private lateinit var gson: Gson

    companion object {
        private const val EDIT = 0
        private const val DELETE = 1
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("Michael", "onCleared AddEventViewModel")
    }

    fun setOnBackButtonClickListener() {
        isFinishPage.value = true
    }

    fun onActivityCreate(totalDate: String, eventJson: String?) {
        this.totalDate = totalDate
        Log.i("Michael", "傳進來的JSON : $eventJson")
        val sdfTotalDate = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
        val dateYear: Date? = sdfTotalDate.parse(totalDate)
        val dateDate: Date? = sdfTotalDate.parse(totalDate)
        if (dateYear == null || dateDate == null) {
            Log.i("Michael", "無法取得數字日期")
            return
        }

        pageTitle.value = totalDate
        gson = Gson()
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
        val currentTime = sdf.format(Date(System.currentTimeMillis()))
        val nowTimeDate: Date? = sdf.parse(currentTime)
        val compareTimeDate: Date? = sdf.parse(totalDate)

        if (nowTimeDate == null || compareTimeDate == null){
            Log.i("Micheal","時間無法轉換")
            return
        }



        Log.i("Michael", "AddEventViewModel 接受到日期 : $totalDate , 現在日期 : $currentTime")
        if (currentTime != totalDate) {
            isBeforeToday.value = nowTimeDate.before(compareTimeDate)
        }
        if (eventJson.isNullOrEmpty()) {
            isShowNoData.value = true
            return
        }
        val turnsType = object : TypeToken<List<EventObject>>() {}.type
        userDataArray = gson.fromJson(eventJson, turnsType)
        if (userDataArray.isNullOrEmpty()) {
            isShowNoData.value = true
            return
        }
        var recyclerViewData: EventObject? = null
        for (data in userDataArray) {
            if (data.totalDate == totalDate) {
                if (data.eventArray.isNullOrEmpty()){
                    isShowNoData.value = true
                }else{
                    isShowNoData.value = false
                    recyclerViewData = data
                }

            }
        }
        if (recyclerViewData == null) {
            isShowNoData.value = true
        } else {
            isShowNoData.value = false
            eventRecyclerViewData.value = recyclerViewData
        }
    }

    fun setOnEventSaveButtonClickListener(eventContent: String?) {
        if (totalTime.isBlank()) {
            showToast.value = "請選擇幾點幾分提醒"
            return
        }
        if (eventContent.isNullOrBlank() || eventContent.isNullOrEmpty()) {
            showToast.value = "請輸入你要做甚麼事情"
            return
        }
        val requestCode: Int = (Math.random() * 999999).toInt()
        //這邊階者做
        val eventData = EventObject()
        if (isNoChecked) {

            if (userDataArray.isNullOrEmpty()) {
                val timeArray = ArrayList<String>()
                val eventArray = ArrayList<String>()
                val notificationRequestCodeArray = ArrayList<Int>()
                timeArray.add(totalTime)
                eventArray.add(eventContent)
                notificationRequestCodeArray.add(requestCode)
                eventData.totalDate = totalDate
                eventData.totalTime = timeArray
                eventData.eventArray = eventArray
                eventData.notificationRequestCodeArray = notificationRequestCodeArray
                val userDataArray = ArrayList<EventObject>()
                userDataArray.add(eventData)
                val eventJson = gson.toJson(userDataArray)
                saveEventData.value = eventJson
                showToast.value = "儲存成功"
                eventRecyclerViewData.value = eventData
            } else {
                var isFoundData = false
                for (data in userDataArray) {
                    if (data.totalDate == totalDate) {
                        data.totalTime.add(totalTime)
                        data.eventArray.add(eventContent)
                        data.notificationRequestCodeArray.add(requestCode)
                        isFoundData = true
                        refreshRecyclerView.value = data
                        break
                    }
                }
                if (!isFoundData) {
                    val timeArray = ArrayList<String>()
                    val eventArray = ArrayList<String>()
                    val notificationRequestCodeArray = ArrayList<Int>()
                    timeArray.add(totalTime)
                    eventArray.add(eventContent)
                    notificationRequestCodeArray.add(requestCode)
                    eventData.totalDate = totalDate
                    eventData.totalTime = timeArray
                    eventData.eventArray = eventArray
                    eventData.notificationRequestCodeArray = notificationRequestCodeArray
                    userDataArray.add(eventData)
                    val eventJson = gson.toJson(userDataArray)
                    saveEventData.value = eventJson
                    showToast.value = "儲存成功"
                    refreshRecyclerView.value = eventData
                } else {
                    val eventJson = gson.toJson(userDataArray)
                    saveEventData.value = eventJson
                    showToast.value = "儲存成功"
                }
            }
            clearTimeAndEditContentData.value = true
            isShowNoData.value = false

            //設置時間通知 此為單一事件

            createNotification("$totalDate $totalTime",eventContent,requestCode)

            return
        }


        if (isYesChecked) {
            val currentMonthDate =
                SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN).parse(totalDate) ?: return
            val currentMillis = currentMonthDate.time
            val currentYear = SimpleDateFormat("yyyy", Locale.TAIWAN).format(Date(currentMillis))
            val currentMonth =
                SimpleDateFormat("MM", Locale.TAIWAN).format(Date(currentMillis)).toInt()
            val currentDate = SimpleDateFormat("dd", Locale.TAIWAN).format(Date(currentMillis))

            if (currentMonth == 12) {
                //show errorCode
                return
            }
            if (userDataArray.isNullOrEmpty()) {
                userDataArray = ArrayList()

                for (i in currentMonth until 13) {
                    val notificationRequestCode: Int = (Math.random() * 999999).toInt()
                    val eventObject = EventObject()
                    val eventArray = ArrayList<String>()
                    val timeArray = ArrayList<String>()
                    val notificationRequestCodeArray = ArrayList<Int>()
                    val month = if (i < 10) "0$i" else i.toString()
                    eventObject.totalDate = "$currentYear/${month}/$currentDate"
                    eventArray.add(eventContent)
                    notificationRequestCodeArray.add(notificationRequestCode)
                    timeArray.add(totalTime)
                    eventObject.eventArray = eventArray
                    eventObject.totalTime = timeArray
                    eventObject.notificationRequestCodeArray = notificationRequestCodeArray
                    userDataArray.add(eventObject)
                    createNotification("${eventObject.totalDate} $totalTime",eventContent,notificationRequestCode)

                }
            } else {
                for (i in currentMonth until 13) {
                    val month = if (i < 10) "0$i" else i.toString()
                    var isDataFound = false
                    val notificationRequestCode: Int = (Math.random() * 999999).toInt()
                    for (data in userDataArray) {
                        if (data.totalDate == "$currentYear/$month/$currentDate") {
                            data.totalTime.add(totalTime)
                            data.eventArray.add(eventContent)
                            data.notificationRequestCodeArray.add(notificationRequestCode)
                            isDataFound = true
                            break
                        }
                    }
                    if (!isDataFound){
                        val eventObject = EventObject()
                        val eventArray = ArrayList<String>()
                        val timeArray = ArrayList<String>()
                        val notificationRequestCodeArray = ArrayList<Int>()
                        eventObject.totalDate = "$currentYear/$month/$currentDate"
                        eventArray.add(eventContent)
                        notificationRequestCodeArray.add(notificationRequestCode)
                        timeArray.add(totalTime)
                        eventObject.eventArray = eventArray
                        eventObject.totalTime = timeArray
                        eventObject.notificationRequestCodeArray = notificationRequestCodeArray
                        userDataArray.add(eventObject)
                    }

                    createNotification("${eventObject.totalDate} $totalTime",eventContent,notificationRequestCode)
                }
            }
            val eventJson = gson.toJson(userDataArray)
            Log.i("Michael","即將儲存的Json : $eventJson")
            saveEventData.value = eventJson
            showToast.value = "每月提醒設定成功"
            for (data in userDataArray){
                if (data.totalDate == totalDate){
                    refreshRecyclerView.value = data
                    isShowNoData.value = false
                    break
                }
            }
        }


    }

    fun setOnTimePickerButtonClickListener() {
        isShowTimePicker.value = true
    }

    fun onCatchTimeListener(hours: String, minute: String) {
        Log.i("Michael", "傳進來的 小時 :$hours, 分鐘 :$minute")
        if (hours.isBlank() || minute.isBlank()) {
            totalTimeLiveData.value =
                SimpleDateFormat("hh:mm a", Locale.TAIWAN).format(Date(System.currentTimeMillis()))
            totalTime =
                SimpleDateFormat("hh:mm a", Locale.TAIWAN).format(Date(System.currentTimeMillis()))
            return
        } else {
            val timeType = if (hours.toInt() > 12) "下午" else "上午"
            val isMorning = (hours.toInt() >= 10)
            val isMinute = (minute.toInt() >= 10)
            val minuteStr = if (isMinute) minute else "0$minute"
            totalTimeLiveData.value =
                if (isMorning) "$hours:$minuteStr $timeType" else "0$hours:$minuteStr $timeType"
            totalTime = "$hours:$minuteStr $timeType"
        }
    }

    fun onAddItemClickListener(eventContent: String, eventObject: EventObject) {
        this.eventContent = eventContent
        this.eventObject = eventObject
        Log.i("Michael", "eventContent : $eventContent")
        isShowSelectModeDialog.value = true

    }

    fun onEditDialogItemClickListener(position: Int) {
        when (position) {
            EDIT -> {
                var index = 0
                for (i in 0 until eventObject.eventArray.size){
                    if (eventObject.eventArray[i] == eventContent){
                        index = i
                    }
                }
                val timeDate = SimpleDateFormat("hh:mm a", Locale.TAIWAN).parse(eventObject.totalTime[index])

                if (timeDate == null){
                    Log.i("Michael","日期無法轉換")
                    return
                }
                val hour = SimpleDateFormat("hh", Locale.TAIWAN).format(Date(timeDate.time))
                val minute = SimpleDateFormat("mm", Locale.TAIWAN).format(Date(timeDate.time))
                val timeType = SimpleDateFormat("a", Locale.TAIWAN).format(Date(timeDate.time))

                val hourInt = if (hour.toInt() < 12 || timeType == "下午"){
                    hour.toInt() + 12
                }else{
                    hour.toInt()
                }
                val editContentObject = EditContentDialogDataObject(eventContent,hourInt.toString(),minute)
                isShowEditContentDialog.value = editContentObject
            }
            DELETE -> {
                var time = ""
                var totalDate = ""
                var notificationRequestCode = 0
                Log.i("Michael", "目前的JSON : ${gson.toJson(userDataArray)}")
                for (data in userDataArray) {
                    if (data.totalDate == eventObject.totalDate) {
                        totalDate = data.totalDate
                        for (i in 0 until data.eventArray.size) {
                            if (data.eventArray[i] == eventContent) {
                                Log.i("Michael", "有刪除")
                                time = data.totalTime[i]
                                notificationRequestCode = data.notificationRequestCodeArray[i]
                                data.eventArray.remove(eventContent)
                                data.totalTime.remove(time)
                                data.notificationRequestCodeArray.remove(notificationRequestCode)
                                break
                            }
                        }
                    }
                }

                val eventJson = gson.toJson(userDataArray)
                saveEventData.value = eventJson

                for (data in eventObject.eventArray) {
                    if (data == eventContent) {
                        eventObject.eventArray.remove(data)
                        break
                    }
                }
                if (eventObject.eventArray.isNullOrEmpty()) {
                    isShowNoData.value = true
                }
                refreshRecyclerView.value = eventObject
                removeNotification("$totalDate $time",eventContent,notificationRequestCode)
            }
        }
    }

    private fun removeNotification(wholeTime :String,eventContent:String,notificationRequestCode:Int){
        val sdf = SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.TAIWAN)
        val wholeDate = sdf.parse(wholeTime)
        if (wholeDate == null){
            Log.i("Michael","日期無法轉換")
            return
        }
        val year = SimpleDateFormat("yyyy", Locale.TAIWAN).format(Date(wholeDate.time))
        val month = SimpleDateFormat("MM", Locale.TAIWAN).format(Date(wholeDate.time))
        val date = SimpleDateFormat("dd", Locale.TAIWAN).format(Date(wholeDate.time))
        val hour = SimpleDateFormat("hh", Locale.TAIWAN).format(Date(wholeDate.time))
        val minute = SimpleDateFormat("mm", Locale.TAIWAN).format(Date(wholeDate.time))
        removeAlarmNotification.value = AlarmNotification(year.toInt(),month.toInt(),date.toInt(),hour.toInt(),minute.toInt(),eventContent,notificationRequestCode)
    }

    private fun createNotification(wholeTime: String,eventContent:String,notificationRequestCode: Int){
        val sdf = SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.TAIWAN)
        val wholeDate = sdf.parse(wholeTime)
        if (wholeDate == null){
            Log.i("Michael","日期無法轉換")
            return
        }
        val year = SimpleDateFormat("yyyy", Locale.TAIWAN).format(Date(wholeDate.time))
        val month = SimpleDateFormat("MM", Locale.TAIWAN).format(Date(wholeDate.time))
        val date = SimpleDateFormat("dd", Locale.TAIWAN).format(Date(wholeDate.time))
        val hour = SimpleDateFormat("hh", Locale.TAIWAN).format(Date(wholeDate.time))
        val minute = SimpleDateFormat("mm", Locale.TAIWAN).format(Date(wholeDate.time))
        isSetAlarmNotification.value = AlarmNotification(year.toInt(),month.toInt(),date.toInt(),hour.toInt(),minute.toInt(),eventContent,notificationRequestCode)
    }

    fun onCatchNewUserData(eventJson: String?) {

        if (eventJson == null) {
            Log.i("Michael", "eventJson is null")
            return
        }
        userDataArray = gson.fromJson(eventJson, object : TypeToken<List<EventObject>>() {}.type)
    }

    fun setOnYesCheckedButtonClickListener(isChecked: Boolean) {
        this.isYesChecked = isChecked
    }

    fun setOnNoCheckedButtonClickListener(isChecked: Boolean) {
        this.isNoChecked = isChecked
    }

    fun setOnConfirmButtonClickListener(eventContentModify: String, totalTime: String) {
        Log.i("Michael","修改過後的內容 事件 : $eventContentModify 時間 : $totalTime")
        val newNotificationRequestCode = Math.random() * 999999
        var oldNotificationRequestCode = 0
        var newWholeTime = ""
        var oldWholeTime = ""
        val oldEventContent = eventContent
        for (i in 0 until eventObject.eventArray.size){
            if (eventObject.eventArray[i] == eventContent){
                eventObject.eventArray[i] = eventContentModify
                oldWholeTime = "${eventObject.totalDate} ${eventObject.totalTime[i]}"
                eventObject.totalTime[i] = totalTime
                newWholeTime = "${eventObject.totalDate} ${eventObject.totalTime[i]}"
                oldNotificationRequestCode = eventObject.notificationRequestCodeArray[i]
                eventObject.notificationRequestCodeArray[i] = newNotificationRequestCode.toInt()
                break
            }
        }
        //去除掉原本的NOTIFICATION
        removeNotification(oldWholeTime,oldEventContent,oldNotificationRequestCode)

        refreshRecyclerView.value = eventObject

        for (data in userDataArray){
            if (data.totalDate == eventObject.totalDate){
                data.totalTime = eventObject.totalTime
                data.eventArray = eventObject.eventArray
                break
            }
        }
        val eventJson = gson.toJson(userDataArray)
        saveEventData.value = eventJson
        //創建一筆新的NOTIFICATION
        createNotification(newWholeTime, eventContentModify,newNotificationRequestCode.toInt())
    }
}