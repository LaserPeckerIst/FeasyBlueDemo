package com.feasycom.feasyblue.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class FeasyCallbackSDK{

    companion object {
        private const val TAG = "FeasyCallbackSDK"


        const val FEASYBLUE_PAGENAME = "com.feasycom.feasyblue"
        const val FEASYBEACON_PAGENAME = "com.feasycom.feasybeacon"
        const val FEASYWIFI_PAGENAME = "com.feasycom.feasywifi"
        const val FEASYSENS_PAGENAME = "com.feasycom.feasysens"
        const val FEASYMESH_PAGENAME = "com.feasycom.feasymesh"


        const val COMMON_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key="
        const val FEASYBLUE_KEY = "033dbc3c-dfde-4184-9536-9ff707cd0e4e"
        const val FEASYBEACON_KEY = "903ed4b4-4aff-4aeb-b4e7-d5128eb95714"
        const val FEASYWIFI_KEY = "24de55ae-9201-460d-a19d-65ace7d4d385"
        const val FEASYSENS_KEY = "0386cf14-b0d9-47ae-a1fc-d2e622607631"
        const val FEASYMESH_KEY = "0e821f50-45ca-43ef-91f3-2d8e2d538ea4"



        const val FEEDBACK_ADVICE = 0
        const val FEEDBACK_BUG = 1
        const val FEEDBACK_UI = 2
        const val FEEDBACK_COOPERATION = 3

        var complete: ((success: Boolean) -> Unit)? = null

        /**
         * 反馈
         * @param context 当前Context
         * @param content 反馈内容
         * @param feedbackType 反馈类型
         * @param complete 反馈结果
         */
        fun feedback(
            context: Context, content: String, feedbackType: Int
        ) {
            // 反馈类型
            val fbType = when (feedbackType) {
                FEEDBACK_ADVICE -> "建议"
                FEEDBACK_BUG -> "功能异常"
                FEEDBACK_UI -> "界面异常"
                FEEDBACK_COOPERATION -> "合作"
                else -> "建议"
            }
            // 请求网址
            val urlString = when (context.packageName) {
                FEASYBLUE_PAGENAME -> {
                    "${COMMON_URL}${FEASYBLUE_KEY}"
                }
                FEASYBEACON_PAGENAME -> {
                    "${COMMON_URL}${FEASYBEACON_KEY}"
                }
                FEASYWIFI_PAGENAME -> {
                    "${COMMON_URL}${FEASYWIFI_KEY}"
                }
                FEASYSENS_PAGENAME -> {
                    "${COMMON_URL}${FEASYSENS_KEY}"
                }
                FEASYMESH_PAGENAME -> {
                    "${COMMON_URL}${FEASYMESH_KEY}"
                }
                else ->  "${COMMON_URL}${FEASYBLUE_KEY}"
            }
            GlobalScope.launch(Dispatchers.IO) {
                //需要在子线程中处理的逻辑
                try {
                    // 网络请求
                    val url = URL(urlString)
                    //2. HttpURLConnection
                    val conn = url.openConnection() as HttpURLConnection
                    //3. POST
                    conn.requestMethod = "POST"
                    //4. Content-Type,这里是固定写法，发送内容的类型
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                    //5. output，这里要记得开启输出流，将自己要添加的参数用这个输出流写进去，传给服务端，这是socket的基本结构
                    conn.doOutput = true
                    val os = conn.outputStream
                    // 发送请求
                    os.write(params(context, content, fbType))
                    os.flush()
                    os.close()
                    val response = conn.responseCode            //获得服务器的响应码
                    complete?.invoke(response == HttpURLConnection.HTTP_OK)
                } catch (e: Exception) {
                    complete?.invoke(false)
                    e.printStackTrace()
                }
            }
        }

        /**
         * 请求参数
         * @param context 当前Context
         * @param content 反馈内容
         * @param fbType 反馈类型
         */
        private fun params(context: Context, content: String, fbType: String): ByteArray {
            val map = JSONObject()
            val subMap = JSONObject()
            val packageManager = context.packageManager
            try {
                map.put("msgtype", "markdown")
                val packageInfo = packageManager.getPackageInfo(
                    context.packageName, 0
                )
                val labelRes = packageInfo.applicationInfo.labelRes
                // app名称
                val appName = context.resources.getString(labelRes)
                // app版本
                val appVersion = packageInfo.versionName
                // 系统版本
                val systemVersion = android.os.Build.VERSION.RELEASE
                // 手机型号
                val phoneType = android.os.Build.BRAND + "(" + android.os.Build.MODEL + ")"

                subMap.put(
                    "content",
                    "<font color=\"warning\">Android </font>《$appName》用户反馈\n>类型：<font color=\"comment\">$fbType</font>\n>APP版本：<font color=\"comment\">$appVersion</font>\n>系统版本：<font color=\"comment\">$systemVersion</font>\n>手机型号：<font color=\"comment\">$phoneType</font>\n>反馈内容：<font color=\"comment\">$content</font>"
                )
                map.put("markdown", subMap)
                //                Log.i("参数", map.toString());
                return map.toString().toByteArray()
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return "".toByteArray()
        }
    }


}