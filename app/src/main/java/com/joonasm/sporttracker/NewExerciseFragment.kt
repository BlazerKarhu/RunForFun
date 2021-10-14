package com.joonasm.sporttracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.joonasm.sporttracker.databinding.FragmentHomeBinding
import com.joonasm.sporttracker.databinding.FragmentNewExerciseBinding

class NewExerciseFragment : Fragment() {
    private var _binding: FragmentNewExerciseBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewExerciseBinding.inflate(layoutInflater)

        binding.startButton.setOnClickListener {
            binding.startButton.visibility = View.INVISIBLE
            binding.endButton.visibility = View.VISIBLE
            parentFragmentManager.commit {
                replace<MapFragment>(R.id.fragment_container_view)
                setReorderingAllowed(true)
                addToBackStack(null) // name can be null
            }
        }

        binding.endButton.setOnClickListener {
            binding.endButton.visibility = View.INVISIBLE
        }


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_exercise, container, false)
    }
}