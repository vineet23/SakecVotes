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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sakec.chembur.sakecvotes.CustomAdapter.MainAdapter;
import com.sakec.chembur.sakecvotes.CustomClass.Poll;
import com.sakec.chembur.sakecvotes.CustomClass.Votes;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment fragment;

    public static MainFragment getInstance(){
        if (fragment==null)
            fragment = new MainFragment();
        return fragment;
    }

    public static MainFragment refresh(){
        fragment = null;
        return getInstance();
    }

    ArrayList<Poll> polls = new ArrayList<>();
    ArrayList<String> pollsID = new ArrayList<>();
    ArrayList<Votes> votes = new ArrayList<>();
    RecyclerView recyclerView;
    MaterialCardView materialCardView;
    MainAdapter adapter;
    boolean created = false;
    ProgressBar progressBar;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_main, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        materialCardView = view.findViewById(R.id.empty_card);
        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new MainAdapter(polls,pollsID,votes);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        //to prevent reload data when reselected
        if(!created) {
            created = true;
            db.collection("votes")
                    .whereEqualTo("user",MainActivity.email)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments())
                            {
                                votes.add(documentSnapshot.toObject(Votes.class));
                            }

                            db.collection("polls")
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (queryDocumentSnapshots.getDocuments().size() > 0) {
                                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                                    Poll poll = documentSnapshot.toObject(Poll.class);
                                                    //to check if user has voted earlier or not
                                                    if (!votes.contains(new Votes(MainActivity.email,documentSnapshot.getId()))) {
                                                        polls.add(poll);
                                                        pollsID.add(documentSnapshot.getId());
                                                        adapter.update(polls, pollsID);
                                                    }
                                                }
                                                if (polls.size()==0)
                                                    materialCardView.setVisibility(View.VISIBLE);
                                            } else {
                                                materialCardView.setVisibility(View.VISIBLE);
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Failed to update , Try Again!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),"Try Again !",Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            if (polls.size()==0)
                materialCardView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

        return  view;
    }

}
