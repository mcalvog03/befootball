package com.example.befootball

import android.media.Rating
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.befootball.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: PartidosListAdapter
    private lateinit var bindig: ActivityMainBinding
    private lateinit var service: PartidosService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindig = ActivityMainBinding.inflate(layoutInflater)
        //enableEdgeToEdge()
        setContentView(bindig.root)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        setupAdapter()
        setupRecycleView()
        setupRetrofit()
    }

    private fun setupAdapter() {
        adapter = PartidosListAdapter()
    }

    private fun setupRecycleView(){
        bindig.recyclerView.apply {
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupRetrofit(){
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(PartidosService::class.java)
    }

    private fun getPartidos() {
        lifecycleScope.launch(Dispatchers.IO) {
            val partidos = service.getReadsPartidos()
            withContext(Dispatchers.Main) {
                adapter.submitList(partidos)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getPartidos()
    }

}