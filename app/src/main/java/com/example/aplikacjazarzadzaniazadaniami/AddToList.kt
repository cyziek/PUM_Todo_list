package com.example.aplikacjazarzadzaniazadaniami

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
import android.os.Build
import android.util.JsonReader
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*
import java.lang.Boolean.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AddToList : Fragment() {

    private var _binding: ListAddBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = ListAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                Toast.makeText(context, "Dodano zadanie!!", Toast.LENGTH_SHORT).show()

                val path = context?.getExternalFilesDir(null)

                val letDirectory = File(path, "LET")
                letDirectory.mkdirs()
                val file = File(letDirectory, "Records.json")

                val zadanie = Zadania()

                zadanie.id = 1

                //switch
                if (binding.notificationAdd.isChecked) {
                    zadanie.notif = TRUE
                } else {
                    zadanie.notif = FALSE
                }

                //title
                zadanie.title = binding.titleAdd.text.toString()

                //calendar
                zadanie.date = Date(binding.dateAdd.date)

                //description
                zadanie.desc = binding.descAdd.text.toString()

                //radiobutton
                when {
                    binding.radioAdd1.isChecked -> zadanie.prior = "Najniższy"
                    binding.radioAdd2.isChecked -> zadanie.prior = "Niski"
                    binding.radioAdd3.isChecked -> zadanie.prior = "Średni"
                    binding.radioAdd4.isChecked -> zadanie.prior = "Wysoki"
                    binding.radioAdd5.isChecked -> zadanie.prior = "Najwyższy"
                }

                if(!file.exists()) {
                    val jsonArray: MutableList<Zadania> = mutableListOf()
                    jsonArray.add(zadanie)
                    val jsonString = Gson().toJson(jsonArray)
                    file.appendText(jsonString)
                }else{
                    val gson = Gson()
                    val jsonArray : MutableList<Zadania> = gson.fromJson(FileReader(file), object : TypeToken<MutableList<Zadania>>(){}.type)
                    jsonArray.add(zadanie)
                    val jsonString = Gson().toJson(jsonArray)
                    file.writeText(jsonString)
                }

//                Log.d("String",path.toString())



                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
                //Log.d("Beniz", "hehe");
            }
        }

        //scroll
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

        //soft keyboard
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