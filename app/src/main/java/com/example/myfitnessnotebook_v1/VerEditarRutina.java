package com.example.myfitnessnotebook_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class VerEditarRutina extends AppCompatActivity {
    ListView listView;
    miBD gestorBD;
    String rutina,user;
    ArrayList<String> listaEjercicios;
    ArrayAdapter arrayAdapter;

    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    boolean isFABOpen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_editar_rutina);
        gestorBD = new miBD(this, "MyFitnessNotebook", null, 1);
        /*Menú flotante para agregar y eliminar ejercicios a nuestro cuaderno*/
        fab = (FloatingActionButton) findViewById(R.id.fabVer);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1Ver);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2Ver);
        fab.setOnClickListener(new View.OnClickListener() {
            /*Comportamiento del botón flotante*/
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });


        /*Cargamos los ejercicios de la rutina*/
        listView = (ListView) findViewById(R.id.rutinaConEjer);
        rutina = getIntent().getStringExtra("nombreRutina");
        user = getIntent().getStringExtra("user");
        listaEjercicios = gestorBD.getEjercicios(rutina,user);
        arrayAdapter = new ArrayAdapter(VerEditarRutina.this, android.R.layout.simple_list_item_2, android.R.id.text1, listaEjercicios) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View vista = super.getView(position, convertView, parent);
                TextView lineaPrincipal = (TextView) vista.findViewById(android.R.id.text1);
                TextView lineaSecundaria = (TextView) vista.findViewById(android.R.id.text2);
                String nombreEjercicio = listaEjercicios.get(position);
                lineaPrincipal.setText(nombreEjercicio);
                ArrayList<Integer> infoEjercicio = gestorBD.getInfoEjercicio(nombreEjercicio, rutina,user);
                String info = infoEjercicio.get(0) + "x" + infoEjercicio.get(1) + " con " + infoEjercicio.get(2) + "Kg";
                lineaSecundaria.setText(info);
                return vista;
            }
        };
        listView.setAdapter(arrayAdapter);

        /*Botón para añadir otro Ejercicio*/
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombreRutina = getIntent().getStringExtra("nombreRutina");
                String numEjer = getIntent().getStringExtra("numEjer");
                Intent i = new Intent(VerEditarRutina.this, addEjercicio.class);
                i.putExtra("nombreRutina", nombreRutina);
                i.putExtra("user",user);
                i.putExtra("numEjer", numEjer);
                startActivityForResult(i, 3);
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        /*Botón para eliminar todos los ejercicios de la rutina de un solo golpe*/
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(VerEditarRutina.this).setTitle("Eliminar todos los ejercicios").setMessage("Se eliminarán todos los ejercicios de la rutina, ¿Desea continuar?").setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ArrayList<String> todosEjercicios = gestorBD.getEjercicios(rutina,user);
                        for(String ejercicio: todosEjercicios){
                            gestorBD.eliminarEjercicio(ejercicio,rutina,user);
                        }
                        listaEjercicios.clear();
                        arrayAdapter.notifyDataSetChanged();
                        Intent iBack = new Intent();
                        iBack.putExtra("rutina", rutina);
                        setResult(Activity.RESULT_OK, iBack);
                        finish();

                    }
                }).setNegativeButton("No",null).show();

            }
        });
        /*Al clickar en un item de la lista llevará al usuario a una interfaz dónde podrá Editar los datos del ejercicio*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String ejercicio = listaEjercicios.get(i);
                System.out.println(ejercicio);
                ArrayList<Integer> infoEjer = gestorBD.getInfoEjercicio(ejercicio, rutina,user);
                System.out.println(infoEjer);

                Intent iVerEditar = new Intent(VerEditarRutina.this, EditarEjercicio.class);
                iVerEditar.putExtra("nombreEjercicio", ejercicio);
                iVerEditar.putExtra("numSeries", infoEjer.get(0).toString());
                iVerEditar.putExtra("numRepes", infoEjer.get(1).toString());
                iVerEditar.putExtra("peso", infoEjer.get(2).toString());
                iVerEditar.putExtra("rutina", rutina);
                iVerEditar.putExtra("user",user);
                startActivityForResult(iVerEditar, 10);
            }
        });
        /*Al clickar un rato en un tiem de la lista se podrá eliminar y saltará una alerta señalándonos lo que va a ocurrir*/

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = i;
                new AlertDialog.Builder(VerEditarRutina.this).setTitle("Eliminar Ejercicio").setMessage("¿Deseas eliminar el ejercicio?").setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(VerEditarRutina.this, "Eliminose", Toast.LENGTH_SHORT).show();
                        gestorBD.eliminarEjercicio(listaEjercicios.get(position),rutina,user);
                        listaEjercicios.remove(position);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("No", null).show();
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {//Se ha añadido un nuevo ejercicio correctamente
                rutina = getIntent().getStringExtra("nombreRutina");
                listaEjercicios = gestorBD.getEjercicios(rutina,user);
                arrayAdapter = new ArrayAdapter(VerEditarRutina.this, android.R.layout.simple_list_item_2, android.R.id.text1, listaEjercicios) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View vista = super.getView(position, convertView, parent);
                        TextView lineaPrincipal = (TextView) vista.findViewById(android.R.id.text1);
                        TextView lineaSecundaria = (TextView) vista.findViewById(android.R.id.text2);
                        String nombreEjercicio = listaEjercicios.get(position);
                        lineaPrincipal.setText(nombreEjercicio);
                        ArrayList<Integer> infoEjercicio = gestorBD.getInfoEjercicio(nombreEjercicio, rutina,user);
                        String info = infoEjercicio.get(0) + "x" + infoEjercicio.get(1) + " con " + infoEjercicio.get(2) + "Kg";
                        lineaSecundaria.setText(info);
                        return vista;
                    }
                };
                listView.setAdapter(arrayAdapter);
                Intent iBack = new Intent();
                iBack.putExtra("rutina", rutina);
                setResult(Activity.RESULT_OK, iBack);

            }
        }
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) { //Se ha editado un ejercicio correctamente
                rutina = getIntent().getStringExtra("nombreRutina");
                listaEjercicios = gestorBD.getEjercicios(rutina,user);
                arrayAdapter = new ArrayAdapter(VerEditarRutina.this, android.R.layout.simple_list_item_2, android.R.id.text1, listaEjercicios) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View vista = super.getView(position, convertView, parent);
                        TextView lineaPrincipal = (TextView) vista.findViewById(android.R.id.text1);
                        TextView lineaSecundaria = (TextView) vista.findViewById(android.R.id.text2);
                        String nombreEjercicio = listaEjercicios.get(position);
                        lineaPrincipal.setText(nombreEjercicio);
                        ArrayList<Integer> infoEjercicio = gestorBD.getInfoEjercicio(nombreEjercicio, rutina,user);
                        String info = infoEjercicio.get(0) + "x" + infoEjercicio.get(1) + " con " + infoEjercicio.get(2) + "Kg";
                        lineaSecundaria.setText(info);
                        return vista;
                    }
                };
                listView.setAdapter(arrayAdapter);

                Intent iBack = new Intent();
                iBack.putExtra("rutina", rutina);
                setResult(Activity.RESULT_OK, iBack);

            }
        }
    }
    private void showFABMenu() {
        isFABOpen = true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_75));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_125));
        //fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_175));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        //fab3.animate().translationY(0);
    }
}