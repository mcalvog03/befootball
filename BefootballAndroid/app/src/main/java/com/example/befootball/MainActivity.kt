package com.example.befootball

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.befootball.databinding.ActivityMainBinding
import com.example.befootball.model.Ligas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: PartidosListAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var partidosService: PartidosService
    private lateinit var ligasService: LigasService
    private var ligas: List<Ligas> = listOf()
    private var jornadas: List<Int> = listOf()

    private var pkLigaSeleccionada: Int? = null
    private var jornadaSeleccionada: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapter()
        setupRecyclerView()
        setupRetrofit()
        getLigas()
    }

    private fun setupAdapter() {
        adapter = PartidosListAdapter()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        partidosService = retrofit.create(PartidosService::class.java)
        ligasService = retrofit.create(LigasService::class.java)
    }

    private fun getLigas() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                ligas = ligasService.getLigas()
                val nombresLigas = ligas.map { it.nombreLiga }

                withContext(Dispatchers.Main) {
                    if (nombresLigas.isNotEmpty()) {
                        val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, nombresLigas)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerLigas.adapter = adapter
                        setupLigaSpinnerListener()
                    } else {
                        Toast.makeText(this@MainActivity, "No se encontraron ligas", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error al cargar ligas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupLigaSpinnerListener() {
        binding.spinnerLigas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val ligaSeleccionada = ligas[position]
                pkLigaSeleccionada = ligaSeleccionada.pkLiga
                getJornadas(ligaSeleccionada.pkLiga)
                getPartidos(pkLigaSeleccionada, jornadaSeleccionada) // Verifica que jornadaSeleccionada no sea null
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupJornadasSpinnerListener() {
        binding.spinnerJornadas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                jornadaSeleccionada = jornadas[position] // Obtenemos el número de la jornada seleccionada
                pkLigaSeleccionada?.let {
                    getPartidos(it, jornadaSeleccionada) // Ahora pasamos ambos, el ligaId y jornadaId
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }



    private fun getJornadas(ligaId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val nuevasJornadas = ligasService.getJornadasByLiga(ligaId) // Llamada correcta a la API para obtener las jornadas
                withContext(Dispatchers.Main) {
                    if (nuevasJornadas.isNotEmpty()) {
                        jornadas = nuevasJornadas
                        val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, jornadas)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerJornadas.adapter = adapter
                        setupJornadasSpinnerListener()
                    } else {
                        Toast.makeText(this@MainActivity, "No se encontraron jornadas", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error al cargar jornadas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun getPartidos(ligaId: Int?, jornadaId: Int?) {
        // Si tenemos un número de jornada, obtenemos los partidos de esa jornada
        if (jornadaId != null) {
            // Llamada para obtener los partidos por jornada
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val partidos = partidosService.getPartidosByJornada(jornadaId) // Solo pasamos jornadaId
                    withContext(Dispatchers.Main) {
                        if (partidos.isNotEmpty()) {
                            adapter.submitList(partidos) // Actualiza el RecyclerView
                        } else {
                            Toast.makeText(this@MainActivity, "No se encontraron partidos para la jornada", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Error al cargar partidos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else if (ligaId != null) {
            // Si no hay jornada seleccionada, obtenemos los partidos por liga
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val partidos = partidosService.getPartidosByLiga(ligaId)
                    withContext(Dispatchers.Main) {
                        if (partidos.isNotEmpty()) {
                            adapter.submitList(partidos) // Actualiza el RecyclerView
                        } else {
                            Toast.makeText(this@MainActivity, "No se encontraron partidos para la liga", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Error al cargar partidos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        pkLigaSeleccionada?.let {
            getPartidos(it, jornadaSeleccionada)
        }
    }
}
