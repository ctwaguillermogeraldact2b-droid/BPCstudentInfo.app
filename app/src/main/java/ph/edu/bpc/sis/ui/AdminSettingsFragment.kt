package ph.edu.bpc.sis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.FragmentAdminSettingsBinding

class AdminSettingsFragment : Fragment() {
    private var _binding: FragmentAdminSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.switchEnrollment.isChecked = StudentRepository.currentTerm().enrollmentOpen
        binding.switchEnrollment.setOnCheckedChangeListener { _, isChecked ->
            StudentRepository.setEnrollmentOpen(isChecked)
            Toast.makeText(
                requireContext(),
                if (isChecked) "Enrollment is now OPEN" else "Enrollment is now CLOSED",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.switchEnrollment.isChecked = StudentRepository.currentTerm().enrollmentOpen
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
