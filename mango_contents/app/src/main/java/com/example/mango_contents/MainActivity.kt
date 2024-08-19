package com.example.mango_contents

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val items = mutableListOf<ContentsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bookmarkButton = findViewById<TextView>(R.id.bookmarkBtn)
        bookmarkButton.setOnClickListener{

            val intent = Intent(this, BookMarkActivity::class.java)
            startActivity(intent)

        }
        items.add(
            ContentsModel(
                "https://www.siksinhot.com/P/354862",
                "https://img.siksinhot.com/place/1412235832752688.jpg?w=307&h=300&c=Y",
                "그리다디저트"
            )
        )
        items.add(
            ContentsModel(
                "https://www.siksinhot.com/P/323",
                "https://img.siksinhot.com/place/1530597402238010.jpg?w=307&h=300&c=Y",
                "새벽집 청담동점"
            )
        )
        items.add(
            ContentsModel(
                "https://www.siksinhot.com/P/242539",
                "https://img.siksinhot.com/place/1542173423137230.jpg?w=307&h=300&c=Y",
                "목포집"
            )
        )
        items.add(
            ContentsModel(
                "https://www.siksinhot.com/P/148199",
                "https://img.siksinhot.com/place/1462860983401175.png?w=307&h=300&c=Y",
                "벽제갈비"
            )
        )
        items.add(
            ContentsModel(
                "https://www.siksinhot.com/P/348908",
                "https://img.siksinhot.com/place/1510631137733804.jpg?w=307&h=300&c=Y",
                "스와니예"
            )
        )
        items.add(
            ContentsModel(
                "https://www.siksinhot.com/P/375271",
                "https://img.siksinhot.com/place/1583453831645740.jpg?w=307&h=300&c=Y",
                "스시인"
            )
        )
        items.add(
            ContentsModel(
                "https://www.siksinhot.com/P/861407",
                "https://img.siksinhot.com/place/1676002986314416.jpg?w=307&h=300&c=Y",
                "무오키 MUOKI"
            )
        )



        val recyclerView = findViewById<RecyclerView>(R.id.rv)
        val rvAdapter = RVAdapter(baseContext, items)
        recyclerView.adapter = rvAdapter

        rvAdapter.itemClick = object: RVAdapter.ItemClick{
            override fun onClick(view: View, position: Int) {

                val intent = Intent(baseContext, ViewActivity::class.java)
                intent.putExtra("url", items[position].url)
                intent.putExtra("title", items[position].titleText)
                intent.putExtra("imageUrl", items[position].imageUrl)
                startActivity(intent)
            }

        }
        recyclerView.layoutManager = GridLayoutManager(this, 2)

    }
}