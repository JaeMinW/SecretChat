package com.example.mango_contents

import android.os.Bundle
import android.webkit.WebView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.database

class ViewActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        auth = Firebase.auth

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        val webView = findViewById<WebView>(R.id.webView)
        webView.loadUrl(intent.getStringExtra("url").toString())

        val database = Firebase.database
        val myBookMark = database.getReference("bookmark_ref")

        val url = intent.getStringExtra("url").toString()
        val imageUrl = intent.getStringExtra("imageUrl").toString()
        val title = intent.getStringExtra("title").toString()

        val saveText = findViewById<TextView>(R.id.saveText)

        saveText.setOnClickListener{
            myBookMark
                .child(auth.currentUser!!.uid)
                .push()
                .setValue(ContentsModel(url, imageUrl, title))
        }

    }
}