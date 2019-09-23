package com.sakec.chembur.sakecvotes;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    TextView name,email,adminEmailId;
    Button signOut,add,remove;
    ProgressBar progressBar;
    MaterialCardView adminCard,EmailCard;
    private FirebaseAuth mAuth;
    TextInputEditText mail;
    FirebaseFirestore db;
    String admins;
    boolean created = false;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment fragment;

    public static SettingsFragment getInstance(){
        if (fragment==null)
            fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email_id);
        signOut = view.findViewById(R.id.sign_out);
        add = view.findViewById(R.id.add);
        remove = view.findViewById(R.id.remove);
        adminCard = view.findViewById(R.id.AdminCardView);
        EmailCard = view.findViewById(R.id.AddCardView);
        mail = view.findViewById(R.id.mailID_text);
        adminEmailId = view.findViewById(R.id.admin_email_id);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        if(!MainActivity.email.equals("vineet.patel@sakec.ac.in"))
        {
            adminCard.setVisibility(View.GONE);
            EmailCard.setVisibility(View.GONE);
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        name.setText(MainActivity.name);
        email.setText(MainActivity.email);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Add();
               mail.setText("");
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Remove();
                mail.setText("");
            }
        });

        if(!created)
        {
            created = true;
            progressBar.setVisibility(View.VISIBLE);

            db = FirebaseFirestore.getInstance();
            db.collection("auth")
                    .get()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),"Admin Update Failed",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    StringBuilder builder = new StringBuilder();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments())
                    {
                        builder.append(documentSnapshot.getString("user")+"\n");
                    }
                    admins = String.valueOf(builder).trim();
                    adminEmailId.setText(admins);
                }
            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }else {
            adminEmailId.setText(admins);
        }

        return view;
    }

    public void Add(){
        final String email = mail.getText().toString().trim();
        db = FirebaseFirestore.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        if (email.endsWith("sakec.ac.in") && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            db.collection("auth")
                    .whereEqualTo("user", email)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.getDocuments().size() == 0) {
                                Map<String, Object> auth = new HashMap<>();
                                auth.put("user", email);

                                db.collection("auth")
                                        .add(auth)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Refresh();
                                                Toast.makeText(getContext(), "Added Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Failed to add, Try Again!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "User already exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed to add", Toast.LENGTH_SHORT).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }else {
            Toast.makeText(getContext(),"Email ID is invalid",Toast.LENGTH_SHORT).show();
        }
    }

    public void Remove(){
        final String email = mail.getText().toString().trim();
        db = FirebaseFirestore.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        if (email.endsWith("sakec.ac.in") && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            db.collection("auth")
                    .whereEqualTo("user",email)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.getDocuments().size()>0)
                            {
                                DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                                DocumentReference documentReference = snapshot.getReference();
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Refresh();
                                        Toast.makeText(getContext(),"Deleted Successfully",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getContext(),"Failed to delete",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                Toast.makeText(getContext(),"No User Found",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),"Failed , Try Again",Toast.LENGTH_SHORT).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }else {
            Toast.makeText(getContext(), "Invalid Email ID", Toast.LENGTH_SHORT).show();
        }
    }

    public void Refresh(){
        progressBar.setVisibility(View.VISIBLE);

        db = FirebaseFirestore.getInstance();
        db.collection("auth")
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Admin Update Failed",Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                StringBuilder builder = new StringBuilder();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments())
                {
                    builder.append(documentSnapshot.getString("user")+"\n");
                }
                admins = String.valueOf(builder).trim();
                adminEmailId.setText(admins);
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}
