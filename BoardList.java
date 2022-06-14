package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class BoardList extends AppCompatActivity {

    ListView listView;
    Button regbutton;
    ArrayList<String> titleList = new ArrayList<>();
    ArrayList<Integer> boardIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list);
        listView = findViewById(R.id.listView);
        regbutton = findViewById(R.id.reg_button);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BoardList.this, BoardDetail.class);
                intent.putExtra("boardId", boardIdList);
                startActivity(intent);

            }
        });


        regbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BoardList.this, RegisterBoard.class);
                intent.putExtra("boardId", boardIdList);
                startActivity(intent);
            }
        });
    }


    // onResume() 은 해당 액티비티가 화면에 나타날 때 호출됨
    @Override
    protected void onResume() {
        super.onResume();
// 해당 액티비티가 활성화 될 때, 게시물 리스트를 불러오는 함수를 호출
        GetBoardList getBoardList = new GetBoardList();
        getBoardList.execute();
    }


    class GetBoardList extends AsyncTask<Void, Void, String> {
        String sendMsg="", receiveMsg;
        String serverIp = "https://ehtest11.toast.paas-ta.com/user/all"; // 연결할 jsp주소
        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            titleList.clear();
            boardIdList.clear();

            try {

                JSONArray jsonArray = new JSONArray(result);

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String title = jsonObject.optString("title");
                    int boardId = jsonObject.optInt("boardId");

                    titleList.add(title);
                    boardIdList.add(boardId);

                }

                ArrayAdapter arrayAdapter = new ArrayAdapter<String>(BoardList.this, android.R.layout.simple_list_item_1, titleList);
                listView.setAdapter(arrayAdapter);

                arrayAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        @Override
        protected String doInBackground(Void ... voids) {
            try {
                String str;
                URL url = new URL(serverIp);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if(conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                } else {
                    Log.i("통신 결과", conn.getResponseCode()+"에러");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return receiveMsg;

        }
    }
}