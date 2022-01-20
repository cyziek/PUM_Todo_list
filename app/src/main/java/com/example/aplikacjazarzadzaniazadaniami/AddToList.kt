package com.example.aplikacjazarzadzaniazadaniami

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import java.lang.Integer.parseInt

class AddToList : Fragment() {

    private var _binding: ListAddBinding? = null

    private val binding get() = _binding!!

    private val pickImage = 100
    private val camera = 101
    private var imageUri: Uri? = null
    private var filePath = ""

    private var notificationManager: NotificationManagerCompat ?= null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = ListAddBinding.inflate(inflater, container, false)

        notificationManager = this.context?.let { NotificationManagerCompat.from(it) }

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
            val timepicker = TimePickerDialog(this.context, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(year,month,dayOfMonth)
                binding.dateAdd.date = calendar.timeInMillis
            },
            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            timepicker.show()
        }

        binding.add.setOnClickListener{
            var pozycja = 1
            var id = 1
            var chk = FALSE
            val path = context?.getExternalFilesDir(null)
            val letDirectory = File(path, "LET")
            if(File(letDirectory,"Records.json").exists()) {
                val jsonArray: MutableList<Zadania> = Gson().fromJson(
                    FileReader(File(letDirectory, "Records.json")),
                    object : TypeToken<MutableList<Zadania>>() {}.type
                )
                pozycja = jsonArray.size+1
                id = (jsonArray[jsonArray.size-1].id + 1)
            }

            Log.d("Hehe", pozycja.toString())

            if(binding.descAdd.text.toString().trim().equals("") || binding.titleAdd.text.toString().trim().equals("")){
                Toast.makeText(context, "Brak danych!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Dodano zadanie!!", Toast.LENGTH_SHORT).show()

                val path = context?.getExternalFilesDir(null)

                val letDirectory = File(path, "LET")
                letDirectory.mkdirs()
                val file = File(letDirectory, "Records.json")

                val zadanie = Zadania()

                zadanie.id = id

                //switch
                if (binding.notificationAdd.isChecked) {
                    zadanie.notif = TRUE
                    chk = TRUE
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
                if(binding.notificationAdd.isChecked) {
                    createNotificationChannel(id)
                    Toast.makeText(this.context, "Powiadomienie ustawione!", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this.context, ReminderBroadcast::class.java)
                    intent.putExtra("VALUE_ID", id)
                    intent.putExtra("VALUE_TITLE", binding.titleAdd.text.toString())
                    intent.putExtra("VALUE_DESC", binding.descAdd.text.toString())
                    val pendingIntent: PendingIntent =
                        PendingIntent.getBroadcast(this.context, id, intent, PendingIntent.FLAG_IMMUTABLE)
                    val alarmManager: AlarmManager =
                        context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    var czas = binding.dateAdd.date
                    if(czas < System.currentTimeMillis()){
                        czas = System.currentTimeMillis()
                    }
                    val czas1 = 1000 * 0

                    when {
                        binding.radioAdd1.isChecked -> alarmManager.set(AlarmManager.RTC_WAKEUP, (czas + czas1), pendingIntent)
                        binding.radioAdd2.isChecked -> {
                            if ((czas + czas1) - System.currentTimeMillis() < 600000) {
                                alarmManager.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis()), pendingIntent)
                            } else {
                                alarmManager.set(AlarmManager.RTC_WAKEUP, ((czas + czas1) - 600000), pendingIntent)
                            }
                        }
                        binding.radioAdd3.isChecked -> {
                            if ((czas + czas1) - System.currentTimeMillis() < 1200000) {
                                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent)
                            } else {
                                alarmManager.set(AlarmManager.RTC_WAKEUP, ((czas + czas1) - 1200000), pendingIntent)
                            }
                        }
                        binding.radioAdd4.isChecked ->{
                            if ((czas + czas1) - System.currentTimeMillis() < 2400000) {
                                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent)
                            } else {
                                alarmManager.set(AlarmManager.RTC_WAKEUP, ((czas + czas1) - 2400000), pendingIntent)
                            }
                        }
                        binding.radioAdd5.isChecked ->{
                            if ((czas + czas1) - System.currentTimeMillis() < 3600000) {
                                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent)
                            } else {
                                alarmManager.set(AlarmManager.RTC_WAKEUP, ((czas + czas1) - 3600000), pendingIntent)
                            }
                        }
                    }
                }

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
            Log.d("URI", imageUri.toString())
            binding.img.setImageURI(imageUri)
            filePath = imageUri?.let { this.context?.let { it1 -> getPath(it1, it) } }.toString()
            Log.d("Hehe", Uri.fromFile(File(filePath)).toString())

        }else if(resultCode == RESULT_OK && requestCode == camera){
            var imageBitmap = data?.extras?.get("data") as Bitmap
            binding.img.setImageBitmap(imageBitmap)
            Log.d("BitMap", imageBitmap.toString())
            imageUri = this.context?.let { getImageUri(it, imageBitmap) }
            var path = this.context?.let { imageUri?.let { it1 -> getPath(it, it1) } }
            filePath = path.toString()
        }
    }

    fun getPath(context: Context, uri: Uri): String? {
        val isKitKatorAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        if (isKitKatorAbove && DocumentsContract.isDocumentUri(context, uri)) {
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

    fun createNotificationChannel(id: Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = "channel_$id"
            val channel1 = NotificationChannel(channel, "Channel $id", importance)
            channel1.description = "Channel $id"

            val notificationManager: NotificationManager? =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}