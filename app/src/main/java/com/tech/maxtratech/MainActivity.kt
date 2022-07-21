package com.tech.maxtratech

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.kbeanie.multipicker.api.Picker
import com.kbeanie.multipicker.api.VideoPicker
import com.kbeanie.multipicker.api.callbacks.VideoPickerCallback
import com.kbeanie.multipicker.api.entity.ChosenVideo
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.MimeType
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import com.tech.maxtratech.Util.NetworkCheck
import com.tech.maxtratech.databinding.ActivityMainBinding
import com.tech.umr.Network.ApiEndpoint
import com.tech.umr.Network.RetroInstance
import com.tech.umr.Repo.Repository
import java.io.File


class MainActivity : AppCompatActivity(), VideoPickerCallback {
    private lateinit var binding: ActivityMainBinding

    private lateinit var imageList: ArrayList<File>


    private lateinit var video: File
//    private lateinit var videothumbnail: File

    private lateinit var videoPicker: VideoPicker


    private lateinit var progressDialog: ProgressDialog


    private val retroInstance by lazy {
        RetroInstance().create(ApiEndpoint::class.java)
    }
    private val repository by lazy {
        Repository(retroInstance)
    }


    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModelFactory(repository)
        )[MainViewModel::class.java]
    }

    companion object {
        val userId = "100"
        val postType = "1"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        progressDialog = ProgressDialog(this)

        imageList = ArrayList()
        videoPicker = VideoPicker(this)
        video = File("")
        binding.result.text = "Response : \n"

//        videothumbnail =thumb

        binding.selectImage.setOnClickListener {
            FishBun.with(this)
                .setImageAdapter(GlideAdapter())
                .setMaxCount(4)
                .setActionBarColor(
                    Color.parseColor("#FF3700B3"),
                    Color.parseColor("#FF3700B3"),
                    false
                )
                .setAllViewTitle("All photos")
                .setActionBarTitle("Select Photos")
                .exceptMimeType(listOf(MimeType.GIF))
                .textOnNothingSelected("Please select at least one image!")
                .setPickerSpanCount(3)
                .startAlbum()
        }


        binding.selectVideo.setOnClickListener {
            videoPicker.setVideoPickerCallback(this)
            videoPicker.pickVideo()
        }



        binding.submit.setOnClickListener {
            binding.tvName.error = ""
            binding.tvDescription.error = ""
            if (!NetworkCheck.isOnline(this)) {
                val snak =
                    Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_LONG)
                snak.setAction("ok") { snak.dismiss() }
                snak.show()
                return@setOnClickListener
            }

            if (binding.tvName.editText?.text.toString() == "") {
                binding.tvName.error = "Please enter name"
                return@setOnClickListener
            }
            if (binding.tvDescription.editText?.text.toString() == "") {
                binding.tvDescription.error = "Please enter description"
                return@setOnClickListener
            }
            if (imageList.isEmpty()) {
                Toast.makeText(
                    this, "Please select at least one image",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            if (video.path.toString() == "") {
                Toast.makeText(
                    this, "Please select video",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            viewModel.setData(
                binding.tvName.editText?.text.toString(),
                userId,
                postType,
                binding.tvDescription.editText?.text.toString(),
                imageList,
                video,
                /*videothumbnail*/
            )


        }


        viewModel.openData.observe(this) {
            if (it.status == "1") {
                Snackbar.make(binding.root, "Successfully Uploaded", Snackbar.LENGTH_LONG).show()

                binding.result.text =
                    "Response : \n ${it.data.name}\n${it.data.discription}\n${it.data.images}\n${it.data.videos}\n${it.data.created_at}\n"

                imageList.clear()
                video = File("")
                binding.tvName.editText?.setText("")
                binding.tvDescription.editText?.setText("")
                binding.selectImage.text = "Select Image"
                binding.selectVideo.text = "Select Video"
            } else {
                Toast.makeText(this, Gson().toJson(it.message), Toast.LENGTH_LONG).show()
            }
        }

        viewModel.loading.observe(this) {
            if (it) {
                toggleProgressDialog(true)
            } else {
                toggleProgressDialog(false)
            }
        }

        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, Gson().toJson(it), Toast.LENGTH_LONG).show()
        }


    }


    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = managedQuery(uri, projection, null, null, null)
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {


            FishBun.FISHBUN_REQUEST_CODE -> if (resultCode == RESULT_OK) {
                // path = imageData.getStringArrayListExtra(Define.INTENT_PATH);
                // you can get an image path(ArrayList<String>) on <0.6.2
                val path: ArrayList<Uri> =
                    data?.getParcelableArrayListExtra<Parcelable>(FishBun.INTENT_PATH) as ArrayList<Uri>
                // you can get an image path(ArrayList<Uri>) on 0.6.2 and later

                for (i in path) {
                    imageList.add(File(getPath(i)))
                }
                binding.selectImage.text = "Change  ${imageList.size}"

            }
            Picker.PICK_VIDEO_DEVICE -> if (resultCode == RESULT_OK) {
                if (videoPicker == null) {
                    videoPicker = VideoPicker(this)
                    videoPicker.setVideoPickerCallback(this)
                }
                videoPicker.submit(data)
                binding.selectVideo.text = "Change"
            }

        }
    }


    private fun toggleProgressDialog(show: Boolean) {
        this.runOnUiThread {
            if (show) {
                progressDialog = ProgressDialog.show(this, "", "Uploading file...", true);
            } else {
                progressDialog.dismiss();
            }
        }
    }

    override fun onError(s: String?) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    override fun onVideosChosen(list: MutableList<ChosenVideo>?) {
        if (list != null) {
            if (list.size > 0) {
                Toast.makeText(this, list[0].originalPath, Toast.LENGTH_SHORT).show()
                video = File(list[0].originalPath)
            }
        }
    }

    private fun requests() {
        val write = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) // To check if you have permission call ContextCompat.checkSelfPermission
        val storage =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val listPermissionsNeeded: MutableList<String> = java.util.ArrayList()
        if (write != PackageManager.PERMISSION_GRANTED) {                  // if camera permission is not granted ask permission and add it to arraylist listPermissionsNeeded using add funct
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
            }
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) // if arraylist listPermissionsNeeded is not empty
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(listPermissionsNeeded.toTypedArray(), 100)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            showFileChooser("someType");
        }
    }

}