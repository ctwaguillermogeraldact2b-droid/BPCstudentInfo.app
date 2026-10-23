package ph.edu.bpc.sis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.data.Announcement
import ph.edu.bpc.sis.databinding.ItemAnnouncementBinding

class AnnouncementAdapter(private val items: List<Announcement>) :
    RecyclerView.Adapter<AnnouncementAdapter.VH>() {

    inner class VH(val b: ItemAnnouncementBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAnnouncementBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.tvTitle.text = item.title
        holder.b.tvBody.text = item.body
        holder.b.tvSource.text = holder.itemView.context
            .getString(R.string.announcement_meta, item.source, item.postedOn)
    }
}
