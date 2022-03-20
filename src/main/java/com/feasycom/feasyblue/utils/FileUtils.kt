package com.feasycom.feasyblue.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class FileUtils {
    companion object{

        private const val TAG = "FileUtils"
    }

    fun saveBitmap(context: Context, bitmap: Bitmap, bitName: String, isSuccess: ((success: Boolean) -> Unit)){
        val fileName = when(Build.BRAND){
            "xiaomi" -> {
                Log.e(TAG, "saveBitmap: 小米" )
                Environment.getExternalStorageDirectory().path + "/DCIM/Camera/" + bitName
            }
            "HUAWEI" -> {
                Log.e(TAG, "saveBitmap: 华为" )
                Environment.getExternalStorageDirectory().path + "/DCIM/Camera/" + bitName
            }
            "GOOGLE" ->{
                Environment.getExternalStorageDirectory().path +"/DCIM/"+bitName
            }
            else -> {
                Log.e(TAG, "saveBitmap: " + Build.BRAND )
                Environment.getExternalStorageDirectory().path +"/DCIM/"+bitName
            }
        }
        val file = File(fileName)
        if(file.exists()){
            isSuccess.invoke(true)
        }else {
            try {
                val out = FileOutputStream(file)
                if(bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)){
                    out.flush()
                    out.close()
                }
            }catch (e: FileNotFoundException) {
                isSuccess.invoke(false)
                e.printStackTrace();
            }
            catch (e: IOException) {
                isSuccess.invoke(false)
                e.printStackTrace();
            }
            isSuccess.invoke(true)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)))
        }
    }

}