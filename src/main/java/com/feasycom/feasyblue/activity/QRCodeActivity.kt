package com.feasycom.feasyblue.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.utils.FileUtils
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_qrcode.*
import kotlinx.android.synthetic.main.activity_qrcode.toolbar
import kotlinx.android.synthetic.main.activity_qrcode.toolbarTitle


class QRCodeActivity : AppCompatActivity() {

    private lateinit var mResultDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)
        initView()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
            it.setDisplayHomeAsUpEnabled(true)
        }
        toolbarTitle.text = getString(R.string.wx)
        toolbar.setNavigationOnClickListener { finish() }

        img.setImageDrawable(resources.getDrawable(R.drawable.wx))
        save.setOnClickListener {
            PermissionX.init(this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .request { allGranted, _, _ ->
                    if (allGranted) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy()) {
                            if (!query()){
                                val imgName = "wx.jpg"
                                val contentValues = ContentValues()
                                contentValues.put(MediaStore.Images.ImageColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                                contentValues.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, imgName)
                                contentValues.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpg")
                                contentValues.put(MediaStore.Downloads.TITLE, imgName)
                                val insert = contentResolver.insert(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    contentValues
                                )
                                insert?.let {
                                    try {
                                        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.wx);
                                        val outputStream = contentResolver.openOutputStream(it)
                                        outputStream?.let {
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                                            it.close()
                                            mResultDialog = AlertDialog.Builder(this@QRCodeActivity)
                                                .setMessage(getString(R.string.qr_success))
                                                .setPositiveButton(android.R.string.ok, null)
                                                .show()
                                        }
                                    }catch (e: Exception){
                                        e.printStackTrace()
                                        mResultDialog = AlertDialog.Builder(this@QRCodeActivity)
                                            .setMessage(getString(R.string.qr_failed))
                                            .setPositiveButton(android.R.string.ok, null)
                                            .show()
                                    }
                                }?: let {
                                    mResultDialog = AlertDialog.Builder(this@QRCodeActivity)
                                        .setMessage(getString(R.string.qr_failed))
                                        .setPositiveButton(android.R.string.ok, null)
                                        .show()
                                }
                            }else {
                                mResultDialog = AlertDialog.Builder(this@QRCodeActivity)
                                    .setMessage(getString(R.string.qr_success))
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show()
                            }
                        }else {
                            FileUtils().saveBitmap(this, BitmapFactory.decodeResource(resources, R.drawable.wx), "wechat.jpg"){
                                mResultDialog = AlertDialog.Builder(this@QRCodeActivity)
                                    .setMessage(getString(if(it){
                                        R.string.qr_success
                                    }else {
                                        R.string.qr_failed
                                    }))
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show()
                                mResultDialog.setCanceledOnTouchOutside(false)
                            }
                        }
                    }
                }
        }
    }

    private fun query():Boolean{
        val externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Images.Media.DISPLAY_NAME + "=?"
        val arg = arrayOf("wx.jpg")
        val cursor = contentResolver.query(
            externalContentUri,
            null,
            selection,
            arg,
            null
        )
        return if(cursor != null && cursor.moveToFirst()){
            val columnIndexOrThrow =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            var long = cursor.getLong(columnIndexOrThrow)

            cursor.close()
            long != 0L
        }else {
            Log.e(TAG, "query: 查询失败" )
            false
        }
    }

    private fun isUriExist(uri: Uri): Boolean{
        val cursor = contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )
        return cursor != null && cursor.moveToFirst()
    }

    companion object{
        private const val TAG = "QRCodeActivity"

        fun activityStart(context: Context){
            val intent = Intent(context, QRCodeActivity::class.java)
            context.startActivity(intent)

        }
    }

}
