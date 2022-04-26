package com.example.myfitnessnotebook_v1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdaptadorListViewRutinas extends BaseAdapter {
    private Context contexto;
    private LayoutInflater inflater;
    private String[] datos;
    private int[] imagenes;

    public AdaptadorListViewRutinas(Context pContext, String[] pDatos, int[] pImagenes) {
        contexto = pContext;
        datos = pDatos;
        imagenes = pImagenes;
        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return datos.length;
    }

    @Override
    public Object getItem(int i) {
        return datos[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.listview_rutinas,null);
        TextView nombre = (TextView) view.findViewById(R.id.textViewRutinaPer);
        ImageView img = (ImageView) view.findViewById(R.id.imagen);
        nombre.setText(datos[i]);
        img.setImageResource(imagenes[0]);
        return view;
    }
}
