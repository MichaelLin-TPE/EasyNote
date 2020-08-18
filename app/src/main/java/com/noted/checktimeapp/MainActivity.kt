package com.noted.checktimeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.noted.checktimeapp.add_activity.AddEventActivity
import com.noted.checktimeapp.databinding.ActivityMainBinding
import com.noted.checktimeapp.databinding.SelectMonthViewLayoutBinding

class MainActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityMainBinding

    private val viewModel : MainViewModel by viewModels()

    private lateinit var viewPresenter: MainViewPresenter

    private lateinit var adapter: MainAdapter

    companion object{
        private const val WEEK = 0

        private const val DATE = 1
    }


    /**
     * Handle onNewIntent() to inform the fragment manager that the
     * state is not saved.  If you are handling new intents and may be
     * making changes to the fragment state, you want to be sure to call
     * through to the super-class here first.  Otherwise, if your state
     * is saved but the activity is not stopped, you could get an
     * onNewIntent() call which happens before onResume() and trying to
     * perform fragment operations at that point will throw IllegalStateException
     * because the fragment manager thinks the state is still saved.
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.i("Michael","接收到更新畫面指令")
        viewModel.onCatchNewEvent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        dataBinding.vm = viewModel
        dataBinding.lifecycleOwner = this

        viewModel.onActivityCreate()

        viewPresenter = MainViewPresenterImpl()


        //underLine
        viewModel.monthTextLiveData.observeForever {
            val content = SpannableString(it)
            content.setSpan(UnderlineSpan(),0,content.length,0)
            dataBinding.mainTextMonth.text = content
            dataBinding.textMonth = it
        }

        viewModel.recyclerViewWeekViewLiveData.observeForever {
            viewPresenter.setWeekData(it)
        }
        viewModel.recyclerViewDateViewLiveData.observeForever {
            viewPresenter.setYearAndMonth(it.currentYear,it.currentMonth)
            viewPresenter.setDateData(it.dateArray)
            if (!UserManager.getInstance(this).getEventJson().isNullOrEmpty()){
                viewPresenter.setUserData(UserManager.getInstance(this).getEventJson())
            }
            adapter = MainAdapter(viewPresenter)
            val gridLayoutManager = GridLayoutManager(this,7)
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when(adapter.getItemViewType(position)){
                        WEEK -> {

                            1
                        }
                        DATE ->{

                            1
                        }
                        else ->{

                            7
                        }
                    }
                }

            }
            dataBinding.mainRecyclerView.layoutManager = gridLayoutManager
            dataBinding.mainRecyclerView.adapter = adapter
            adapter.setOnSetDateItemClickListener(object :MainAdapter.OnSetDateItemClickListener{
                override fun onItemClick(totalDate: String) {
                    Log.i("Michael","totalDate : $totalDate")
                    viewModel.onSetDateItemClickListener(totalDate)
                }
            })
            adapter.setOnRemoveButtonClickListener(object : MainAdapter.OnSetRemoveButtonClickListener{
                override fun onRemoveButtonClick() {
                    adapter.notifyDataSetChanged()
                }
            })
        }

        viewModel.refreshEvent.observeForever {
            if (!it){
                return@observeForever
            }
            if (UserManager.getInstance(this).getEventJson().isNullOrEmpty()){
                return@observeForever
            }
            viewPresenter.setUserData(UserManager.getInstance(this).getEventJson())
            adapter.notifyDataSetChanged()
        }

        viewModel.showToDoView.observeForever {
            if (!it){
                return@observeForever
            }
            viewPresenter.setToDoView(UserManager.getInstance(this).getEventJson())
        }


        viewModel.refreshRecyclerViewLiveData.observeForever {
            viewPresenter.setYearAndMonth(it.currentYear,it.currentMonth)
            viewPresenter.setDateData(it.dateArray)
            adapter.notifyDataSetChanged()
        }

        viewModel.intentToAddEventActivityData.observeForever {
            val intent = Intent(this,AddEventActivity::class.java)
            intent.putExtra("date",it)
            startActivity(intent)
        }

        viewModel.isShowSelectMonthView.observeForever {
            if (!it){
                return@observeForever
            }
            val selectMonthBinding = SelectMonthViewLayoutBinding.inflate(LayoutInflater.from(this),null,false)
            selectMonthBinding.selectMonthRecyclerView.layoutManager = GridLayoutManager(this,4)
            val selectAdapter = SelectMonthAdapter()
            selectMonthBinding.selectMonthRecyclerView.adapter = selectAdapter
            val builder = AlertDialog.Builder(this)
                .setView(selectMonthBinding.root)
            val dialog = builder.create()
            dialog.show()


            selectAdapter.setOnItemClickListener(object :SelectMonthAdapter.OnItemClickListener{
                override fun onClick(month: String) {
                    Log.i("Michael","點擊了 : $month 月")
                    viewModel.onItemClickListener(month)
                    dialog.dismiss()
                }
            })

        }


    }

    override fun onRestart() {
        super.onRestart()
        viewModel.onCatchNewEvent()
    }


    override fun onResume() {
        super.onResume()
        Log.i("Michael","目前使用者資料 : ${UserManager.getInstance(this).getEventJson()}")
    }
}