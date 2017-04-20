package com.example.jonathan.exercise2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.ArrayList;

public class SearchAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> myItems;
    private LayoutInflater myInflater;
    private SparseBooleanArray bookChecked;
    private SparseArray<Integer> bookQty;

    public SearchAdapter(Context paramContext, ArrayList<HashMap<String, String>> paramBookList){
        super(paramContext, R.layout.list_item);
        context = paramContext;
        myItems = paramBookList;
        bookChecked = new SparseBooleanArray();
        bookQty = new SparseArray<>();
        myInflater = LayoutInflater.from(paramContext);
    }

    @Override
    public int getCount(){
        return myItems.size();
    }

    @Override
    public Object getItem(int position){
        return myItems.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getPosition(Object item){
        return myItems.indexOf(item);
    }


    public boolean getBookChecked(int position){
        return bookChecked.get(position);
    }

    public int getBookQty(int position){
        return bookQty.get(position);
    }


    @Override
    public int getViewTypeCount(){
        return 1;
    }

    @Override
    public int getItemViewType(int position){
        return 1;
    }

    //http://theeye.pe.kr/archives/1253
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        final ViewHolder myViewHolder;

        if(convertView == null){
            myViewHolder = new ViewHolder();

            convertView = myInflater.inflate(R.layout.list_item, parent, false);
            myViewHolder.titleView = (TextView) convertView.findViewById(R.id.title);
            myViewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            myViewHolder.qtyView = (EditText) convertView.findViewById(R.id.quantity);
            //myViewHolder.purchaseButton = (Button) convertView.findViewById(R.id.purchaseButton);
            myViewHolder.wholeView = (LinearLayout) convertView.findViewById(R.id.wholeList);


            convertView.setTag(myViewHolder);
        }
        else{
            myViewHolder = (ViewHolder) convertView.getTag();
        }


        myViewHolder.titleView.setText(myItems.get(position).get("title").toString());
        myViewHolder.position = position;


        myViewHolder.titleView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You clicked the title, man!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, BookDetail.class);
                intent.putExtra("title", myItems.get(position).get("title").toString());
                intent.putExtra("ISBN", myItems.get(position).get("ISBN").toString());
                intent.putExtra("price", myItems.get(position).get("price").toString());

                context.startActivity(intent);
            }
        });

        
        myViewHolder.qtyView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() >= 1){
                    bookQty.put(position, Integer.parseInt(myViewHolder.qtyView.getText().toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                return;
            }
        });



        myViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bookChecked.put(position, myViewHolder.checkBox.isChecked());
            }
        });


        return convertView;
    }

    private class ViewHolder{
        LinearLayout wholeView;
        TextView titleView;
        CheckBox checkBox;
        EditText qtyView;
        //Button purchaseButton;
        int position;

    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }

    public void update(ArrayList<HashMap<String, String>> paramBookList){
        myItems = paramBookList;
        notifyDataSetChanged();
    }

    public void clearing(){
        //for(int i = 0; i<getCount(); i++){
            bookQty.clear();
            bookChecked.clear();
        //}
        notifyDataSetChanged();

    }


}
