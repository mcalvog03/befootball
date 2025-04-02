package com.example.befootball

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.befootball.databinding.ItemPartidosBinding

class PartidosListAdapter : ListAdapter<Partidos, RecyclerView.ViewHolder>(PartidoDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_partidos, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val partido = getItem(position)
        (holder as ViewHolder).run {
            with(binding) {
                partidos.text = partido.pkPartido.toString()
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
}