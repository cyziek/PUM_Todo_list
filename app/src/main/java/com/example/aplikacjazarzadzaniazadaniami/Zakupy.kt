package com.example.aplikacjazarzadzaniazadaniami

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.ui.AppBarConfiguration
import com.example.aplikacjazarzadzaniazadaniami.databinding.ActivityMainBinding
import com.example.aplikacjazarzadzaniazadaniami.databinding.ZakupyBinding
import android.R
import android.app.AlertDialog
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.text.SimpleDateFormat
import kotlin.random.Random
import android.content.DialogInterface
import android.text.InputType
import android.view.ContextThemeWrapper
import android.widget.EditText


class Zakupy : Fragment() {

    private var _binding: ZakupyBinding? = null
    private val binding get() = _binding!!

//    private val list = generateDummyList(10)
//    private val adapter = AdapterZakupy(list)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = ZakupyBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//            binding.rec1.adapter = adapter
        binding.rec1.layoutManager = LinearLayoutManager(this.context)
        binding.rec1.setHasFixedSize(true)

        binding.fab1.setOnClickListener{
            val alert: AlertDialog.Builder = AlertDialog.Builder(ContextThemeWrapper(this.context, R.style.ThemeOverlay_Material_Dialog_Alert))
            alert.setTitle("Podaj przedmiot")

            val input = EditText(this.context)
            input.hint = "Przedmiot"
            input.inputType = InputType.TYPE_CLASS_TEXT
            alert.setView(input)

            alert.setPositiveButton(
                "Dodaj",
                DialogInterface.OnClickListener { dialog, whichButton ->
                    //Your action here
                })

            alert.setNegativeButton("Anuluj",
                DialogInterface.OnClickListener { dialog, whichButton -> })

            alert.show()
//                val index = 0;
//
//                val newItem = CardViewZakupy(
//                    "Hehe",
//                    true
//                )
//                list.add(index, newItem)
//                adapter.notifyItemInserted(index)
        }
    }


//    private fun generateDummyList(size: Int): ArrayList<CardViewZakupy>{
//        val list = ArrayList<CardViewZakupy>()
//
//        for(i in 0 until size) {
//            val item = CardViewZakupy(
//                text1 = "hehe",
//                checkbox = true
//            )
//            list += item
//        }
////                count++
//        return list
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}