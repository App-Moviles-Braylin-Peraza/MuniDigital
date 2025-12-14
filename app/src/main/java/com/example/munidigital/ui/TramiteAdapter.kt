package com.example.munidigital.ui

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView
import com.example.munidigital.DetalleTramiteActivity
import com.example.munidigital.ImageViewerActivity
import com.example.munidigital.R
import com.example.munidigital.model.Tramite

class TramiteAdapter(private val tramites: List<Tramite>) : RecyclerView.Adapter<TramiteAdapter.TramiteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TramiteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tramite, parent, false)
        return TramiteViewHolder(view)
    }

    override fun onBindViewHolder(holder: TramiteViewHolder, position: Int) {
        val tramite = tramites[position]
        val context = holder.itemView.context
        holder.title.text = tramite.title
        holder.description.text = tramite.description ?: context.getString(R.string.sin_descripcion_tramite)

        val status = tramite.status ?: context.getString(R.string.pendiente)
        holder.status.text = status

        when (status.lowercase()) {
            "pendiente" -> holder.status.setTextColor(Color.parseColor("#FF9800"))
            "aprobado" -> holder.status.setTextColor(Color.parseColor("#4CAF50"))
            "rechazado" -> holder.status.setTextColor(Color.parseColor("#F44336"))
            else -> holder.status.setTextColor(Color.parseColor("#757575"))
        }

        val photoThumb = holder.photoThumb
        val firstPhotoUrl = tramite.attachments?.firstOrNull {
            it.file_type?.startsWith("image") == true ||
                    it.file_url.endsWith(".jpg") ||
                    it.file_url.endsWith(".jpeg") ||
                    it.file_url.endsWith(".png") ||
                    it.file_url.endsWith(".webp")
        }

        if (firstPhotoUrl != null) {
            Glide.with(context)
                .load(firstPhotoUrl.file_url)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .into(photoThumb)
            photoThumb.visibility = View.VISIBLE

            photoThumb.setOnClickListener {
                val intent = Intent(context, ImageViewerActivity::class.java)
                intent.putExtra("IMAGE_URL", firstPhotoUrl.file_url)
                context.startActivity(intent)
            }
        } else {
            photoThumb.setImageResource(android.R.drawable.ic_menu_gallery)
            photoThumb.visibility = View.INVISIBLE
            photoThumb.setOnClickListener(null)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetalleTramiteActivity::class.java)
            intent.putExtra("TRAMITE_ID", tramite.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = tramites.size

    class TramiteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tramite_title)
        val description: TextView = itemView.findViewById(R.id.tramite_description)
        val status: TextView = itemView.findViewById(R.id.tramite_status)
        val photoThumb: ImageView = itemView.findViewById(R.id.tramite_photo_thumb)
    }
}