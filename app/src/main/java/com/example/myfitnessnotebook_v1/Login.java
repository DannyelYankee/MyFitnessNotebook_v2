package com.example.myfitnessnotebook_v1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
    EditText userNameText, passwordText;
    String userName, password;
    Button btnLogin, btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*PROXIMAMENTE.......*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameText = (EditText) findViewById(R.id.username);
        passwordText = (EditText) findViewById(R.id.password);

        userName = userNameText.getText().toString();
        password = passwordText.getText().toString();
        btnLogin = (Button) findViewById(R.id.btnLogin2);
        btnRegister = (Button) findViewById(R.id.btn_register);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = userNameText.getText().toString();
                password = passwordText.getText().toString();
                System.out.println("BOTON LOGIN click");

                if (userName.trim().length() > 0 && password.trim().length() > 0) {
                    Data.Builder datos = new Data.Builder().putString("user", userName).putString("password", password);
                    OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpLogin.class).setInputData(datos.build()).build();
                    WorkManager.getInstance(Login.this).getWorkInfoByIdLiveData(otwr.getId()).observe(Login.this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState().isFinished()) {
                                Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                                System.out.println("RESULTADO LOGIN --> " + resultadoPhp);
                                if (resultadoPhp) {//se logueó correctamente
                                    Intent i = new Intent(Login.this, MainActivity.class);
                                    i.putExtra("user",userName);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Email o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                    WorkManager.getInstance(Login.this).enqueue(otwr);
                } else {
                    Toast.makeText(Login.this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Te lleva a la interfaz de Registrar usuario
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, SignUpActivity.class);
                startActivityForResult(i, 100);

            }
        });


    }


}