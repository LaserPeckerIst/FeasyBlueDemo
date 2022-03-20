package com.feasycom.feasyblue.activity

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.feasycom.ble.controler.FscBleCentralApiImp
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.dialog.FirstDialogFragment
import com.feasycom.feasyblue.fragment.AboutFragment
import com.feasycom.feasyblue.fragment.CommunicationFragment
import com.feasycom.feasyblue.fragment.SettingFragment
import com.feasycom.feasyblue.fragment.StoreFragment
import com.feasycom.feasyblue.utils.getBoolean
import com.feasycom.feasyblue.utils.putBoolean
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity: BaseActivity() {

    lateinit var mFscDevice: FscBleCentralApiImp
    override fun initView() {

    }

    lateinit var mFirstDialogFragment: FirstDialogFragment
    private fun firstIn(){
        if (::mFirstDialogFragment.isInitialized){
            return
        }else {
            val data = getBoolean("first", false)
            mFirstDialogFragment = FirstDialogFragment()
            if (!mFirstDialogFragment.isAdded){
                if (!data){
                    // 显示弹窗
                    with(mFirstDialogFragment){
                        onAgree = {
                            putBoolean("first", true)
                            initPermission()
                            dismiss()
                        }
                        onRefuse = {
                            finish()
                        }

                        show(supportFragmentManager, "first")
                    }
                }else {
                    initPermission()
                }
            }
        }
    }

    private fun initPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PermissionX.init(this)
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)

                .request { allGranted, _, _ ->
                    if (allGranted) {
                        Log.i(TAG, "权限已全部授予")



                    }
                }
        }else {
            PermissionX.init(this)
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION)

                .request { allGranted, _, _ ->
                    if (allGranted) {
                        Log.i(TAG, "权限已全部授予")
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        firstIn()

        with(viewPager2){
            adapter = object : FragmentStateAdapter(this@MainActivity) {
                override fun getItemCount() = 4
                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> CommunicationFragment()
                        1 -> SettingFragment()
                        2 -> StoreFragment()
                        else -> AboutFragment()
                    }
                }
            }
            offscreenPageLimit = 1
            isUserInputEnabled = false
        }
        navigation_bar.setOnNavigationItemSelectedListener {
            viewPager2.setCurrentItem(when (it.itemId) {
                R.id.communication -> 0
                R.id.setting -> 1
                R.id.store -> 2
                else -> 3
            }, false)
            true
        }
        navigation_bar.selectedItemId = when(intent.getIntExtra("position", 0)){
            0 -> R.id.communication
            1 -> R.id.setting
            2 -> R.id.store
            else -> R.id.about
        }
    }

    override fun getLayout() = R.layout.activity_main




    companion object{
        private const val TAG = "MainActivity"
        fun activityStart(context: Context, position: Int){
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("position", position)
            context.startActivity(intent)
        }
    }
}


fun String?.showStr(context: Activity) {
    if (this?.isNotEmpty() == true) {
        GlobalScope.launch(Dispatchers.Main){
            Toast.makeText(context, this@showStr, Toast.LENGTH_SHORT).show()
        }
    }
}

fun Activity.showStr(str: String){
    if (str.isNotEmpty()) {
        runOnUiThread {
            Toast.makeText(this@showStr, str, Toast.LENGTH_SHORT).show()
        }
    }
}