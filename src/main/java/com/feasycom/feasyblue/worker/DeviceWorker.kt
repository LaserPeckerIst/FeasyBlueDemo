package com.feasycom.feasyblue.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.feasycom.feasyblue.dao.DeviceDatabase
import com.feasycom.feasyblue.utils.getStr
import com.feasycom.feasyblue.utils.putStr
import com.feasycom.network.DeviceNetwork
import com.feasycom.network.bean.DeviceParameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownServiceException

const val UNKNOWN_DEVICE = 0
const val ORDINARY_DEVICE = 1
const val BEACON_DEVICE = 2

const val ALL_FUNCTIONS = 0


class DeviceWorker(var context: Context, workerParameters: WorkerParameters): CoroutineWorker(context, workerParameters) {

    private var deviceDao = DeviceDatabase.getDataBase(context).deviceDao()

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO){
            Log.e(TAG, "doWork: ${deviceDao.queryAllBeaconByDeviceType(1).size}", )
            var size = deviceDao.queryAllBeaconByDeviceType(1).size

            val parameter = DeviceParameter(
                ORDINARY_DEVICE,
                ALL_FUNCTIONS,
                if(size == 0) "" else context.getStr("hash", ""),
                true,
                0,
                100
            )
            try {
                val allDevice = DeviceNetwork.getAllDevice(parameter)
                if (allDevice.code == 200){
                    allDevice.data?.let {
                        it.hash?.let { context.putStr("hash", it) }
                        launch {
                            deviceDao.deleteAllBeacons()
                            Log.e(TAG, allDevice.data?.list.toString())
                            deviceDao.addDevices(it.list)
                        }
                    }

                }
            }catch (e: UnknownServiceException){
                Log.e(TAG, "doWork: " + e )
                e.printStackTrace()
                Result.failure()
            }catch (e: Exception){
                Log.e(TAG, "doWork: " + e )
                e.printStackTrace()
                Result.failure()
            }catch (e: java.lang.Exception){
                Log.e(TAG, "doWork: " + e )
                e.printStackTrace()
                Result.failure()
            }
            Result.success()
        }
    }

    companion object{
        private const val TAG = "BeaconWorker"
    }
}