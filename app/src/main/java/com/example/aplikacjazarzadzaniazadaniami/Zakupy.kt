package com.example.aplikacjazarzadzaniazadaniami

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikacjazarzadzaniazadaniami.databinding.ZakupyBinding


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

            val builder = AlertDialog.Builder(this.context,R.style.AlertDialogCustom)
                .create()
            val view = layoutInflater.inflate(R.layout.custom_dialog,null)
            val  button = view.findViewById<Button>(R.id.dialogDismiss_button)
            builder.setView(view)
            button.setOnClickListener {
                builder.dismiss()
            }
            builder.setCanceledOnTouchOutside(false)
            builder.show()

//            alert.setTitle("Podaj przedmiot")
//            val input = EditText(this.context)
//            input.hint = "Przedmiot"
//            input.inputType = InputType.TYPE_CLASS_TEXT
//            alert.setView(input)
//
//            alert.setPositiveButton(
//                "Dodaj",
//                DialogInterface.OnClickListener { dialog, whichButton ->
//                    //Your action here
//                })
//
//            alert.setNegativeButton("Anuluj",
//                DialogInterface.OnClickListener { dialog, whichButton -> })
//
//            alert.show()
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
//                count++
//        return list
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}