package com.noted.checktimeapp.week_view

import android.content.Context
import android.util.TypedValue
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.noted.checktimeapp.databinding.WeekViewBinding
import com.noted.checktimeapp.week_view.WeekDayObject

class WeekViewHolder(
    var dataBinding: WeekViewBinding,
    context: Context
) : RecyclerView.ViewHolder(dataBinding.root) {

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
        dataBinding.weekLayout.layoutParams = params
    }

    fun setData(weekDay: String) {
        val weekDayObject =
            WeekDayObject(weekDay)
        dataBinding.vm = weekDayObject
        dataBinding.executePendingBindings()
    }


}