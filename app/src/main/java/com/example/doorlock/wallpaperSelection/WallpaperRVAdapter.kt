package com.example.doorlock.wallpaperSelection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.doorlock.R

class WallpaperRVAdapter(private val walllpaperlist : ArrayList<WallpaperModal>, private val context: Context) : RecyclerView.Adapter<WallpaperRVAdapter.WallpaperViewHolder>()
{

    private val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.wallpaperitem,parent,false)
        val height = parent.minimumHeight/4
        itemView.minimumHeight = height
        return WallpaperViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: WallpaperViewHolder, wallpaperposition : Int) {
        val wallpaper = walllpaperlist[wallpaperposition]
        val checkposition = sharedPreferences.getInt("wallpapers_selected", -1)
        holder.selectedDoorCheckbox.isChecked = checkposition == wallpaperposition
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(wallpaperposition, wallpaper)
            sharedPreferences.edit().putInt("wallpapers_selected", wallpaperposition).apply()
            notifyDataSetChanged()
        }
        holder.wallpaperimage.setImageResource(walllpaperlist.get(wallpaperposition).ivwallpaper)
    }


    override fun getItemCount():Int {
      return walllpaperlist.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, wallpaper: WallpaperModal)
    }
    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }


    class WallpaperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val wallpaperimage : ImageView = itemView.findViewById(R.id.imagewallpaper)
        val selectedDoorCheckbox: CheckBox = itemView.findViewById(R.id.checkbox_item)

    }

}

