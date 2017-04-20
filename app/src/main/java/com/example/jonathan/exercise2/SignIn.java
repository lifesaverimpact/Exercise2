package com.example.jonathan.exercise2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import android.app.ProgressDialog;



import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;



public class SignIn extends AppCompatActivity {

    EditText input_ID, input_PW;
    private int byGetOrPost = 1;


    SharedPreferences sessionManager; //= getApplicationContext().getSharedPreferences("sessionID",0); // 여기선 선언만해주어야한다. getApplicationContext() 가 먹히질 않는다 여기서.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        input_ID = (EditText) findViewById(R.id.IDinput);
        input_PW = (EditText) findViewById(R.id.passwordInput);


    }

    // 이 Login function은 xml 파일에 onClick 으로 지정되어있다. 헷갈리지말자.
    public void Login(View view){
        String name = input_ID.getText().toString();
        //String address = editTextAdd.getText().toString();

        loginTask(name);


    }


    private void loginTask(final String name){

        final String nameToPass = name;


        class InsertData extends AsyncTask<String, Void, String>{
            ProgressDialog loading;

            // TODO chris what is this for?
            String sessionID = "";


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SignIn.this, "Please Wait", null, true, true);
            }


            @Override
            protected void onPostExecute(String s) {

                // TODO: 4/9/2017 토스트 메시지가 User Found 나 No Such User Found 이어야하는데 왜 s 는 그냥 blank 인지.. --> 해결. break; 를 없애라.
                super.onPostExecute(s);
                loading.dismiss();

                if(s.equalsIgnoreCase("User Found")){
                    Intent i = new Intent(SignIn.this, Search.class);
                    i.putExtra("loggedInName", nameToPass);

                    // TODO shared preference set
                    sessionManager = getApplicationContext().getSharedPreferences("sessionID", 0);      // sessionID 는 그야말로 세어드프리퍼런스의 이름이다. 이 이름을 사용하는 한 계속 값을 가져올 수 있다.
                    Editor editor = sessionManager.edit();      // editor 가 필수로 필요하다. 값을 집어넣고 커밋하려면.
                    editor.putString("key_value", nameToPass);  // key_value 는 내가 지정할 수 있는 영역. sessionID 라는 쉐어프리퍼러 에서 하나의 해쉬값을 가진다고 보인다.
                    editor.commit();

                    startActivity(i);

                }
                else{
                    Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String name = (String)params[0];
                    //String address = (String)params[1];

                    String link="http://people.aero.und.edu/~clee/457/2/login.php";
                    String data  = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                    //data += "&" + URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        //break;   // 이것이 나를 굉장히 힘들게 만들었따.........
                    }
                    //TODO check actual way to get cookie
                    sessionID = conn.getHeaderField("Set-Cookie");

                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }

        }

        InsertData task = new InsertData();
        task.execute(name);
    }



    public void onClick_signUp (View v){
        Intent intent_toSignUp = new Intent(getApplicationContext(), SignUp.class);
        startActivity(intent_toSignUp);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        Log.d("test", "onPrepareOptionsMenu - 옵션메뉴가 " +
//                "화면에 보여질때 마다 호출됨");
        //if(bLog){ // 로그인 한 상태: 로그인은 안보이게, 로그아웃은 보이게
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setEnabled(false);
//        }else{ // 로그 아웃 한 상태 : 로그인 보이게, 로그아웃은 안보이게
//            menu.getItem(0).setEnabled(false);
//            menu.getItem(1).setEnabled(true);
//        }

        //bLog = !bLog;   // 값을 반대로 바꿈

        return super.onPrepareOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.myAccount){
            Toast.makeText(getApplicationContext(), "My Account!", Toast.LENGTH_SHORT).show();
            return true;

        }
        else if(id == R.id.logout){
            Toast.makeText(getApplicationContext(), "Log Out!", Toast.LENGTH_SHORT).show();
//            SharedPreferences.Editor editor = sessionManager.edit();
//            editor.remove("key_value");
//            editor.clear();
//            Intent i = new Intent(Search.this, SignIn.class);
//            startActivity(i);

        }

        return super.onOptionsItemSelected(item);
    }
}
