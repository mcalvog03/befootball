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

    private fun setupLigaSpinnerListener() {
        binding.spinnerLigas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val ligaSeleccionada = ligas[position]
                pkLigaSeleccionada = ligaSeleccionada.pkLiga
                jornadaSeleccionada = null
                getJornadas(ligaSeleccionada.pkLiga) // Traemos las jornadas para la liga seleccionada
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun getJornadas(ligaId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Obtenemos todos los partidos
                val partidos = partidosService.getAllPartidos()

                // Filtramos los partidos por la liga seleccionada y extraemos las jornadas
                val jornadasUnicas = partidos
                    .filter { it.liga.pkLiga == ligaId } // Filtramos por la liga seleccionada
                    .map { it.jornada } // Extraemos las jornadas
                    .distinct() // Eliminamos duplicados
                    .sorted() // Ordenamos las jornadas

                withContext(Dispatchers.Main) {
                    if (jornadasUnicas.isNotEmpty()) {
                        // Si hay jornadas, las cargamos en el Spinner
                        jornadas = jornadasUnicas
                        val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, jornadas)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spinnerJornadas.adapter = adapter
                        setupJornadasSpinnerListener() // Configuramos el listener del Spinner de Jornadas
                    } else {
                        // Si no hay jornadas disponibles
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
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Primero, aseguramos que tenemos una jornada seleccionada
                if (jornadaId != null) {
                    // Filtrar partidos por ligaId y jornadaId
                    val partidos = partidosService.getAllPartidos()

                    val partidosFiltrados = partidos
                        .filter { it.liga.pkLiga == ligaId && it.jornada == jornadaId } // Filtramos por liga y jornada
                        .sortedBy { it.fecha } // Ordenamos los partidos por fecha, si es necesario

                    withContext(Dispatchers.Main) {
                        if (partidosFiltrados.isNotEmpty()) {
                            // Si hay partidos, los mostramos en el RecyclerView
                            adapter.submitList(partidosFiltrados)
                        } else {
                            Toast.makeText(this@MainActivity, "No se encontraron partidos para esta jornada", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Si no hay jornada seleccionada, podemos hacer algo aquí, por ejemplo:
                    Toast.makeText(this@MainActivity, "Por favor, selecciona una jornada", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error al cargar los partidos", Toast.LENGTH_SHORT).show()
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
