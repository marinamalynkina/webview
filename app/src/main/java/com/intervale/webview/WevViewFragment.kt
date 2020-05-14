package com.intervale.webview

import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.*
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.intervale.webview.databinding.InputUrlFragmentBinding
import com.intervale.webview.databinding.WebviewFragmentBinding

class WevViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.webview_fragment, container, false)

    val refresh: SwipeRefreshLayout?
        get() = view?.findViewById(R.id.refresh)

    val webView: WebView?
        get() = view?.findViewById(R.id.web_view)

    val edittext: EditText?
        get() = view?.findViewById(R.id.edittext)


    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (webView?.canGoBack() == true) {
                webView?.goBack()
            }else  {
                isEnabled = false
                activity?.onBackPressed()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, onBackPressedCallback)

        var url = arguments?.getString("url")
        url?.let {
            if (!it.startsWith("http://") && !it.startsWith("https://")) {
                url = "http://${url}"
            }
        }

        WebviewFragmentBinding.bind(view).run {
            webView.apply {
                if (arguments?.getBoolean("chromeclient") == true) webChromeClient = WebChromeClient()
                webViewClient = MyWebViewClient()
                loadUrl(url)
                settings.apply {
                    builtInZoomControls = true
                    defaultZoom = WebSettings.ZoomDensity.FAR
                    javaScriptEnabled = arguments?.getBoolean("javaScriptEnabled") ?: true
                    javaScriptCanOpenWindowsAutomatically = arguments?.getBoolean("javaScriptEnabled") ?: true
                    allowContentAccess = true
                    requestFocusFromTouch()
                    requestFocus(View.FOCUS_DOWN)
                    clearCache(true)
                }
            }
            edittext.setText(url)
            edittext.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    webView.loadUrl(edittext.text.toString())
                    true
                }else false
            })
            refresh.setOnClickListener {
                 webView.loadUrl(url)
            }

        }
    }

    fun showProgress() {
    }

    fun hideProgress() {
    }

    inner class MyWebViewClient(
    ) : WebViewClient() {

        val TAG = "WebView"

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            showProgress()
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            hideProgress()
            edittext?.setText(url)
//            view.clearCache(true)
            Log.i(TAG, "onPageFinished: ${url}")
        }

        override fun onLoadResource(view: WebView?, url: String?) {
            super.onLoadResource(view, url)
            Log.i(TAG, "load resource by url: ${url}")
        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            Log.e(TAG, "request.url: ${request.url}")
            Log.e(TAG, "request.isForMainFrame: ${request.isForMainFrame}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Log.e(TAG, "request.isRedirect: ${request.isRedirect}")
            }
            request.requestHeaders.keys.forEach { key ->
                Log.e(TAG, "request.header: ${key} -> ${request.requestHeaders.get(key)}")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e(TAG, "error.errorCode: ${error.errorCode}")
                Log.e(TAG, "error.description: ${error.description}")
            }

        }
        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            Log.e(TAG, "errorCode: ${errorCode}")
            Log.e(TAG, "description: ${description}")
            Log.e(TAG, "failingUrl: ${failingUrl}")
        }
        override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.e(TAG, "errorResponse.statusCode: ${errorResponse.statusCode}")
            }
        }
    }

}