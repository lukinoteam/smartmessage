package com.example.scratchmessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import adapter.adapterinterface.ClickListener;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import adapter.ListContactAdapter;
import database.ChatLogSQLHelper;
import database.ContactSQLHelper;
import model.Contact;

public class HomeActivity extends AppCompatActivity implements ClickListener {

    ImageView imgAvatar;
    TextView tvUsername;
    ImageButton btnSetting;
    EditText edtSearch;
    RecyclerView rvContact;
    ImageButton btnAddFriend;
    ImageButton btnFriendRequests;

    RecyclerView.Adapter listContactAdapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<Contact> contacts;

    Contact contactSession;

    ContactSQLHelper contactSQLHelper;

    GoogleSignInOptions gso;
    GoogleApiClient mGoogleApiClient;

    String userNameSession;
    String avaUriSession;
    boolean onlineSession;

    ContactReceiver contactReceiver;
    IntentFilter contactFilter;

    class ContactReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            contacts = contactSQLHelper.getAll();
            listContactAdapter = new ListContactAdapter(contacts);
            ((ListContactAdapter) listContactAdapter).setClickListener(HomeActivity.this);
            rvContact.setAdapter(listContactAdapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imgAvatar = findViewById(R.id.imgAvatar);
        tvUsername = findViewById(R.id.tvUsername);
        btnSetting = findViewById(R.id.btnSetting);
        edtSearch = findViewById(R.id.edtSearch);
        rvContact = findViewById(R.id.rvContact);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        btnFriendRequests = findViewById(R.id.btnFriendRequests);

        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREFERENCES_ACCOUNT_SESSION", Context.MODE_PRIVATE);

        userNameSession = sharedPreferences.getString("userNameSession", "");
        avaUriSession = sharedPreferences.getString("avaUriSession", "");
        onlineSession = sharedPreferences.getBoolean("onlineSession", false);

        if (avaUriSession.compareTo("default") == 0) {
            imgAvatar.setImageResource(R.mipmap.default_avatar);
        } else {
            Uri mUri = Uri.parse(avaUriSession);
            Picasso.get().load(mUri).into(imgAvatar);
        }

        contactSQLHelper = new ContactSQLHelper(this);
        contactSession = new Contact(avaUriSession, userNameSession, "", onlineSession);

//        tvUsername.setText(userNameSession);

        contacts = contactSQLHelper.getAll();

        rvContact.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        rvContact.setLayoutManager(layoutManager);

        contactReceiver = new ContactReceiver();
        contactFilter = new IntentFilter("newContact");

        registerReceiver(contactReceiver, contactFilter);

        listContactAdapter = new ListContactAdapter(contacts);
        ((ListContactAdapter) listContactAdapter).setClickListener(HomeActivity.this);
        rvContact.setAdapter(listContactAdapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchKeyword = String.valueOf(edtSearch.getText());
                ArrayList<Contact> result = new ArrayList<>();
                if (TextUtils.isEmpty(searchKeyword)) {
                    listContactAdapter = new ListContactAdapter(contacts);
                    ((ListContactAdapter) listContactAdapter).setClickListener(HomeActivity.this);
                    rvContact.setAdapter(listContactAdapter);
                } else {
                    for (Contact c : contacts) {

                        int compareUsername = -1;
                        if (searchKeyword.length() <= c.getUserName().length()) {
                            compareUsername = searchKeyword.compareTo(c.getUserName().substring(0, searchKeyword.length()));
                        }

                        int compareNickname = -1;
                        if (searchKeyword.length() <= c.getNickName().length()) {
                            compareNickname = searchKeyword.compareTo(c.getNickName().substring(0, searchKeyword.length()));
                        }

                        if (compareNickname == 0 || compareUsername == 0) {
                            result.add(c);
                        }
                        listContactAdapter = new ListContactAdapter(result);
                        ((ListContactAdapter) listContactAdapter).setClickListener(HomeActivity.this);
                        rvContact.setAdapter(listContactAdapter);
                    }
                }
            }
        });

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, FindFriendActivity.class);
                startActivity(intent);
            }
        });

        btnFriendRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, FriendRequestActivity.class);
                startActivityForResult(intent, 3);
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NotNull Status status) {
                                ChatLogSQLHelper chatLogSQLHelper = new ChatLogSQLHelper(HomeActivity.this);
                                ContactSQLHelper contactSQLHelper = new ContactSQLHelper(HomeActivity.this);

                                chatLogSQLHelper.deleteTable();
                                contactSQLHelper.deleteTable();

                                SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREFERENCES_ACCOUNT_SESSION", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString("userNameSession", "");
                                editor.putString("emailSession", "");
                                editor.putString("idSession", "");
                                editor.apply();

                                Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
                                startActivity(intent);
                            }
                        });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(contactReceiver, contactFilter);
        listContactAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(contactReceiver);
    }

    @Override
    public void onParentClick(View view, int position) {
        Contact contact = contacts.get(position);
        Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
        intent.putExtra("idFriend", contact.getId());
        intent.putExtra("position", position);
        intent.putExtra("avaUriFriend", contact.getAvaUri());
        intent.putExtra("userNameFriend", contact.getUserName());
        intent.putExtra("nickNameFriend", contact.getNickName());
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case RESULT_OK:
                contacts = contactSQLHelper.getAll();
                listContactAdapter = new ListContactAdapter(contacts);
                ((ListContactAdapter) listContactAdapter).setClickListener(HomeActivity.this);
                rvContact.setAdapter(listContactAdapter);
        }
    }
}
