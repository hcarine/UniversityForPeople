package com.example.cari.universit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
        String listItemText = textViewItem.getText().toString();
        Toast.makeText(context, "Item: " + listItemText, Toast.LENGTH_SHORT).show();
        inflateContent(view);

//        listaNotas = (ListView) view.findViewById(R.id.lista_notas);
//        NotasArrayAdapterItem adapter = new NotasArrayAdapterItem(this, R.layout.activity_disciplina, disciplina.getNotas());
//        listaNotas.setAdapter(adapter);


    }

    private void inflateContent(View view){
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.gradeContent);
        LayoutInflater inflater =(LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myView = inflater.inflate(R.layout.notas_disciplina, null);
        layout.addView(myView);
    }


}