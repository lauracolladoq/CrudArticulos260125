package com.example.crudarticulos260125.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.crudarticulos260125.databinding.ArticuloLayoutBinding
import com.example.crudarticulos260125.models.Articulo

class ArticuloViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    private val binding = ArticuloLayoutBinding.bind(v)
    fun render(item: Articulo, onBorrar: (Articulo) -> Unit, onEdit: (Articulo) -> Unit) {
        binding.tvNombre.text = item.nombre
        binding.tvDescripcion.text = item.descripcion
        binding.tvPrecio.text = item.precio.toString()

        binding.btnBorrar.setOnClickListener {
            onBorrar(item)
        }
        binding.btnEditar.setOnClickListener {
            onEdit(item)
        }
    }

}