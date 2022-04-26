package com.example.myfitnessnotebook_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class addRoutine extends AppCompatActivity {
    miBD gestorBD;
    EditText nombreRutinaText;
    String nombreRutina, user;
    Boolean agregado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_routine);
        Button add = (Button) findViewById(R.id.btn_addRoutine);
        user = getIntent().getStringExtra("user");
        gestorBD = new miBD(this, "MyFitnessNotebook", null, 1);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombreRutinaText = (EditText) findViewById(R.id.addRoutine_nombreRutina);
                nombreRutina = nombreRutinaText.getText().toString();
                if (nombreRutina.equals("")) {
                    /*Nos aseguramos de que el campo no está vacío*/
                    Toast.makeText(addRoutine.this, "Por favor, ingrese un nombre para la rutina", Toast.LENGTH_SHORT).show();
                } else {
                    agregado = gestorBD.agregarRutina(nombreRutina,user);
                    if (agregado) {
                        //Si no existe ninguna rutina con este nombre
                        Intent i = new Intent();
                        i.putExtra("rutina", nombreRutina.toString());
                        setResult(Activity.RESULT_OK, i);
                        finish();
                    } else {
                        //Salta un aviso para cambiar el nombre de la rutina
                        Toast.makeText(addRoutine.this, "Ya existe una rutina con el nombre " + nombreRutina + ". Por favor pruebe con otro nombre.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


}