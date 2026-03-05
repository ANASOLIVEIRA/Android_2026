package com.example.atv3_log_debug

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.util.Log
import android.view.View

class MainActivity2 : AppCompatActivity() {

    private companion object {
        private const val TAG = "ATV3"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        Log.d(TAG, "MainActivity2 onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "MainActivity2 onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity2 onDestroy")
    }

    fun voltar(view: View) {
        Log.w(TAG, "MainActivity2 voltar -> retornando Tela 1")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}