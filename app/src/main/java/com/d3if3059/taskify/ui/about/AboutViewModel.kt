package com.d3if3059.taskify.ui.about

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d3if3059.taskify.data.About
import com.d3if3059.taskify.network.AboutApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AboutViewModel : ViewModel() {
    private val data = MutableLiveData<List<About>>()

    init {
        retrieveData()
    }

    fun getData(): LiveData<List<About>> = data

    private fun retrieveData() {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                data.postValue(AboutApi.service.getAbout())
            } catch (e: Exception) {
                Log.d("TasksViewModel", "Failure: ${e.message}")
            }
        }
    }
}
