package com.noted.checktimeapp.to_do_view

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import androidx.databinding.Observable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.noted.checktimeapp.MainActivity
import com.noted.checktimeapp.MainAdapter
import com.noted.checktimeapp.R
import com.noted.checktimeapp.UserManager
import com.noted.checktimeapp.add_activity.EventObject
import com.noted.checktimeapp.databinding.ToDoLayoutBinding
import kotlinx.coroutines.NonCancellable.cancel
import java.util.ArrayList

class ToDoViewHolder(
    private val dataBinding: ToDoLayoutBinding,
    val context: Context
) : RecyclerView.ViewHolder(dataBinding.root) {

    private var dialog: AlertDialog? = null

    private lateinit var adapter: ToDoAdapter

    private lateinit var removeListener: MainAdapter.OnSetRemoveButtonClickListener

    fun setData(
        userDataArray: ArrayList<EventObject>,
        currentMonth: String,
        currentYear: String
    ) {
        val viewModel = ToDoViewModel()
        viewModel.setData(userDataArray,currentMonth,currentYear)
        dataBinding.vm = viewModel
        dataBinding.executePendingBindings()
        dataBinding.toDoRecyclerView.layoutManager = LinearLayoutManager(context)


        viewModel.recyclerViewData.observeForever {
            adapter = ToDoAdapter()
            adapter.setData(it.userDataArray,it.currentYear,it.currentMonth)
            dataBinding.toDoRecyclerView.adapter = adapter
        }

        viewModel.isShowNoticeDialog.addOnPropertyChangedCallback(object:
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {

                if (dialog == null || !dialog!!.isShowing){
                    val builder = AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.warming))
                        .setMessage(context.getString(R.string.sure_to_delete_day_line))
                        .setPositiveButton(context.getString(R.string.confirm)
                        ) { _, _ ->
                            viewModel.onDialogConfirmClickListener()
                        }
                        .setNegativeButton(context.getString(R.string.cancel)
                        ) { _, _ ->

                        }
                    dialog = builder.create()
                    dialog!!.show()
                }
            }
        })
        viewModel.refreshRecyclerViewData.observeForever {
            Log.i("Michael","更新資料")
            adapter.setData(it.userDataArray,it.currentYear,it.currentMonth)
            adapter.notifyDataSetChanged()
            removeListener.onRemoveButtonClick()
        }

        viewModel.saveUserData.observeForever {
            UserManager.getInstance(context).saveEventData(it)
            val refreshIntent = Intent(context,MainActivity::class.java)
            context.startActivity(refreshIntent)
        }

    }

    fun setRemoveButtonClickListener(removeListener: MainAdapter.OnSetRemoveButtonClickListener) {
        this.removeListener = removeListener
    }


}