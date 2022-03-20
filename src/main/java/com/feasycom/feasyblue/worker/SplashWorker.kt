package com.feasycom.feasyblue.worker

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.feasycom.network.DeviceNetwork
import com.feasycom.network.bean.Parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownServiceException

class SplashWorker(var context: Context, workerParameters: WorkerParameters): CoroutineWorker(context, workerParameters) {

    private val parameter = Parameter("blue")
    private var verion: Int = 0
    private var files: MutableList<String> = mutableListOf()

    override suspend fun doWork(): Result{
        return withContext(Dispatchers.IO){
            files = getFile()
            try {
                val splash = DeviceNetwork.getLanch(parameter)
                val substring = splash.data.image.substring(splash.data.image.lastIndexOf('/') + 1)
                if(splash.code == 200){
                    verion = splash.data.verion
                    files.filter {
                        it.contains("${verion}.png")
                    }.apply {
                        if(size == 0){
                            val downImage = DeviceNetwork.downImg("blue", substring)
                            context.openFileOutput("${verion}.png", MODE_PRIVATE).use {
                                it.write(downImage.bytes())
                            }
                        }
                    }
                }
            }catch (e: ExceptionInInitializerError){
                Result.failure()
            }catch (e: UnknownServiceException){
                e.printStackTrace()
                Result.failure()
            }catch (e: Exception){
                e.printStackTrace()
                Result.failure()
            }
            Result.success()
        }
    }

    private fun getFile(): MutableList<String>{
        return context.fileList().toMutableList()
    }

    companion object{
        private const val TAG = "SplashWorker"
    }


}