package com.example.scratchmessage;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ContactSetting extends AppCompatActivity {

    Button btnBack;
    Button btnSave;
    TextView tvUsername;
    ImageView imgAvatar;
    EditText edtNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_setting);

        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        tvUsername = findViewById(R.id.tvUsername);
        imgAvatar = findViewById(R.id.imgAvatar);
        edtNickname = findViewById(R.id.edtNickname);

        Intent dataIntent = getIntent();

        String avaUri = dataIntent.getStringExtra("avaUri");
        String userName = dataIntent.getStringExtra("username");
        final String nickName = dataIntent.getStringExtra("nickname");

        if (avaUri.compareTo("default") == 0) {
            imgAvatar.setImageResource(R.mipmap.default_avatar);
        } else {
            Picasso.get().load(Uri.parse(avaUri)).into(imgAvatar);
        }

        tvUsername.setText(userName);
        edtNickname.setText(nickName);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String changedNickname = String.valueOf(edtNickname.getText());
                if (nickName.compareTo(changedNickname) == 0){
                    onBackPressed();
                }
                else{
                    Intent changeIntent = new Intent();
                    changeIntent.putExtra("changedNickname", changedNickname);

                    setResult(RESULT_OK, changeIntent);
                    finish();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
