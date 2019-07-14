package adapter;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import model.Relation;


public class ListRequestAdapter extends RecyclerView.Adapter<ListRequestAdapter.ViewHolder> {

    private ArrayList<Relation> relations;
    private String userNameSession;
    private String avaUriSession;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();

    class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imgAvatar;
        TextView tvUsername;
        ImageButton btnAccept;
        ImageButton btnDecline;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.view = itemView;
            this.imgAvatar = itemView.findViewById(R.id.imgAvatar);
            this.tvUsername = itemView.findViewById(R.id.tvUsername);
            this.btnAccept = itemView.findViewById(R.id.btnAccept);
            this.btnDecline = itemView.findViewById(R.id.btnDecline);
        }
    }

    public ListRequestAdapter(ArrayList<Relation> relations, Context context) {
        this.relations = relations;
        SharedPreferences sharedPreferences = context.getSharedPreferences("SHARED_PREFERENCES_ACCOUNT_SESSION", Context.MODE_PRIVATE);
        this.userNameSession = sharedPreferences.getString("userNameSession", "");
        this.avaUriSession = sharedPreferences.getString("avaUriSession", "");
    }

    @NonNull
    @Override
    public ListRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View requestView = inflater.inflate(R.layout.request_list_item, viewGroup, false);

        ViewHolder vh = new ViewHolder(requestView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ListRequestAdapter.ViewHolder viewHolder, int i) {
        final Relation relation = relations.get(i);

        String avaUri = relations.get(i).getAvaUri();

        if (avaUri.compareTo("default") == 0) {
            viewHolder.imgAvatar.setImageResource(R.mipmap.default_avatar);
        } else {
            Uri mUri = Uri.parse(avaUri);
            Picasso.get().load(mUri).into(viewHolder.imgAvatar);
        }

        viewHolder.tvUsername.setText(relation.getUserName());

        viewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Relation request = new Relation(avaUriSession, userNameSession, "", 2);
                Relation receive = new Relation(relation.getAvaUri(), relation.getUserName(), "", 2);

                ref.child("friend-list").child(userNameSession).child(relation.getUserName()).setValue(receive);
                ref.child("friend-list").child(relation.getUserName()).child(userNameSession).setValue(request);
            }
        });

        viewHolder.btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return relations.size();
    }
}
