package ph.edu.bpc.sis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.data.ClassSession
import ph.edu.bpc.sis.databinding.ItemScheduleBinding

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.VH>() {

    private val items = mutableListOf<ClassSession>()

    fun submit(sessions: List<ClassSession>) {
        items.clear()
        items.addAll(sessions)
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemScheduleBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.tvStart.text = item.start
        holder.b.tvEnd.text = item.end
        holder.b.tvSubject.text = holder.itemView.context
            .getString(R.string.subject_line, item.code, item.title)
        holder.b.tvMeta.text = holder.itemView.context
            .getString(R.string.room_and_teacher, item.room, item.instructor)
    }
}
