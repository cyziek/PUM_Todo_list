package com.example.aplikacjazarzadzaniazadaniami

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.aplikacjazarzadzaniazadaniami.databinding.TaskViewerBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.text.SimpleDateFormat
import android.graphics.BitmapFactory
import android.util.Log
import android.view.*
import androidx.navigation.fragment.findNavController
import java.util.*

class TaskViewer : Fragment() {

    private var _binding: TaskViewerBinding? = null

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
        _binding = TaskViewerBinding.inflate(inflater, container, false)


        val path = context?.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        val format = SimpleDateFormat("dd/MM/yyy HH:mm")
        val pozycja = TodoList.getPozycja()
        setPozycja(pozycja)
        if(File(letDirectory,"Records.json").exists()) {
            val jsonArray: MutableList<Zadania> = Gson().fromJson(
                FileReader(File(letDirectory, "Records.json")),
                object : TypeToken<MutableList<Zadania>>() {}.type
            )
            binding.test.text = jsonArray[pozycja!!].title
            binding.termin.text = "Termin: " +  format.format(jsonArray[pozycja].date)
            binding.desc.text = jsonArray[pozycja!!].desc
            binding.switch1.isChecked = jsonArray[pozycja!!].notif == true
            binding.switch1.isEnabled = false

            when(jsonArray[pozycja!!].prior){
                "Najniższy" -> binding.radioAdd1.isChecked = true
                "Niski" -> binding.radioAdd2.isChecked = true
                "Średni" -> binding.radioAdd3.isChecked = true
                "Wysoki" -> binding.radioAdd4.isChecked = true
                "Najwyższy" -> binding.radioAdd5.isChecked = true
            }

            binding.radioAdd1.isEnabled = false
            binding.radioAdd2.isEnabled = false
            binding.radioAdd3.isEnabled = false
            binding.radioAdd4.isEnabled = false
            binding.radioAdd5.isEnabled = false

            if(jsonArray[pozycja].imgpath != ""){
                if(File(jsonArray[pozycja].imgpath.toString()).exists()) {
                    Log.d("hehe", jsonArray[pozycja].imgpath.toString())
                    val myBitmap = BitmapFactory.decodeFile(jsonArray[pozycja].imgpath)
                    binding.imgView.setImageBitmap(myBitmap)
                    binding.imgView.setBackgroundResource(R.drawable.img)
                }else{
                    binding.imgView.setBackgroundResource(R.drawable.img_1)
                }
            }
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.edit).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId){
        R.id.edit -> {
            findNavController().navigate(R.id.action_task_viewer_to_todoEdit)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}