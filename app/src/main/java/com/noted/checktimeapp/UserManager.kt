package com.noted.checktimeapp

import android.content.Context
import android.content.SharedPreferences

class UserManager(context: Context) {

    private var sp: SharedPreferences = context.getSharedPreferences("userData",Context.MODE_PRIVATE)

    companion object{
        @Volatile private var instance :UserManager? = null

        fun getInstance(context: Context):UserManager{
            return if (instance == null){
                UserManager(context)
            }else{
                instance!!
            }
        }
    }
    fun saveEventData(evenJson :String){
        val editor = sp.edit()
        editor.putString("event_json",evenJson)
        editor.apply()
    }
    fun getEventJson(): String? {
        return sp.getString("event_json","")
    }
}