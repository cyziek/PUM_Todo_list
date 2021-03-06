package com.example.aplikacjazarzadzaniazadaniami

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikacjazarzadzaniazadaniami.databinding.ZakupyBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader

class Zakupy : Fragment(), AdapterZakupy.OnItemClickListener, AdapterZakupy.OnItemLongClickListener{

    private var _binding: ZakupyBinding? = null
    private val binding get() = _binding!!

    private val list = generateDummyList(10)
    private val adapter = AdapterZakupy(list, this, this)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = ZakupyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.clear).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId){
        R.id.clear -> {
            clearAll()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val path = context?.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        if(File(letDirectory,"Zakupy.json").exists()) {
            val jsonArray : MutableList<ZakupyClass> = Gson().fromJson(FileReader(File(letDirectory,"Zakupy.json")), object : TypeToken<MutableList<ZakupyClass>>(){}.type)

            val list = generateDummyList(jsonArray.size)
//            Log.d("hehe", "hehe: ")
            binding.rec1.adapter = AdapterZakupy(list, this, this)
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

                    if(File(letDirectory,"Zakupy.json").exists()) {
                        val jsonArray : MutableList<ZakupyClass> = Gson().fromJson(FileReader(File(letDirectory,"Zakupy.json")), object : TypeToken<MutableList<ZakupyClass>>(){}.type)


                        val list = generateDummyList(jsonArray.size)
                        binding.rec1.adapter = AdapterZakupy(list, this, this)
                        binding.rec1.layoutManager = LinearLayoutManager(this.context)
                        binding.rec1.setHasFixedSize(true)
                    }
                    Toast.makeText(context, "Dodano przedmiot!", Toast.LENGTH_SHORT).show()
                    builder.dismiss()
                }else{
                    Toast.makeText(context, "Nie podano nazwy przedmiotu!", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setCanceledOnTouchOutside(false)
            builder.show()
        }
    }

    override fun onItemClick(position: Int) {
        val path = context?.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        val file = File(letDirectory, "Zakupy.json")
        if(File(letDirectory,"Zakupy.json").exists()) {
            val jsonArray: MutableList<ZakupyClass> = Gson().fromJson(
                FileReader(File(letDirectory, "Zakupy.json")),
                object : TypeToken<MutableList<ZakupyClass>>() {}.type)

            when (jsonArray[position].itemPurchase) {
                true -> jsonArray[position].itemPurchase = false
                false -> jsonArray[position].itemPurchase = true
            }
            val jsonString = Gson().toJson(jsonArray)
            file.writeText(jsonString)
            Log.d("Hehe", jsonArray[position].itemPurchase.toString())
        }
        adapter.notifyItemChanged(position)
        sortArray(position)
    }

    override fun onItemLongClick(position: Int){
        val builder = AlertDialog.Builder(this.context,R.style.AlertDialogCustom)
            .create()
        val view = layoutInflater.inflate(R.layout.delete_dialog,null)
        val button = view.findViewById<Button>(R.id.dialogDelete_dismiss)
        val buttonacc = view.findViewById<Button>(R.id.dialogDelete_accept)
        builder.setView(view)

        button.setOnClickListener {
            builder.dismiss()
        }
        buttonacc.setOnClickListener{
            val path = context?.getExternalFilesDir(null)
            val letDirectory = File(path, "LET")
            val file = File(letDirectory, "Zakupy.json")
            if(File(letDirectory,"Zakupy.json").exists()) {
                val jsonArray: MutableList<ZakupyClass> = Gson().fromJson(
                    FileReader(File(letDirectory, "Zakupy.json")),
                    object : TypeToken<MutableList<ZakupyClass>>() {}.type
                )
                jsonArray.removeAt(position)
                val jsonString = Gson().toJson(jsonArray)
                file.writeText(jsonString)
            }
            findNavController().navigate(R.id.action_zakupy_lista_to_self)
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun sortArray(position: Int){
        val path = context?.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        val file = File(letDirectory, "Zakupy.json")
        if(File(letDirectory,"Zakupy.json").exists()) {
            val jsonArray: MutableList<ZakupyClass> = Gson().fromJson(
                FileReader(File(letDirectory, "Zakupy.json")),
                object : TypeToken<MutableList<ZakupyClass>>() {}.type
            )
            var count = 0
            var jsonArrayTemp: ZakupyClass
            for(i in jsonArray){
                while (count < jsonArray.size - 1) {
                    if (!jsonArray[count].itemPurchase && jsonArray[count + 1].itemPurchase) {
                        jsonArrayTemp= jsonArray[count]
                        jsonArray[count] = jsonArray[count + 1]
                        jsonArray[count + 1]= jsonArrayTemp
                    }
                    count++
                }
                count = 0
            }
            val jsonString = Gson().toJson(jsonArray)
            file.writeText(jsonString)
        }
        findNavController().navigate(R.id.action_zakupy_lista_to_self)
    }

    private fun generateDummyList(size: Int): List<CardViewZakupy>{
        val list = ArrayList<CardViewZakupy>()

        val path = context?.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        var count = 0
        if(File(letDirectory,"Zakupy.json").exists()) {
            val jsonArray : MutableList<ZakupyClass> = Gson().fromJson(FileReader(File(letDirectory,"Zakupy.json")), object : TypeToken<MutableList<ZakupyClass>>(){}.type)
            count = 0
            while(count < jsonArray.size){
                    val item = CardViewZakupy(
                        jsonArray[count].item.toString(),
                        jsonArray[count].itemPurchase
                    )
                Log.d("Hehe", jsonArray[count].itemPurchase.toString())
                list += item
                count++
            }
        }
        return list
    }

    private fun clearAll(){
        val path = context?.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        val file = File(letDirectory, "Zakupy.json")
        var count = 0
        if(File(letDirectory,"Zakupy.json").exists()) {
            val jsonArray: MutableList<ZakupyClass> = Gson().fromJson(
                FileReader(File(letDirectory, "Zakupy.json")),
                object : TypeToken<MutableList<ZakupyClass>>() {}.type)

            while(count < jsonArray.size){
                jsonArray[count].itemPurchase = false
                count++
            }

            val jsonString = Gson().toJson(jsonArray)
            file.writeText(jsonString)
        }
        findNavController().navigate(R.id.action_zakupy_lista_to_self)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}