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

    public void calculate(View view) {
        EditText matrikelEditText = findViewById(R.id.matrikelInput);
        String responseText = this.getPairsWithSameDivisor(matrikelEditText.getText().toString());
        TextView resultTextView = findViewById(R.id.resultText);
        resultTextView.setText(responseText);
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

    private String getPairsWithSameDivisor(String matriculationNumber){
        StringBuilder result = new StringBuilder();
        int[] arr = convertStringToIntArray(matriculationNumber);
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (shareDivisor(arr[i], arr[j])){
                    result.append("(").append(i).append(",").append(j).append(") ");
                }
            }
        }
        return result.toString();
    }

    private boolean shareDivisor(int i, int j){
        if (i <= 1 || j <= 1) {
            return false;
        }
        for (int k = 2; k <= Math.min(i, j); k++) {
            if (i % k == 0 && j % k == 0){
                return true;
            }
        }
        return false;
    }

    private int[] convertStringToIntArray(String s){
        char[] carr = s.toCharArray();
        int[] iarr = new int[carr.length];
        for (int i = 0; i < carr.length; i++){
            iarr[i] = Character.getNumericValue(carr[i]);
        }
        return iarr;
    }
}