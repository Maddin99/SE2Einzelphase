package com.example.se2einzelphase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void send(View view){
        Handler handler = new Handler();
        new Thread(
            () -> {
                EditText matrikelEditText = findViewById(R.id.matrikelInput);
                String responseText = this.getStudentInfoFromServer(matrikelEditText.getText().toString());
                handler.post(
                    () -> {
                        TextView resultTextView = findViewById(R.id.resultText);
                        resultTextView.setText(responseText);
                    }
                );
            }
        ).start();
    }

    private String getStudentInfoFromServer(String matriculationNumber){
        String responseText = "";
        try {
            Socket socket = new Socket("se2-isys.aau.at", 53212);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeBytes(matriculationNumber);
            socket.shutdownOutput();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            responseText = in.readLine();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseText;
    }
}