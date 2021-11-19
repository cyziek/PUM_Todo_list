package com.example.aplikacjazarzadzaniazadaniami

import android.R.attr
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.aplikacjazarzadzaniazadaniami.databinding.ListAddBinding
import android.widget.Toast
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import android.app.Activity
import android.content.Intent as Intent
import android.R.attr.data
import android.content.Context
import android.util.Log
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.widget.RadioButton
import java.io.File
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


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

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance()

        binding.dateAdd.setOnDateChangeListener{view, year, month, dayOfMonth ->
            calendar.set(year,month,dayOfMonth)
            binding.dateAdd.date = calendar.timeInMillis
        }

        binding.add.setOnClickListener{
            if(binding.descAdd.text.toString().trim().equals("") || binding.titleAdd.text.toString().trim().equals("")){
                Toast.makeText(context, "Brak danych!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Dodano zadanie!", Toast.LENGTH_SHORT).show()

                val path = context?.getExternalFilesDir(null)

                val letDirectory = File(path, "LET")
                letDirectory.mkdirs()
                val file = File(letDirectory, "Records.txt")

                //switch
                if(binding.notificationAdd.isChecked){
                    file.appendText("1")
                }else{
                    file.appendText("0")
                }
                file.appendText(";")


                //title
                file.appendText(binding.titleAdd.text.toString())
                file.appendText(";")

                //calendar
                val selectedDate:Long = binding.dateAdd.date
                calendar.timeInMillis = selectedDate
                val dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT)
                file.appendText(dateFormatter.format(calendar.time))
                file.appendText(";")

                //description
                file.appendText(binding.descAdd.text.toString())
                file.appendText(";")

                //radiobutton
                when{
                    binding.radioAdd1.isChecked -> file.appendText("Najniższy")
                    binding.radioAdd2.isChecked -> file.appendText("Niski")
                    binding.radioAdd3.isChecked -> file.appendText("Średni")
                    binding.radioAdd4.isChecked -> file.appendText("Wysoki")
                    binding.radioAdd5.isChecked -> file.appendText("Najwyższy")
                }

                file.appendText("\n")
                Log.d("String",path.toString())

                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
                //Log.d("Beniz", "hehe");
            }
        }

        binding.descAdd.setOnTouchListener(OnTouchListener { v, event ->
            if (binding.descAdd.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        return@OnTouchListener true
                    }
                }
            }
            false
        })

        binding.scrollAdd.setOnTouchListener(OnTouchListener { v, event ->
                val imm =
                    requireContext()!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                false
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}