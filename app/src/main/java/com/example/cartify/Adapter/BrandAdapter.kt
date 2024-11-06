package com.example.cartify.Adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cartify.Model.BrandModel
import com.example.cartify.R
import com.example.cartify.databinding.ViewholderBrandBinding

class BrandAdapter(val items: MutableList<BrandModel>) :
    RecyclerView.Adapter<BrandAdapter.Viewholder>() {

    private var selectedPosition = -1
    private var lastSelectedPosition = -1
    private lateinit var context: Context

    class Viewholder(val binding: ViewholderBrandBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandAdapter.Viewholder {
        context = parent.context
        val binding = ViewholderBrandBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: BrandAdapter.Viewholder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            title.text = item.title

            Glide.with(context)
                .load(item.picUrl)
                .into(pic)

            root.setOnClickListener {
                lastSelectedPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(lastSelectedPosition)
                notifyItemChanged(selectedPosition)
            }

            title.setTextColor(context.resources.getColor(R.color.white))
            if (selectedPosition == position) {
                pic.setBackgroundResource(0)
                mainLayout.setBackgroundResource(R.drawable.purple_bg)
                ImageViewCompat.setImageTintList(
                    pic,
                    ColorStateList.valueOf(context.getColor(R.color.white))
                )

                title.visibility = View.VISIBLE
            } else {
                pic.setBackgroundResource(R.drawable.grey_bg)
                mainLayout.setBackgroundResource(0)
                ImageViewCompat.setImageTintList(
                    pic,
                    ColorStateList.valueOf(context.getColor(R.color.black))
                )

                title.visibility = View.GONE
            }

        }
    }

    override fun getItemCount(): Int = items.size

}