package com.feasycom.feasyblue.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.feasycom.feasyblue.App
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.utils.getStr
import com.feasycom.feasyblue.utils.putStr
import com.feasycom.feasyblue.worker.DeviceWorker
import com.feasycom.network.DeviceNetwork
import com.feasycom.network.bean.Parameter
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*


@SuppressLint("SetTextI18n")
class SplashScreenActivity: BaseActivity() {


   /* private val mViewModel by lazy {
        ViewModelProvider(this)[SplashViewModel::class.java]
    }*/

    private val countDownTimer: CountDownTimer by lazy {
        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                toMain.text = "${millisUntilFinished / 1000}s | Close"
            }

            override fun onFinish() {
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    override fun initView() {

        val workManager = WorkManager.getInstance(App.context!!)

        val deviceWorker = OneTimeWorkRequest.Builder(DeviceWorker::class.java).build()

        workManager.beginWith(listOf(deviceWorker)).enqueue()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(false)
            }else {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            }
        }catch (e: NoSuchMethodError){
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }

        countDownTimer.start()


        Glide.with(this@SplashScreenActivity)
            .load(getStr("lanch").let {
                if(it.isNotEmpty()){
                    it
                }else {
                    R.drawable.load
                }
            })
            .into(lanch_img)

        downloadImage()

        toMain.setOnClickListener {
            countDownTimer.cancel()
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            finish()
        }
    }


    private fun downloadImage() {
        MainScope().launch(Dispatchers.IO) {
            try {
                val parameter = Parameter("blue")
                val splash = DeviceNetwork.getLanch(parameter)
                if(splash.code == 200){
                    val url = splash.data.image
                    val context: Context = applicationContext
                    val target: FutureTarget<File> = Glide.with(context)
                        .asFile()
                        .load(url)
                        .submit()
                    val imageFile = target.get()
                    Log.e(TAG, "downloadImage: ${imageFile.path}" )
                    putStr("lanch", imageFile.path)

                    withContext(Dispatchers.Main){
                        Glide.with(this@SplashScreenActivity)
                            .load(imageFile.path)
                            .into(lanch_img)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    override fun getLayout() = R.layout.activity_splash

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            countDownTimer.cancel()
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            finish()
            false
        }else {
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }

    companion object{
        private const val TAG = "SplashScreenActivity"
    }
}