package com.example.mango_contents

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database

class BookMarkActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    private val contentModels = mutableListOf<ContentsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = com.google.firebase.ktx.Firebase.auth

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_mark)

        val recyclerview = findViewById<RecyclerView>(R.id.rv)
        val rvAdapter = RVAdapter(this, contentModels)
        recyclerview.adapter = rvAdapter

        recyclerview.layoutManager = GridLayoutManager(this, 2)

        val database = com.google.firebase.ktx.Firebase.database
        val myBookmarkRef = database.getReference("bookmark_ref")

        myBookmarkRef
            .child(auth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (dataModel in snapshot.children) {

                        Log.d("Datamodel", dataModel.toString())
                        contentModels.add(dataModel.getValue(ContentsModel::class.java)!!)

                    }
                    rvAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Bookmark", "dbError")
                }

            })

    }
}