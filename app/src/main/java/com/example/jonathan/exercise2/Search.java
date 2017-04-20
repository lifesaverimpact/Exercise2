package com.example.jonathan.exercise2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
import android.widget.Toast;

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
import java.util.List;


public class Search extends AppCompatActivity {

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


    String myJSON_gitt;
    JSONArray info = null;
    ArrayList<HashMap<String, String>> infoList;


    TextView testy;

    String sessionID;

    String customerID;
    String customerTotal;

    SearchAdapter myAdapter;


    String keyword;


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

        infoList = new ArrayList<HashMap<String, String>>();

        testy = (TextView) findViewById(R.id.testTextView);

        //myAdapter = new SearchAdapter(this, )



        try{

            sessionManager = getApplicationContext().getSharedPreferences("sessionID", 0);
            sessionID = sessionManager.getString("key_value", null);

            data = getIntent();
            //checkName.setText(data.getStringExtra("loggedInName") + "님이(가) 로그인하셨습니다.");
            checkName.setText( "Welcome, " + sessionID + "!");  // 이거 야매로 그냥 username 만 받아온건데.. cookie-set 이거 알아봐야한다. 크리스한테 물어보자.
        }catch(Exception e){
            e.printStackTrace();
            checkName.setText("Please log in again.");
        }

        getIDAndTotal();
    }

    void getIDAndTotal(){
        getIDAndTotalTask giatt = new getIDAndTotalTask();
        giatt.execute(sessionID);
    }

    private class getIDAndTotalTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            String customerName = params[0];
            String uri = "http://people.aero.und.edu/~clee/457/2/getIDAndTotal.php";

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
            myJSON_gitt=result;
            getIDAndTotalFinal();

        }
    }

    protected void getIDAndTotalFinal(){
        try{
            JSONObject jsonObj_gitt = new JSONObject(myJSON_gitt);
            info = jsonObj_gitt.getJSONArray(TAG_RESULTS);

            JSONObject j = info.getJSONObject(0);
            customerID = j.getString("ID");
            customerTotal = j.getString("total");

//            HashMap<String, String> infoHashMap = new HashMap<String, String>;
//
//            infoHashMap.put("ID", ID);
//            infoHashMap.put("total", total);
//
//            infoList.add(infoHashMap);


        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    // 이 search function은 xml 파일에 onClick 으로 지정되어있다. 헷갈리지말자.
    public void Search(View view){

        keyword = input_search.getText().toString();
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

                HashMap<String,String> booksHashMap = new HashMap<String,String>();

                booksHashMap.put(TAG_TITLE,title);
                booksHashMap.put(TAG_ISBN,ISBN);
                booksHashMap.put(TAG_PRICE,price);

                bookList.add(booksHashMap);
            }

            String testyString = String.valueOf(bookList.size());
            testy.setText("We found " + testyString + " book(s)");

            /*
            ListAdapter adapter = new SimpleAdapter(
                    Search.this, bookList, R.layout.list_item,
                    //new String[]{TAG_TITLE,TAG_ISBN,TAG_PRICE},
                    new String[]{TAG_TITLE},
                    //new int[]{R.id.title, R.id.ISBN, R.id.price}
                    new int[]{R.id.title}
            );
            */

            //SearchAdapter myAdapter = new SearchAdapter(Search.this, bookList);
            myAdapter = new SearchAdapter(Search.this, bookList);
            list.setAdapter(myAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public void goPurchase(View view){

        //SearchAdapter myAdapter = new SearchAdapter(Search.this, bookList);
        goPurchase(myAdapter);

    }

    void goPurchase(SearchAdapter paramMyAdapter){
        //Toast.makeText(getApplicationContext(), sessionID, Toast.LENGTH_SHORT).show();
        //SearchAdapter myAdapter = new SearchAdapter(Search.this, bookList);
        SearchAdapter myAdapter = paramMyAdapter;
        if(myAdapter != null){

            for(int i = 0; i < myAdapter.getCount(); i++ ){
                if(myAdapter.getBookChecked(i)){

                    HashMap<String, String> tempBook = (HashMap<String, String>) myAdapter.getItem(i);
                    JSONObject jsonBook = new JSONObject();
                    try{
                        jsonBook.put(TAG_ISBN, tempBook.get(TAG_ISBN).toString());
                        jsonBook.put("quantity", myAdapter.getBookQty(i));
                        //JSONArray purchaseList = new JSONArray();g
                        //purchaseList.put(jsonBook);



//                    // 여기에 uncheck 를 넣어보려한다.
//                    ListView lv = (ListView) findViewById(R.id.listView);
//                    CheckBox cb;
//                    cb = (CheckBox)lv.getChildAt(i).findViewById(R.id.checkBox);
//                    cb.setChecked(false);
//                    myAdapter.notifyDataSetChanged();


                        purchaseTask postPurchase = new purchaseTask();
                        postPurchase.execute(jsonBook);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }

        }

    }

    private class purchaseTask extends AsyncTask<JSONObject, Void, Boolean> {

//        JSONObject jsonBook;
//        JSONObject response;
//
//
//        public purchaseTask(JSONObject jsonBook){
//            this.jsonBook = jsonBook;
//        }
//
//        public purchaseTask() {
//
//        }

        @Override
        protected Boolean doInBackground(JSONObject... params) {

            JSONObject jsonBook = params[0];

            if(jsonBook.length() == 0){
                return false;
            }
            try{
                HashMap<String, String> dataToPHP = new HashMap<>();
                dataToPHP.put("act", "Purchase");
                dataToPHP.put("varname", "0");
                dataToPHP.put("quantity", jsonBook.getString("quantity"));


                String uri = "http://people.aero.und.edu/~clee/457/2/purchase.php";

                BufferedReader bufferedReader = null;


                String data  = URLEncoder.encode("customerName", "UTF-8") + "=" + URLEncoder.encode(sessionID, "UTF-8") + "&";
                data += URLEncoder.encode("act", "UTF-8") + "=" + URLEncoder.encode("Purchase", "UTF-8") + "&";
                data += URLEncoder.encode("varname", "UTF-8") + "=" + URLEncoder.encode("0", "UTF-8") + "&";
                data += URLEncoder.encode("quantity", "UTF-8") + "=" + URLEncoder.encode(jsonBook.getString("quantity"), "UTF-8") + "&";
                data += URLEncoder.encode("ISBN", "UTF-8") + "=" + URLEncoder.encode(jsonBook.getString("ISBN"), "UTF-8");

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


                return true;

            }
            catch(Exception e){

                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean success){
            if(success){
//                SearchAdapter adapter = myAdapter;
//                ListView lv = (ListView) findViewById(R.id.listView);
//
//
//                CheckBox cb;
//
//                for(int i=0; i<lv.getChildCount();i++)
//                {
//                    cb = (CheckBox)lv.getChildAt(i).findViewById(R.id.checkBox);
//                    cb.setChecked(false);
//                }
//                adapter.notifyDataSetChanged();

                SearchAdapter adapter = myAdapter;
                myAdapter.clearing();
//                ViewGroup vg = (ViewGroup) findViewById(R.id.activity_search);
//                removeAllChecks(vg);
//                myAdapter.notifyDataSetChanged();

                // 여기서 다시 한 번 searchTask를 불러오는 이유는, checkbox들과 edittext들을 초기화 하기 위해서 이다.
                searchTask search = new searchTask();
                search.execute(keyword);
                Toast.makeText(getApplicationContext(), "Successfully purchased the book(s)", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Sorry, couldn't purchase the book(s)", Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.myAccount){

            Intent i = new Intent(Search.this, Account.class);
            i.putExtra("customerID", customerID);
            i.putExtra("customerTotal", customerTotal);
            startActivity(i);

        }
        else if(id == R.id.logout){

            SharedPreferences.Editor editor = sessionManager.edit();
            editor.remove("key_value");
            editor.clear();

            Toast.makeText(getApplicationContext(), "Log out", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Search.this, SignIn.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }


//    private void removeAllChecks(ViewGroup vg) {
//        View v = null;
//        for(int i = 0; i < vg.getChildCount(); i++){
//            try {
//                v = vg.getChildAt(i);
//                ((CheckBox)v).setChecked(false);
//            }
//            catch(Exception e1){ //if not checkBox, null View, etc
//                try {
//                    removeAllChecks((ViewGroup)v);
//                }
//                catch(Exception e2){ //v is not a view group
//                    continue;
//                }
//            }
//        }
//
//    }

    /*
    private class myListAdapter extends SimpleAdapter {

        private Context myContext;
        //ArrayList<HashMap<String, String>> bookList;
        private List<HashMap<String, String>> myItems;
        private int myResource; // Layout ID


        private myListAdapter(Context context, ArrayList<HashMap<String, String>> paramBookList, int paramResource, String[] from, int[] to){
            super(context, paramBookList, paramResource, from, to);
            myContext = context;
            myItems = paramBookList;
            myResource = paramResource;

        }

        @Override
        public int getCount() {
            return myItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
    */

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