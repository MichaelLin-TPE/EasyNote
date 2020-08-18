package com.noted.checktimeapp.to_do_view

import com.noted.checktimeapp.add_activity.EventObject

data class ToDoDataObject(val userDataArray :ArrayList<EventObject>, val currentYear:String, val currentMonth:String) {
}