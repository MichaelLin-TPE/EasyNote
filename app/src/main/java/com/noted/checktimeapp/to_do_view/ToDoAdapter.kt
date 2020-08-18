package com.noted.checktimeapp.to_do_view

import android.annotation.SuppressLint
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.noted.checktimeapp.R
import com.noted.checktimeapp.add_activity.EventObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ToDoAdapter() : RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {


    private var userDataArray: ArrayList<EventObject> = ArrayList()
    private var currentYear: String = ""
    private var currentMonth: String = ""

    fun setData(userDataArray: ArrayList<EventObject>,
                currentYear: String,
                currentMonth: String){
        this.userDataArray = userDataArray
        this.currentYear = currentYear
        this.currentMonth = currentMonth
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.to_do_item,
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        var itemCount = 0

        if (userDataArray.isNullOrEmpty()){
            return 0
        }
        for (data in userDataArray) {
            if (data.totalDate.contains("$currentYear/$currentMonth")) {
                itemCount += data.totalTime.size
            }
        }
        return itemCount
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val dataArray = ArrayList<String>()
        val timeArray = ArrayList<String>()
        var index = 0
        for (data in userDataArray) {
            if (data.totalDate.contains("$currentYear/$currentMonth")) {

                if (data.totalTime.isNullOrEmpty() || data.eventArray.size == 0){
                    continue
                }
                for (i in 0 until data.totalTime.size) {
                    index++
                    dataArray.add("$index. ${data.totalDate} ${data.eventArray[i]} ${data.totalTime[i]}")
                    timeArray.add("${data.totalDate} ${data.totalTime[i]}")
                }
            }
        }

        if (dataArray.isNullOrEmpty()){
            return
        }
        val eventString = dataArray[position]
        val time = timeArray[position]
        val sdf = SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.TAIWAN)

        val currentDateYear = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN).format(Date(System.currentTimeMillis()))

        val currentHour = SimpleDateFormat("hh", Locale.TAIWAN).format(Date(System.currentTimeMillis()))
        val currentMinute = SimpleDateFormat("mm", Locale.TAIWAN).format(Date(System.currentTimeMillis()))
        val currentTimeType = SimpleDateFormat("a",Locale.TAIWAN).format(Date(System.currentTimeMillis()))

        var hours = ""
        if (currentTimeType.equals("下午") && currentHour.toInt() < 10){
            hours = (currentHour.toInt()+12).toString()
        }else{
            hours = currentHour
        }
        val currentTime = "$currentDateYear $hours:$currentMinute $currentTimeType"

        val currentDate: Date? = sdf.parse(currentTime)
        val dataDate: Date? = sdf.parse(time)
        if (currentDate == null || dataDate == null) {
            return
        }
        if (currentDate.time >= dataDate.time || currentDate.after(dataDate)) {
            val paint = holder.content.paint
            paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
            paint.isAntiAlias = true
            holder.content.text = "$eventString 時間已過"
        } else {
            holder.content.text = eventString
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var content: TextView = itemView.findViewById(R.id.to_do_item_content)

    }
}