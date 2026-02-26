package com.example.atv2_consultacep

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var etCep: EditText
    private lateinit var tvResultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCep = findViewById(R.id.etCep)
        tvResultado = findViewById(R.id.tvResultado)
    }

    fun Executar(view: View) {
        Log.d("CLIQUE", "Botão clicado!")

        val cepDigitado = etCep.text.toString().trim()

        // Validação: CEP deve ter 8 dígitos numéricos
        if (cepDigitado.length != 8 || !cepDigitado.all { it.isDigit() }) {
            tvResultado.text = "CEP inválido. Digite 8 números."
            return
        }

        tvResultado.text = "Consultando..."

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://brasilapi.com.br/api/cep/v2/$cepDigitado")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("HTTP", "Erro na requisição", e)
                runOnUiThread { tvResultado.text = "Erro na requisição" }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    runOnUiThread { tvResultado.text = "CEP não encontrado (HTTP ${response.code})" }
                    return
                }

                val resposta = response.body?.string() ?: return
                Log.d("HTTP", "Resposta: $resposta")

                val json = JSONObject(resposta)

                val cep = json.optString("cep")
                val state = json.optString("state")
                val city = json.optString("city")
                val neighborhood = json.optString("neighborhood")
                val street = json.optString("street")

                val location = json.optJSONObject("location")
                val coordinates = location?.optJSONObject("coordinates")
                val latitude = coordinates?.optString("latitude") ?: "N/A"
                val longitude = coordinates?.optString("longitude") ?: "N/A"

                Log.d("JSON", "CEP: $cep")
                Log.d("JSON", "Estado: $state")
                Log.d("JSON", "Cidade: $city")
                Log.d("JSON", "Bairro: $neighborhood")
                Log.d("JSON", "Rua: $street")
                Log.d("JSON", "Lat: $latitude | Long: $longitude")

                val texto =
                    "CEP: $cep\n" +
                            "Estado: $state\n" +
                            "Cidade: $city\n" +
                            "Bairro: $neighborhood\n" +
                            "Rua: $street\n" +
                            "Lat: $latitude\n" +
                            "Long: $longitude"

                runOnUiThread {
                    tvResultado.text = texto
                }
            }
        })
    }
}