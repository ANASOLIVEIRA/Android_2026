package com.example.miniproject_sense

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miniproject_sense.db.AppDatabase
import com.example.miniproject_sense.ui.TelemetryAdapter
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var adapter: TelemetryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainHistory)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rv = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvHistory)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = TelemetryAdapter(emptyList())
        rv.adapter = adapter

        val db = AppDatabase.get(this)

        lifecycleScope.launch {
            val items = db.telemetryDao().last(200)
            adapter.update(items)
        }
    }
}