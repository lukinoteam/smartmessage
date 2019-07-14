package com.example.scratchmessage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapter.ListRequestAdapter;
import model.Relation;

public class FriendRequestActivity extends AppCompatActivity {

    TextView tvNoRequest;
    RecyclerView rvRequest;
    RecyclerView.LayoutManager layoutManager;

    ListRequestAdapter listRequestAdapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    ArrayList<Relation> relations;
    String userNameSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        tvNoRequest = findViewById(R.id.tvNoRequest);
        rvRequest = findViewById(R.id.rvRequest);

        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREFERENCES_ACCOUNT_SESSION", Context.MODE_PRIVATE);
        userNameSession = sharedPreferences.getString("userNameSession", "");

        relations = new ArrayList<>();

        rvRequest.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        rvRequest.setLayoutManager(layoutManager);

        listRequestAdapter = new ListRequestAdapter(relations, FriendRequestActivity.this);
        rvRequest.setAdapter(listRequestAdapter);

        ref.child("friend-list").child(userNameSession).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                relations = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Relation relation = data.getValue(Relation.class);

                    if (relation != null && relation.getStatus() == 0) {
                        relations.add(relation);
                    }
                }

                if (relations.size() > 0){
                    tvNoRequest.setText("");
                }
                else {
                    tvNoRequest.setText("You don't have any request.");
                }
                listRequestAdapter = new ListRequestAdapter(relations, FriendRequestActivity.this);
                rvRequest.setAdapter(listRequestAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
