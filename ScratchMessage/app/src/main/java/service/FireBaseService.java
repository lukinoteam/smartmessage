package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import database.ChatLogSQLHelper;
import database.ContactSQLHelper;
import model.Contact;
import model.Message;
import model.Relation;
import model.User;

public class FireBaseService extends Service {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference accountRef = database.getReference().child("account");
    DatabaseReference chatLogRef = database.getReference().child("chat-log");
    DatabaseReference friendListRef = database.getReference().child("friend-list");

    SharedPreferences accountPreferences;

    ChatLogSQLHelper chatlogSqlHelper;
    ContactSQLHelper contactSQLHelper;

    String userNameSession;

    class ChangeNicknameReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            userNameSession = accountPreferences.getString("userNameSession", "");

            String avaUri = intent.getStringExtra("avauri");
            String userName = intent.getStringExtra("username");
            String nickName = intent.getStringExtra("nickname");
            boolean online = intent.getBooleanExtra("online", false);

            Relation receive = new Relation(avaUri, userName, nickName, 2);

            if (userNameSession != null) {
                friendListRef.child(userNameSession).child(userName).setValue(receive);
            }
        }
    }

    class SignUpReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String id = accountPreferences.getString("idSession", "");
            String userName = accountPreferences.getString("userNameSession", "");
            String email = accountPreferences.getString("emailSession", "");
            String strUri = accountPreferences.getString("avaUriSession", "");

            User user = new User(id, userName, email, strUri);
            if (id != null) {
                accountRef.child("google").child(id).setValue(user);
            }
        }
    }

    class RetrieveDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            retrieveData();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        accountPreferences = getSharedPreferences("SHARED_PREFERENCES_ACCOUNT_SESSION", Context.MODE_PRIVATE);
        chatlogSqlHelper = new ChatLogSQLHelper(this);
        contactSQLHelper = new ContactSQLHelper(this);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ChangeNicknameReceiver changeNicknameReceiver = new ChangeNicknameReceiver();
        IntentFilter filter1 = new IntentFilter("changeNickname");
        registerReceiver(changeNicknameReceiver, filter1);

        SignUpReceiver signUpReceiver = new SignUpReceiver();
        IntentFilter filter2 = new IntentFilter("signUpGoogleAccount");
        registerReceiver(signUpReceiver, filter2);


        RetrieveDataReceiver dataReceiver = new RetrieveDataReceiver();
        IntentFilter filter3 = new IntentFilter("retrieveData");
        registerReceiver(dataReceiver, filter3);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void retrieveData(){
        userNameSession = accountPreferences.getString("userNameSession", "");

        chatLogRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.child(userNameSession).getChildren()) {
                    Message m = data.getValue(Message.class);
                    if (!chatlogSqlHelper.isExists(m.getId())) {
                        chatlogSqlHelper.insert(m);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        friendListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.child(userNameSession).getChildren()) {
                    Relation relation = data.getValue(Relation.class);
                    if ((!contactSQLHelper.isExists(relation.getUserName())) && (relation.getStatus() == 2)){
                        contactSQLHelper.insert(new Contact(relation.getAvaUri(), relation.getUserName(), relation.getNickName(), false));

                        Intent contactIntent = new Intent("newContact");
                        sendBroadcast(contactIntent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        chatLogRef.child(userNameSession).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                if (!chatlogSqlHelper.isExists(message.getId())) {
                    chatlogSqlHelper.insert(message);

                    Intent messageIntent = new Intent("message");
                    messageIntent.putExtra("newMessage", message);
                    sendBroadcast(messageIntent);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        friendListRef.child(userNameSession).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Relation relation = dataSnapshot.getValue(Relation.class);

                if ((!contactSQLHelper.isExists(relation.getUserName())) && (relation.getStatus() == 2) && relation.getUserName().compareTo(userNameSession) != 0){
                    contactSQLHelper.insert(new Contact(relation.getAvaUri(), relation.getUserName(), "", false));

                    Intent contactIntent = new Intent("newContact");
                    sendBroadcast(contactIntent);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
