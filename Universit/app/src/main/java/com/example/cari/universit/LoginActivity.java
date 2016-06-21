package com.example.cari.universit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by cari on 23/05/16.
 */
public class LoginActivity extends AppCompatActivity {
    public static final String PREFS = "SIGAPREFS";

    Button btLogin;
    EditText txtUser;
    EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        btLogin = (Button) findViewById(R.id.btnLogin);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendGET();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        txtUser = (EditText) findViewById(R.id.txtUser);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
    }

    private void sendGET() throws IOException {

        createEnvironment();
        String userName= txtUser.getText().toString();
        String userPassword = txtPassword.getText().toString();
        Boolean successLogin = login(userName, userPassword);

        if(successLogin){
            openMain();
        }else{
            Context context = getApplicationContext();
            CharSequence text = "Usuário ou senha incorretos.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            Toast.makeText(context, text, duration).show();
        }
    }


    private boolean login(String username, String password) throws IOException{
        String cookieUrl = "http://siga.udesc.br/siga/inicial.do?evento=cookie";
        Connection.Response docCookie = Jsoup.connect(cookieUrl).ignoreHttpErrors(true).timeout(3000).execute();

        String url = "http://siga.udesc.br/siga/j_security_check?j_username=" + username + "&j_password=" + password;
        Connection.Response doc = Jsoup.connect(url).cookies(docCookie.cookies()).ignoreHttpErrors(true).timeout(3000).execute();

        if(doc.headers().containsKey("Cache-Control")){
            Elements elements = doc.parse().select("table.altMenu tr td:first-child table td:first-child");

            SharedPreferences settings = getSharedPreferences(PREFS, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("username",username);
            editor.putString("password",password);
            editor.commit();
            return true;
        }
        return false;
    }

    private String createSession() throws IOException {
        URL authUrl = new URL("http://siga.udesc.br/siga/inicial.do?evento=cookie");
        HttpURLConnection authCon = (HttpURLConnection) authUrl.openConnection();
        authCon.connect();
        StringBuilder sb = new StringBuilder();

        List<String> cookies = authCon.getHeaderFields().get("Set-Cookie");
        if (cookies != null) {
            for (String cookie : cookies) {
                if (sb.length() > 0) {
                    sb.append("; ");
                }

                String value = cookie.split(";")[0];
                sb.append(value);
            }
        }
        return sb.toString();
    }

    private void createEnvironment(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }

    private void openMain(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
