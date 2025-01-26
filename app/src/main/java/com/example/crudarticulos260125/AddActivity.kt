package com.example.crudarticulos260125

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crudarticulos260125.databinding.ActivityAddBinding
import com.example.crudarticulos260125.models.Articulo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding

    private var nombre = ""
    private var descripcion = ""
    private var precio = 0F

    private var editando = false
    private var articulo = Articulo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setListeners()
        getDatos()
        setDatos()

        // Variable editar
        Log.d("EDITAR", "editando: $editando")
    }

    //----------------------------------------------------------------------------------------------
    private fun getDatos() {
        val datos = intent.extras
        if (datos != null) {
            articulo = datos.getSerializable("ITEM") as Articulo
            editando = true
        }
    }

    //----------------------------------------------------------------------------------------------
    private fun setDatos() {
        if (editando) {
            binding.etNombre.setText(articulo.nombre)
            binding.etDescripcion.setText(articulo.descripcion)
            binding.etPrecio.setText(articulo.precio.toString())
            binding.btnAdd.text = "EDITAR"
            binding.tvTitulo.text = "Editar artículo"
            // Nombre no editable
            binding.etNombre.isEnabled = false
        } else {
            binding.btnAdd.text = "AÑADIR"
            binding.tvTitulo.text = "Añadir artículo"
        }
    }

    //----------------------------------------------------------------------------------------------
    private fun setListeners() {
        binding.btnCancelar.setOnClickListener {
            finish()
        }
        binding.btnAdd.setOnClickListener {
            addItem()
        }
    }

    //----------------------------------------------------------------------------------------------
    private fun addItem() {
        if (!datosOk()) return

        val tiendaRef = FirebaseDatabase.getInstance().getReference("tienda")
        val item = Articulo(nombre, descripcion, precio)

        if (!editando) {
            // Verificar si el nombre ya existe
            tiendaRef.orderByChild("nombre").equalTo(item.nombre)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.childrenCount > 0) {
                            Toast.makeText(
                                this@AddActivity,
                                "Ya existe un artículo con ese nombre",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Guardar el artículo  si no existe el nombre
                            tiendaRef.push().setValue(item).addOnSuccessListener {
                                Toast.makeText(
                                    this@AddActivity,
                                    "Artículo guardado con éxito",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }.addOnFailureListener {
                                Toast.makeText(
                                    this@AddActivity,
                                    "Error al guardar el artículo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        } else {
            // Si estamos editando, buscamos el artículo por nombre y lo actualizamos
            tiendaRef.get().addOnSuccessListener { dataSnapshot ->
                for (nodo in dataSnapshot.children) {
                    val nombreExistente = nodo.child("nombre").getValue(String::class.java)
                    if (nombreExistente == item.nombre) {
                        // Actualizar el artículo
                        tiendaRef.child(nodo.key.toString()).setValue(item)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@AddActivity,
                                    "Artículo actualizado con éxito",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this@AddActivity,
                                    "Error al actualizar el artículo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(
                    this@AddActivity,
                    "Error al guardar el artículo",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    //----------------------------------------------------------------------------------------------
    private fun datosOk(): Boolean {
        // Comprobar que el nombre sea único también
        nombre = binding.etNombre.text.toString().trim()
        if (nombre.length < 3) {
            binding.etNombre.error = "Error, el campo nombre debe tener 3 caracteres"
            return false
        }
        descripcion = binding.etDescripcion.text.toString().trim()
        if (descripcion.length < 10) {
            binding.etDescripcion.error =
                "Error, el campo descripción debe tener al menos 10 caracteres"
            return false
        }

        precio = binding.etPrecio.text.toString().toFloat()
        if (precio <= 0 || precio > 10000) {
            binding.etPrecio.error = "Error, el precio debe estar entre 0 y 10000"
            return false
        }
        return true
    }
}