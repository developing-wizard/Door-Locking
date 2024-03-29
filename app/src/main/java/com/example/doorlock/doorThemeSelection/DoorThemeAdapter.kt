package com.example.doorlock.doorThemeSelection


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.doorlock.R

class DoorThemeAdapter(
    private val courseList: ArrayList<DoorThemeModal>,
    private val context: Context
) : RecyclerView.Adapter<DoorThemeAdapter.CourseViewHolder>() {
    private var sharedPreferences =
        context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.course_rv_item,
            parent, false
        )
        val height = parent.measuredHeight / 4
        itemView.minimumHeight = height
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, doorposition: Int) {
        val door = courseList[doorposition]
        val checkposition = sharedPreferences.getInt("item_selected", -1)
        holder.selectedDoorCheckbox.isChecked = checkposition == doorposition

        holder.doorCard.setOnClickListener {
            onItemClickListener?.onItemClick(doorposition, door)
            sharedPreferences.edit().putInt("item_selected", doorposition).apply()
            notifyDataSetChanged()
        }
        holder.doorCard.setImageResource(courseList.get(doorposition).doorimg)
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, doors: DoorThemeModal) {
        }
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }


    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val doorCard: ImageView = itemView.findViewById(R.id.door_img)
        val selectedDoorCheckbox: CheckBox = itemView.findViewById(R.id.checkbox_item)

    }

}
