package com.intervale.webview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.intervale.webview.databinding.InputUrlFragmentBinding

class InputUrlFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.input_url_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        InputUrlFragmentBinding.bind(view).run {
            button.setOnClickListener { v: View? ->
                (activity as MainActivity).navController.navigate(R.id.webview_dest, bundleOf(
                    "url" to edittext.text.toString(),
                    "chromeclient" to chromeclient.isChecked,
                    "javaScriptEnabled" to javaScriptEnabled.isChecked,
                    "javaScriptCanOpenWindowsAutomatically" to javaScriptCanOpenWindowsAutomatically.isChecked,
                    "domStorageEnabled" to domStorageEnabled.isChecked
                ))
            }
        }
    }
}