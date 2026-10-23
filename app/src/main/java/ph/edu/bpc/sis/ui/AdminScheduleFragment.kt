package ph.edu.bpc.sis.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ph.edu.bpc.sis.ManageStudentActivity
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.FragmentAdminScheduleBinding

class AdminScheduleFragment : Fragment() {
    private var _binding: FragmentAdminScheduleBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StudentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = StudentAdapter { student ->
            val intent = Intent(requireContext(), ManageStudentActivity::class.java)
            intent.putExtra("student_no", student.studentNo)
            startActivity(intent)
        }
        binding.rvScheduleStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvScheduleStudents.adapter = adapter
        adapter.submitList(StudentRepository.getAllStudents())
    }

    override fun onResume() {
        super.onResume()
        adapter.submitList(StudentRepository.getAllStudents())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
