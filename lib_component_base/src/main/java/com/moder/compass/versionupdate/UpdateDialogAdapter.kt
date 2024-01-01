package com.moder.compass.versionupdate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.moder.compass.component.base.R

/**
 * @author sunmeng
 * create at 2021-04-26
 * Email: sunmeng12@baidu.com
 */
class UpdateDialogAdapter(private val context: Context, private val items: ArrayList<String>) :
        RecyclerView.Adapter<UpdateDialogAdapter.ViewHolder>() {

    /**
     *
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.update_item_layout, parent, false)
        return ViewHolder(view);
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val title = holder.itemView.findViewById<TextView>(R.id.tv_item)
        title.text = items[position]
    }

    override fun getItemCount() = items.size

}