package com.doyal2020.whatsapps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.doyal2020.whatsapps.Adapter.MessagesAdapter;
import com.doyal2020.whatsapps.Holder.Messages;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {



    private String messageReceiverID,messageReceiverName,messageReciverImage,messageSenderID;
    private  TextView userName,userLastSeen;
    private EditText messageTextInPut;
    private ImageButton sendMessageBtn,sendFileBtn;
    private CircleImageView userProfileImage;
    private Toolbar mToolbar;
    private RecyclerView myRecyclerViewList;

    private String currentDate,currentTime;
    private String checker=" ",myUri=" ",pdf="";
    private  Uri imageUri;

    private StorageTask upLoadTask;
    private ProgressDialog mProgressDialog;

    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);





        messageReceiverID=getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName=getIntent().getExtras().get("visit_user_name").toString();
        messageReciverImage=getIntent().getExtras().get("visit_user_image").toString();


        mDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp");
        mAuth=FirebaseAuth.getInstance();
        messageSenderID=mAuth.getCurrentUser().getUid();

        mProgressDialog=new ProgressDialog(this);

        initializeControllers();



        userName.setText(messageReceiverName);
        Picasso.get().load(messageReciverImage).placeholder(R.drawable.profile_icon).into(userProfileImage);

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();

            }
        });




    }



    private void initializeControllers() {

        mToolbar=findViewById(R.id.chat_toolbar_id);
        setSupportActionBar(mToolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view=layoutInflater.inflate(R.layout.custom_user_layout,null);
        actionBar.setCustomView(view);



        userName=findViewById(R.id.custom_profile_name_id);
        userLastSeen=findViewById(R.id.custom_user_last_seen_id);
        userProfileImage=findViewById(R.id.custom_profile_image_id);

        messageTextInPut=findViewById(R.id.input_send_message_id);
        sendMessageBtn=findViewById(R.id.send_inputMessage_button_id);
        sendFileBtn=findViewById(R.id.image_sends_files_btn);
        myRecyclerViewList=findViewById(R.id.private_send_message_users);

        messagesAdapter=new MessagesAdapter(messagesList);
        myRecyclerViewList.setAdapter(messagesAdapter);
        linearLayoutManager=new LinearLayoutManager(this);
        myRecyclerViewList.setLayoutManager(linearLayoutManager);

        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM dd, yyyy");
        currentDate=simpleDateFormat.format(calForDate.getTime());

        Calendar calForTime=Calendar.getInstance();
        SimpleDateFormat simpletimeFormat=new SimpleDateFormat("hh: mm a");
        currentTime=simpletimeFormat.format(calForTime.getTime());

        DisplayLastSeen();

        sendFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence optinons[]=new CharSequence[]

                        {

                                "Image",
                                "PDF Files",
                                "Ms word files"
                        };

                AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select the file");

                builder.setItems(optinons, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which==0)
                        {
                                checker="image";

                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"Select Image"),438);
                        }
                        if (which==1)
                        {
                            checker="pdf";
                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent,"Select PDF Files"),438);

                        }
                        if (which==2)
                        {
                            checker="docx";

                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(intent.createChooser(intent,"Select Ms Word Files"),438);
                        }

                    }
                });

                builder.show();
            }
        });




    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==438  && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();



            mProgressDialog.setTitle("Sending Image......");
            mProgressDialog.setMessage("Please wait, we are sending that images ..........");
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.show();


            if (!checker.equals("image"))
            {

                StorageReference storageReference=FirebaseStorage.getInstance().getReference().child("Document Files");

                final    String sendMessageRef="Message/"+messageSenderID+"/"+messageReceiverID;
                final String receivedMessageRef="Message/"+messageReceiverID+"/"+messageSenderID;

                DatabaseReference userMessageKeyRef=mDatabase.child("Message")
                        .child(messageSenderID).child(messageReceiverID).push();

                final String messagePushID=userMessageKeyRef.getKey();


                final StorageReference filepath=storageReference.child(messagePushID +" . " + checker);


                filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downlaodUri=uriTask.getResult();
                        String pdfLink=downlaodUri.toString();

                        Map messageTextBody=new HashMap();
                        messageTextBody.put("message",pdfLink);
                        messageTextBody.put("name",imageUri.getLastPathSegment());
                        messageTextBody.put("type",checker);
                        messageTextBody.put("from",messageSenderID);
                        //image file send
                        messageTextBody.put("to",messageReceiverID);
                        messageTextBody.put("messageID",messagePushID);
                        messageTextBody.put("time",currentTime);
                        messageTextBody.put("date",currentDate);

                        Map messageTextDetails=new HashMap();
                        messageTextDetails.put(sendMessageRef+"/"+messagePushID,messageTextBody);
                        messageTextDetails.put(receivedMessageRef+"/"+messagePushID,messageTextBody);

                        mDatabase.updateChildren(messageTextDetails);
                        mProgressDialog.dismiss();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(ChatActivity.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                        double p=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();

                        mProgressDialog.setMessage((int) p+ " % Uploading..........");
                    }
                });

             /*   filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful())
                        {


                          UploadTask.TaskSnapshot dwonload=task.getResult();
                           pdf=dwonload.toString();




                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        mProgressDialog.dismiss();
                        Toast.makeText(ChatActivity.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                        double p=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();

                        mProgressDialog.setMessage((int) p+ " % Uploading..........");
                    }
                });



              */
            }

            else if (checker.equals("image"))
            {


                StorageReference storageReference=FirebaseStorage.getInstance().getReference().child("Images File");

             final    String sendMessageRef="Message/"+messageSenderID+"/"+messageReceiverID;
               final String receivedMessageRef="Message/"+messageReceiverID+"/"+messageSenderID;

                DatabaseReference userMessageKeyRef=mDatabase.child("Message")
                        .child(messageSenderID).child(messageReceiverID).push();

               final String messagePushID=userMessageKeyRef.getKey();


                final StorageReference filepath=storageReference.child(messagePushID +" . " + " jpg");

                upLoadTask=filepath.putFile(imageUri);

                upLoadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()){
                            throw  task.getException();
                        }

                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {


                        Uri downloadUrl=task.getResult();
                        myUri=downloadUrl.toString();

                        Map messageTextBody=new HashMap();
                        messageTextBody.put("message",myUri);
                        messageTextBody.put("name",imageUri.getLastPathSegment());
                        messageTextBody.put("type",checker);
                        messageTextBody.put("from",messageSenderID);

                        //image file send

                        messageTextBody.put("to",messageReceiverID);
                        messageTextBody.put("messageID",messagePushID);
                        messageTextBody.put("time",currentTime);
                        messageTextBody.put("date",currentDate);





                        Map messageTextDetails=new HashMap();
                        messageTextDetails.put(sendMessageRef+"/"+messagePushID,messageTextBody);
                        messageTextDetails.put(receivedMessageRef+"/"+messagePushID,messageTextBody);


                        mDatabase.updateChildren(messageTextDetails).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()){
                                    mProgressDialog.dismiss();
                                    Toast.makeText(ChatActivity.this, "Message sent successful", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    mProgressDialog.dismiss();

                                    String message=task.getException().toString();

                                    Toast.makeText(ChatActivity.this, "Error"+message, Toast.LENGTH_SHORT).show();
                                }

                                messageTextInPut.setText("");
                            }
                        });

                    }
                });



            }

            else
            {
                mProgressDialog.dismiss();
                Toast.makeText(this, "Nothing Select , Error", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private  void DisplayLastSeen(){

        mDatabase.child("Users").child(messageSenderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("userState").hasChild("state"))
                {

                    String state=dataSnapshot.child("userState").child("state").getValue().toString();
                    String date=dataSnapshot.child("userState").child("date").getValue().toString();
                    String time=dataSnapshot.child("userState").child("time").getValue().toString();

                    if (state.equals("online")){
                        userLastSeen.setText("online");
                    }
                    else  if (state.equals("offline")){
                        userLastSeen.setText("Last seen:  "+date +" "+time);
                    }
                }
                else {
                    userLastSeen.setText("offline");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {

        super.onStart();

        mDatabase.child("Message").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Messages messages=dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                      messagesAdapter.notifyDataSetChanged();

                      myRecyclerViewList.smoothScrollToPosition(myRecyclerViewList.getAdapter().getItemCount());


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

    }


    private void sendMessage() {

        final String messageText=messageTextInPut.getText().toString();

        if (TextUtils.isEmpty(messageText)){
            Toast.makeText(this, "first message type here..", Toast.LENGTH_SHORT).show();
        }
        else {

            String sendMessageRef="Message/"+messageSenderID+"/"+messageReceiverID;
            String receivedMessageRef="Message/"+messageReceiverID+"/"+messageSenderID;

            DatabaseReference userMessageKeyRef=mDatabase.child("Message")
                    .child(messageSenderID).child(messageReceiverID).push();

            String messagePushID=userMessageKeyRef.getKey();

            Map messageTextBody=new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderID);

            //image file send

            messageTextBody.put("to",messageReceiverID);
            messageTextBody.put("messageID",messagePushID);
            messageTextBody.put("time",currentTime);
            messageTextBody.put("date",currentDate);





            Map messageTextDetails=new HashMap();
            messageTextDetails.put(sendMessageRef+"/"+messagePushID,messageTextBody);
            messageTextDetails.put(receivedMessageRef+"/"+messagePushID,messageTextBody);


            mDatabase.updateChildren(messageTextDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Message sent successful", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                messageTextInPut.setText("");
                }
            });


        }


    }


}
