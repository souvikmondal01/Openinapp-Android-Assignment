package com.kivous.openinapp_assignment.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kivous.openinapp_assignment.R
import com.kivous.openinapp_assignment.databinding.ListLinkBinding
import com.kivous.openinapp_assignment.models.LinkInfo

class LinkInfoAdapter(private val context: Context, private val list: List<LinkInfo>) :
    RecyclerView.Adapter<LinkInfoAdapter.ViewHolder>() {

    class ViewHolder(val binding: ListLinkBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ListLinkBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_link,
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.binding.linkInfo = data
        Glide.with(context).load(data.original_image).centerCrop().into(holder.binding.ivImg)

    }

    override fun getItemCount(): Int {
        return if (list.size > 4) {
            4
        } else {
            list.size
        }
    }
}