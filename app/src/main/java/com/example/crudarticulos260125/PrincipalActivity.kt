package com.example.crudarticulos260125

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crudarticulos260125.adapters.ArticuloAdapter
import com.example.crudarticulos260125.databinding.ActivityPrincipalBinding
import com.example.crudarticulos260125.models.Articulo
import com.example.crudarticulos260125.providers.ArticuloProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class PrincipalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrincipalBinding
    var adapter = ArticuloAdapter(
        mutableListOf<Articulo>(),
        { item -> borrarItem(item) },
        { item -> editarItem(item) })
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    val tiendaRef = FirebaseDatabase.getInstance().getReference("tienda")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().getReference("agenda")
        setRecycler()
        setListeners()
        setMenu()
    }

    //----------------------------------------------------------------------------------------------
    private fun setMenu() {
        binding.nv.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item_logout -> {
                    auth.signOut()
                    finish()
                    true
                }

                R.id.item_salir -> {
                    finishAffinity()
                    true
                }

                R.id.item_borrar -> {
                    borrarTodo()
                    true
                }

                else -> false
            }
            true
        }
    }

    //----------------------------------------------------------------------------------------------
    private fun borrarTodo() {
        tiendaRef.removeValue()
            .addOnSuccessListener {
                getDatos()
                Toast.makeText(this, "Todos los items borrados", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al borrar", Toast.LENGTH_SHORT).show()
            }
    }

    //----------------------------------------------------------------------------------------------
    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvArticulo.layoutManager = layoutManager

        binding.rvArticulo.adapter = adapter
        getDatos()
    }

    //----------------------------------------------------------------------------------------------
    private fun getDatos() {
        val articuloProvider = ArticuloProvider()
        articuloProvider.getDatos { todosLosRegistros ->
            adapter.lista = todosLosRegistros
            adapter.notifyDataSetChanged()
        }
    }

    private fun setListeners() {
        binding.floatingActionButton.setOnClickListener {
            irActivityAdd()
        }

    }

    private fun irActivityAdd(bundle: Bundle? = null) {
        val intent = Intent(this, AddActivity::class.java)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    private fun borrarItem(item: Articulo) {
        tiendaRef.get().addOnSuccessListener {
            for (nodo in it.children) {
                val nombre = nodo.child("nombre").getValue(String::class.java)
                if (nombre == item.nombre) {
                    nodo.ref.removeValue()
                        .addOnSuccessListener {
                            val position = adapter.lista.indexOf(item)
                            if (position != -1) {
                                Toast.makeText(this, "Item borrado", Toast.LENGTH_SHORT).show()
                                getDatos()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al borrar", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

    }

    private fun editarItem(item: Articulo) {
        val b = Bundle().apply {
            putSerializable("ITEM", item)
        }
        irActivityAdd(b)
    }

    override fun onResume() {
        super.onResume()
        getDatos()
    }
}