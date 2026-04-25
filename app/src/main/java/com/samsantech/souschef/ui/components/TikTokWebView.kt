//package com.samsantech.souschef.ui.components
//
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.webkit.JavascriptInterface
//import android.webkit.WebResourceRequest
//import android.webkit.WebView
//import android.webkit.WebViewClient
//import android.widget.Toast
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.width
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//
//@Composable
//fun TikTokWebView(postId: String, height: Int, width: Int, onPlayed: () -> Unit = {}) {
//    val context = LocalContext.current
//    val webView = remember { WebView(context) }
//
//    AndroidView(
//        modifier = Modifier.height(height.dp).width(width.dp),
//        factory = {
//            webView.apply {
//                settings.javaScriptEnabled = true
//                addJavascriptInterface(
//                    WebAppInterface {
//                        onPlayed()
//                    },
//                    "AndroidInterface"
//                )
//                val url = "https://www.tiktok.com/player/v1/$postId?&music_info=0&description=0&native_context_menu=0&closed_caption=0&autoplay=0"
//
//                webViewClient = object : WebViewClient() {
//                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
//                        try {
//                            // Check if TikTok app is installed
//                            val packageManager = context.packageManager
//                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                            val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
//
//                            if (resolveInfo != null) {
//                                // TikTok app is installed, open in the app
//                                context.startActivity(intent)
//                            } else {
//                                // TikTok app is not installed, open in browser
//                                context.startActivity(intent)
//                            }
//                        } catch (e: Exception) {
//                            Toast.makeText(context, "An error while opening the TikTok video.", Toast.LENGTH_LONG).show()
//                        }
//
//                        return true
//                    }
//
//                    override fun onPageFinished(view: WebView, url: String) {
//                        super.onPageFinished(view, url)
//                    }
//                }
//
//                val htmlContent = """
//                    <html>
//                        <body style="padding: 0; margin: 0;">
//                            <iframe
//                                style="border: none;"
//                                height="${height}px"
//                                width= "${width}px"
//                                src="$url"
//                                allow="fullscreen"
//                                id="tiktokIframe"
//                            >
//                            </iframe>
//                        </body>
//                        <script>
//                            window.addEventListener('message', (event) => {
//                                if (event.data && event.data.type === 'onStateChange' && event.data.value === 1) {
//                                    AndroidInterface.onStateChange(1);
//                                }
//                            });
//                        </script>
//                    </html>
//                """
//
//                loadData(htmlContent, "text/html", "UTF-8")
//            }
//        }
//    )
//}
//
//class WebAppInterface(private val onPlayed: () -> Unit) {
//    @JavascriptInterface
//    fun onStateChange(value: Int) {
//        if (value == 1) {
//            onPlayed()
//        }
//    }
//}
