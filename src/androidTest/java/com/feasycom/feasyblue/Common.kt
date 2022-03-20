package com.feasycom.feasyblue

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert

const val BASIC_SAMPLE_PACKAGE = "com.feasycom.blue"
const val mCommonTimeOut = 500L
const val LAUNCH_TIMEOUT = mCommonTimeOut * 10
const val mConnectTimeOut = mCommonTimeOut * 30
const val mScannerTimeOut = mCommonTimeOut * 12
const val mDeviceAddress = "DC:0D:30:00:06:87"
const val mDeviceStatus = "[已配对] "
const val mDeviceName = "826"
const val mDeviceConnectPin = "000000"





fun getUiDevice(): UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())


fun UiDevice.startApp() {
    val launcherPackage = launcherPackageName
    MatcherAssert.assertThat(launcherPackage, CoreMatchers.notNullValue())

    wait(
        Until.hasObject(By.pkg(launcherPackage).depth(0)),
        LAUNCH_TIMEOUT
    )
    // Launch the blueprint app
    val context = ApplicationProvider.getApplicationContext<Context>()
    val intent = context.packageManager
        .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE)
    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) // Clear out any previous instances

    context.startActivity(intent)

    // Wait for the app to appear
    wait(
        Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
        LAUNCH_TIMEOUT
    )
}

