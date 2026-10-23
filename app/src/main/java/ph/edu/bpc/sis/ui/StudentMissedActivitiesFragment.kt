package ph.edu.bpc.sis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.edu.bpc.sis.ManageStudentActivity
import ph.edu.bpc.sis.TeacherManageStudentActivity
import ph.edu.bpc.sis.data.MissedActivity
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.DialogAddMissedActivityBinding
import ph.edu.bpc.sis.databinding.FragmentStudentMissedActivitiesBinding
import ph.edu.bpc.sis.databinding.ItemMissedActivityBinding

class StudentMissedActivitiesFragment : Fragment() {

    private var _binding: FragmentStudentMissedActivitiesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MissedActivitiesAdapter

    private val currentStudentNo: String?
        get() = (activity as? TeacherManageStudentActivity)?.studentNo
            ?: (activity as? ManageStudentActivity)?.studentNo

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudentMissedActivitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val studentNo = currentStudentNo ?: return

        adapter = MissedActivitiesAdapter(
            onClick = { item -> showAddEditDialog(studentNo, item) },
            onLongClick = { item -> showDeleteDialog(studentNo, item) }
        )
        binding.rvMissedActivities.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMissedActivities.adapter = adapter

        binding.btnAddMissedActivity.setOnClickListener {
            showAddEditDialog(studentNo, null)
        }

        refresh(studentNo)
    }

    private fun refresh(studentNo: String) {
        val list = StudentRepository.missedActivities(studentNo)
        adapter.submitList(list)
        binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        binding.rvMissedActivities.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun showAddEditDialog(studentNo: String, existing: MissedActivity?) {
        val dialogBinding = DialogAddMissedActivityBinding.inflate(layoutInflater)

        existing?.let {
            dialogBinding.etSubjectCode.setText(it.subjectCode)
            dialogBinding.etActivityTitle.setText(it.activityTitle)
            dialogBinding.etDueDate.setText(it.dueDate)
            dialogBinding.etStatus.setText(it.status)
            dialogBinding.etRemarks.setText(it.remarks)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (existing == null) "Add Missed Activity" else "Edit Missed Activity")
            .setView(dialogBinding.root)
            .setPositiveButton(if (existing == null) "Add" else "Update") { _, _ ->
                val subjectCode = dialogBinding.etSubjectCode.text?.toString().orEmpty().trim()
                val title = dialogBinding.etActivityTitle.text?.toString().orEmpty().trim()
                val dueDate = dialogBinding.etDueDate.text?.toString().orEmpty().trim()
                val status = dialogBinding.etStatus.text?.toString().orEmpty().trim().ifEmpty { "Pending" }
                val remarks = dialogBinding.etRemarks.text?.toString().orEmpty().trim()

                if (subjectCode.isNotEmpty() && title.isNotEmpty()) {
                    val activityItem = if (existing != null) {
                        existing.copy(
                            subjectCode = subjectCode,
                            activityTitle = title,
                            dueDate = dueDate,
                            status = status,
                            remarks = remarks
                        )
                    } else {
                        MissedActivity(
                            subjectCode = subjectCode,
                            activityTitle = title,
                            dueDate = dueDate,
                            status = status,
                            remarks = remarks
                        )
                    }

                    if (existing != null) {
                        StudentRepository.updateMissedActivity(studentNo, activityItem)
                    } else {
                        StudentRepository.addMissedActivity(studentNo, activityItem)
                    }
                    refresh(studentNo)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteDialog(studentNo: String, item: MissedActivity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Missed Activity?")
            .setMessage("Are you sure you want to delete ${item.activityTitle}?")
            .setPositiveButton("Delete") { _, _ ->
                StudentRepository.deleteMissedActivity(studentNo, item.id)
                refresh(studentNo)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        currentStudentNo?.let { refresh(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class MissedActivitiesAdapter(
    private val onClick: (MissedActivity) -> Unit,
    private val onLongClick: (MissedActivity) -> Unit
) : ListAdapter<MissedActivity, MissedActivitiesAdapter.Holder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemMissedActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)
        holder.binding.tvSubjectCode.text = item.subjectCode
        holder.binding.tvTitle.text = item.activityTitle
        holder.binding.tvDueDate.text = if (item.dueDate.isNotEmpty()) "Due: ${item.dueDate}" else "No due date"
        holder.binding.tvStatus.text = item.status
        
        if (item.remarks.isNotEmpty()) {
            holder.binding.tvRemarks.text = "Note: ${item.remarks}"
            holder.binding.tvRemarks.visibility = View.VISIBLE
        } else {
            holder.binding.tvRemarks.visibility = View.GONE
        }

        holder.binding.root.setOnClickListener { onClick(item) }
        holder.binding.root.setOnLongClickListener {
            onLongClick(item)
            true
        }
    }

    class Holder(val binding: ItemMissedActivityBinding) : RecyclerView.ViewHolder(binding.root)

    private object DiffCallback : DiffUtil.ItemCallback<MissedActivity>() {
        override fun areItemsTheSame(oldItem: MissedActivity, newItem: MissedActivity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MissedActivity, newItem: MissedActivity): Boolean {
            return oldItem == newItem
        }
    }
}
