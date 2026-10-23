package ph.edu.bpc.sis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.data.Subject
import ph.edu.bpc.sis.databinding.ItemOfferedSubjectBinding

/**
 * Subject picker for the enrollment form.
 * Reports the current selection upward so the footer can keep a running unit count.
 */
class OfferedSubjectAdapter(
    private val items: List<Subject>,
    private val onSelectionChanged: (List<Subject>) -> Unit
) : RecyclerView.Adapter<OfferedSubjectAdapter.VH>() {

    private val selectedCodes = mutableSetOf<String>()

    val selected: List<Subject> get() = items.filter { it.code in selectedCodes }

    val totalUnits: Int get() = selected.sumOf { it.units }

    inner class VH(val b: ItemOfferedSubjectBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemOfferedSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val context = holder.itemView.context
        val open = item.slotsLeft > 0

        holder.b.tvCode.text = item.code
        holder.b.tvTitle.text = item.title
        holder.b.tvUnits.text = context.resources
            .getQuantityString(R.plurals.units, item.units, item.units)
        holder.b.tvMeta.text = "${item.startTime} - ${item.endTime}  ·  Room: ${item.room}"

        holder.b.tvSlots.text = if (open) {
            context.resources.getQuantityString(R.plurals.slots_left, item.slotsLeft, item.slotsLeft)
        } else {
            context.getString(R.string.slots_closed)
        }
        holder.b.tvSlots.setTextColor(
            ContextCompat.getColor(context, if (open) R.color.status_pass else R.color.status_fail)
        )

        holder.b.cbSelect.setOnCheckedChangeListener(null)
        holder.b.cbSelect.isChecked = item.code in selectedCodes
        holder.b.cbSelect.isEnabled = open
        holder.b.root.alpha = if (open) 1f else 0.55f

        holder.b.cbSelect.setOnCheckedChangeListener { _, checked ->
            if (checked) selectedCodes.add(item.code) else selectedCodes.remove(item.code)
            onSelectionChanged(selected)
        }

        holder.b.root.setOnClickListener {
            if (open) holder.b.cbSelect.isChecked = !holder.b.cbSelect.isChecked
        }
    }

    fun clearSelection() {
        selectedCodes.clear()
        notifyDataSetChanged()
        onSelectionChanged(selected)
    }
}
