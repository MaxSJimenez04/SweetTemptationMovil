package com.example.sweettemptation.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.sweettemptation.R;
import com.example.sweettemptation.dto.ProductoDTO;

import java.util.List;

public class ProductosAdapter extends ArrayAdapter<ProductoDTO> {

    public ProductosAdapter(@NonNull Context context, @NonNull List<ProductoDTO> objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return crearVista(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return crearVista(position, convertView, parent);
    }

    private View crearVista(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView tv = convertView.findViewById(android.R.id.text1);
        ProductoDTO producto = getItem(position);

        if (producto != null) {
            tv.setText(producto.getNombre());
        }

        return convertView;
    }
}
