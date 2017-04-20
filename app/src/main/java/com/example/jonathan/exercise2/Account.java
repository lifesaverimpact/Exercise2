package com.example.jonathan.exercise2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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

public class Account extends AppCompatActivity {

    TextView customerID;
    TextView customerName;
    TextView totalExpense;

    SharedPreferences sessionManager;
    String sessionID;

    Intent data;
    String customerID_str;
    String customerTotal_str;

    String myJSON;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_TITLE = "title";
    private static final String TAG_QUANTITY = "quantity";
    JSONArray books = null;
    ArrayList<HashMap<String, String>> bookList;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        data = getIntent();
        customerID_str = data.getStringExtra("customerID");
        customerTotal_str = data.getStringExtra("customerTotal");

        bookList = new ArrayList<HashMap<String, String>>();
        list = (ListView) findViewById(R.id.listViewForMyAccount);

        customerID = (TextView) findViewById(R.id.customerID);
        customerName = (TextView) findViewById(R.id.customerName);
        totalExpense = (TextView) findViewById(R.id.totalExpense);

        sessionManager = getApplicationContext().getSharedPreferences("sessionID", 0);
        sessionID = sessionManager.getString("key_value", null);

        customerID.setText("ID: " + customerID_str);
        customerName.setText("Name: " + sessionID);
        totalExpense.setText("Total: $" + customerTotal_str);


        bringInfo();


    }

    private void bringInfo(){
        bringInfoTask bit = new bringInfoTask();
        bit.execute(sessionID);
    }

    private class bringInfoTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            String customerName = params[0];
            String uri = "http://people.aero.und.edu/~clee/457/2/account.php";

            BufferedReader bufferedReader = null;
            try {

                String data  = URLEncoder.encode("customerName", "UTF-8") + "=" + URLEncoder.encode(customerName, "UTF-8");

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
            //bookList.clear();

            for(int i=0;i<books.length();i++){
                JSONObject c = books.getJSONObject(i);
                String IDFromPHP = c.getString("ID");
                String quantityFromPHP = c.getString(TAG_QUANTITY);
                String titleFromPHP = c.getString(TAG_TITLE);
                String totalFromPHP = c.getString("total");

//                customerID.setText("ID: " + ID);
                totalExpense.setText("Total: $" + totalFromPHP);

                HashMap<String,String> booksHashMap = new HashMap<String,String>();

                booksHashMap.put(TAG_TITLE,titleFromPHP);
                booksHashMap.put(TAG_QUANTITY,quantityFromPHP);

                bookList.add(booksHashMap);
            }


//            String testyString = String.valueOf(bookList.size());
//            testy.setText("Total number of books found = " + testyString);


            ListAdapter adapter = new SimpleAdapter(
                    Account.this, bookList, R.layout.list_item_account,
                    //new String[]{TAG_TITLE,TAG_ISBN,TAG_PRICE},
                    new String[]{TAG_TITLE,TAG_QUANTITY},
                    //new int[]{R.id.title, R.id.ISBN, R.id.price}
                    new int[]{R.id.title_account,R.id.quantity_account}
            );


            list.setAdapter(adapter);
//            SearchAdapter myAdapter = new SearchAdapter(Search.this, bookList);
//            list.setAdapter(myAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
