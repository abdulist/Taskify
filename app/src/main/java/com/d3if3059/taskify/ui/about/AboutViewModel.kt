package com.d3if3059.taskify.ui.about

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d3if3059.taskify.data.About
import com.d3if3059.taskify.network.AboutApi
import com.d3if3059.taskify.network.ApiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AboutViewModel : ViewModel() {
    private val data = MutableLiveData<List<About>>()
    private val status = MutableLiveData<ApiStatus>()


    init {
        retrieveData()
    }

    fun getData(): LiveData<List<About>> = data
    fun getStatus(): LiveData<ApiStatus> = status

    private fun retrieveData() {
        viewModelScope.launch (Dispatchers.IO) {
            status.postValue(ApiStatus.LOADING)
            try {
                data.postValue(AboutApi.service.getAbout())
                status.postValue(ApiStatus.SUCCESS)
            } catch (e: Exception) {
                Log.d("TasksViewModel", "Failure: ${e.message}")
                status.postValue(ApiStatus.FAILED)
            }
        }
    }
}
