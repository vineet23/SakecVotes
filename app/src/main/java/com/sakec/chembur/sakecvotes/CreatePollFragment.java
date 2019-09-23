package com.sakec.chembur.sakecvotes;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreatePollFragment extends Fragment {


    public CreatePollFragment() {
        // Required empty public constructor
    }

    public static CreatePollFragment fragment;

    public static CreatePollFragment getInstance(){
        if (fragment==null)
            fragment = new CreatePollFragment();
        return fragment;
    }

    boolean created = false;
    boolean empty = false;
    FirebaseFirestore db;
    MaterialCardView emptyCard,createCard;
    TextInputEditText title,descr,optionO,optionT;
    String title_str,desc_str,optionO_str,optionT_str;
    Button create;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_poll, container, false);

        title = view.findViewById(R.id.title_text);
        descr = view.findViewById(R.id.desc_text);
        optionO = view.findViewById(R.id.option1_text);
        optionT = view.findViewById(R.id.option2_text);
        create = view.findViewById(R.id.create_button);
        progressBar = view.findViewById(R.id.progressBar);
        emptyCard = view.findViewById(R.id.empty_card);
        createCard = view.findViewById(R.id.create_card);

        db = FirebaseFirestore.getInstance();

        if(!created) {
            progressBar.setVisibility(View.VISIBLE);
            created = true;
            db.collection("auth")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                if (documentSnapshot.getString("user").equals(MainActivity.email)) {
                                    createCard.setVisibility(View.VISIBLE);
                                    emptyCard.setVisibility(View.GONE);
                                }
                            }
                            if (createCard.getVisibility() == View.GONE) {
                                empty = true;
                                emptyCard.setVisibility(View.VISIBLE);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed , Try Again !", Toast.LENGTH_SHORT).show();
                            createCard.setVisibility(View.GONE);
                            emptyCard.setVisibility(View.GONE);
                        }
                    })
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    progressBar.setVisibility(View.GONE);
                }
            });
            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCreate();
                }
            });
        }else {
            if (empty)
            {
                emptyCard.setVisibility(View.VISIBLE);
            }else {
                createCard.setVisibility(View.VISIBLE);
                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onCreate();
                    }
                });
            }
        }
        return view;
    }

    public void onCreate(){
        title_str = title.getText().toString().trim();
        if (title_str.equals(""))
        {
            Toast.makeText(getContext(), "Title can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(title_str.length()>20){
            Toast.makeText(getContext(), "Title can't be so big", Toast.LENGTH_SHORT).show();
            return;
        }
        if (title_str.length()<3)
        {
            Toast.makeText(getContext(), "Title can't be so small", Toast.LENGTH_SHORT).show();
            return;
        }
        desc_str = descr.getText().toString().trim();
        if (desc_str.equals(""))
        {
            Toast.makeText(getContext(), "Description can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (desc_str.length()<10)
        {
            Toast.makeText(getContext(),"Description can't be so small",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!desc_str.endsWith("?"))
            desc_str = desc_str.concat(" ?");
        optionO_str = optionO.getText().toString().trim();
        if (optionO_str.equals(""))
        {
            Toast.makeText(getContext(), "Option 1 can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (optionO_str.length()>15)
        {
            Toast.makeText(getContext(), "Option 1 can't be so big", Toast.LENGTH_SHORT).show();
            return;
        }
        optionT_str = optionT.getText().toString().trim();
        if (optionT_str.equals(""))
        {
            Toast.makeText(getContext(), "Option 2 can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (optionT_str.compareToIgnoreCase(optionO_str)==0)
        {
            Toast.makeText(getContext(),"Option 1 & Option 2 can't be same",Toast.LENGTH_SHORT).show();
            return;
        }
        if (optionT_str.length()>15)
        {
            Toast.makeText(getContext(),"Option 2 can't be so big",Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        db.collection("auth")
                .whereEqualTo("user",MainActivity.email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size()!=0)
                        {
                            // Create a new poll
                            Map<String, Object> poll = new HashMap<>();
                            poll.put("title", title_str);
                            poll.put("descr", desc_str);
                            poll.put("optionO", optionO_str);
                            poll.put("optionT",optionT_str);
                            poll.put("user",MainActivity.email);
                            poll.put("countO",0);
                            poll.put("countT",0);

                            // Add a new document with a generated ID
                            db.collection("polls")
                                    .add(poll)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d("create", "DocumentSnapshot added with ID: " + documentReference.getId());
                                            Toast.makeText(getContext(),"Poll Created",Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("create", "Error adding document", e);
                                            Toast.makeText(getContext(),"Failed , Try Again",Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    })
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            title.setText("");
                                            descr.setText("");
                                            optionO.setText("");
                                            optionT.setText("");
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                        }else {
                            Toast.makeText(getContext(),"Open the App Again!",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Failed , Try Again !",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}
