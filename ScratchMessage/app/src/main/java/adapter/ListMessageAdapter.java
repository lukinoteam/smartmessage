package adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.scratchmessage.ImageViewer;
import com.example.scratchmessage.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import adapter.adapterinterface.ClickListener;
import model.Contact;
import model.Message;

public class ListMessageAdapter extends RecyclerView.Adapter<ListMessageAdapter.ViewHolder> {

    private ArrayList<Message> messages;
    private Contact contactSend;
    private Contact contactReceive;
    private Context context;

    private static final int send_type = 0;
    private static final int receive_type = 1;

    static class ViewHolder extends RecyclerView.ViewHolder{

        View view;
        ImageView imgAvatarSend;
        ImageView imgAvatarReceive;
        TextView tvMessageContent;
        ImageView imgPictureContent;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            imgAvatarSend = itemView.findViewById(R.id.imgAvatarSend);
            imgAvatarReceive = itemView.findViewById(R.id.imgAvatarReceive);
            tvMessageContent = itemView.findViewById(R.id.tvMessageContent);
            imgPictureContent = itemView.findViewById(R.id.imgPictureContent);
        }
    }

    public ListMessageAdapter(ArrayList<Message> messages, Contact contactSend, Contact contactReceive, Context context) {
        this.messages = messages;
        this.contactSend = contactSend;
        this.contactReceive = contactReceive;
        this.context = context;
    }


    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getStatus();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View messageView = null;
        switch (type){
            case send_type:
                messageView = inflater.inflate(R.layout.message_list_item_send, parent, false);
                break;
            case receive_type:
                messageView = inflater.inflate(R.layout.message_list_item_receive, parent, false);
                break;
        }

        ViewHolder vh = new ViewHolder(messageView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        final Message message = messages.get(position);

        if (contactSend.getAvaUri().compareTo("default") == 0){
            viewHolder.imgAvatarSend.setImageResource(R.mipmap.default_avatar);
        }
        else{
            Uri sUri = Uri.parse(contactSend.getAvaUri());
            Picasso.get().load(sUri).into(viewHolder.imgAvatarSend);
        }

        if (contactReceive.getAvaUri().compareTo("default") == 0){
            viewHolder.imgAvatarReceive.setImageResource(R.mipmap.default_avatar);
        }
        else{
            Uri rUri = Uri.parse(contactReceive.getAvaUri());
            Picasso.get().load(rUri).into(viewHolder.imgAvatarReceive);
        }

        if (message.getType() == 0){
            viewHolder.imgPictureContent.setVisibility(View.GONE);
            viewHolder.tvMessageContent.setText(message.getContent());

        }else{
            viewHolder.tvMessageContent.setVisibility(View.GONE);
            Picasso.get().load(Uri.parse(message.getContent())).into(viewHolder.imgPictureContent);
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ImageViewer.class);
                    intent.putExtra("uri", message.getContent());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


}
