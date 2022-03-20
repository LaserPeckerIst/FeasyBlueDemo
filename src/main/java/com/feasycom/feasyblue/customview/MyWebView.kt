package com.feasycom.feasyblue.customview

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import com.feasycom.feasyblue.R
import kotlinx.android.synthetic.main.store_view.view.*

class MyWebView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): LinearLayout(
    context,
    attrs,
    defStyleAttr
){
    var onUrlChangeListener: ((b: Boolean) -> Unit)? = null
    private lateinit var mUrl: String

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MyWebView)
        a.getString(R.styleable.MyWebView_url)?.let {
            mUrl = it
        }

        a.recycle()
        initView(context)
        if (::mUrl.isInitialized){
            initWebView()
        }
    }




    private fun initView(context: Context) {
        addView(LayoutInflater.from(context).inflate(R.layout.store_view, this, false))
    }

    private fun initWebView() {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                onUrlChangeListener?.let {
                    try {
                        if (url != mUrl) {
                            it.invoke(false)
                        } else {
                            it.invoke(true)
                        }
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                try {
                    progressBar?.progress = newProgress
                    if (newProgress == 100) {
                        progressBar?.visibility = GONE
                    } else {
                        if (progressBar?.visibility == GONE) {
                            progressBar?.visibility = VISIBLE
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
        with(webView.settings){
            setJavaScriptEnabled(true);
            setJavaScriptCanOpenWindowsAutomatically(true);
            setAllowFileAccess(true);// 设置允许访问文件数据
            setSupportZoom(true);
            setBuiltInZoomControls(true);
            setJavaScriptCanOpenWindowsAutomatically(true);
            setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            setDomStorageEnabled(true);
            setDatabaseEnabled(true);
        }
        webView.loadUrl(mUrl)
    }


    fun goBack(){
        webView.goBack()
    }

    fun reload(){
        webView.reload()
    }

    fun setUrl(url: String){
        mUrl = url
        initWebView()
    }
}