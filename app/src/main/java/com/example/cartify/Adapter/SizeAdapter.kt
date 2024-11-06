package com.example.cartify.Adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cartify.R
import com.example.cartify.databinding.ViewholderBrandBinding
import com.example.cartify.databinding.ViewholderColorBinding
import com.example.cartify.databinding.ViewholderSizeBinding

class SizeAdapter(val items: MutableList<String>) :
    RecyclerView.Adapter<SizeAdapter.Viewholder>() {

    private var selectedPosition = -1
    private var lastSelectedPosition = -1
    private lateinit var context: Context

    class Viewholder(val binding: ViewholderSizeBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeAdapter.Viewholder {
        context = parent.context
        val binding = ViewholderSizeBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: SizeAdapter.Viewholder, position: Int) {

        with(holder.binding) {

          txtSize.text=items[position]

            root.setOnClickListener {
                lastSelectedPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(lastSelectedPosition)
                notifyItemChanged(selectedPosition)
            }

            if (selectedPosition == position) {
                colorLayout.setBackgroundResource(R.drawable.grey_bg_selected)
                txtSize.setTextColor(context.resources.getColor(R.color.purple))
            } else {
                colorLayout.setBackgroundResource(R.drawable.grey_bg)
                txtSize.setTextColor(context.resources.getColor(R.color.purple))
            }

        }
    }

    override fun getItemCount(): Int = items.size

}