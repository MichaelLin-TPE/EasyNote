package com.noted.checktimeapp.date_view

import android.content.Context
import android.util.TypedValue
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.noted.checktimeapp.MainAdapter
import com.noted.checktimeapp.add_activity.EventObject
import com.noted.checktimeapp.databinding.DateViewBinding
import kotlin.collections.ArrayList

class DateViewHolder(private val dataBinding: DateViewBinding, private val context: Context) : RecyclerView.ViewHolder(dataBinding.root) {


    private lateinit var dataDateObject: DateDataObject

    init {
        val metrics = context.resources.displayMetrics
        //取得螢幕寬度
        val width: Float = context.resources.displayMetrics.widthPixels.toFloat()
        //除以7
        val singleItemSize: Float = width / 7
        //轉成DP
        val singleItemDb:Float = singleItemSize / metrics.density

        val pix :Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,singleItemDb,context.resources.displayMetrics)
            .toInt()
        val params = ConstraintLayout.LayoutParams(pix,pix)
        dataDateObject = DateDataObject()
        dataBinding.dateLayout.layoutParams = params
    }
    fun setData(
        date: String,
        currentYear: String,
        currentMonth: String,
        userDataArray: ArrayList<EventObject>
    ) {
        dataDateObject.showView(date,currentYear,currentMonth,context,userDataArray)
        dataBinding.vm = dataDateObject
        dataBinding.executePendingBindings()
    }

    fun setOnSetDateItemClickListener(listener: MainAdapter.OnSetDateItemClickListener) {
        dataBinding.listener = listener
    }

}