package com.sakec.chembur.sakecvotes.CustomAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sakec.chembur.sakecvotes.MainActivity;
import com.sakec.chembur.sakecvotes.CustomClass.Poll;
import com.sakec.chembur.sakecvotes.CustomClass.Votes;
import com.sakec.chembur.sakecvotes.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

    ArrayList<Poll> polls;
    ArrayList<String> pollsID;
    ArrayList<Votes> votes;
    FirebaseFirestore db ;

    public MainAdapter(ArrayList<Poll> polls,ArrayList<String> pollsID,ArrayList<Votes> votes) {
        this.polls = polls;
        this.pollsID = pollsID;
        this.votes = votes;
        db = FirebaseFirestore.getInstance();
    }

    public void update(ArrayList<Poll> polls,ArrayList<String> pollsID)
    {
        this.polls = polls;
        this.pollsID = pollsID;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.main_list_item, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.bind(i);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return polls.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        Button optionO,optionT;
        TextView optionO_pro_txt,optionT_pro_txt,title,desc;
        ProgressBar optionO_pro,optionT_pro;
        LinearLayout progressLinearLayout;
        Context context;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            optionO = itemView.findViewById(R.id.main_option_one);
            optionT = itemView.findViewById(R.id.main_option_two);
            title = itemView.findViewById(R.id.main_title);
            desc = itemView.findViewById(R.id.main_desc);
            optionO_pro_txt = itemView.findViewById(R.id.option1_text_pro);
            optionT_pro_txt = itemView.findViewById(R.id.option2_text_pro);
            optionO_pro = itemView.findViewById(R.id.progressBar_opt_o);
            optionT_pro = itemView.findViewById(R.id.progressBar_opt_t);
            progressLinearLayout = itemView.findViewById(R.id.progress_linear);
        }

        public void bind(final int position)
        {
            try {
                title.setText(polls.get(position).getTitle());
                desc.setText(polls.get(position).getDescr());
                optionO.setText(polls.get(position).getOptionO());
                optionT.setText(polls.get(position).getOptionT());

                if (votes.contains(new Votes(MainActivity.email, pollsID.get(position)))) {
                    progressLinearLayout.setVisibility(View.VISIBLE);
                    Voted(position);
                } else {
                    optionO.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //increase the count of option 1
                            if (progressLinearLayout.getVisibility() == View.GONE) {
                                Voted(position);
                                IncCountO(position);
                            }
                        }
                    });

                    optionT.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //increase the count of option 1
                            if (progressLinearLayout.getVisibility() == View.GONE) {
                                Voted(position);
                                IncCountT(position);
                            }
                        }
                    });
                }
            }catch (Exception e){}
        }

        public void Voted(int position){
            calculatePercent(position);
            progressLinearLayout.setVisibility(View.VISIBLE);
            optionO.setOnClickListener(null);
            optionO.setClickable(false);
            optionT.setOnClickListener(null);
            optionT.setClickable(false);
        }

        public void calculatePercent(int position){
            int optionOpercent,optionTpercent;
            int total = polls.get(position).getCountO()+polls.get(position).getCountT();
            if(total==0){
                optionOpercent = 0;
                optionTpercent = 0;
            }
            else {
                optionOpercent = (int) ((float)(polls.get(position).getCountO()*100)/total);
                optionTpercent = 100-optionOpercent;
            }
            optionO_pro_txt.setText(polls.get(position).getOptionO()+" "+optionOpercent+"%");
            optionT_pro_txt.setText(polls.get(position).getOptionT()+" "+optionTpercent+"%");
            optionO_pro.setProgress(optionOpercent);
            optionT_pro.setProgress(optionTpercent);
        }

        //to increment the count & insert it in votes collection

        public void IncCountO(int position){
            int incCountO = polls.get(position).getCountO()+1;
            polls.get(position).setCountO(incCountO);
            calculatePercent(position);
            pollUpdate(position);
        }

        public void IncCountT(int position){
            int incCountT = polls.get(position).getCountT()+1;
            polls.get(position).setCountT(incCountT);
            calculatePercent(position);
            pollUpdate(position);
        }

        public void pollUpdate(final int position){
            final Map<String,Object> votes = new HashMap<>();
            votes.put("pollID",pollsID.get(position));
            votes.put("user", MainActivity.email);

            db.collection("votes")
                    .add(votes)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            AddVotes(MainActivity.email,pollsID.get(position));
                            Toast.makeText(context,"Voted Successfully",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context,"Failed , Try Again !",Toast.LENGTH_SHORT).show();
                }
            });

            Map<String,Object> poll = new HashMap<>();
            poll.put("title",polls.get(position).getTitle());
            poll.put("descr",polls.get(position).getDescr());
            poll.put("optionO",polls.get(position).getOptionO());
            poll.put("optionT",polls.get(position).getOptionT());
            poll.put("user",polls.get(position).getUser());
            poll.put("countO",polls.get(position).getCountO());
            poll.put("countT",polls.get(position).getCountT());

            db.collection("polls")
                    .document(pollsID.get(position))
                    .update(poll)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Network Failure!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        public void AddVotes(String user,String pollID){
            votes.add(new Votes(user,pollID));
        }
    }
}


