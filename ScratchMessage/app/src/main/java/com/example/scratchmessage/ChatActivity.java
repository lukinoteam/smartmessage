package com.example.scratchmessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
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

import com.facebook.stetho.Stetho;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import adapter.ListMessageAdapter;
import adapter.adapterinterface.ClickListener;
import database.ChatLogSQLHelper;
import database.ContactSQLHelper;
import model.Contact;
import model.Message;

public class ChatActivity extends AppCompatActivity {

    ImageButton btnBack;
    ImageView imgAvatar;
    TextView tvContactName;
    ImageButton btnSetting;
    RecyclerView rvMessage;
    ImageButton btnSendMessage;
    ImageButton btnSendPicture;
    EditText edtMessage;

    RecyclerView.Adapter listMessageAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Message> messages;

    Contact contactSession;
    Contact contactFriend;
    ChatLogSQLHelper sqlHelper;
    ContactSQLHelper contactSQLHelper;

    MessageReceiverOnChildAdded messageReceiver;
    IntentFilter singleFilter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String userNameSession;
    String avaUriSession;
    boolean onlineSession;

    String idFriend;
    String avaUriFriend;
    String userNameFriend = "";
    String nickNameFriend;
    boolean onlineFriend;
    int position;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference uploadRef = storage.getReference("upload");

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference chatLogRef = database.getReference().child("chat-log");



    class MessageReceiverOnChildAdded extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Message m = intent.getParcelableExtra("newMessage");

            if (m.getFriend().compareTo(userNameFriend) == 0) {
                messages.add(m);
                listMessageAdapter.notifyItemInserted(messages.size() - 1);
                if (messages.size() > 0) {
                    rvMessage.smoothScrollToPosition(messages.size() - 1);
                }
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        messageReceiver = new MessageReceiverOnChildAdded();
        singleFilter = new IntentFilter("message");
        registerReceiver(messageReceiver, singleFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sqlHelper = new ChatLogSQLHelper(this);
        contactSQLHelper = new ContactSQLHelper(this);

        btnBack = findViewById(R.id.btnBack);
        imgAvatar = findViewById(R.id.imgAvatar);
        tvContactName = findViewById(R.id.tvContactName);
        btnSetting = findViewById(R.id.btnSetting);
        rvMessage = findViewById(R.id.rvMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        btnSendPicture = findViewById(R.id.btnSendPicture);
        edtMessage = findViewById(R.id.edtMessage);

        SharedPreferences userSession = getSharedPreferences("SHARED_PREFERENCES_ACCOUNT_SESSION", MODE_PRIVATE);
        userNameSession = userSession.getString("userNameSession", "");
        avaUriSession = userSession.getString("avaUriSession", "");
        onlineSession = userSession.getBoolean("onlineSession", false);

        final Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        idFriend = intent.getStringExtra("idFriend");
        avaUriFriend = intent.getStringExtra("avaUriFriend");
        userNameFriend = intent.getStringExtra("userNameFriend");
        nickNameFriend = intent.getStringExtra("nickNameFriend");
        onlineFriend = intent.getBooleanExtra("onlineFriend", false);

        if (avaUriFriend.compareTo("default") == 0) {
            imgAvatar.setImageResource(R.mipmap.default_avatar);
        } else {
            Picasso.get().load(avaUriFriend).into(imgAvatar);
        }

        sharedPreferences = getSharedPreferences("SHARED_PREFERENCES_DRAFT_MESSAGE", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String draft = sharedPreferences.getString(userNameFriend, "");
        if (!TextUtils.isEmpty(draft)) {
            edtMessage.setText(draft);
        }

        if (nickNameFriend.compareTo("") == 0) {
            tvContactName.setText(userNameFriend);
        } else {
            tvContactName.setText(nickNameFriend);
        }

        contactSession = new Contact(avaUriSession, userNameSession, "", onlineSession);
        contactFriend = new Contact(avaUriFriend, userNameFriend, nickNameFriend, onlineFriend);

        messages = sqlHelper.getAllByFriend(userNameFriend);
        Collections.sort(messages);

        rvMessage.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        rvMessage.setLayoutManager(layoutManager);

        listMessageAdapter = new ListMessageAdapter(messages, contactSession, contactFriend, ChatActivity.this);

        rvMessage.setAdapter(listMessageAdapter);
        if (messages.size() > 0) {
            rvMessage.smoothScrollToPosition(messages.size() - 1);
        }

        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String content = String.valueOf(edtMessage.getText());
                if (!TextUtils.isEmpty(content)) {
                    editor.putString(userNameFriend, content);
                    editor.apply();
                }
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = String.valueOf(edtMessage.getText());
                if (!TextUtils.isEmpty(content)) {
                    edtMessage.setText("");
                    editor.putString(userNameFriend, "");
                    editor.apply();

                    Message send = new Message(content, 0, userNameFriend, 0);
                    Message receive = new Message(content, 1, userNameSession, 0);

                    chatLogRef.child(userNameSession).child(send.getId()).setValue(send);
                    chatLogRef.child(userNameFriend).child(receive.getId()).setValue(receive);

                }
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingIntent = new Intent(ChatActivity.this, ContactSetting.class);
                settingIntent.putExtra("nickname", nickNameFriend);
                settingIntent.putExtra("avaUri", avaUriFriend);
                settingIntent.putExtra("username", userNameFriend);
                startActivityForResult(settingIntent, 1);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSendPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 2);//one can be replaced with any action code

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(messageReceiver);
    }

    @Override
    public void onBackPressed() {
        Intent changeIntent = new Intent();
        changeIntent.putExtra("nickNameFriend", nickNameFriend);
        changeIntent.putExtra("position", position);
        setResult(RESULT_OK, changeIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                switch (resultCode) {
                    case RESULT_OK:
                        String changedNickname = data.getStringExtra("changedNickname");
                        Contact contact = new Contact(idFriend, avaUriFriend, userNameFriend, changedNickname, onlineFriend);
                        contactSQLHelper.insert(contact);
                        if (changedNickname.compareTo("") == 0) {
                            tvContactName.setText(userNameFriend);
                        } else {
                            tvContactName.setText(changedNickname);
                            nickNameFriend = changedNickname;
                        }

                        Intent changeNickname = new Intent("changeNickname");
                        changeNickname.putExtra("nickname", changedNickname);
                        changeNickname.putExtra("username", userNameFriend);
                        changeNickname.putExtra("avauri", avaUriFriend);
                        changeNickname.putExtra("online", onlineFriend);
                        sendBroadcast(changeNickname);

                        break;
                }
                break;
            case 2:
                switch (resultCode) {
                    case RESULT_OK:
                        if (data != null) {
                            Uri file = data.getData();

                            final StorageReference fileRef = uploadRef.child(String.valueOf(UUID.randomUUID()));

                            fileRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String content = uri.toString();

                                            Message send = new Message(content, 0, userNameFriend, 1);
                                            Message receive = new Message(content, 1, userNameSession, 1);

                                            chatLogRef.child(userNameSession).child(send.getId()).setValue(send);
                                            chatLogRef.child(userNameFriend).child(receive.getId()).setValue(receive);
                                        }
                                    });
                                }
                            });
                        }
                        break;
                }
                break;
        }
    }
}
