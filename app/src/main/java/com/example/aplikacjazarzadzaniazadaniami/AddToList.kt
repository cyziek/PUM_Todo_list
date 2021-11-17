package com.example.aplikacjazarzadzaniazadaniami

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.findNavController
import com.example.aplikacjazarzadzaniazadaniami.databinding.ListAddBinding
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat.getSystemService
import android.app.Activity
import android.view.View.OnFocusChangeListener
import androidx.core.content.ContextCompat.getSystemService










/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddToList : Fragment() {

    private var _binding: ListAddBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = ListAddBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.add.setOnClickListener{ view ->
            Snackbar.make(view, "Hehe GUZIG :-DDD", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        binding.add.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}