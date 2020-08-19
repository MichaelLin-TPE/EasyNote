package com.noted.checktimeapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MainViewModelUnitTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    lateinit var viewModel: MainViewModel

    var monthArray = ArrayList<String>()

    @Before
    fun init(){
        viewModel = MainViewModel()
        monthArray.add("01")
        monthArray.add("02")
        viewModel.setMonthArray(monthArray)

    }
    @Test
    fun onActivityCreate(){
        viewModel.onActivityCreate()
        val weekArray = ArrayList<String>()
        weekArray.add("一")
        weekArray.add("二")
        Assert.assertEquals(weekArray,viewModel.recyclerViewWeekViewLiveData.value)
        Assert.assertEquals(true,viewModel.showToDoView.value)

    }
}