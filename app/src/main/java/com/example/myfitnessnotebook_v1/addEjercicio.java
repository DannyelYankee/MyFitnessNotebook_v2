package com.example.myfitnessnotebook_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class addEjercicio extends AppCompatActivity {
    TextView nombreRutina;
    TextView numEjer;
    EditText nombreEjer, series, repeticiones, peso;
    Button btn_addEjer;
    miBD gestorBD;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ejercicio);
        gestorBD = new miBD(this, "MyFitnessNotebook", null, 1);


        nombreRutina = (TextView) findViewById(R.id.nombreRutina);
        numEjer = (TextView) findViewById(R.id.currentNumEjer);
        btn_addEjer = (Button) findViewById(R.id.btn_addEjer);
        nombreEjer = (EditText) findViewById(R.id.nombreEjercicio);
        series = (EditText) findViewById(R.id.series);
        repeticiones = (EditText) findViewById(R.id.repeticiones);
        peso = (EditText) findViewById(R.id.peso);

        user = getIntent().getStringExtra("user");
        String nombreRutinaExtra = getIntent().getStringExtra("nombreRutina");
        nombreRutina.setText(nombreRutinaExtra);
        String numEjerExtra = getIntent().getStringExtra("numEjer");
        ArrayList<String> numEjercicios = gestorBD.getEjercicios(nombreRutinaExtra, user);
        numEjer.setText(String.valueOf(numEjercicios.size()));
        btn_addEjer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Nos aseguramos de que no haya ningún campo vacío*/
                if (nombreEjer.getText().toString().equals("") || series.getText().toString().equals("") || repeticiones.getText().toString().equals("") || peso.getText().toString().equals("")) {
                    Toast.makeText(addEjercicio.this, "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show();
                } else {

                    int numSeries = Integer.parseInt(series.getText().toString());
                    String nombreEjercicio = nombreEjer.getText().toString();
                    int repes = Integer.parseInt(repeticiones.getText().toString());
                    int pesoKG = Integer.parseInt(peso.getText().toString());
                    String rutina = getIntent().getStringExtra("nombreRutina");
                    /*Se agrega el ejercicio a la BBDD*/
                    gestorBD.agregarEjercicio(nombreEjercicio, numSeries, repes, pesoKG, rutina, user);
                    Intent iBack = new Intent();
                    iBack.putExtra("rutina", nombreRutinaExtra);
                    setResult(Activity.RESULT_OK, iBack);
                    finish();
                }
            }
        });

    }

}