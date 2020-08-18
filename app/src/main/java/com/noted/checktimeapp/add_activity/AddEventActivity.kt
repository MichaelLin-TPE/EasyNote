package com.noted.checktimeapp.add_activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.noted.checktimeapp.MainActivity
import com.noted.checktimeapp.R
import com.noted.checktimeapp.UserManager
import com.noted.checktimeapp.databinding.ActivityAddEventBinding
import com.noted.checktimeapp.databinding.EditContentDialogLayoutBinding
import java.util.*

class AddEventActivity : AppCompatActivity() {

    private lateinit var dataBinding : ActivityAddEventBinding

    private val viewModel : AddEventViewModel by viewModels()

    private var hours = ""

    private var minute = ""

    private var adapter: AddAdapter? = null

    private lateinit var editContentDialogDataBinding : EditContentDialogLayoutBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_event)
        dataBinding.vm = viewModel
        dataBinding.lifecycleOwner = this

        initBundle()

        dataBinding.addEventEdit.addTextChangedListener(object : TextWatcher{

            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dataBinding.eventContent = s.toString()
                Log.i("Michael","字的變化 : ${s.toString()}")
            }

        })

        viewModel.isShowTimePicker.observeForever {
            if (!it){
                return@observeForever
            }
            val timePicker = TimePicker(this)
            AlertDialog.Builder(this)
                .setView(timePicker)
                .setPositiveButton(resources.getString(R.string.confirm)
                ) { _, _ ->
                    viewModel.onCatchTimeListener(hours, minute)
                }.create().show()
            timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
                Log.i("Michael","hour : $hourOfDay , minute : $minute")
                this.hours = hourOfDay.toString()
                this.minute = minute.toString()
            }
        }
        viewModel.showToast.observeForever {
            Toast.makeText(this,it,Toast.LENGTH_LONG).show()
        }

        viewModel.saveEventData.observeForever {
            Log.i("Michael","即將儲存的JSON : $it")
            UserManager.getInstance(this).saveEventData(it)
            viewModel.onCatchNewUserData(UserManager.getInstance(this).getEventJson())
        }

        viewModel.eventRecyclerViewData.observeForever {
            if (adapter == null){
                adapter = AddAdapter()
            }
            adapter!!.setData(it)
            dataBinding.addRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
            dataBinding.addRecyclerView.adapter = adapter
            adapter!!.setOnAddItemClickListener(object : AddAdapter.OnAddItemClickListener{
                override fun onClick(eventContent: String) {
                       viewModel.onAddItemClickListener(eventContent,it)
                }
            })
        }

        viewModel.refreshRecyclerView.observeForever {
            if (adapter == null){
                adapter = AddAdapter()
                adapter!!.setData(it)
                dataBinding.addRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
                dataBinding.addRecyclerView.adapter = adapter
            }else{
                adapter!!.setData(it)
                adapter!!.notifyDataSetChanged()
            }
            adapter!!.setOnAddItemClickListener(object:AddAdapter.OnAddItemClickListener{
                override fun onClick(eventContent: String) {
                    viewModel.onAddItemClickListener(eventContent,it)
                }

            })

        }
        viewModel.clearTimeAndEditContentData.observeForever {
            dataBinding.addEventEdit.setText("")
            dataBinding.addTimeInfo.text = ""
        }

        viewModel.isSetAlarmNotification.observeForever {
            //設置鬧鈴
            Log.i("Michael","設置鬧鈴")
            val calendar = Calendar.getInstance()
            calendar.set(it.year,it.month-1 ,it.date , it.hours,it.minutes,0)
            val intent = Intent(this,AlarmReceiver::class.java)
            intent.putExtra("event",it.event)
            val pending = PendingIntent.getBroadcast(this,it.requestCode,intent,0)
            val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarm.setExact(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,pending)

        }

        viewModel.removeAlarmNotification.observeForever {
            Log.i("Michael","移除鬧鐘")
            val calendar = Calendar.getInstance()
            calendar.set(it.year,it.month-1 ,it.date , it.hours,it.minutes,0)
            val intent = Intent(this,AlarmReceiver::class.java)
            intent.putExtra("event",it.event)
            val pending = PendingIntent.getBroadcast(this,it.requestCode,intent,0)
            val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarm.cancel(pending)
        }

        viewModel.isFinishPage.observeForever {
            if (!it){
                return@observeForever
            }
            finish()
        }


        viewModel.isShowSelectModeDialog.observeForever {
            if (!it){
                return@observeForever
            }
            val listArray = arrayOf("編輯","刪除")
            val builder = AlertDialog.Builder(this)
                .setItems(listArray
                ) { _, position ->
                    viewModel.onEditDialogItemClickListener(position)
                }
            val dialog = builder.create()
            dialog.show()
        }



        viewModel.isShowEditContentDialog.observeForever {

            editContentDialogDataBinding = EditContentDialogLayoutBinding.inflate(LayoutInflater.from(this),null,false)
            editContentDialogDataBinding.editContentEdit.setText(it.eventContent)
            editContentDialogDataBinding.editContentTimePicker.hour = it.hour.toInt()
            editContentDialogDataBinding.editContentTimePicker.minute = it.minutes.toInt()

            var hours = ""
            var minutes = ""
            var timeType = ""
            editContentDialogDataBinding.editContentTimePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
                timeType = if (hourOfDay > 12) "下午" else "上午"
                hours = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
                minutes = if (minute < 10) "0$minute" else minute.toString()
            }

            val builder = AlertDialog.Builder(this)
                .setView(editContentDialogDataBinding.root)
            val dialog = builder.create()
            dialog.show()
            editContentDialogDataBinding.editContentBtnConfirm.setOnClickListener {
                val eventContent = editContentDialogDataBinding.editContentEdit.text.toString()
                viewModel.setOnConfirmButtonClickListener(eventContent,"$hours:$minutes $timeType")
                dialog.dismiss()
            }
        }



        //監聽RadioButton 事件
        dataBinding.addYesEveryMonthButton.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.i("Michael","監聽 要的按鈕 是否被按了 : $isChecked")
            if (isChecked){
                dataBinding.addYesEveryMonthButtonNo.isChecked = false
            }
            viewModel.setOnYesCheckedButtonClickListener(isChecked)
        }
        dataBinding.addYesEveryMonthButtonNo.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.i("Michael","監聽 不要的按鈕 是否被按了 : $isChecked")
            if (isChecked){
                dataBinding.addYesEveryMonthButton.isChecked = false
            }
            viewModel.setOnNoCheckedButtonClickListener(isChecked)
        }


    }

    private fun initBundle() {
        val intent = intent
        val bundle = intent.extras ?: return

        val totalDate = bundle.getString("date","")
        if (totalDate.isNullOrBlank()){
            return
        }
        val eventJson = UserManager.getInstance(this).getEventJson()
        viewModel.onActivityCreate(totalDate,eventJson)


    }
}