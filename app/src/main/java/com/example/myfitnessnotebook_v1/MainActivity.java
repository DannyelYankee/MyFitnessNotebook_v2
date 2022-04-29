package com.example.myfitnessnotebook_v1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    FloatingActionButton fab4;

    boolean isFABOpen;
    Button btnLogout;
    ListView listView;
    ArrayList<String> rutinas;
    HashMap<String, Integer> hashRutinas;
    //ArrayAdapter arrayAdapter;
    miBD gestorBD;
    String user;
    ImageView perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gestorBD = new miBD(this, "MyFitnessNotebook", null, 1);
        user = getIntent().getStringExtra("user");
        /*Menú flotante para agregar y eliminar rutinas a nuestro cuaderno*/
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);

        perfil = (ImageView) findViewById(R.id.perfil);

        btnLogout = (Button) findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Login.class);
                startActivity(i);
                finish();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            /*Botón para añadir un entrenamiento*/
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, addRoutine.class);
                i.putExtra("user", user);
                startActivityForResult(i, 1);
                closeFABMenu();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            /*Botón para borrar toda la BBDD
             * Aparecerá un dialago para advertir de lo que va a ocurrir*/
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this).setTitle("Borrar toda la BBDD").setMessage("Se borrarán todos los datos de la aplicación, ¿Desea continuar?").setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.deleteDatabase("MyFitnessNotebook");
                        rutinas = new ArrayList<>();
                        AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(), new String[]{}, new int[]{});
                        listView.setAdapter(arrayAdapter);

                        /*Preparamos notificación que saldrá con un icono de Warning para avisar de que la BBDD ha sido vaciada por completo*/
                        System.out.println("BORRAR TODA BBDD");
                        /*
                        NotificationManager elManager = (NotificationManager) getSystemService(MainActivity.this.NOTIFICATION_SERVICE);
                        NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(MainActivity.this, "IdCanal");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel elCanal = new NotificationChannel("CanalBBDD", "Notificacion Eliminar", NotificationManager.IMPORTANCE_DEFAULT);
                            elManager.createNotificationChannel(elCanal);
                        }
                        elBuilder.setSmallIcon(R.drawable.ic_baseline_warning_24);
                        elBuilder.setContentTitle("Base de Datos vaciada");
                        elBuilder.setContentText("Se ha vaciado la Base de Datos");
                        elBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        elBuilder.setAutoCancel(true);
                        elManager.notify(1, elBuilder.build());*/

                        final String CHANNEL_ID = "HEADS_UP_NOTIFICATION";
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "HEADS_UP_NOTIFICATION", NotificationManager.IMPORTANCE_HIGH);
                        getSystemService(NotificationManager.class).createNotificationChannel(channel);
                        Notification.Builder notificacion = new Notification.Builder(MainActivity.this, CHANNEL_ID);
                        notificacion.setContentTitle("Base de Datos vaciada");
                        notificacion.setContentText("Se ha vaciado la Base de Datos");
                        notificacion.setSmallIcon(R.drawable.ic_launcher_foreground);
                        notificacion.setAutoCancel(true);
                        NotificationManagerCompat.from(MainActivity.this).notify(1, notificacion.build());
                    }
                }).setNegativeButton("No", null).show();
                closeFABMenu();
            }
        });
        System.out.println("CARGAR FOTO DE PERFIL");
        cargarFotoPerfil(user);
        System.out.println("CARGADA FOTO DE PERFIL");
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent elIntentFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(elIntentFoto, 777);
                closeFABMenu();
            }

        });
        fab4.setOnClickListener(new View.OnClickListener() {
            /*Al clickar en este botón, se enviará un mensaje a todos los usuarios que tengan la app instalada.
            * Un mensaje de motivación para que cojan la pala y se pongan a laburar en su cuerpo*/
            @Override
            public void onClick(View view) {

                Data datos = new Data.Builder().putString("user", user).build();
                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpMensaje.class).setInputData(datos).build();
                WorkManager.getInstance(MainActivity.this).getWorkInfoByIdLiveData(otwr.getId()).observe(MainActivity.this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                            if (resultadoPhp) {
                                Intent i = getIntent();
                                startActivity(i);
                                finish();
                            }
                        }
                    }
                });
                WorkManager.getInstance(MainActivity.this).enqueue(otwr);
                closeFABMenu();
            }
        });
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



        /*List view para mostrar las rutinas creadas dinámicamente*/
        listView = findViewById(R.id.listViewRutinas);
        hashRutinas = new HashMap<>();
        rutinas = gestorBD.getRutinas(user);
        String[] rutinasArray;
        if (rutinas != null) {
            this.inicializarHashMap();
            //arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, rutinas);

            rutinasArray = this.convertirArray(rutinas);
        } else {
            rutinasArray = new String[0];
        }
        int[] imagenes = {R.drawable.zyzz};
        AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(), rutinasArray, imagenes);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String nombreRutina = rutinas.get(i);
                if (hashRutinas.get(nombreRutina) == 0) {
                    /*Si la rutina se acaba de crear se lleva al usuario a una interfaz para que añada el primer ejercicio*/

                    Intent iEjercicio = new Intent(MainActivity.this, addEjercicio.class);
                    iEjercicio.putExtra("nombreRutina", nombreRutina);
                    iEjercicio.putExtra("user", user);
                    iEjercicio.putExtra("numEjer", hashRutinas.get(nombreRutina).toString());
                    startActivityForResult(iEjercicio, 2);
                } else {
                    /*Si la rutina ya tiene algún ejercicio se lleva al usuario a una interfaz donde aparecen listados los ejericios
                     * y podrá añadir más ejercicios, editarlos y/o borrarlos*/

                    Intent iVerEditar = new Intent(MainActivity.this, VerEditarRutina.class);
                    System.out.println(nombreRutina);
                    iVerEditar.putExtra("nombreRutina", nombreRutina);
                    iVerEditar.putExtra("user", user);
                    iVerEditar.putExtra("numEjer", hashRutinas.get(nombreRutina).toString());
                    startActivityForResult(iVerEditar, 4);
                }
            }
        });
        /*Al clickar durante x segundos una rutina saltará una alerta para eliminar la rutina si así lo desea el usuario*/
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = i;
                new AlertDialog.Builder(MainActivity.this).setTitle("Eliminar rutina").setMessage("¿Deseas eliminar la rutina?").setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gestorBD.eliminarRutina(rutinas.get(position), user);
                        hashRutinas.remove(rutinas.get(position));
                        rutinas.remove(position);
                        int[] imagenes = {R.drawable.zyzz};
                        String[] rutinasArray = convertirArray(rutinas);
                        AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(), rutinasArray, imagenes);
                        listView.setAdapter(arrayAdapter);
                    }
                }).setNegativeButton("No", null).show();

                return true;

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //La rutina se agregó correctamente
                String rutina = data.getStringExtra("rutina");
                rutinas = gestorBD.getRutinas(user);
                ArrayList<String> ejercicios = gestorBD.getEjercicios(rutina, user);
                this.hashRutinas.put(rutina, ejercicios.size());
                //arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, rutinas);
                //listView.setAdapter(arrayAdapter);
                int[] imagenes = {R.drawable.zyzz};
                String[] rutinasArray = this.convertirArray(rutinas);
                AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(), rutinasArray, imagenes);
                listView.setAdapter(arrayAdapter);
            }
        }
        if (requestCode == 2) {//se ha añadido el primer ejer a la rutina
            if (resultCode == RESULT_OK) {
                System.out.println("se ha añadido el primer ejer a la rutina");
                String rutina = data.getStringExtra("rutina");
                ArrayList<String> ejercicios = gestorBD.getEjercicios(rutina, user);
                this.hashRutinas.put(rutina, ejercicios.size());
                System.out.println(hashRutinas);
                rutinas = gestorBD.getRutinas(user);
                //arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, rutinas);
                //listView.setAdapter(arrayAdapter);
                int[] imagenes = {R.drawable.zyzz};
                String[] rutinasArray = this.convertirArray(rutinas);
                AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(), rutinasArray, imagenes);
                listView.setAdapter(arrayAdapter);
            }
        }
        if (requestCode == 4) { //Se ha añadido algun ejercicio más desde la clase VerEditarRutina
            if (resultCode == RESULT_OK) {
                String rutina = data.getStringExtra("rutina");
                ArrayList<String> ejercicios = gestorBD.getEjercicios(rutina, user);
                this.hashRutinas.put(rutina, ejercicios.size());
                rutinas = gestorBD.getRutinas(user);
                //arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, rutinas);
                //listView.setAdapter(arrayAdapter);
                int[] imagenes = {R.drawable.zyzz};
                String[] rutinasArray = this.convertirArray(rutinas);
                AdaptadorListViewRutinas arrayAdapter = new AdaptadorListViewRutinas(getApplicationContext(), rutinasArray, imagenes);
                listView.setAdapter(arrayAdapter);

            }
        }
        if (requestCode == 777) { //Recogemos la miniatura, la almacenamos en la BBDD del servidor
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap laMiniatura = (Bitmap) extras.get("data");
                //redimensionamos
                int anchoDestino = perfil.getWidth();
                int altoDestino = perfil.getHeight();
                int anchoImagen = laMiniatura.getWidth();
                int altoImagen = laMiniatura.getHeight();
                float ratioImagen = (float) anchoImagen / (float) altoImagen;
                float ratioDestino = (float) anchoDestino / (float) altoDestino;
                int anchoFinal = anchoDestino;
                int altoFinal = altoDestino;
                if (ratioDestino > ratioImagen) {
                    anchoFinal = (int) ((float) altoDestino * ratioImagen);
                } else {
                    altoFinal = (int) ((float) anchoDestino / ratioImagen);
                }
                Bitmap bitmapRedimensionado = Bitmap.createScaledBitmap(laMiniatura, anchoFinal, altoFinal, true);
                insertImagen(user, bitmapRedimensionado);
                cargarFotoPerfil(user);
            }

        }
    }

    public String[] convertirArray(ArrayList<String> lista) {
        String[] array = new String[lista.size()];
        int j = 0;
        for (String i : lista) {
            array[j] = i;
            j++;
        }
        return array;
    }

    private void inicializarHashMap() {

        ArrayList<String> rutinas = gestorBD.getRutinas(user);
        System.out.println(rutinas);
        for (String rutina : rutinas) {
            ArrayList<String> ejercicios = gestorBD.getEjercicios(rutina, user);
            this.hashRutinas.put(rutina, ejercicios.size());
        }
        System.out.println(hashRutinas);
    }

    private void showFABMenu() {
        isFABOpen = true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_75));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_125));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_175));
        fab4.animate().translationY(-getResources().getDimension(R.dimen.standard_225));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
        fab4.animate().translationY(0);
    }

    private void insertImagen(String usuario, Bitmap laMiniatura) {
        /*Inserta la imagen en la BBDD del servidor*/
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        laMiniatura.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] fotoTransformada = stream.toByteArray();
        if (gestorBD.getImagen(usuario) != null) {
            gestorBD.clearImagen(user);
        }
        gestorBD.insertarImagen(usuario, fotoTransformada);
        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter("usuario", usuario);
        builder.appendQueryParameter("titulo", "fotoPerfil");
        Data datos = new Data.Builder().putString("usuario", usuario).putString("titulo", "fotoPerfil").build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpInsertImagen.class).setInputData(datos).build();
        WorkManager.getInstance(MainActivity.this).getWorkInfoByIdLiveData(otwr.getId()).observe(MainActivity.this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo != null && workInfo.getState().isFinished()) {
                    Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                    System.out.println("RESULTADO INSERT IMAGEN --> " + resultadoPhp);
                    if (resultadoPhp) {//se logueó correctamente
                        Intent i = getIntent();
                        startActivity(i);
                        finish();
                    }
                }
            }
        });
        WorkManager.getInstance(MainActivity.this).enqueue(otwr);

    }

    public void cargarFotoPerfil(String user) {
        /*Obtiene la foto de la BBDD y la pone en el imageview*/
        Data datos = new Data.Builder().putString("usuario", user).build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(phpSelectImagen.class).setInputData(datos).build();
        WorkManager.getInstance(MainActivity.this).getWorkInfoByIdLiveData(otwr.getId()).observe(MainActivity.this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo != null && workInfo.getState().isFinished()) {
                    Boolean resultadoPhp = workInfo.getOutputData().getBoolean("exito", false);
                    System.out.println("RESULTADO INSERT IMAGEN --> " + resultadoPhp);
                    if (resultadoPhp) {
                        byte[] decodificado = gestorBD.getImagen(user);
                        if (decodificado != null) {
                            Bitmap elBitmap = BitmapFactory.decodeByteArray(decodificado, 0, decodificado.length);
                            perfil.setImageBitmap(elBitmap);
                        }
                    }
                }
            }
        });
        WorkManager.getInstance(MainActivity.this).enqueue(otwr);
    }
}