package com.feasycom.feasyblue.utils

import androidx.annotation.Keep
import java.util.*

@Keep
private val HEX_ARRAY =
    charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
@Keep
fun ByteArray.bytesToHex(): String {
    if (size <= 0 || size <= 0) return ""
    val maxLength = size.coerceAtMost(size - 0)
    val hexChars = StringBuilder()
    for (j in 0 until maxLength) {
        val v: Int = get(j).toInt() and 0xFF
        hexChars.append(HEX_ARRAY[v ushr 4]).append(HEX_ARRAY[v and 0x0F]).append(" ")
    }
    return hexChars.toString()
}
@Keep
fun String.toHexString() = toByteArray(Charsets.ISO_8859_1).bytesToHex()
@Keep
fun String.hexToByte(): String{
    if (length <= 0) return " "
    var str = replace(" ", "")
    //如果是奇位数，最后一个数前面补0
    if (str.length % 2 == 1) {
        val strBuf = StringBuffer(str)
        strBuf.insert(str.length - 1, '0')
        str = strBuf.toString()
    }
    val result = ByteArray(str.length / 2)
    var curr = ""
    var i = 0
    while (i < str.length) {
        curr = str.substring(i, i + 2)
        if (i == 0) {
            result[i] = curr.toInt(16).toByte()
        } else {
            result[i / 2] = curr.toInt(16).toByte()
        }
        i += 2
    }
    return String(result, Charsets.ISO_8859_1)
}

@Keep
fun String.getParam() = try {
    val param = this.substring(this.indexOf("="), this.length).replace("\r\n", "")
        .replace("OK", "").replace("=", "")
    param
} catch (e: java.lang.Exception) {
    e.printStackTrace()
    null
}


@Keep
fun String.parsingVersion(): String{
    val split = this.split(",")
    split.filter {
        it.contains(".")
    }.let {
        return it[0].replace(Regex("[a-zA-Z.]"), "")
    }
}
@Keep
fun String.parsingModel(): String{
    val split = this.split(",")
    split.filter {
        it.contains(Regex("(FSC|-|BP|BT)"))
    }.let {
        return it[0].replace("FSC-","")
    }
}

@Keep
fun ByteArray.toHexString(): String{
    val sb = java.lang.StringBuilder(size)
    for (b in this) {
        sb.append(String.format("%02x ", b).uppercase(Locale.ROOT))
    }
    return sb.toString()
}

