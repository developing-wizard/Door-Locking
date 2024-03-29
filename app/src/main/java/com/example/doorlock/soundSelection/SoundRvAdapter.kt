package com.example.doorlock.soundSelection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.doorlock.R

class SoundRvAdapter(private  val soundlist : List<SoundModal>, private val context: Context) :
    RecyclerView.Adapter<SoundRvAdapter.SoundViewHolder>() {
    private var sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.sounditems, parent ,false)
        val height = parent.minimumHeight/4
        itemView.minimumHeight = height
        return SoundViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        return soundlist.size
    }
    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) {
        val sounds = soundlist[position]
        val checkposition = sharedPreferences.getInt("soundss_selected", -1)
        holder.selectedDoorCheckbox.isChecked = checkposition == position
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position,sounds)
            sharedPreferences.edit().putInt("soundss_selected", position).apply()
            notifyDataSetChanged()

        }
        holder.images.setImageResource(soundlist.get(position).soundimage)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, sound: SoundModal)
    }
    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    class SoundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
    val images : ImageView = itemView.findViewById(R.id.soundsimage)
        val selectedDoorCheckbox: CheckBox = itemView.findViewById(R.id.checkbox_item)

    }
}