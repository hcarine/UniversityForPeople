package com.example.cari.universit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by cari on 19/06/16.
 */
public class OnItemClickListenerListViewItem implements AdapterView.OnItemClickListener {


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Context context = view.getContext();

        TextView textViewItem = ((TextView) view.findViewById(R.id.nome_disciplina));

        // get the clicked item name
        String listItemText = textViewItem.getText().toString();
//
//        // get the clicked item ID
//        String listItemId = textViewItem.getTag().toString();

        // just toast it
        Toast.makeText(context, "Item: " + listItemText, Toast.LENGTH_SHORT).show();
        inflateContent(view);

    }

    private void inflateContent(View view){
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.gradeContent);
        LayoutInflater inflater =(LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myView = inflater.inflate(R.layout.notas_disciplina, null);
        layout.addView(myView);
    }
}