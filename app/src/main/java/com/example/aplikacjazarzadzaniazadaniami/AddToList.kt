package com.example.aplikacjazarzadzaniazadaniami

import android.Manifest
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
import android.app.Activity.RESULT_OK
import android.content.ContentUris
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*
import java.lang.Boolean.*
import java.util.*
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.provider.MediaStore
import android.provider.MediaStore.Images

class AddToList : Fragment() {

    private var _binding: ListAddBinding? = null

    private val binding get() = _binding!!

    private val pickImage = 100
    private val camera = 101
    private var imageUri: Uri? = null
    private var filePath = ""

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

        binding.imageView.setOnClickListener {
            (activity as MainActivity).checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, pickImage)
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            startActivityForResult(intent, pickImage)
        }

        binding.camera.setOnClickListener{
            (activity as MainActivity).checkPermission(Manifest.permission.CAMERA, camera)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, camera)
        }

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

                zadanie.imgpath = filePath

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            binding.img.setImageURI(imageUri)
            filePath = imageUri?.let { this.context?.let { it1 -> getPath(it1, it) } }.toString()
            Log.d("Hehe", filePath)
        }else if(resultCode == RESULT_OK && requestCode == camera){
            var imageBitmap = data?.extras?.get("data") as Bitmap
            binding.img.setImageBitmap(imageBitmap)
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
        val path = Images.Media.insertImage(inContext.contentResolver, inImage, System.currentTimeMillis().toString(), null)
        return Uri.parse(path)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}