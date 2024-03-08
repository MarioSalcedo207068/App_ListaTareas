package salcedo.mario.listatareas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.room.Room

class MainActivity : AppCompatActivity() {
    lateinit var et_tarea: EditText
    lateinit var btn_agregar: Button
    lateinit var listview_tareas: ListView
    lateinit var lista_tareas: ArrayList<Tarea>
    lateinit var adaptor: ArrayAdapter<Tarea>
    lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        et_tarea = findViewById(R.id.et_tarea)
        btn_agregar = findViewById(R.id.btn_agregar)
        listview_tareas = findViewById(R.id.listview_tareas)

        lista_tareas = ArrayList()
        adaptor = ArrayAdapter(this,android.R.layout.simple_list_item_1, lista_tareas)
        listview_tareas.adapter =adaptor

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "tareas-db"
        ).allowMainThreadQueries().build()

        cargar_tareas()

        btn_agregar.setOnClickListener {
            var tarea_str = et_tarea.text.toString()

            if(!tarea_str.isNullOrEmpty()){
                var tarea =  Tarea(desc = tarea_str)
                db.userDao().agregarTarea(tarea)
                cargar_tareas()
                adaptor.notifyDataSetChanged()
                et_tarea.setText("")
            }else{
                Toast.makeText(this,"llenar campo", Toast.LENGTH_SHORT).show()
            }
        }

        listview_tareas.onItemClickListener= AdapterView.OnItemClickListener{
            parent,view, position, id ->
            val tareaSeleccionada = lista_tareas[position]
            mostrarDialogo(tareaSeleccionada)

        }

    }


    private fun mostrarDialogo(tarea: Tarea) {
        val opciones = arrayOf("Actualizar", "Eliminar")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccione una acción")
        builder.setItems(opciones) { dialog, which ->
            when (which) {
                0 -> {
                    // Actualiza
                    mostrarDialogoActualizar(tarea)
                }
                1 -> {
                    // Elimina
                    db.userDao().eliminarTarea(tarea)
                    cargar_tareas()
                    adaptor.notifyDataSetChanged()
                }
            }
        }
        builder.show()
    }

    private fun mostrarDialogoActualizar(tarea: Tarea) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Actualizar tarea")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Aceptar") { dialog, which ->
            val nuevaDescripcion = input.text.toString()
            if (nuevaDescripcion.isNotEmpty()) {
                tarea.desc = nuevaDescripcion
                db.userDao().actualizarTarea(tarea)
                cargar_tareas()
                adaptor.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun cargar_tareas(){
        var lista_db = db.userDao().obtenerTareas()
        lista_tareas.clear()
        for(tarea in lista_db){
            lista_tareas.add(tarea)
        }
    }

}