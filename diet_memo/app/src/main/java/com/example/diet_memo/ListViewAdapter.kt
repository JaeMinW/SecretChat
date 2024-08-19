package com.example.diet_memo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ListViewAdapter(val list : MutableList<DataModel>) : BaseAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var convertView = convertView
        if(convertView == null){
            convertView = LayoutInflater.from(parent?.context).inflate(R.layout.listview_item, parent, false)
        }

        val data = convertView?.findViewById<TextView>(R.id.listViewDataArea)
        val memo = convertView?.findViewById<TextView>(R.id.ListViewMemoArea)

        data!!.text = list[position].date
        memo!!.text = list[position].memo

        return convertView!!
    }
}