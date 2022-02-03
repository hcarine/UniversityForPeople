package com.example.cari.universit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String PREFS = "SIGAPREFS";

    ListView listaDisciplinas;
    ListView listaNotas;
    TextView nameUser;
    List<Disciplina> disciplinas = null;
    RelativeLayout layout;
    RelativeLayout gradeLayout;
    View viewDisciplinas;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initCredentials();
        initNavigationDrawer();
        inflateContent();

        try {
            initDisciplinas();
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("exception");
            Toast.makeText(this, "Não foi possível carregar as disciplinas. Verifique sua internet!", Toast.LENGTH_SHORT).show();
        }
        listaDisciplinas = (ListView) findViewById(R.id.lista_disciplinas);
        final DisciplinaArrayAdapterItem adapter = new DisciplinaArrayAdapterItem(this, R.layout.activity_notas, disciplinas.toArray(new Disciplina[disciplinas.size()]));
        listaDisciplinas.setAdapter(adapter);
        listaDisciplinas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Context context = view.getContext();
                TextView textViewItem = ((TextView) view.findViewById(R.id.nome_disciplina));
                String listItemText = textViewItem.getText().toString();
                Disciplina disciplina = (Disciplina) textViewItem.getTag();
                Toast.makeText(context, "Item: " + listItemText +" Codigo: " + disciplina.getCodigo(), Toast.LENGTH_SHORT).show();

                try {
                    getNotas(disciplina);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Não foi possível carregar as notas. Verifique sua internet!", Toast.LENGTH_SHORT).show();
                }

                layout.removeAllViews();
                LayoutInflater inflater =(LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View myView = inflater.inflate(R.layout.notas_disciplina, null);
                layout.addView(myView);

                listaNotas = (ListView) MainActivity.this.findViewById(R.id.list_grades);
                NotasArrayAdapterItem adapter = new NotasArrayAdapterItem(MainActivity.this, R.layout.activity_disciplina, disciplina.getNotas().toArray(new Nota[disciplina.notas.size()]));
                listaNotas.setAdapter(adapter);
            }
        });

    }
    private void initCredentials(){
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        username = settings.getString("username", "");
        password = settings.getString("password", "");
    }

    private void getNotas(Disciplina disciplina) throws IOException {
        String cookieUrl = "http://siga.udesc.br/siga/inicial.do?evento=cookie";
        Connection.Response docCookie = Jsoup.connect(cookieUrl).ignoreHttpErrors(true).timeout(3000).execute();

        String url = "http://siga.udesc.br/siga/j_security_check?j_username=" + username + "&j_password=" + password;
        Connection.Response doc = Jsoup.connect(url).cookies(docCookie.cookies()).ignoreHttpErrors(true).timeout(3000).execute();

        String urlNotas = "http://siga.udesc.br/siga/com/executaconsultapersonaliz.do";
        Document docNotas = Jsoup.connect(urlNotas).cookies(doc.cookies()).ignoreHttpErrors(true).timeout(3000).
                data("modoPlc", "consultaPlc")
                .data("evento", "Executar Consulta")
                .data("lPeriodoLetivo", disciplina.getPeriodoLetivo())
                .data("lCurso", disciplina.getCurso())
                .data("lDisciplina", disciplina.getCodigo())
                .data("estadoJanelaimportacao", "fecha")
                .data("idConsulta", "3")
                .data("exibecomp", "N")
                .data("exibeComboPerLet", "S")
                .post();
        Elements notasHtml = docNotas.select("#resultado tr");
        for(Element el : notasHtml){
            if(el.hasClass("linhapar") || el.hasClass("linhaimpar"))
                disciplina.getNotas().add(mapToNota(el.getAllElements().get(0).children()));
        }
    }
    private Nota mapToNota(Elements atividadesHtml){
        Nota nota = new Nota();
        nota.setCodigoAvaliacao(atividadesHtml.get(0).text());
        nota.setNomeAvaliacao(atividadesHtml.get(1).text());
        nota.setDataAvaliacao(atividadesHtml.get(2).text());
        nota.setNota(atividadesHtml.get(3).text());
        nota.setMediaTurma(atividadesHtml.get(4).text());
        return nota;
    }

    private void inflateContent(){
        layout = (RelativeLayout) findViewById(R.id.gradeContent);
        LayoutInflater inflater =(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewDisciplinas = inflater.inflate(R.layout.content_main, null);
        layout.addView(viewDisciplinas);
    }

    private void initNavigationDrawer(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        nameUser = (TextView) header.findViewById(R.id.user_name);
        nameUser.setText(settings.getString("username", "Sem nome"));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.grade) {
           returnHome();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void returnHome(){
        layout.removeAllViews();
        layout.addView(viewDisciplinas);
    }
    private void initDisciplinas() throws IOException {
        String cookieUrl = "http://siga.udesc.br/siga/inicial.do?evento=cookie";
        Connection.Response docCookie = Jsoup.connect(cookieUrl).ignoreHttpErrors(true).timeout(3000).execute();

        String url = "http://siga.udesc.br/siga/j_security_check?j_username=" + username + "&j_password=" + password;
        Connection.Response doc = Jsoup.connect(url).cookies(docCookie.cookies()).ignoreHttpErrors(true).timeout(3000).execute();

        String urlNotas = "http://siga.udesc.br/siga/com/executaconsultapersonaliz.do?evento=executaConsulta&id=2&exe=S";
        Document docNotas = Jsoup.connect(urlNotas).cookies(doc.cookies()).ignoreHttpErrors(true).timeout(3000).get();

        String urlNotasEspecifica = "http://siga.udesc.br/siga/com/executaconsultapersonaliz.do?evento=executaConsulta&id=3";
        Document docNotasEspecifica = Jsoup.connect(urlNotasEspecifica).cookies(doc.cookies()).ignoreHttpErrors(true).timeout(3000).get();

        Elements codigos = docNotasEspecifica.select("#lDisciplina").get(0).children();

        String periodoLetivo = docNotas.select("select#lPeriodoLetivo option[selected]").get(0).attr("value");
        String curso = docNotas.select("select#lCurso option[selected]").get(0).attr("value");
        Elements element = docNotas.select("#resultado tr");
        disciplinas = new ArrayList<>();
        for(Element el : element){
            if(el.hasClass("linhapar") || el.hasClass("linhaimpar"))
                disciplinas.add(mapToDisciplina(el.getAllElements().get(0).children(), codigos, periodoLetivo,curso));
        }
    }

    private Disciplina mapToDisciplina(Elements elements, Elements codigos, String periodoLetivo, String curso){
        Disciplina disciplina = new Disciplina();
        disciplina.setNome(elements.get(0).text());
        disciplina.setTurma(elements.get(1).text());
        disciplina.setMedia(elements.get(2).text());
        disciplina.setFaltas(elements.get(7).text());
        disciplina.setPeriodoLetivo(periodoLetivo);
        disciplina.setCurso(curso);
        disciplina.setCodigo(getCodigo(disciplina.getNome(), codigos));
        return disciplina;
    }

    private String getCodigo(String nome, Elements codigos) {
        for(Element el : codigos){
            if(nome.equals(el.ownText())){
                return el.attr("value");
            }
        }
        return null;
    }

    class Disciplina{
        private String nome;
        private String turma;
        private String media;
        private String faltas;
        private String codigo;
        private String curso;
        private String periodoLetivo;
        private Date ultimaAtualizacao = new Date();
        private List<Nota> notas = new ArrayList<>();

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getTurma() {
            return turma;
        }

        public void setTurma(String turma) {
            this.turma = turma;
        }

        public String getMedia() {
            return media;
        }

        public void setMedia(String media) {
            this.media = media;
        }

        public String getFaltas() {
            return faltas;
        }

        public void setFaltas(String faltas) {
            this.faltas = faltas;
        }

        public Date getUltimaAtualizacao() {
            return ultimaAtualizacao;
        }

        public void setUltimaAtualizacao(Date ultimaAtualizacao) {
            this.ultimaAtualizacao = ultimaAtualizacao;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public List<Nota> getNotas() {
            return notas;
        }

        public void setNotas(List<Nota> notas) {
            this.notas = notas;
        }

        public String getCurso() {
            return curso;
        }

        public void setCurso(String curso) {
            this.curso = curso;
        }

        public String getPeriodoLetivo() {
            return periodoLetivo;
        }

        public void setPeriodoLetivo(String periodoLetivo) {
            this.periodoLetivo = periodoLetivo;
        }

        @Override
        public String toString() {
            return "Disciplina{" +
                    "nome='" + nome + '\'' +
                    ", turma='" + turma + '\'' +
                    ", media='" + media + '\'' +
                    ", faltas='" + faltas + '\'' +
                    ", codigo='" + codigo + '\'' +
                    ", ultimaAtualizacao=" + ultimaAtualizacao +
                    ", notas=" + notas +
                    '}';
        }
    }

    class Nota{
        private String codigoAvaliacao;
        private String nomeAvaliacao;
        private String dataAvaliacao;
        private String nota;
        private String mediaTurma;
        private Date ultimaAtualizacao = new Date();

        public String getCodigoAvaliacao() {
            return codigoAvaliacao;
        }

        public void setCodigoAvaliacao(String codigoAvaliacao) {
            this.codigoAvaliacao = codigoAvaliacao;
        }

        public String getNomeAvaliacao() {
            return nomeAvaliacao;
        }

        public void setNomeAvaliacao(String nomeAvaliacao) {
            this.nomeAvaliacao = nomeAvaliacao;
        }

        public String getDataAvaliacao() {
            return dataAvaliacao;
        }

        public void setDataAvaliacao(String dataAvaliacao) {
            this.dataAvaliacao = dataAvaliacao;
        }

        public String getNota() {
            return nota;
        }

        public void setNota(String nota) {
            this.nota = nota;
        }

        public String getMediaTurma() {
            return mediaTurma;
        }

        public void setMediaTurma(String mediaTurma) {
            this.mediaTurma = mediaTurma;
        }

        public Date getUltimaAtualizacao() {
            return ultimaAtualizacao;
        }

        public void setUltimaAtualizacao(Date ultimaAtualizacao) {
            this.ultimaAtualizacao = ultimaAtualizacao;
        }

        @Override
        public String toString() {
            return "Nota{" +
                    "codigoAvaliacao='" + codigoAvaliacao + '\'' +
                    ", nomeAvaliacao='" + nomeAvaliacao + '\'' +
                    ", dataAvaliacao='" + dataAvaliacao + '\'' +
                    ", nota='" + nota + '\'' +
                    ", mediaTurma='" + mediaTurma + '\'' +
                    '}';
        }
    }
}
