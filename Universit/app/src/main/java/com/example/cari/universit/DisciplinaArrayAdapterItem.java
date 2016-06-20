package com.example.cari.universit;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by cari on 19/06/16.
 */
public class DisciplinaArrayAdapterItem extends ArrayAdapter<MainActivity.Disciplina> {

    Context context;
    int resourceId;
    MainActivity.Disciplina[] disciplinas;

    public DisciplinaArrayAdapterItem(Context context, int resource, MainActivity.Disciplina[] disciplinas) {
        super(context, resource, disciplinas);

        this.context = context;
        this.resourceId = resource;
        this.disciplinas = disciplinas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resourceId, parent, false);
        }

        // object item based on the position
        MainActivity.Disciplina disciplina = disciplinas[position];

        // get the TextView and then set the text (item name) and tag (item ID) values
        TextView media = (TextView) convertView.findViewById(R.id.media_disciplina);
        media.setText(disciplina.getMedia());
        TextView nome = (TextView) convertView.findViewById(R.id.nome_disciplina);
        nome.setText(disciplina.getNome());
        nome.setTag(disciplina);
        TextView faltas = (TextView) convertView.findViewById(R.id.faltas_disciplina);
        faltas.setText(disciplina.getFaltas());
        TextView atualizacao = (TextView) convertView.findViewById(R.id.last_update);
        atualizacao.setText(DateUtils.getRelativeTimeSpanString(disciplina.getUltimaAtualizacao().getTime()));

        return convertView;
    }
}
