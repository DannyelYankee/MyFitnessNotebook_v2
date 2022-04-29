package com.example.myfitnessnotebook_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.regex.*;

public class SignUpActivity extends AppCompatActivity {
    EditText usernameText, psswd1Text, psswd2Text;
    String username, psswd1, psswd2, token;
    miBD gestorBD;
    Button btnSU;
    Boolean logueado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        /*Recogemos los valores del formulario de registro de usuario*/
        usernameText = (EditText) findViewById(R.id.usernameSU);
        psswd1Text = (EditText) findViewById(R.id.passwordSU);
        psswd2Text = (EditText) findViewById(R.id.passwordSU2);
        username = usernameText.getText().toString();
        psswd1 = psswd1Text.getText().toString();
        psswd2 = psswd2Text.getText().toString();
        //Obtenemos el token de Firebase para almacenarlo en la BBDD
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> token = instanceIdResult.getToken());

        gestorBD = new miBD(this, "MyFitnessNotebook", null, 1);
        /*Registramos el usuario en la BBDD del servidor */
        btnSU = (Button) findViewById(R.id.btnLoginSU);

        btnSU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = usernameText.getText().toString();
                psswd1 = psswd1Text.getText().toString();
                psswd2 = psswd2Text.getText().toString();
                System.out.println("Email: " + username + " psswd1: " + psswd1 + " psswd2: " + psswd2 + " token: "+token);
                if (validarEmail(username)) {
                    if (validarContra(psswd1, psswd2)) {
                        System.out.println("Contraseña valida--> " + validarContra(psswd1, psswd2));
                        Data datos = new Data.Builder().putString("user", username).putString("password", psswd1).putString("token", token).build();
                        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpRegistro.class).setInputData(datos).build();
                        WorkManager.getInstance(SignUpActivity.this).getWorkInfoByIdLiveData(otwr.getId()).observe(SignUpActivity.this, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if (workInfo != null && workInfo.getState().isFinished()) {
                                    Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                                    System.out.println(resultadoPhp);
                                    if (resultadoPhp) {
                                        Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                                        i.putExtra("user", username);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "El email ya está en uso, por favor pruebe con otro", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                        WorkManager.getInstance(SignUpActivity.this).enqueue(otwr);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Contraseña incorrecta. Tu contraseña debe tener mínimo ocho caracteres, al menos una letra mayúscula, una letra minúscula, un número y un carácter especial.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public boolean validarEmail(String email) {
        boolean cumpleEmail = false;
        //Vamos a comprobar que el email cumple con el siguiente patrón daniel@correo.com
        if (email.trim().length() > 0) {
            String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
            if (Pattern.compile(regexPattern).matcher(email).matches()) {
                cumpleEmail = true;
            }
        }
        return cumpleEmail;
    }

    public boolean validarContra(String pass1, String pass2) {
        boolean cumplePass = false;
        //mínimo ocho caracteres, al menos una letra mayúscula, una letra minúscula, un número y un carácter especial. D@ja1920
        String patron = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$";
        if (pass1.trim().length() > 0) {
            if (pass1.equals(pass2)) {
                if (Pattern.compile(patron).matcher(pass1).matches()) {
                    cumplePass = true;
                }
            }
        }
        System.out.println(pass1 + " --> " + Pattern.compile(patron).matcher(pass1).matches());
        return cumplePass;

    }

}