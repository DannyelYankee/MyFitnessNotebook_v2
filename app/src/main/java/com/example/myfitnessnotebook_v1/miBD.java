package com.example.myfitnessnotebook_v1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class miBD extends SQLiteOpenHelper {
    //public static final int DATABASE_VERSION = 1;
    //public static final String DATABASE_NAME = "MyFitnessBook.db";

    public miBD(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /*Tabla Imagen:
         * correo    contra*/
        sqLiteDatabase.execSQL("CREATE TABLE Imagen ('usuario' VARCHAR(255) PRIMARY KEY NOT NULL, 'imagen' blob)");
        /*Tabla Rutina:
         * nombre */
        sqLiteDatabase.execSQL("CREATE TABLE Rutinas ('nombre' VARCHAR(255) PRIMARY KEY NOT NULL, 'usuario' VARCHAR(255))");

        /*Tabla Ejercicio:
         * nombre    numSeries   numRepes    peso   */
        sqLiteDatabase.execSQL("CREATE TABLE Ejercicios ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'nombre' VARCHAR(255) NOT NULL, 'numSeries' INTEGER, 'numRepes' INTEGER, 'peso' INTEGER, 'rutina' VARCHAR(255), 'usuario' VARCHAR(255))");


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public byte[] getImagen(String user){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Imagen WHERE usuario=?";
        Cursor c = db.rawQuery(query, new String[]{user});
        byte[] imagen = null;
        while (c.moveToNext()) {
            int i = c.getColumnIndex("imagen");
            imagen = c.getBlob(i);
        }
        c.close();
        return imagen;
    }

    public void clearImagen(String user){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Imagen","usuario=?",new String[]{String.valueOf(user)});
        db.close();
    }

    public void insertarImagen(String user,byte[] foto){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO Imagen VALUES (?,?)";
        SQLiteStatement sqLiteStatement = db.compileStatement(sql);
        sqLiteStatement.clearBindings();
        sqLiteStatement.bindString(1,user);
        sqLiteStatement.bindBlob(2,foto);
        sqLiteStatement.executeInsert();
        db.close();
    }
    public ArrayList<String> getRutinas(String usuario) {
        /*Devuelve los nombres de las rutinas*/
        ArrayList<String> rutinas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Rutinas WHERE usuario=?";
        Cursor c = db.rawQuery(query, new String[]{usuario});
        while (c.moveToNext()) {
            int i = c.getColumnIndex("nombre");
            String nombre = c.getString(i);
            rutinas.add(nombre);
        }
        c.close();
        return rutinas;
    }

    public boolean agregarRutina(String nombre,String usuario) {
        //Agrega nueva rutina a la BBDD
        boolean agregado = false;
        ArrayList<String> rutinas = this.getRutinas(usuario);
        if (!rutinas.contains(nombre)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("nombre", nombre);
            values.put("usuario",usuario);
            long newRowId = db.insert("Rutinas", null, values);
            db.close();
            agregado = true;
        }
        return agregado;
    }

    public ArrayList<String> getEjercicios(String rutina,String usuario) {
        /*Devuelve una lista con los nombres de todos los ejercicios dada una rutina*/
        ArrayList<String> ejercicios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Ejercicios WHERE rutina= ? AND usuario=?";
        Cursor c = db.rawQuery(query, new String[]{rutina,usuario});
        while (c.moveToNext()) {
            int i = c.getColumnIndex("nombre");
            String nombre = c.getString(i);
            ejercicios.add(nombre);
        }
        c.close();
        db.close();
        Log.i("ejercicios", "size: " + ejercicios.size());
        return ejercicios;
    }

    public ArrayList<Integer> getInfoEjercicio(String ejercicio, String rutina, String usuario) {
        /*Dado el nombre de un ejercicio y el nombre de la rutina a la que pertenece:
        * devuelve una lista con los datos de dicho ejercicio: nº series, nº repeticiones y peso*/
        ArrayList<Integer> info = new ArrayList<Integer>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Ejercicios WHERE nombre= ? AND rutina= ? AND usuario=?";
        Cursor c = db.rawQuery(query, new String[]{ejercicio, rutina,usuario});
        while (c.moveToNext()) {
            int indexSeries = c.getColumnIndex("numSeries");
            int indexRepes = c.getColumnIndex("numRepes");
            int indexPeso = c.getColumnIndex("peso");
            info.add(c.getInt(indexSeries));
            info.add(c.getInt(indexRepes));
            info.add(c.getInt(indexPeso));
        }
        c.close();
        db.close();

        return info;
    }

    public void agregarEjercicio(String nombreEjer, int series, int repeticiones, int peso, String nombreRutina,String usuario) {
        //Agrega un ejercicio nuevo a la BBDD
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombreEjer);
        values.put("numSeries", series);
        values.put("numRepes", repeticiones);
        values.put("peso", peso);
        values.put("rutina", nombreRutina);
        values.put("usuario",usuario);
        long newRowId = db.insert("Ejercicios", null, values);

        Log.i("agregarEjercicio", "agregado");
        db.close();
    }
    public void eliminarEjercicio(String nombreEjer, String rutina, String usuario){
        //Elimina un ejercicio en específico perteneciente a una rutina en concreto
        int id = this.getID(nombreEjer,rutina,usuario);
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println("id de "+nombreEjer+"--->"+id);
        db.delete("Ejercicios","id=?",new String[]{String.valueOf(id)});
        db.close();
    }
    public void eliminarRutina(String rutina,String usuario){
        //Elimina una rutina de la BBDD
        SQLiteDatabase db = this .getWritableDatabase();
        /*Primero eliminamos todos los ejercicios relacionados con la rutina*/
        db.delete("Ejercicios","rutina=? AND usuario=?",new String[]{rutina,usuario});
        db.delete("Rutinas","nombre=? AND usuario=?",new String[]{rutina,usuario});
        db.close();
    }
    public int getID(String ejercicio, String rutina, String usuario) {
        //Dado el nombre de un ejercicio y el de la rutina a la que pertence, devuelve el id de dicha row de la BBDD
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Ejercicios WHERE nombre = ? AND rutina = ? AND usuario=?";
        Cursor c = db.rawQuery(query, new String[]{ejercicio, rutina,usuario});
        int id = -1;
        while (c.moveToNext()) {
            int index = c.getColumnIndex("id");
            id = c.getInt(index);
        }
        return id;
    }
    public void editarEjercicio(String nombreOriginal, String nombreEjer, int series, int repeticiones, int peso, String nombreRutina, String usuario) {
        //Actualiza en la BBDD los datos de un ejercicio
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int idEjercicio = this.getID(nombreOriginal, nombreRutina,usuario);
        values.put("numSeries", series);
        values.put("numRepes", repeticiones);
        values.put("peso", peso);
        db.update("Ejercicios", values, "id=?", new String[]{String.valueOf(idEjercicio)});
        if (nombreEjer != nombreOriginal) {//Si el usuario decide cambiar el nombre del ejercicio
            ContentValues values2 = new ContentValues();
            values2.put("nombre", nombreEjer);
            db.update("Ejercicios", values2, "id=?", new String[]{String.valueOf(idEjercicio)});
        }
        db.close();
        System.out.println("Update hecho");
    }
}
