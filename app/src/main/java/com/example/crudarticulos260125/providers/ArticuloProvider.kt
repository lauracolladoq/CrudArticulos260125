package com.example.crudarticulos260125.providers

import com.example.crudarticulos260125.models.Articulo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ArticuloProvider {
    private val database = FirebaseDatabase.getInstance().getReference("tienda")
    fun getDatos(datosArticulo: (MutableList<Articulo>) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listado = mutableListOf<Articulo>() //mutable list vacia
                for (item in snapshot.children) {
                    val valor = item.getValue(Articulo::class.java)
                    if (valor != null) {
                        listado.add(valor)
                    }
                }
                listado.sortBy { it.nombre }
                datosArticulo(listado)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al leer realtime: ${error.message}")
            }

        })
    }
}