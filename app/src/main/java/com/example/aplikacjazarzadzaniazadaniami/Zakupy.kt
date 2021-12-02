package com.example.aplikacjazarzadzaniazadaniami

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikacjazarzadzaniazadaniami.databinding.ZakupyBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader


class Zakupy : Fragment() {

    private var _binding: ZakupyBinding? = null
    private val binding get() = _binding!!

    private val list = generateDummyList(10)
    private val adapter = AdapterZakupy(list)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = ZakupyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val path = context?.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        if(File(letDirectory,"Zakupy.json").exists()) {
            val jsonArray : MutableList<ZakupyClass> = Gson().fromJson(FileReader(File(letDirectory,"Zakupy.json")), object : TypeToken<MutableList<ZakupyClass>>(){}.type)


            val list = generateDummyList(jsonArray.size)
//            Log.d("hehe", "hehe: ")
            binding.rec1.adapter = AdapterZakupy(list)
            binding.rec1.layoutManager = LinearLayoutManager(this.context)
            binding.rec1.setHasFixedSize(true)
        }

        binding.fab1.setOnClickListener{

            val builder = AlertDialog.Builder(this.context,R.style.AlertDialogCustom)
                .create()
            val view = layoutInflater.inflate(R.layout.custom_dialog,null)
            val przedmiot: EditText = view.findViewById(R.id.itemname)
            val button = view.findViewById<Button>(R.id.dialogDismiss_button)
            val buttonacc = view.findViewById<Button>(R.id.dialogAccept_button)
            builder.setView(view)
            button.setOnClickListener {
                builder.dismiss()
            }
            buttonacc.setOnClickListener{
                if(przedmiot.text.toString().trim() != "") {
                    val path = context?.getExternalFilesDir(null)

                    val letDirectory = File(path, "LET")
                    letDirectory.mkdirs()
                    val file = File(letDirectory, "Zakupy.json")

                    val zakup = ZakupyClass()

                    zakup.id = 1

                    zakup.item = przedmiot.text.toString()
//                    Log.d("Hehe", przedmiot.text.toString())
                    zakup.itemPurchase = true

                    if(!file.exists()) {
                        val jsonArray: MutableList<ZakupyClass> = mutableListOf()
                        jsonArray.add(zakup)
                        val jsonString = Gson().toJson(jsonArray)
                        file.appendText(jsonString)
                    }else{
                        val gson = Gson()
                        val jsonArray : MutableList<ZakupyClass> = gson.fromJson(FileReader(file), object : TypeToken<MutableList<ZakupyClass>>(){}.type)
                        jsonArray.add(zakup)
                        val jsonString = Gson().toJson(jsonArray)
                        file.writeText(jsonString)
                    }
                }else{
                    Toast.makeText(context, "Nie podano nazwy przedmiotu!", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setCanceledOnTouchOutside(false)
            builder.show()
        }
    }


    private fun generateDummyList(size: Int): List<CardViewZakupy>{
        val list = ArrayList<CardViewZakupy>()

        val path = context?.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        var count = 0
        if(File(letDirectory,"Zakupy.json").exists()) {
            val jsonArray : MutableList<ZakupyClass> = Gson().fromJson(FileReader(File(letDirectory,"Zakupy.json")), object : TypeToken<MutableList<ZakupyClass>>(){}.type)
            while(count < jsonArray.size){
                val item = CardViewZakupy(
                    jsonArray[count].item.toString(),
                    true
                )
                list += item
                count++
            }
        }
        return list
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}