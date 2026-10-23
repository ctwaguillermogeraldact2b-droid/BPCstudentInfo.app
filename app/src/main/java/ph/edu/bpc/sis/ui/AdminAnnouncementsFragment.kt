package ph.edu.bpc.sis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.edu.bpc.sis.R
import ph.edu.bpc.sis.data.Announcement
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.FragmentAdminAnnouncementsBinding
import ph.edu.bpc.sis.databinding.ItemAnnouncementAdminBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminAnnouncementsFragment : Fragment() {
    private var _binding: FragmentAdminAnnouncementsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AnnouncementAdminAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminAnnouncementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = AnnouncementAdminAdapter { announcement ->
            showEditAnnouncementDialog(announcement)
        }
        binding.rvAnnouncementsAdmin.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAnnouncementsAdmin.adapter = adapter

        binding.btnAddAnnouncement.setOnClickListener {
            showAddAnnouncementDialog()
        }
        adapter.submitList(StudentRepository.announcements())
    }

    private fun showAddAnnouncementDialog() {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_announcement, null)
        val etTitle = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etTitle)
        val etBody = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBody)
        val etSource = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etSource)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("New Announcement")
            .setView(view)
            .setPositiveButton("Post") { _, _ ->
                val title = etTitle.text.toString().trim()
                val body = etBody.text.toString().trim()
                val source = etSource.text.toString().trim()
                val date = SimpleDateFormat("d MMMM yyyy", Locale.US).format(Date())

                if (title.isNotEmpty() && body.isNotEmpty()) {
                    StudentRepository.addAnnouncement(Announcement(title, body, date, source))
                    adapter.submitList(StudentRepository.announcements())
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditAnnouncementDialog(announcement: Announcement) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_announcement, null)
        val etTitle = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etTitle)
        val etBody = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBody)
        val etSource = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etSource)

        etTitle.setText(announcement.title)
        etBody.setText(announcement.body)
        etSource.setText(announcement.source)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Announcement")
            .setView(view)
            .setPositiveButton("Update") { _, _ ->
                val title = etTitle.text.toString().trim()
                val body = etBody.text.toString().trim()
                val source = etSource.text.toString().trim()
                
                if (title.isNotEmpty() && body.isNotEmpty()) {
                    StudentRepository.updateAnnouncement(announcement, announcement.copy(
                        title = title, body = body, source = source
                    ))
                    adapter.submitList(StudentRepository.announcements())
                }
            }
            .setNeutralButton("Delete") { _, _ ->
                StudentRepository.deleteAnnouncement(announcement)
                adapter.submitList(StudentRepository.announcements())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        adapter.submitList(StudentRepository.announcements())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class AnnouncementAdminAdapter(private val onClick: (Announcement) -> Unit) : RecyclerView.Adapter<AnnouncementAdminAdapter.Holder>() {
    private var list: List<Announcement> = emptyList()

    fun submitList(newList: List<Announcement>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemAnnouncementAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list[position]
        holder.binding.tvTitle.text = item.title
        holder.binding.tvMeta.text = holder.itemView.context.getString(
            R.string.announcement_meta,
            item.postedOn,
            item.source
        )
        holder.binding.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = list.size

    class Holder(val binding: ItemAnnouncementAdminBinding) : RecyclerView.ViewHolder(binding.root)
}
