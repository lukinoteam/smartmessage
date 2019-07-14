package adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.scratchmessage.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import model.Relation;
import model.User;

import static com.example.scratchmessage.R.drawable.ic_add_blue_35sdp;
import static com.example.scratchmessage.R.drawable.ic_check_blue_35sdp;
import static com.example.scratchmessage.R.drawable.ic_friend_blue_35sdp;

public class ListFindResultAdapter extends RecyclerView.Adapter<ListFindResultAdapter.ViewHolder> {

    private ArrayList<User> users;
    private String userNameSession;
    private String avaUriSession;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();

    class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imgAvatar;
        TextView tvUsername;
        ImageButton btnAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.imgAvatar = itemView.findViewById(R.id.imgAvatar);
            this.tvUsername = itemView.findViewById(R.id.tvUsername);
            this.btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }

    public ListFindResultAdapter(ArrayList<User> users, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SHARED_PREFERENCES_ACCOUNT_SESSION", Context.MODE_PRIVATE);
        this.userNameSession = sharedPreferences.getString("userNameSession", "");
        this.avaUriSession = sharedPreferences.getString("avaUriSession", "");
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View resultView = inflater.inflate(R.layout.find_result_list_item, parent, false);

        ViewHolder vh = new ViewHolder(resultView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final User user = users.get(position);

        String avaUri = users.get(position).getAvaUri();

        if (avaUri.compareTo("default") == 0) {
            viewHolder.imgAvatar.setImageResource(R.mipmap.default_avatar);
        } else {
            Uri mUri = Uri.parse(users.get(position).getAvaUri());
            Picasso.get().load(mUri).into(viewHolder.imgAvatar);
        }

        viewHolder.tvUsername.setText(user.getUserName());

        ref.child("friend-list").child(userNameSession).child(user.getUserName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Relation request = dataSnapshot.getValue(Relation.class);
                if (request == null || request.getStatus() == -1) {
                    viewHolder.btnAdd.setImageResource(ic_add_blue_35sdp);
                    viewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Relation request = new Relation(user.getAvaUri(), user.getUserName(), "", 1);
                            ref.child("friend-list").child(userNameSession).child(user.getUserName()).setValue(request);

                            Relation receive = new Relation(avaUriSession, userNameSession, "", 0);
                            ref.child("friend-list").child(user.getUserName()).child(userNameSession).setValue(receive);

                            viewHolder.btnAdd.setImageResource(ic_check_blue_35sdp);
                        }
                    });
                } else if (request.getStatus() == 0 || request.getStatus() == 1) {
                    viewHolder.btnAdd.setImageResource(ic_check_blue_35sdp);
                } else if (request.getStatus() == 2) {
                    viewHolder.btnAdd.setImageResource(ic_friend_blue_35sdp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

}
