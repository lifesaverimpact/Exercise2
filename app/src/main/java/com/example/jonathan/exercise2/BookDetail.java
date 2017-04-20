package com.example.jonathan.exercise2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class BookDetail extends AppCompatActivity {

    TextView output_title;
    TextView output_ISBN;
    TextView output_price;

    Intent data;

    SharedPreferences sessionManager;
    String sessionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        output_title = (TextView) findViewById(R.id.detail_title);
        output_ISBN = (TextView) findViewById(R.id.detail_ISBN);
        output_price = (TextView) findViewById(R.id.detail_price);

        try{
            sessionManager = getApplicationContext().getSharedPreferences("sessionID", 0);
            sessionID = sessionManager.getString("key_value", null);

            data = getIntent();

            String myTitle = data.getExtras().getString("title");
            String myISBN = data.getExtras().getString("ISBN");
            String myPrice = data.getExtras().getString("price");

            output_title.setText(myTitle);
            output_ISBN.setText(myISBN);
            output_price.setText(myPrice);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
