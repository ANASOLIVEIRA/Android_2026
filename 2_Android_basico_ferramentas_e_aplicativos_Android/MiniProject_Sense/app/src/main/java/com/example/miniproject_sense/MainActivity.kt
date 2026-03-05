package com.example.miniproject_sense

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.miniproject_sense.db.AppDatabase
import com.example.miniproject_sense.db.TelemetryEntity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.math.abs

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var db: AppDatabase

    private lateinit var tvTemp: TextView
    private lateinit var tvHum: TextView
    private lateinit var tvMotion: TextView
    private lateinit var tvStatus: TextView

    private val client = OkHttpClient()

    private val handler = Handler(Looper.getMainLooper())
    private var running = false

    // Endpoint HTTP local (ajuste se seu servidor estiver em outra porta)
    private val urlTelemetry = "http://127.0.0.1:5000/telemetry"

    // SensorManager (movimento)
    private lateinit var sensorManager: SensorManager
    private var accel: Sensor? = null
    private var lastMotionValue: Double = 0.0

    private val pollRunnable = object : Runnable {
        override fun run() {
            if (!running) return
            fetchTelemetryHttp()
            handler.postDelayed(this, 2000) // 2 segundos
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = AppDatabase.get(this)

        tvTemp = findViewById(R.id.tvTemp)
        tvHum = findViewById(R.id.tvHum)
        tvMotion = findViewById(R.id.tvMotion)
        tvStatus = findViewById(R.id.tvStatus)

        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnStop = findViewById<Button>(R.id.btnStop)
        val btnHistory = findViewById<Button>(R.id.btnHistory)

        btnStart.setOnClickListener {
            running = true
            tvStatus.text = "Status: monitorando..."
            Log.d("APP", "Monitoramento iniciado")
            handler.post(pollRunnable)
        }

        btnStop.setOnClickListener {
            running = false
            tvStatus.text = "Status: parado"
            Log.d("APP", "Monitoramento parado")
        }

        btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accel == null) {
            tvMotion.text = "Movimento: indisponível"
            Log.w("SENSOR", "Acelerômetro não disponível")
        }
    }

    override fun onResume() {
        super.onResume()
        accel?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0].toDouble()
            val y = event.values[1].toDouble()
            val z = event.values[2].toDouble()
            lastMotionValue = abs(x) + abs(y) + abs(z)
            tvMotion.text = "Movimento (acc): $lastMotionValue"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // não usado
    }

    private fun fetchTelemetryHttp() {
        val request = Request.Builder().url(urlTelemetry).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("HTTP", "Falha HTTP", e)
                runOnUiThread { tvStatus.text = "Status: falha HTTP" }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    Log.e("HTTP", "HTTP ${response.code}")
                    runOnUiThread { tvStatus.text = "Status: HTTP ${response.code}" }
                    return
                }

                val body = response.body?.string() ?: return
                val json = JSONObject(body)

                val ts = json.optLong("ts")
                val temperature = json.optDouble("temperature")
                val humidity = json.optDouble("humidity")

                Log.d("JSON", "ts=$ts T=$temperature H=$humidity mov=$lastMotionValue")

                runOnUiThread {
                    tvTemp.text = "Temperatura: $temperature"
                    tvHum.text = "Umidade: $humidity"
                    tvStatus.text = "Status: OK"
                }

                lifecycleScope.launch {
                    db.telemetryDao().insert(
                        TelemetryEntity(
                            ts = ts,
                            temperature = temperature,
                            humidity = humidity,
                            motionValue = lastMotionValue
                        )
                    )
                }
            }
        })
    }
}