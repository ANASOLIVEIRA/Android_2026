package com.example.atv3_log_debug

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.util.Log
import android.view.View

class MainActivity : AppCompatActivity() {

    private companion object {
        private const val TAG = "ATV3"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "MainActivity onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "MainActivity onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity onDestroy")
    }

    fun IrActivity2(view: View) {
        Log.i(TAG, "MainActivity IrActivity2 -> abrindo Tela 2")
        startActivity(Intent(this, MainActivity2::class.java))
    }
}