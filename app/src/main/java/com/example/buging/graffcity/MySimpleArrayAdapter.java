package com.example.buging.graffcity;

/**
 * Created by Buging on 02-01-2016.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public MySimpleArrayAdapter(Context context, String[] values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.nombre_item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.foto_item);
        textView.setText(values[position]);
        // Change the icon for Windows and iPhone
        String s = values[position];
        if (s.startsWith("Rojo") || s.startsWith("Verde")) {
            imageView.setImageResource(R.drawable.logo);
        } else {
            imageView.setImageResource(R.drawable.agregar_image);
        }

        return rowView;
    }
}