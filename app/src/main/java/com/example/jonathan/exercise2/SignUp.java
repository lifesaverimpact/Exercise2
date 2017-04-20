package com.example.jonathan.exercise2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


// http://webnautes.tistory.com/828
public class SignUp extends AppCompatActivity {

    private EditText editTextName;
    //private EditText editTextAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextName = (EditText) findViewById(R.id.nameInput);
        //editTextAdd = (EditText) findViewById(R.id.address);


    }

    // 이 insert function은 xml 파일에 onClick 으로 지정되어있다. 헷갈리지말자.
    public void insert(View view){
        String name = editTextName.getText().toString();
        //String address = editTextAdd.getText().toString();

        insertToDatabase(name);


    }

    private void insertToDatabase(String name){

        class InsertData extends AsyncTask<String, Void, String>{
            ProgressDialog loading;



            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SignUp.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {

                // TODO: 4/9/2017 토스트 메시지가 success 나 failure 여야하는데 왜 s 는 그냥 빈칸인지..
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String name = (String)params[0];
                    //String address = (String)params[1];

                    String link="http://people.aero.und.edu/~clee/457/2/register.php";
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
                        //break;
                    }
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




    public void onClick_back (View v){
        Intent intent_toSignIn = new Intent(getApplicationContext(), SignIn.class);
        startActivity(intent_toSignIn);
    }

//    public void onClick_signUp (View v){
//       Intent intent_toRegister2 = new Intent(getApplicationContext(), Register2.class);
//       startActivity(intent_toRegister2);
//    }

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
