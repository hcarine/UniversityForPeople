package com.example.cari.universit;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by cari on 10/06/16.
 */
public class NotasArrayAdapterItem extends ArrayAdapter<MainActivity.Nota> {
        Context context;
        int resourceId;
        MainActivity.Nota[] notas;

        public NotasArrayAdapterItem(Context context, int resource, MainActivity.Nota[] notas) {
            super(context, resource, notas);

            this.context = context;
            this.resourceId = resource;
            this.notas = notas;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                convertView = inflater.inflate(resourceId, parent, false);
            }

            MainActivity.Nota nota = notas[position];
            TextView nome_avaliacao = (TextView) convertView.findViewById(R.id.nome_avaliacao);
            nome_avaliacao.setText(nota.getNomeAvaliacao());
            TextView nota_avaliacao = (TextView) convertView.findViewById(R.id.nota);
            nota_avaliacao.setText(nota.getNota());
            nota_avaliacao.setTag(nota);
            TextView data = (TextView) convertView.findViewById(R.id.data_avaliacao);
            data.setText(nota.getDataAvaliacao());
            TextView media_turma = (TextView) convertView.findViewById(R.id.media_turma);
            media_turma.setText(nota.getMediaTurma());
            TextView atualizacao = (TextView) convertView.findViewById(R.id.atualizacao);
            atualizacao.setText(DateUtils.getRelativeTimeSpanString(nota.getUltimaAtualizacao().getTime()));

            return convertView;
        }
    }