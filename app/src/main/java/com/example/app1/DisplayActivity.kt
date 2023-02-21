package com.example.app1

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class DisplayActivity : AppCompatActivity() {

    private var mFirstNameReceived: String? = null
    private var mLastNameReceived: String? = null

    //UI elements
    private var mTvloggedInText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        //Get text view
        mTvloggedInText = findViewById<View>(R.id.loggedInText) as TextView
        //Get the intent that created this activity.
        val bundle = intent.extras
        if(bundle!=null){
            mFirstNameReceived = bundle.getString("fName")
            mLastNameReceived = bundle.getString("lName")
        }
        mTvloggedInText!!.text=(mFirstNameReceived+" "+mLastNameReceived+" is logged in!")
    }
}