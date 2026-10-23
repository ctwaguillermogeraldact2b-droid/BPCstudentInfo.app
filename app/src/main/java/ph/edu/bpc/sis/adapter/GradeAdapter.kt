package ph.edu.bpc.sis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.asGrade
import ph.edu.bpc.sis.data.GradeRecord
import ph.edu.bpc.sis.databinding.ItemGradeBinding

class GradeAdapter(private val items: List<GradeRecord>) :
    RecyclerView.Adapter<GradeAdapter.VH>() {

    inner class VH(val b: ItemGradeBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemGradeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.b.tvCode.text = item.code
        holder.b.tvTitle.text = item.title
        holder.b.tvFinalGrade.text = item.finalGrade.asGrade()
        holder.b.tvBreakdown.text = context.getString(
            R.string.grade_breakdown,
            item.midterm.asGrade(),
            item.finals.asGrade(),
            item.units
        )
        holder.b.tvRemarks.text = item.remarks

        val colorRes = when (item.remarks) {
            "Passed" -> R.color.status_pass
            "Conditional" -> R.color.status_warn
            "Failed" -> R.color.status_fail
            else -> R.color.ink_muted
        }
        holder.b.tvRemarks.setTextColor(ContextCompat.getColor(context, colorRes))
    }
}
