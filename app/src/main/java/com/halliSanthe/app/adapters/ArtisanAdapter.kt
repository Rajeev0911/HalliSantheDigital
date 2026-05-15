package com.halliSanthe.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.halliSanthe.app.R
import com.halliSanthe.app.models.Artisan

class ArtisanAdapter(
    private val artisanList: List<Artisan>,
    private val onItemClick: (Artisan) -> Unit
) : RecyclerView.Adapter<ArtisanAdapter.ArtisanViewHolder>() {

    class ArtisanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfile: ImageView = itemView.findViewById(R.id.iv_artisan_profile)
        val tvInitials: TextView = itemView.findViewById(R.id.tv_artisan_initials)
        val tvName: TextView = itemView.findViewById(R.id.tv_artisan_name)
        val tvSpecialty: TextView = itemView.findViewById(R.id.tv_artisan_specialty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtisanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_artisan_list, parent, false)
        return ArtisanViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtisanViewHolder, position: Int) {
        val artisan = artisanList[position]
        holder.tvName.text = artisan.name
        holder.tvSpecialty.text = artisan.specialty

        if (artisan.imageRes != null) {
            holder.ivProfile.visibility = View.VISIBLE
            holder.tvInitials.visibility = View.GONE
            holder.ivProfile.setImageResource(artisan.imageRes)
        } else {
            holder.ivProfile.visibility = View.GONE
            holder.tvInitials.visibility = View.VISIBLE
            holder.tvInitials.text = artisan.initials
            
            // Generate a color based on the name for variety
            val colors = listOf(
                0xFF2E7D32.toInt(), // Green
                0xFF1565C0.toInt(), // Blue
                0xFFD84315.toInt(), // Deep Orange
                0xFF6A1B9A.toInt(), // Purple
                0xFF00838F.toInt()  // Cyan
            )
            val colorIndex = Math.abs(artisan.name.hashCode()) % colors.size
            holder.tvInitials.setBackgroundColor(colors[colorIndex])
        }

        holder.itemView.setOnClickListener { onItemClick(artisan) }
    }

    override fun getItemCount(): Int = artisanList.size
}
