package com.example.app1

import android.content.ActivityNotFoundException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import android.os.Build
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log;

class MainActivity : AppCompatActivity(), View.OnClickListener {
    //UI elements we want to control
    private var mButtonSubmit: Button? = null
    private var mCameraButton: Button? = null
    private var mEtFirstName: EditText? = null
    private var mEtMiddleName: EditText? = null
    private var mEtLastName: EditText? = null
    private var picTaken: Boolean = false

    //Variables to hold the string
    private var mFirstName: String? = null
    private var mMiddleName: String? = null
    private var mLastName: String? = null

    //variable that holds filepath to profilePic
    private var filePathString: String? =null

    //imageView that holds pic
    private var mPicView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation=android.content.pm.ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        //Get the buttons
        mButtonSubmit = findViewById(R.id.submitButton)
        mButtonSubmit!!.setOnClickListener(this)

        mCameraButton = findViewById(R.id.photoButton)
        mCameraButton!!.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.submitButton -> {
                //First, get the string from the EditText
                mEtFirstName = findViewById(R.id.firstNameInput)
                mFirstName = mEtFirstName!!.text.toString()

                mEtMiddleName = findViewById(R.id.middleNameInput)
                mMiddleName = mEtMiddleName!!.text.toString()

                mEtLastName = findViewById(R.id.lastNameInput)
                mLastName = mEtLastName!!.text.toString()

                //Check if the EditText string is empty
                if (mFirstName.isNullOrBlank() || mLastName.isNullOrBlank()) {
                    //notify user of incorrect input
                    Toast.makeText(this@MainActivity, "Enter first and last name please", Toast.LENGTH_SHORT).show()
                }
                else if (!picTaken){
                    Toast.makeText(this@MainActivity, "Take profile pic first", Toast.LENGTH_SHORT).show()
                }
                else{
                    //Start an activity and pass the EditText string to it.
                    val messageIntent = Intent(this, DisplayActivity::class.java)
                    val nameBundle = Bundle()
                    nameBundle.putString("fName",mFirstName)
                    nameBundle.putString("mName",mMiddleName)
                    nameBundle.putString("lName",mLastName)
                    messageIntent.putExtras(nameBundle)
                    this.startActivity(messageIntent)
                }
            }
            R.id.photoButton -> {
                //The button press should open a camera
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try{
                    displayCameraThumbnail.launch(cameraIntent)
                }catch(ex: ActivityNotFoundException){
                    //Do error handling here
                }
            }
        }
    }

    private val displayCameraThumbnail = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            picTaken=true
            mPicView = findViewById<View>(R.id.profilePic) as ImageView
            var thumbnailImage: Bitmap? = null
            if (Build.VERSION.SDK_INT >= 33) {
                thumbnailImage = result.data!!.getParcelableExtra("data", Bitmap::class.java)
                mPicView!!.setImageBitmap(thumbnailImage)
            }
            else{
                thumbnailImage = result.data!!.getParcelableExtra<Bitmap>("data")
                mPicView!!.setImageBitmap(thumbnailImage)
            }
            //Open a file and write to it
            if (isExternalStorageWritable) {
                filePathString = savePic(thumbnailImage)
                Log.i("filePath", filePathString!!)
            } else {
                Toast.makeText(this, "External storage not writable.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    private fun savePic(finalBitmap: Bitmap?): String {
        val root = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val myDir = File("$root/saved_images")
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fname = "Thumbnail_$timeStamp.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //Save the view hierarchy
        super.onSaveInstanceState(outState)
        //save string of thumbnail
        outState.putString("filepath", filePathString)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        //Restore the view hierarchy automatically
        super.onRestoreInstanceState(savedInstanceState)
        //Restore stuff
        mPicView = findViewById<View>(R.id.profilePic) as ImageView
        val thumbnailImage = BitmapFactory.decodeFile(savedInstanceState.getString("filepath"))
        filePathString=savedInstanceState.getString("filepath")
        if (thumbnailImage != null) {
            picTaken=true
            mPicView!!.setImageBitmap(thumbnailImage)
        }
    }
}