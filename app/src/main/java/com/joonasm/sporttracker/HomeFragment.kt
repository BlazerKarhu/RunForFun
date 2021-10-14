package com.joonasm.sporttracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.joonasm.sporttracker.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)

        /*binding.newExercise.setOnClickListener {
            (activity as MainActivity).hideBottomNav()
            parentFragmentManager.commit {
                replace<MapFragment>(R.id.fragment_container_view)
                add<NewExerciseFragment>(R.id.fragment_container_view)
                setReorderingAllowed(true)
                addToBackStack(null) // name can be null
            }

        }*/


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}