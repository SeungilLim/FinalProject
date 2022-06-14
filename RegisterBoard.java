package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegisterBoard extends AppCompatActivity {

    EditText[] editTexts = new EditText[1];
    Button button1, button2;
    String boardId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_board);
        editTexts[0] = findViewById(R.id.editTextTextPersonName);
        editTexts[1] = findViewById(R.id.editTextTextPersonName2);
        button1 = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        boardId = getIntent().getStringExtra("userid");

        button1.setOnClickListener(new View.OnClickListener() {
            List<Board> list = new ArrayList<Board>();
            @Override
            public void onClick(View v) {
                try {
                    if (editTexts[0].getText().toString().isEmpty() || editTexts[1].getText().toString().isEmpty()) {
                        Toast.makeText(RegisterBoard.this,
                                "제목과 내용은 필수입니다.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String[] params = new String[3];
                        params[0] = editTexts[0].getText().toString().trim();
                        params[1] = editTexts[1].getText().toString().trim();
                        params[2] = null; //사용자의 정보 등록 추후 추가
                        params[3] = getTime();//글 작성 시간

                        new Task().execute(params);

                        setResult(RESULT_OK);finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = dateFormat.format(date);

        return getTime;
    }

    public class Task extends AsyncTask<String, Void, String> {

        String sendMsg="", receiveMsg = "0";
        String serverIp = "https://projectDB-openlab05-26.toast.paas-ta.com/project/"; // 연결할 jsp주소

        @Override
        protected String doInBackground(String ... params) {
            try {
                String str;

                serverIp += (params[0]+"?title="+params[1]+"&content="+params[2]+"&author="+params[3]+"&createdAt="+params[4]);

                URL url = new URL(serverIp);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("PUT");//DB 등록시 PUT? 수정시 POST?
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                osw.write(sendMsg);
                osw.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return receiveMsg;

        }
    }
}