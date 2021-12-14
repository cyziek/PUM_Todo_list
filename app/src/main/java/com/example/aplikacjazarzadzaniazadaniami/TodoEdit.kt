package com.example.aplikacjazarzadzaniazadaniami

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.aplikacjazarzadzaniazadaniami.databinding.TodoEditBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileReader
import java.lang.Boolean.*
import java.util.*

class TodoEdit : Fragment() {

    private var _binding: TodoEditBinding? = null
    private val binding get() = _binding!!

    private val pickImage = 100
    private val camera = 101
    private var imageUri: Uri? = null
    private var filePath = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TodoEditBinding.inflate(inflater, container, false)
        val path = context?.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        val pozycja = TodoList.getPozycja()
        TaskViewer.setPozycja(pozycja)
        if(File(letDirectory,"Records.json").exists()) {
            val jsonArray: MutableList<Zadania> = Gson().fromJson(
                FileReader(File(letDirectory, "Records.json")),
                object : TypeToken<MutableList<Zadania>>() {}.type
            )
            binding.test.setText(jsonArray[pozycja!!].title)
            binding.desc.setText(jsonArray[pozycja!!].desc)
            var date = jsonArray[pozycja!!].date
            var millis = date?.time
            if (millis != null) {
                binding.calendarView.setDate(millis, true, true)
            }

            val calendar = Calendar.getInstance()

            binding.calendarView.setOnDateChangeListener{view, year, month, dayOfMonth ->
                val timepicker = TimePickerDialog(this.context, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(year,month,dayOfMonth)
                    binding.calendarView.date = calendar.timeInMillis
                    Toast.makeText(this.context, "Hehe", Toast.LENGTH_SHORT).show()
                },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)
                timepicker.show()
            }

            binding.switch1.isChecked = jsonArray[pozycja!!].notif == true

            when(jsonArray[pozycja].prior){
                "Najniższy" -> binding.radioAdd1.isChecked = true
                "Niski" -> binding.radioAdd2.isChecked = true
                "Średni" -> binding.radioAdd3.isChecked = true
                "Wysoki" -> binding.radioAdd4.isChecked = true
                "Najwyższy" -> binding.radioAdd5.isChecked = true
            }

            filePath = jsonArray[pozycja].imgpath.toString()

            if(jsonArray[pozycja].imgpath != ""){
                if(File(jsonArray[pozycja].imgpath.toString()).exists()) {
                    Log.d("hehe", jsonArray[pozycja].imgpath.toString())
                    val myBitmap = BitmapFactory.decodeFile(jsonArray[pozycja].imgpath)
                    binding.imgedit.setImageBitmap(myBitmap)
                    binding.imgedit.setBackgroundResource(R.drawable.img)
                }else{
                    binding.imgedit.setBackgroundResource(R.drawable.img_1)
                }
            }
        }

        binding.imageViewEdit.setOnClickListener {
            (activity as MainActivity).checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, pickImage)
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            startActivityForResult(intent, pickImage)
        }

        binding.cameraEdit.setOnClickListener{
            (activity as MainActivity).checkPermission(Manifest.permission.CAMERA, camera)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, camera)
        }

        binding.delimg.setOnClickListener{
            binding.imgedit.setBackgroundResource(R.drawable.img_1)
            binding.imgedit.setImageBitmap(null)
            filePath = ""
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.accbutton).isVisible = true
        menu.findItem(R.id.zadania).isVisible = false
        menu.findItem(R.id.zakupy).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId){
        R.id.accbutton -> {
            if(binding.desc.text.toString().trim().equals("") || binding.test.text.toString().trim().equals("")){
                Toast.makeText(context, "Brak danych!", Toast.LENGTH_SHORT).show()
            }else {
                editTask()
                Toast.makeText(context, "Dodano zadanie!!", Toast.LENGTH_SHORT).show()
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            binding.imgedit.setImageURI(imageUri)
            filePath = imageUri?.let { this.context?.let { it1 -> getPath(it1, it) } }.toString()
            Log.d("Hehe", filePath)
        }else if(resultCode == Activity.RESULT_OK && requestCode == camera){
            var imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imgedit.setImageBitmap(imageBitmap)
            imageUri = this.context?.let { getImageUri(it, imageBitmap) }
            var path = this.context?.let { imageUri?.let { it1 -> getPath(it, it1) } }
            filePath = path.toString()
        }
    }

    fun getPath(context: Context, uri: Uri): String? {
        val isKitKatorAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKatorAbove && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = uri?.let { context.getContentResolver().query(it, projection, selection, selectionArgs,null) }
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, System.currentTimeMillis().toString(), null)
        return Uri.parse(path)
    }

    private fun editTask(){
        val path = context?.getExternalFilesDir(null)
        val letDirectory = File(path, "LET")
        val pozycja = TodoList.getPozycja()
        TaskViewer.setPozycja(pozycja)
        val file = File(letDirectory, "Records.json")
        if(File(letDirectory,"Records.json").exists()) {
            val jsonArray: MutableList<Zadania> = Gson().fromJson(
                FileReader(File(letDirectory, "Records.json")),
                object : TypeToken<MutableList<Zadania>>() {}.type
            )
            jsonArray[pozycja!!].date = Date(binding.calendarView.date)
            jsonArray[pozycja].desc = binding.desc.text.toString()
            jsonArray[pozycja].imgpath = filePath

            if (binding.switch1.isChecked) {
                jsonArray[pozycja].notif = TRUE
            } else {
                jsonArray[pozycja].notif = FALSE
            }

            when {
                binding.radioAdd1.isChecked -> jsonArray[pozycja].prior = "Najniższy"
                binding.radioAdd2.isChecked -> jsonArray[pozycja].prior = "Niski"
                binding.radioAdd3.isChecked -> jsonArray[pozycja].prior = "Średni"
                binding.radioAdd4.isChecked -> jsonArray[pozycja].prior = "Wysoki"
                binding.radioAdd5.isChecked -> jsonArray[pozycja].prior = "Najwyższy"
            }

            jsonArray[pozycja].title = binding.test.text.toString()

            val jsonString = Gson().toJson(jsonArray)
            file.writeText(jsonString)
        }
        findNavController().navigate(R.id.action_todoEdit_to_FirstFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}