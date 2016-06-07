package com.example.cari.universit;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by cari on 23/05/16.
 */
public class LoginActivity extends AppCompatActivity {
    Button btLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        btLogin = (Button) findViewById(R.id.login);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LoginActivity.sendGET();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private static void sendGET() throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        // your first request that does the authentication
        URL authUrl = new URL("http://siga.udesc.br/siga/inicial.do?evento=cookie");
        HttpURLConnection authCon = (HttpURLConnection) authUrl.openConnection();
        authCon.connect();

// temporary to build request cookie header
        StringBuilder sb = new StringBuilder();

// find the cookies in the response header from the first request
        List<String> cookies = authCon.getHeaderFields().get("Set-Cookie");
        if (cookies != null) {
            for (String cookie : cookies) {
                if (sb.length() > 0) {
                    sb.append("; ");
                }

                // only want the first part of the cookie header that has the value
                String value = cookie.split(";")[0];
                sb.append(value);
            }
        }

// build request cookie header to send on all subsequent requests
        String cookieHeader = sb.toString();

// with the cookie header your session should be preserved
        URL regUrl = new URL("http://siga.udesc.br/siga/j_security_check?j_username=08042123922&j_password=88636169");
        HttpURLConnection regCon = (HttpURLConnection) regUrl.openConnection();
        regCon.setRequestProperty("Cookie", cookieHeader);
        regCon.connect();
        InputStream ins = regCon.getInputStream();
        InputStreamReader isr = new InputStreamReader(ins);
        BufferedReader in = new BufferedReader(isr);

        String inputLine;

        while ((inputLine = in.readLine()) != null)
        {
            System.out.println(inputLine);
        }

        in.close();
    }

    private void openMain(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
