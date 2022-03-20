package com.feasycom.feasyblue.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.adapter.CharacteristicsDetailAdapter
import kotlinx.android.synthetic.main.activity_characteristic_detail.*

class CharacteristicDetailActivity: BaseActivity(){
    lateinit var mAddress: String
    @SuppressLint("SetTextI18n")
    override fun initView() {
        intent?.getStringExtra("address")?.let {
            mAddress = it
        }?: finish()

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false);
        toolbarTitle.text = getString(R.string.characteristicInfo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        ServiceSelectActivity.bluetoothGattCharacteristic?.let {
            uuid.text = "${it.uuid}"
            with(recyclerView){
                layoutManager = LinearLayoutManager(this@CharacteristicDetailActivity)
                adapter = CharacteristicsDetailAdapter(mAddress, it)
            }
        }
    }

    override fun getLayout() = R.layout.activity_characteristic_detail


    companion object{
        @RequiresApi(Build.VERSION_CODES.N)
        fun activityStart(
            context: Context,
            address: String
        ){
            val intent = Intent(context, CharacteristicDetailActivity::class.java)
            intent.putExtra("address", address)
            context.startActivity(intent)
        }
    }

}