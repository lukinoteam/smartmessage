package com.example.scratchmessage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapter.ListContactAdapter;
import adapter.ListFindResultAdapter;
import model.Contact;
import model.User;

public class FindFriendActivity extends AppCompatActivity {

    EditText edtSearch;
    RecyclerView rvFindResult;
    RecyclerView.LayoutManager layoutManager;
    ListFindResultAdapter listFindResultAdapter;

    ArrayList<User> users;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();

    String userNameSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        edtSearch = findViewById(R.id.edtSearch);
        rvFindResult = findViewById(R.id.rvFindResult);

        users = new ArrayList<>();

        rvFindResult.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        rvFindResult.setLayoutManager(layoutManager);

        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREFERENCES_ACCOUNT_SESSION", Context.MODE_PRIVATE);
        userNameSession = sharedPreferences.getString("userNameSession", "");

        listFindResultAdapter = new ListFindResultAdapter(users, FindFriendActivity.this);
        rvFindResult.setAdapter(listFindResultAdapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                final String searchKeyWord = String.valueOf(edtSearch.getText());
                users = new ArrayList<>();
                if (searchKeyWord.length() != 0) {
                    ref.child("account").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            users = new ArrayList<>();
                            for (DataSnapshot data: dataSnapshot.child("google").getChildren()) {
                                User user = data.getValue(User.class);
                                if (user != null) {
                                    if ((userNameSession.compareTo(user.getUserName()) != 0) && (user.getUserName().substring(0, searchKeyWord.length()).compareTo(searchKeyWord) == 0)){
                                        users.add(user);
                                    }
                                }
                            }
                            listFindResultAdapter = new ListFindResultAdapter(users, FindFriendActivity.this);
                            rvFindResult.setAdapter(listFindResultAdapter);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    listFindResultAdapter = new ListFindResultAdapter(users, FindFriendActivity.this);
                    rvFindResult.setAdapter(listFindResultAdapter);
                }

            }
        });
    }
}
