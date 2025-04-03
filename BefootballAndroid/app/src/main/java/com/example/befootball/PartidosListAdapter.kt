package com.example.befootball

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.befootball.databinding.ItemPartidosBinding
import com.example.befootball.model.Partidos
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PartidosListAdapter : ListAdapter<Partidos, RecyclerView.ViewHolder>(PartidoDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_partidos, parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val partido = getItem(position)
        (holder as ViewHolder).run {
            with(binding) {
                liga.text = partido.liga.nombreLiga
                local.text = partido.equipoLocal.nombreEquipo
                golesLocal.text = partido.golesLocal.toString()
                visitante.text = partido.equipoVisitante.nombreEquipo
                golesVisitante.text = partido.golesVisitante.toString()
                estado.text = partido.estado
                jornada.text = "Jornada " + partido.jornada.toString()

                // Formatear la fecha
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                val fechaFormateada = LocalDateTime.parse(partido.fecha).format(formatter)
                fecha.text = fechaFormateada
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemPartidosBinding.bind(view)
    }

    private class PartidoDiff : DiffUtil.ItemCallback<Partidos>(){
        override fun areItemsTheSame(oldItem: Partidos, newItem: Partidos): Boolean {
            return oldItem.pkPartido == newItem.pkPartido
        }

        override fun areContentsTheSame(oldItem: Partidos, newItem: Partidos): Boolean {
            return oldItem == newItem
        }
    }

    // Mostrar jornadas en orden
    fun submitOrderedList(partidos: List<Partidos>) {
        val orderedPartidos = partidos.sortedBy { it.jornada }
        submitList(orderedPartidos)
    }
}