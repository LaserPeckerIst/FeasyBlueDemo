package com.feasycom.feasyblue

import android.bluetooth.BluetoothDevice
import org.junit.Test
import org.mozilla.universalchardet.CharsetListener
import java.io.UnsupportedEncodingException
import org.mozilla.universalchardet.UniversalDetector







class test {
    @Test
    @Throws(UnsupportedEncodingException::class)
    fun abc() {
        var a = "AT+ADVIN=114"
        println(a.split("AT+"))
        println(a.split("AT+")[1].split("="))
        println(a.split("AT+")[1].split("=")[0])
    }

    /**
     * 获取文件编码类型
     *
     * @param bytes 文件bytes数组
     * @return      编码类型
     */
    fun getEncoding(bytes: ByteArray): String {
        val defaultEncoding = "UTF-8"
        val detector = UniversalDetector(object: CharsetListener {
            override fun report(charset: String?) {
                println(charset)
            }
        })
        detector.handleData(bytes, 0, bytes.size)

        detector.dataEnd()
        var encoding = detector.detectedCharset
        detector.reset()
        println("字符编码是：${encoding}")
        if (encoding == null) {
            encoding = defaultEncoding
        }
        return encoding
    }

    @Test
    private fun ccc(){
        val strValue = "OK\$C30264003200"
        if (strValue.length >= 15 && strValue.contains("OK\$")) {
            println("符合")
        }else {
            println("不符合")
        }
    }
    lateinit var abc:BluetoothDevice

    @Test
    private fun abbb(){
        var s: String = ""
        println(s.isNotBlank())
    }
}
