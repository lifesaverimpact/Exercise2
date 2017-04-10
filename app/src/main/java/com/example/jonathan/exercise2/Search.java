package com.example.jonathan.exercise2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

// http://webnautes.tistory.com/829
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;


public class Search extends Activity {

    // SharedPreference 가져오기
    SharedPreferences sessionManager; // = getApplicationContext().getSharedPreferences("sessionID",0);


    TextView checkName;
    Intent data;

    // 여기서 title 은 그냥 변수일 뿐임
    EditText input_search;

    // http://webnautes.tistory.com/829
    String myJSON;
    private static final String TAG_RESULTS="result";
    private static final String TAG_TITLE = "title";
    private static final String TAG_ISBN = "ISBN";
    private static final String TAG_PRICE ="price";
    JSONArray books = null;
    ArrayList<HashMap<String, String>> bookList;
    ListView list;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        checkName = (TextView)findViewById(R.id.announce);

        // input_search에 searchInput value를 연결해줌
        input_search = (EditText)findViewById(R.id.searchInput);


        list = (ListView) findViewById(R.id.listView);
        bookList = new ArrayList<HashMap<String,String>>();
        //getData("http://people.aero.und.edu/~clee/457/2/search.php");

        try{

            sessionManager = getApplicationContext().getSharedPreferences("sessionID", 0);
            String sessionID = sessionManager.getString("key_value", null);

            data = getIntent();
            //checkName.setText(data.getStringExtra("loggedInName") + "님이(가) 로그인하셨습니다.");
            checkName.setText( sessionID + "님이(가) 로그인하셨습니다.");  // 이거 야매로 그냥 username 만 받아온건데.. cookie-set 이거 알아봐야한다. 크리스한테 물어보자.
        }catch(Exception e){
            e.printStackTrace();
            checkName.setText("다시로그인해주세요.");
        }
    }

    // 이 search function은 xml 파일에 onClick 으로 지정되어있다. 헷갈리지말자.
    public void Search(View view){

        String keyword = input_search.getText().toString();
        searchTask search = new searchTask();
        search.execute(keyword);
    }

    private class searchTask extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... params) {

                String keyword = params[0];
                String uri = "http://people.aero.und.edu/~clee/457/2/search.php";

                BufferedReader bufferedReader = null;
                try {

                    String data  = URLEncoder.encode("keyword", "UTF-8") + "=" + URLEncoder.encode(keyword, "UTF-8");

                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();


                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();


                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }

                    return sb.toString().trim();

                }catch(Exception e){
                    return null;
                }



            }

            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                showList();
            }
    }





    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            books = jsonObj.getJSONArray(TAG_RESULTS);
            //chris
            bookList.clear();
            for(int i=0;i<books.length();i++){
                JSONObject c = books.getJSONObject(i);
                String title = c.getString(TAG_TITLE);
                String ISBN = c.getString(TAG_ISBN);
                String price = c.getString(TAG_PRICE);

                HashMap<String,String> books = new HashMap<String,String>();

                books.put(TAG_TITLE,title);
                books.put(TAG_ISBN,ISBN);
                books.put(TAG_PRICE,price);

                bookList.add(books);
            }

            ListAdapter adapter = new SimpleAdapter(
                    Search.this, bookList, R.layout.list_item,
                    new String[]{TAG_TITLE,TAG_ISBN,TAG_PRICE},
                    new int[]{R.id.title, R.id.ISBN, R.id.price}
            );

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

//    public void getData(String url){
//        class GetDataJSON extends AsyncTask<String, Void, String>{
//
//            @Override
//            protected String doInBackground(String... params) {
//
//                String uri = params[0];
//
//                BufferedReader bufferedReader = null;
//                try {
//                    URL url = new URL(uri);
//                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                    StringBuilder sb = new StringBuilder();
//
//                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
//
//                    String json;
//                    while((json = bufferedReader.readLine())!= null){
//                        sb.append(json+"\n");
//                    }
//
//                    return sb.toString().trim();
//
//                }catch(Exception e){
//                    return null;
//                }
//
//
//
//            }
//
//            @Override
//            protected void onPostExecute(String result){
//                myJSON=result;
//                showList();
//            }
//        }
//        GetDataJSON g = new GetDataJSON();
//        g.execute(url);
//    }




}