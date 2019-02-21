package com.jordylangen.woodstorage.example

import android.content.Intent
import android.os.PersistableBundle
import android.os.Bundle
import android.view.View

import com.jordylangen.woodstorage.view.WoodStorageViewActivity

import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.button_open_overview)?.setOnClickListener {
            startActivity(Intent(this@MainActivity, WoodStorageViewActivity::class.java))
        }

        findViewById<View>(R.id.button_log_exception)?.setOnClickListener {
            Timber.e(Throwable("Something horrible happened!"),"oh noes, an exception!")
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        Timber.d("onSaveInstanceState")
    }

    override fun onPause() {
        super.onPause()
        Timber.d("onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
    }
}
