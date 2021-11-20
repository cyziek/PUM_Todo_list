package com.example.aplikacjazarzadzaniazadaniami

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.aplikacjazarzadzaniazadaniami.databinding.ListTodoBinding
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.FileReader


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TodoList : Fragment() {

    private var _binding: ListTodoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
            binding.rec.adapter = Adapter(list)
            binding.rec.layoutManager = LinearLayoutManager(this.context)
            binding.rec.setHasFixedSize(true)
        }

//        val path = context?.getExternalFilesDir(null)
//        val letDirectory = File(path, "LET")
//        val file = File(letDirectory, "Records.txt")
//        if(file.length() != 0L) {
//            val inputStream: InputStream = File(letDirectory, "Records.txt").inputStream()
//            val lineList = mutableListOf<String>()
//            var chuj = 200
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
//                chuj += 200
//                count++
//                binding.todoId?.addView(textView)
//            }
//        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

    }

    private fun generateDummyList(size: Int): List<CardView>{
        val list = ArrayList<CardView>()

        val path = context?.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        var count = 0
        if(File(letDirectory,"Records.json").exists()) {
            val jsonArray : MutableList<Zadania> = Gson().fromJson(FileReader(File(letDirectory,"Records.json")), object : TypeToken<MutableList<Zadania>>(){}.type)
            while(count < jsonArray.size){
                val drawable = R.drawable.ic_baseline_delete_24
                val item = CardView(
                    jsonArray[count].title.toString(),
                    jsonArray[count].desc.toString(),
                    drawable,
                    "Termin: " + jsonArray[count].date,
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