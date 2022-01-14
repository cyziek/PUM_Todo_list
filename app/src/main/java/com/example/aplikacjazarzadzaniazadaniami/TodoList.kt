package com.example.aplikacjazarzadzaniazadaniami

import android.app.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.aplikacjazarzadzaniazadaniami.databinding.ListTodoBinding
import java.io.File
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.FileReader
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class TodoList : Fragment(), Adapter.OnItemClickListener, Adapter.OnItemLongClickListener {

    private var _binding: ListTodoBinding? = null

    private val binding get() = _binding!!

    companion object {
        private var pozycja: Int ?= null

        fun setPozycja(pozycja: Int?) {
            this.pozycja = pozycja
        }

        fun getPozycja(): Int? {
            return pozycja
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = ListTodoBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val path = context?.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        if(File(letDirectory,"Records.json").exists()) {
            val jsonArray : MutableList<Zadania> = Gson().fromJson(FileReader(File(letDirectory,"Records.json")), object : TypeToken<MutableList<Zadania>>(){}.type)


            val list = generateDummyList(jsonArray.size)
            binding.rec.adapter = Adapter(list, this, this)
            binding.rec.layoutManager = LinearLayoutManager(this.context)
            binding.rec.setHasFixedSize(true)
        }

        binding.fab.setOnClickListener {

            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onItemClick(position: Int){
        setPozycja(position)
        findNavController().navigate(R.id.action_FirstFragment_to_task_viewer)
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
            val file = File(letDirectory, "Records.json")
            if(File(letDirectory,"Records.json").exists()) {
                val jsonArray: MutableList<Zadania> = Gson().fromJson(
                    FileReader(File(letDirectory, "Records.json")),
                    object : TypeToken<MutableList<Zadania>>() {}.type
                )
                jsonArray.removeAt(position)
                val jsonString = Gson().toJson(jsonArray)
                file.writeText(jsonString)
            }
            findNavController().navigate(R.id.action_FirstFragment_self)
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun generateDummyList(size: Int): List<CardView>{
        val list = ArrayList<CardView>()

        val path = context?.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        var count = 0
        val format = SimpleDateFormat("dd/MM/yyy HH:mm")
        if(File(letDirectory,"Records.json").exists()) {
            val jsonArray : MutableList<Zadania> = Gson().fromJson(FileReader(File(letDirectory,"Records.json")), object : TypeToken<MutableList<Zadania>>(){}.type)
            while(count < jsonArray.size){
                var split = jsonArray[count].desc.toString().split("\n")
                var output =
                    if(split[0].length > 30){
                        split[0].substring(0, 30) + "..."
                    }else{
                        split[0]
                    }
                val item = CardView(
                    jsonArray[count].title.toString(),
                    output,
                    "Termin: " + format.format(jsonArray[count].date),
                    jsonArray[count].prior.toString()
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