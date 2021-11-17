package com.example.aplikacjazarzadzaniazadaniami

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.aplikacjazarzadzaniazadaniami.databinding.ListAddBinding
import android.widget.Toast

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

        binding.add.setOnClickListener{
            Toast.makeText(context, "Dodano zadanie!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
//            Log.d("Beniz", "hehe");
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}