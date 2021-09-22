package com.borshevskiy.sqlitetest

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.borshevskiy.sqlitetest.databinding.ActivityEditBinding
import com.borshevskiy.sqlitetest.db.IntentConstants
import com.borshevskiy.sqlitetest.db.MyDbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {

    val dbManager = MyDbManager(this)
    var id = 0
    var isEditState = false
    val image_request_code = 10
    var tempImageUri = "empty"

    lateinit var binding: ActivityEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getMyIntents()
    }

    fun onClickAddImage(view: View) {
        binding.ImageLayout.visibility = View.VISIBLE
        binding.fbAddPicture.visibility = View.GONE
    }

    fun onClickDeleteImage(view: View) {
        binding.ImageLayout.visibility = View.GONE
        binding.fbAddPicture.visibility = View.VISIBLE
        tempImageUri = "empty"
    }

    fun onClickChooseImage(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, image_request_code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == image_request_code) {
            binding.imageView.setImageURI(data?.data)
            tempImageUri = data?.data.toString()
            contentResolver.takePersistableUriPermission(data?.data!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun onClickSave(view: View) {
        val myTitle = binding.editTitle.text.toString()
        val myContent = binding.editContent.text.toString()

        if (myTitle != "" && myContent != "") {

            CoroutineScope(Dispatchers.Main).launch {
                if (isEditState) {
                    dbManager.updateDb(myTitle,myContent,tempImageUri,id, getCurrentTime())
                } else {
                    dbManager.insertToDb(myTitle,myContent,tempImageUri, getCurrentTime())
                }
            }
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        dbManager.openDb()

    }

    override fun onDestroy() {
        super.onDestroy()
        dbManager.closeDb()
    }

    fun getMyIntents() {
        binding.fbEditState.visibility = View.GONE
        val i = intent
        if (i != null) {
            if (i.getStringExtra(IntentConstants.I_TITLE_KEY) != null) {
                isEditState = true
                binding.editTitle.isEnabled = false
                binding.editContent.isEnabled = false
                binding.fbAddPicture.visibility = View.GONE
                binding.fbEditState.visibility = View.VISIBLE
                binding.editTitle.setText(i.getStringExtra(IntentConstants.I_TITLE_KEY))
                binding.editContent.setText(i.getStringExtra(IntentConstants.I_DESC_KEY))

                id = i.getIntExtra(IntentConstants.I_ID_KEY,0)

                if(i.getStringExtra(IntentConstants.I_URI_KEY) != "empty") {
                    tempImageUri = i.getStringExtra(IntentConstants.I_URI_KEY)!!
                    binding.imageView.setImageURI(Uri.parse(tempImageUri))
                    binding.ImageLayout.visibility = View.VISIBLE
                    binding.editImage.visibility = View.GONE
                    binding.deleteImage.visibility = View.GONE
                }
            }
        }
    }

    fun onEditEnable(view: android.view.View) {
        binding.fbEditState.visibility = View.GONE
        binding.editTitle.isEnabled = true
        binding.editContent.isEnabled = true
        binding.fbAddPicture.visibility = View.VISIBLE
        if (tempImageUri == "empty") return
        binding.editImage.visibility = View.VISIBLE
        binding.deleteImage.visibility = View.VISIBLE
    }

    private fun getCurrentTime(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy kk:mm", Locale.getDefault())
        return formatter.format(time)
    }
}