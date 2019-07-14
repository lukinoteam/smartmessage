package adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.scratchmessage.R;
import adapter.adapterinterface.ClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import model.Contact;

public class ListContactAdapter extends RecyclerView.Adapter<ListContactAdapter.ViewHolder> {

    private ArrayList<Contact> contacts;
    private ClickListener clickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{

        View view;
        ImageView imgAvatar;
        TextView tvNickName;

        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvNickName = itemView.findViewById(R.id.tvNickName);

        }
    }

    public ListContactAdapter(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ListContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View contactView = inflater.inflate(R.layout.contact_list_item, parent ,false);

        ViewHolder vh = new ViewHolder(contactView);
        return vh;
    }

    public ClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        String avaUri = contacts.get(position).getAvaUri();

        if (avaUri.compareTo("default") == 0){
            viewHolder.imgAvatar.setImageResource(R.mipmap.default_avatar);
        }
        else{
            Uri mUri = Uri.parse(avaUri);
            Picasso.get().load(mUri).into(viewHolder.imgAvatar);
        }
        String nickName = contacts.get(position).getNickName();
        String userName = contacts.get(position).getUserName();

        if (nickName.compareTo("") == 0){
            viewHolder.tvNickName.setText(userName);
        }

        else {
            viewHolder.tvNickName.setText(nickName);
        }

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onParentClick(view, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }




}
