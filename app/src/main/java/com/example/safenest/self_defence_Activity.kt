package com.example.safenest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class self_defence_Activity : AppCompatActivity() {

    private lateinit var youtubeWebView : WebView
    private lateinit var externalLink: TextView
    private lateinit var content: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_self_defence)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }


        youtubeWebView = findViewById<WebView>(R.id.youtubeWebView)
        val webSettings: WebSettings = youtubeWebView.settings
        webSettings.javaScriptEnabled = true
        youtubeWebView.webViewClient = WebViewClient()
        youtubeWebView.loadUrl("https://www.youtube.com/embed/KVpxP3ZZtAc")
        content = findViewById(R.id.instructionsContent)
        // External link to Documentation
        externalLink = findViewById<TextView>(R.id.externalLink)
        externalLink.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.your-documentation-link.com"))
            startActivity(browserIntent)
        }
    }
}