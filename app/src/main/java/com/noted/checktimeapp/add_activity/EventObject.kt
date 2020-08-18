package com.noted.checktimeapp.add_activity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class EventObject() :Serializable {
    @SerializedName("total_date")
    var totalDate:String = ""
    @SerializedName("total_time")
    var totalTime = ArrayList<String>()
    @SerializedName("event_array")
    var eventArray = ArrayList<String>()
    @SerializedName("notification_request_code_array")
    var notificationRequestCodeArray = ArrayList<Int>()
}