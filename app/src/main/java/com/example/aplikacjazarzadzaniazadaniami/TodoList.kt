package com.example.aplikacjazarzadzaniazadaniami

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.media.Image
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceControl
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
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
//            Log.d("hehe", "hehe: ")
            binding.rec.adapter = Adapter(list, this, this)
            binding.rec.layoutManager = LinearLayoutManager(this.context)
            binding.rec.setHasFixedSize(true)
        }

//        val path = context?.getExternalFilesDir(null)
//        val letDirectory = File(path, "LET")
//        val file = File(letDirectory, "Records.txt")
//        if(file.length() != 0L) {
//            val inputStream: InputStream = File(letDirectory, "Records.txt").inputStream()
//            val lineList = mutableListOf<String>()
//            var count = 1
//
//            inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it)} }
//            lineList.forEach{
//                val textView = TextView(this.context)
//                val params= ConstraintLayout.LayoutParams(MATCH_PARENT,150)
//                textView.id = R.id.TEXT_ID+count
//                params.setMargins(0,200,0,0)
//                textView.layoutParams = params
//                //textView.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_text_style))
//                textView.text = it
//
//                // Add TextView to LinearLayout
//                count++
//                binding.todoId?.addView(textView)
//            }
//        }

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
        val format = SimpleDateFormat("dd/MM/yyy")
        if(File(letDirectory,"Records.json").exists()) {
            val jsonArray : MutableList<Zadania> = Gson().fromJson(FileReader(File(letDirectory,"Records.json")), object : TypeToken<MutableList<Zadania>>(){}.type)
            while(count < jsonArray.size){
                val item = CardView(
                    jsonArray[count].title.toString(),
                    jsonArray[count].desc.toString(),
                    "Termin: " + format.format(jsonArray[count].date),
                    "Priorytet: " + jsonArray[count].prior.toString()
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