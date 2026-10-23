package ph.edu.bpc.sis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.data.ClassSession
import ph.edu.bpc.sis.databinding.ItemNextClassBinding

class NextClassAdapter(private val items: List<ClassSession>) :
    RecyclerView.Adapter<NextClassAdapter.VH>() {

    inner class VH(val b: ItemNextClassBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemNextClassBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.tvTime.text = item.start
        holder.b.tvSubject.text = item.title
        holder.b.tvMeta.text = holder.itemView.context
            .getString(R.string.next_class_meta, item.code, item.room, item.end)
    }
}
