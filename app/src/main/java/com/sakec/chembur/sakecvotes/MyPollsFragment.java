package com.sakec.chembur.sakecvotes;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sakec.chembur.sakecvotes.CustomAdapter.MyPollsAdapter;
import com.sakec.chembur.sakecvotes.CustomClass.Poll;
import com.sakec.chembur.sakecvotes.CustomClass.Votes;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyPollsFragment extends Fragment {


    public MyPollsFragment() {
        // Required empty public constructor
    }

    public static MyPollsFragment fragment;

    public static MyPollsFragment getInstance(){
        if (fragment==null)
            fragment = new MyPollsFragment();
        return fragment;
    }

    public static MyPollsFragment refresh(){
        fragment = null;
        return getInstance();
    }

    RecyclerView recyclerView;
    MyPollsAdapter adapter;
    ProgressBar progressBar;
    MaterialCardView materialCardView;
    FirebaseFirestore db;
    boolean created = false;
    ArrayList<Votes> votes = new ArrayList<>();
    ArrayList<Poll> polls = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_polls, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        materialCardView = view.findViewById(R.id.empty_card);

        materialCardView.setVisibility(View.GONE);

        adapter = new MyPollsAdapter(polls);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        if(!created) {
            created = true;
            db.collection("votes")
                    .whereEqualTo("user", MainActivity.email)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.getDocuments().size() > 0) {
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                    votes.add(documentSnapshot.toObject(Votes.class));
                                }
                                for (Votes votess : votes) {
                                    db.collection("polls")
                                            .document(votess.getPollID())
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if(documentSnapshot.exists()) {
                                                        polls.add(documentSnapshot.toObject(Poll.class));
                                                        adapter.update(polls);
                                                    }
                                                    if (polls.size()==0)
                                                        materialCardView.setVisibility(View.VISIBLE);
                                                }
                                            });
                                }
                                progressBar.setVisibility(View.GONE);
                            } else {
                                materialCardView.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Try Again Later !", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            if(polls.size()>0){
                progressBar.setVisibility(View.GONE);
                materialCardView.setVisibility(View.GONE);
            }else {
                progressBar.setVisibility(View.GONE);
                materialCardView.setVisibility(View.VISIBLE);
            }
        }

        return  view;
    }

}
