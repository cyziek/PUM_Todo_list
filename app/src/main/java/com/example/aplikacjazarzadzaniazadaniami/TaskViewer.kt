package com.example.aplikacjazarzadzaniazadaniami

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aplikacjazarzadzaniazadaniami.databinding.TaskViewerBinding

class TaskViewer : Fragment() {

    private var _binding: TaskViewerBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TaskViewerBinding.inflate(inflater, container, false)
        binding.test.text = this.arguments?.getInt("position").toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}