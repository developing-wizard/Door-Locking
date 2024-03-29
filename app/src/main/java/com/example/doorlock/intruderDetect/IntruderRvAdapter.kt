package com.example.doorlock.intruderDetect



import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doorlock.R
import java.io.File

class IntruderRvAdapter(private val imagelist: MutableList<File>, private val context: Context) :
    RecyclerView.Adapter<IntruderRvAdapter.IntruderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntruderViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.intruder_items, parent, false)
        val height = parent.minimumHeight / 4
        itemView.minimumHeight = height
        return IntruderViewHolder(itemView)

    }

        fun removeItem(position: Int) {
            if (position >= 0 && position < imagelist.size) {
                val fileToDelete = imagelist[position]
                if (fileToDelete.exists()) {
                    val deleted = fileToDelete.delete()
                    if (deleted) {
                        imagelist.removeAt(position)
                        notifyItemRemoved(position)
                        if (imagelist.isEmpty()) {
                            Toast.makeText(context,"No More Image",Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context,"Please Refresh the page",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    override fun getItemCount(): Int {
        return imagelist.size
    }

    override fun onBindViewHolder(holder: IntruderViewHolder, position: Int) {
        val reversedPosition = itemCount - 1 - position
        val image = imagelist[reversedPosition]

        if (image.exists()) {
            Glide.with(context)
                .load(image)
                .into(holder.images)
        } else {
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position, image)
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.preview_dialog)
            val window = dialog.window
            val layoutParams = window?.attributes
            dialog.setCancelable(true)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            layoutParams?.width = (getScreenWidth() * 0.99).toInt()
            layoutParams?.height = (getScreenHeight() * 0.8).toInt()
            window?.attributes = layoutParams
            val imageView = dialog.findViewById<ImageView>(R.id.preview_image)
            val deletebutton = dialog.findViewById<Button>(R.id.deletebtn)
            val closebutton = dialog.findViewById<Button>(R.id.close_button)
            Glide.with(context)
                .load(image)
                .into(imageView)

            deletebutton.setOnClickListener {

                removeItem(reversedPosition)
                notifyDataSetChanged()
                dialog.dismiss()
            }
            closebutton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int, image: File)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    class IntruderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val images: ImageView = itemView.findViewById(R.id.intruderimage)

    }

    private fun getScreenWidth(): Int {
        return context.resources.displayMetrics.widthPixels
    }

    private fun getScreenHeight(): Int {
        return context.resources.displayMetrics.heightPixels
    }

}