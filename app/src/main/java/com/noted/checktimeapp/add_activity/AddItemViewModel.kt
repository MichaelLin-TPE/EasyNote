package com.noted.checktimeapp.add_activity

import androidx.databinding.ObservableField

class AddItemViewModel(
    itemContent: String,
    private val eventContent: String
) {
    val itemContentLiveData = ObservableField<String>(itemContent)

    fun setOnAddItemClickListener(listener: AddAdapter.OnAddItemClickListener){
        listener.onClick(eventContent)
    }
}