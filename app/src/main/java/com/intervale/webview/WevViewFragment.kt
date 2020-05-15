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
                if (arguments?.getBoolean("chromeclient") == true)
                    webChromeClient = MyWebChromeClient()
                webViewClient = MyWebViewClient()
                loadUrl(url)
                settings.apply {
                    domStorageEnabled = arguments?.getBoolean("domStorageEnabled") ?: true
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

    val TAG = "WebView"

    inner class MyWebChromeClient: WebChromeClient() {
        override fun  onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            when(consoleMessage.messageLevel()) {
                ConsoleMessage.MessageLevel.ERROR -> Log.e(TAG, "onConsoleMessage ${consoleMessage.message()} - ${consoleMessage.sourceId()} :${consoleMessage.lineNumber()}");
                ConsoleMessage.MessageLevel.WARNING -> Log.w(TAG, "onConsoleMessage ${consoleMessage.message()} - ${consoleMessage.sourceId()} :${consoleMessage.lineNumber()}");
                else -> Log.i(TAG, "${consoleMessage.messageLevel().name} onConsoleMessage ${consoleMessage.message()} - ${consoleMessage.sourceId()} :${consoleMessage.lineNumber()}");
            }
            return true;
        }

        override fun onConsoleMessage(message: String?, lineNumber: Int, sourceID: String?) {
            super.onConsoleMessage(message, lineNumber, sourceID)
            Log.i(TAG, "onConsoleMessage ${message} - ${sourceID} :${lineNumber}");
        }

        override fun onJsAlert(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?
        ): Boolean {
            Log.i(TAG, "onJsAlert ${message} :${result} :: url ${url} ");
            return super.onJsAlert(view, url, message, result)
        }

        override fun onJsPrompt(
            view: WebView?,
            url: String?,
            message: String?,
            defaultValue: String?,
            result: JsPromptResult?
        ): Boolean {
            Log.i(TAG, "onJsPrompt ${message} result:${result} defaultValue: ${defaultValue} :: url ${url} ");
            return super.onJsPrompt(view, url, message, defaultValue, result)
        }

        override fun onPermissionRequest(request: PermissionRequest?) {
            super.onPermissionRequest(request)
            Log.i(TAG, "onPermissionRequest ${request.toString()}");
        }

        override fun onReceivedTouchIconUrl(view: WebView?, url: String?, precomposed: Boolean) {
            super.onReceivedTouchIconUrl(view, url, precomposed)
            Log.i(TAG, "onReceivedTouchIconUrl ${url}");
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            Log.i(TAG, "onReceivedTitle ${title}");
        }

        override fun onReachedMaxAppCacheSize(
            requiredStorage: Long,
            quota: Long,
            quotaUpdater: WebStorage.QuotaUpdater?
        ) {
            super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater)
            Log.i(TAG, "onReachedMaxAppCacheSize requiredStorage: ${requiredStorage} quota: ${quota}");
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }

        override fun onJsConfirm(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?
        ): Boolean {
            Log.i(TAG, "onJsConfirm message: ${message} result: ${result} url: ${url}");
            return super.onJsConfirm(view, url, message, result)

        }

        override fun onJsBeforeUnload(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?
        ): Boolean {
            Log.i(TAG, "onJsBeforeUnload message: ${message} result: ${result} url: ${url}");
            return super.onJsBeforeUnload(view, url, message, result)
        }

        override fun onJsTimeout(): Boolean {
            Log.i(TAG, "onJsTimeout");
            return super.onJsTimeout()
        }
    }

    inner class MyWebViewClient(
    ) : WebViewClient() {

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