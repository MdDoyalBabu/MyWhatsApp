package com.doyal2020.whatsapps.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doyal2020.whatsapps.ChatActivity;
import com.doyal2020.whatsapps.ChatsFragment;
import com.doyal2020.whatsapps.Holder.Messages;
import com.doyal2020.whatsapps.ImageViewerActivity;
import com.doyal2020.whatsapps.MainActivity;
import com.doyal2020.whatsapps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {


    private List<Messages> messagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;

    public MessagesAdapter(List<Messages> messagesList) {
        this.messagesList = messagesList;
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder {

        TextView senderMessagesText,receiverMessagesText;
        CircleImageView receiverProfileImages;
        ImageView senderMessagePicture, reciverMessagePicture;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessagesText=itemView.findViewById(R.id.sender_messages_TextView_Id);
            receiverMessagesText=itemView.findViewById(R.id.receiver_messages_textView_id);
            receiverProfileImages=itemView.findViewById(R.id.messages_profile_image_id);
            senderMessagePicture=itemView.findViewById(R.id.messsage_sender_image_view);
            reciverMessagePicture=itemView.findViewById(R.id.messsage_reciver_image_view);

        }
    }
    @NonNull
    @Override
    public MessagesAdapter.MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layou,parent,false);
      mAuth=FirebaseAuth.getInstance();
       return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesAdapter.MessagesViewHolder holder, final int position) {

        String messageSenderId=mAuth.getCurrentUser().getUid();

        Messages messages=messagesList.get(position);

        String fromUserID=messages.getFrom();
        String fromMessageType=messages.getType();


        userDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp").child("Users").child(fromUserID);


        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("image")){

                    String reciverImage=dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(reciverImage).placeholder(R.drawable.profile_icon).into(holder.receiverProfileImages);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.receiverMessagesText.setVisibility(View.GONE);
        holder.receiverProfileImages.setVisibility(View.GONE);
        holder.senderMessagesText.setVisibility(View.GONE);
        holder.senderMessagePicture.setVisibility(View.GONE);
        holder.reciverMessagePicture.setVisibility(View.GONE);

        if (fromMessageType.equals("text")){

            if (fromUserID.equals(messageSenderId)){

                holder.senderMessagesText.setVisibility(View.VISIBLE);

                holder.senderMessagesText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessagesText.setTextColor(Color.BLACK);
                holder.senderMessagesText.setText(messages.getMessage()+"\n \n"+messages.getTime()+" - "+messages.getDate());

            }
            else {

                holder.receiverMessagesText.setVisibility(View.VISIBLE);
                holder.receiverProfileImages.setVisibility(View.VISIBLE);

                holder.receiverMessagesText.setBackgroundResource(R.drawable.receiver_messages_layou);
                holder.receiverMessagesText.setTextColor(Color.BLACK);
                holder.receiverMessagesText.setText(messages.getMessage());

            }
        }
        else  if (fromMessageType.equals("image"))
        {
            if (fromUserID.equals(messageSenderId))
            {

                holder.senderMessagePicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.senderMessagePicture);

            }
            else
            {
                holder.receiverProfileImages.setVisibility(View.VISIBLE);
                holder.reciverMessagePicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.reciverMessagePicture);


            }
        }
        else if (fromMessageType.equals("pdf") || fromMessageType.equals("docx"))
        {

            if (fromUserID.equals(messageSenderId))
            {
                holder.senderMessagePicture.setVisibility(View.VISIBLE);
                holder.senderMessagePicture.setBackgroundResource(R.drawable.files);
            }
            else
            {
                holder.receiverProfileImages.setVisibility(View.VISIBLE);
                holder.reciverMessagePicture.setVisibility(View.VISIBLE);
                holder.reciverMessagePicture.setBackgroundResource(R.drawable.files);
            }
        }

        if (fromUserID.equals(messageSenderId))
        {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (messagesList.get(position).getType().equals("pdf") || messagesList.get(position).getType().equals("docx") )
                    {

                        CharSequence options[]=new CharSequence[]
                                {

                                        "Delete for me",
                                        "Download and View This Document ",
                                        "Cancel",
                                        "Delete for Everyone"

                                };

                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which==0)
                                {

                                    deleteSentMessage(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);


                                }
                                else  if (which==1)
                                {
                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse( messagesList.get(position).getMessage()));
                                    holder.itemView.getContext().startActivity(intent);
                                }

                                else  if (which==3)
                                {

                                    deleteMessageEveryOne(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);


                                }
                            }
                        });
                        builder.show();


                    }
                    else if (messagesList.get(position).getType().equals("text") )
                    {

                        CharSequence options[]=new CharSequence[]
                                {

                                        "Delete for me",
                                        "Cancel",
                                        "Delete for Everyone"

                                };

                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which==0)
                                {
                                    deleteSentMessage(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }
                                else  if (which==2)
                                {
                                    deleteMessageEveryOne(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }
                            }
                        });
                        builder.show();


                    }
                     else if (messagesList.get(position).getType().equals("image") )
                    {

                        CharSequence options[]=new CharSequence[]
                                {

                                        "Delete for me",
                                        "View This Image ",
                                        "Cancel",
                                        "Delete for Everyone"

                                };

                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which==0)
                                {

                                    deleteSentMessage(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }
                                else  if (which==1)
                                {
                                    Intent intent=new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
                                    intent.putExtra("url",messagesList.get(position).getMessage());
                                    holder.itemView.getContext().startActivity(intent);

                                }
                                else  if (which==3)
                                {
                                    deleteMessageEveryOne(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }
                            }
                        });
                        builder.show();


                    }
                }
            });


        }
        else

        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (messagesList.get(position).getType().equals("pdf") || messagesList.get(position).getType().equals("docx") )
                    {

                        CharSequence options[]=new CharSequence[]
                                {

                                        "Delete for me",
                                        "Download and View This Document ",
                                        "Cancel",

                                };

                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which==0)
                                {
                                    deleteReceiveMessage(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }
                                else  if (which==1)
                                {
                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse( messagesList.get(position).getMessage()));
                                    holder.itemView.getContext().startActivity(intent);
                                }

                            }
                        });
                        builder.show();


                    }
                    else if (messagesList.get(position).getType().equals("text") )
                    {

                        CharSequence options[]=new CharSequence[]
                                {

                                        "Delete for me",
                                        "Cancel",

                                };

                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which==0)
                                {
                                    deleteReceiveMessage(position,holder);
                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }


                            }
                        });
                        builder.show();


                    }
                    else if (messagesList.get(position).getType().equals("image") )
                    {

                        CharSequence options[]=new CharSequence[]
                                {

                                        "Delete for me",
                                        "View This Image ",
                                        "Cancel",

                                };

                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which==0)
                                {
                                    deleteReceiveMessage(position,holder);

                                    Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }
                                else  if (which==1)
                                {

                                    Intent intent=new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
                                    intent.putExtra("url",messagesList.get(position).getMessage());
                                    holder.itemView.getContext().startActivity(intent);

                                }
                            }
                        });
                        builder.show();


                    }
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    private  void  deleteSentMessage(final  int position, final MessagesViewHolder holder)
    {
        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference("WhatsApp");
        rootRef.child("Message")
                .child(messagesList.get(position).getFrom())
                .child(messagesList.get(position).getTo())
                .child(messagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(holder.itemView.getContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

 private  void  deleteReceiveMessage(final  int position, final MessagesViewHolder holder)
    {
        DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference("WhatsApp");
        rootRef.child("Message")
                .child(messagesList.get(position).getTo())
                .child(messagesList.get(position).getFrom())
                .child(messagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(holder.itemView.getContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
 private  void  deleteMessageEveryOne(final  int position, final MessagesViewHolder holder)
    {
        final DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference("WhatsApp");
        rootRef.child("Message")
                .child(messagesList.get(position).getTo())
                .child(messagesList.get(position).getFrom())
                .child(messagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    rootRef.child("Message")
                            .child(messagesList.get(position).getFrom())
                        .child(messagesList.get(position).getTo())
                        .child(messagesList.get(position).getMessageID())
                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(holder.itemView.getContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
                else
                {
                    Toast.makeText(holder.itemView.getContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

}
