package com.example.segundoevalucacion.fragment

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.SearchView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.segundoevalucacion.R


class WebFragment : Fragment() {
    val URL_INICIO ="https://support.google.com/websearch/search?q="
    val SEARCH="/search?q="

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val swipe: SwipeRefreshLayout = view.findViewById(R.id.swipe_refesh)
        val seach : SearchView= view.findViewById(R.id.seach_view)
        val web: WebView = view.findViewById(R.id.web_view)
        swipe.setOnRefreshListener {
            web.reload()
        }
        seach.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                p0?.let {
                    if(URLUtil.isValidUrl((it))){
                        web.loadUrl(it)
                    }else{
                        web.loadUrl("$URL_INICIO$SEARCH$it")
                    }
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
        setWebView(web,swipe)

    }
    companion object {

        fun newInstance(): WebFragment {
            return WebFragment()
        }
    }

    private fun setWebView(web: WebView,swipe: SwipeRefreshLayout) {
        web.webChromeClient=object : WebChromeClient(){

        }
        web.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                //return super.shouldOverrideUrlLoading(view, request)
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                swipe.isRefreshing=true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                swipe.isRefreshing=false
            }
        }
        val setting = web.settings
        setting.javaScriptEnabled=true

        web.loadUrl(URL_INICIO)

    }


}