package com.feasycom.feasyblue

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


/**
 * 测试FeasyBlue修改参数没有断开设备连接的bug
 * 修改完参数后返回Setting 赦免
 */
@RunWith(AndroidJUnit4::class)
class ChangeParameters {

    private lateinit var mUiDevice: UiDevice


    @Before
    fun initView(){
        getUiDevice().apply {

            startApp()

            /*findObject(By.res(BASIC_SAMPLE_PACKAGE, "setting_nav"))
                .click()*/
            mUiDevice = this
        }
    }


    @Test
    fun a(){
        mUiDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "setting")), mCommonTimeOut * 10)?.let {
            // 如果存在Setting 设置按钮，则点击
            it.click()
            while (true){
                b()
            }
        }?: Log.e(TAG, "空")
    }

    private fun b() {
        mUiDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "parameterDefining")), mCommonTimeOut)?.let {
            it.click()
            mUiDevice.wait(Until.findObject(By.text("密码")), mCommonTimeOut).let {
                it.text = "20138888"
                mUiDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "toolbarButton")), mCommonTimeOut)?.let {
                    it.click()
                    mUiDevice.wait(Until.findObject(By.text(mDeviceAddress)), mConnectTimeOut)?.let {
                        it.click()
                        Log.e(TAG, "a: 点击了 设备连接")

                        Thread.sleep(5000)
                        mUiDevice.click(67,133)

                        Thread.sleep(1000)
                        mUiDevice.click(67,133)

                    }
                }
            }
        }
    }

    companion object{
        private const val TAG = "SettingTest"
    }
}