package com.example.myfitnessnotebook_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditarEjercicio extends AppCompatActivity {

    EditText editNombre, editSeries, editRepes, editPeso;
    Button btnEdit;
    miBD gestorBD;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_ejercicio);
        gestorBD = new miBD(this, "MyFitnessNotebook", null, 1);

        editNombre = (EditText) findViewById(R.id.EditNombreEjercicio);
        editSeries = (EditText) findViewById(R.id.EditSeries);
        editRepes = (EditText) findViewById(R.id.EditRepeticiones);
        editPeso = (EditText) findViewById(R.id.EditPeso);

        /*Recogemos los datos actuales del ejercicio*/
        String nombreExtra = getIntent().getStringExtra("nombreEjercicio");
        String seriesExtra = getIntent().getStringExtra("numSeries");
        String repesExtra = getIntent().getStringExtra("numRepes");
        String pesoExtra = getIntent().getStringExtra("peso");
        String rutinaExtra = getIntent().getStringExtra("rutina");
        user = getIntent().getStringExtra("user");
        /*Establecemos los hint de los EditText con los datos actuales para que el usuario sepa cual le interesa cambiar*/
        editNombre.setHint(nombreExtra);
        editSeries.setHint(seriesExtra + " series");
        editRepes.setHint(repesExtra + " repeticiones");
        editPeso.setHint(pesoExtra + " kg");


        btnEdit = (Button) findViewById(R.id.btn_EditEjer);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            /*Botón para actualizar la BBDD con los nuevos datos del ejercicio*/
            @Override
            public void onClick(View view) {
                String nombreEditado = editNombre.getText().toString();
                String seriesEditado = editSeries.getText().toString();
                String repesEditado = editRepes.getText().toString();
                String pesoEditado = editPeso.getText().toString();
                /* Comprobamos que los campos no sean vacíos
                 * En caso de serlo, cogerán el valor actual pues
                 * se entiende que el usuario no quiso cambiar esos valores*/
                if (nombreEditado.equals("")) {
                    nombreEditado = nombreExtra;
                }
                if (seriesEditado.equals("")) {
                    seriesEditado = seriesExtra;
                }
                if (repesEditado.equals("")) {
                    repesEditado = repesExtra;
                }
                if (pesoEditado.equals("")) {
                    pesoEditado = pesoExtra;
                }

                int series = Integer.parseInt(seriesEditado);
                int repes = Integer.parseInt(repesEditado);
                int peso = Integer.parseInt(pesoEditado);
                gestorBD.editarEjercicio(nombreExtra,nombreEditado, series, repes, peso, rutinaExtra,user);
                Intent iBack = new Intent();
                setResult(RESULT_OK);
                finish();

            }
        });

    }
}