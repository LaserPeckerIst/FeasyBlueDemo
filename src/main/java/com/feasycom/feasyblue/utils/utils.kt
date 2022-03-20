package com.feasycom.feasyblue.utils

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.mozilla.universalchardet.CharsetListener
import org.mozilla.universalchardet.UniversalDetector
import java.io.*
import java.nio.charset.Charset
import java.util.*
import kotlin.experimental.and

private const val PREFS_READ_STORAGE_PERMISSION_REQUESTED = "read_storage_permission_requested"
private const val PREFS_WRITE_STORAGE_PERMISSION_REQUESTED = "write_storage_permission_requested"

private const val Hex = "0123456789ABCDEF"

fun isKitkatOrAbove(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
}

fun isExternalStorageLegacy(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy()
}

fun isWriteExternalStoragePermissionsGranted(context: Context?): Boolean {
    return ContextCompat.checkSelfPermission(
        context!!,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}

fun isWriteExternalStoragePermissionDeniedForever(activity: Activity?): Boolean {
    val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
    return (!isWriteExternalStoragePermissionsGranted(activity) // Location permission must be denied
            && preferences.getBoolean(
        PREFS_WRITE_STORAGE_PERMISSION_REQUESTED,
        false
    ) // Permission must have been requested before
            && !ActivityCompat.shouldShowRequestPermissionRationale(
        activity!!,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )) // This method should return false
}

/**
 * 字符串转以小端转int,字符串必须是4个数字，不足4数字后面补0，超过4个数字只取前4个。
 */
fun stringToInt(string: String): Int {
    val str1 = string.substring(0, 2)
    val str2 = string.substring(2, 4)
    val int1: Int = formattingHexToInt(str1)
    val int2: Int = formattingHexToInt(str2)
    val byte1 = int1.toByte()
    val byte2 = int2.toByte()
    return byteToInt_2(byte1, byte2)
}


fun formattingHexToInt(a: String): Int {
    val a1 = a.substring(0, 1)
    val a2 = a.substring(1, 2)
    return formattingOneHexToInt(a1) * 16 + formattingOneHexToInt(
        a2
    )
}

fun byteToInt_2(a: Byte, b: Byte): Int {
    val int_b: Int = (b and 0xff.toByte()).toInt()
    val int_a: Int = (a and 0xff.toByte()).toInt()
    return int_a + (int_b shl 8)
}

// 将十六进制中的字母转为对应的数字
fun formattingOneHexToInt(a: String?): Int {
    return if (a == null || a.length != 1) {
        0
    } else Hex.indexOf(a.uppercase(Locale.getDefault()))
}


// 判断系统版本是否需要打开定位
fun isMarshmallowOrAbove(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}

/**
 * On some devices running Android Marshmallow or newer location services must be enabled in order to scan for Bluetooth LE devices.
 * This method returns whether the Location has been enabled or not.
 *
 * @return true on Android 6.0+ if location mode is different than LOCATION_MODE_OFF. It always returns true on Android versions prior to Marshmallow.
 */
fun isLocationEnabled(context: Context): Boolean {
    if (isMarshmallowOrAbove()) {
        var locationMode = Settings.Secure.LOCATION_MODE_OFF
        try {
            locationMode =
                Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
        } catch (e: Settings.SettingNotFoundException) {
            // do nothing
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF
    }
    return true
}

/**
 * Checks whether Bluetooth is enabled.
 *
 * @return true if Bluetooth is enabled, false otherwise.
 */
fun isBleEnabled(): Boolean {
    val adapter = BluetoothAdapter.getDefaultAdapter()
    return adapter != null && adapter.isEnabled
}


/**
 * 复制
 */
fun Context.copyText(copiedText: String) {
    val clipboardManager: ClipboardManager =
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, copiedText))
}



/**
 * 从uri获取path
 *
 * @param uri content://media/external/file/109009
 *
 *
 * FileProvider适配
 * content://com.tencent.mobileqq.fileprovider/external_files/storage/emulated/0/Tencent/QQfile_recv/
 * content://com.tencent.mm.external.fileprovider/external/tencent/MicroMsg/Download/
 */
fun getFilePathFromContentUri(context: Context, uri: Uri?): String? {
    if (null == uri) return null
    var data: String? = null
    val filePathColumn =
        arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)
    val cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)
    if (null != cursor) {
        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
            data = if (index > -1) {
                cursor.getString(index)
            } else {
                val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                val fileName = cursor.getString(nameIndex)
                getPathFromInputStreamUri(context, uri, fileName)
            }
        }
        cursor.close()
    }
    return data
}

/**
 * 用流拷贝文件一份到自己APP私有目录下
 *
 * @param context
 * @param uri
 * @param fileName
 */
private fun getPathFromInputStreamUri(context: Context, uri: Uri, fileName: String): String? {
    var inputStream: InputStream? = null
    var filePath: String? = null
    if (uri.authority != null) {
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            val file = createTemporalFileFrom(context, inputStream, fileName)
            filePath = file!!.path
        } catch (e: Exception) {
        } finally {
            try {
                inputStream?.close()
            } catch (e: Exception) {
            }
        }
    }
    return filePath
}

@Throws(IOException::class)
private fun createTemporalFileFrom(
    context: Context,
    inputStream: InputStream?,
    fileName: String
): File? {
    var targetFile: File? = null
    if (inputStream != null) {
        var read: Int
        val buffer = ByteArray(8 * 1024)
        //自己定义拷贝文件路径
        targetFile = File(context.externalCacheDir, fileName)
        if (targetFile.exists()) {
            targetFile.delete()
        }
        val outputStream: OutputStream = FileOutputStream(targetFile)
        while (inputStream.read(buffer).also { read = it } != -1) {
            outputStream.write(buffer, 0, read)
        }
        outputStream.flush()
        try {
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return targetFile
}


/**
 * 获取文件编码类型
 *
 * @param bytes 文件bytes数组
 * @return      编码类型
 */
fun ByteArray.getEncoding(): String {
    val defaultEncoding = "UTF-8"
    val detector = UniversalDetector(object: CharsetListener {
        override fun report(charset: String?) {
            Log.e("TAG", "report: $charset" )
        }
    })
    detector.handleData(this, 0, this.size)
    detector.dataEnd()
    val encoding = detector.detectedCharset
    detector.reset()

    return when {
        encoding == defaultEncoding -> {
            String(this, Charset.forName(encoding))
        }
        this[0] in 0 .. 127 -> {
            String(this)
        }
        else -> {
            ""
        }
    }
}