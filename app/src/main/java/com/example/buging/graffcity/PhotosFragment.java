package com.example.buging.graffcity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by Buging on 02-01-2016.
 */
public class PhotosFragment extends Fragment {

    private ListView photos;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        photos = (ListView) rootView.findViewById(R.id.listViewGraffitis);
        context = rootView.getContext();
        populateListView();
        return rootView;
    }

    private void populateListView(){

        String[] myItems = {"Rojo", "Verde", "Amarillo", "Azul"};

        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(context, myItems);

        photos.setAdapter(adapter);    }
}
