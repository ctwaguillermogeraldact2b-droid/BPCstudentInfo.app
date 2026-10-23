package ph.edu.bpc.sis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ph.edu.bpc.sis.adapter.GradeAdapter
import ph.edu.bpc.sis.data.StudentRepository
import ph.edu.bpc.sis.databinding.FragmentGradesBinding
import java.util.Locale

class GradesFragment : Fragment() {

    private var _binding: FragmentGradesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGradesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val records = StudentRepository.grades()

        binding.tvGradesTerm.text = StudentRepository.currentTerm().label

        val gwa = StudentRepository.generalAverage(records)
        binding.tvGwa.text = gwa?.let { String.format(Locale.US, "%.2f", it) } ?: "—"
        binding.tvUnitsEarned.text = records
            .filter { it.remarks == "Passed" }
            .sumOf { it.units }
            .toString()

        binding.rvGrades.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGrades.adapter = GradeAdapter(records)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
