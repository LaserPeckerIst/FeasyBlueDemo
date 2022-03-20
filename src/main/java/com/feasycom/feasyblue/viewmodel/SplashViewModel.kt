package com.feasycom.feasyblue.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.feasycom.feasyblue.App
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.worker.DeviceWorker
import com.feasycom.feasyblue.worker.SplashWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashViewModel: ViewModel() {

    var mUrlLiveData = MutableLiveData<Any>()

    var mFiles: MutableList<String> = mutableListOf()

    private val workManager by lazy {
        WorkManager.getInstance(App.context!!)
    }

    private val splashWorker by lazy {
        OneTimeWorkRequest.Builder(SplashWorker::class.java)
            .build()
    }

    private val deviceWorker by lazy {
        OneTimeWorkRequest.Builder(DeviceWorker::class.java)
            .build()
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            mFiles = getFile()
            if (mFiles.size == 0){
                mUrlLiveData.postValue(R.drawable.load)
            }else{
                mFiles.filter {
                    it.contains(".png") || it.contains(".jpg")
                }.let {
                    if(it.isNotEmpty()){
                        mUrlLiveData.postValue(it[it.lastIndex])
                    }else {
                        mUrlLiveData.postValue(R.drawable.load)
                    }
                }
            }
            workManager.beginWith(listOf(deviceWorker, splashWorker)).enqueue()
        }
    }

    private fun getFile(): MutableList<String>{
        return App.context!!.fileList().toMutableList()
    }


    companion object{
        private const val TAG = "SplashViewModel"
    }

}