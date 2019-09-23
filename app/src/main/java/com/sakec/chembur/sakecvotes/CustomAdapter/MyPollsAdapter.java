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

import com.sakec.chembur.sakecvotes.CustomClass.Poll;
import com.sakec.chembur.sakecvotes.CustomClass.Votes;
import com.sakec.chembur.sakecvotes.R;

import java.util.ArrayList;

public class MyPollsAdapter extends RecyclerView.Adapter<MyPollsAdapter.MyViewHolder> {

    ArrayList<Poll> polls;
    ArrayList<Votes> votes;

    public MyPollsAdapter(ArrayList<Poll> polls,ArrayList<Votes> votes) {
        this.polls = polls;
        this.votes = votes;
    }

    public void update(ArrayList<Poll> polls,ArrayList<Votes> votes){
        this.polls = polls;
        this.votes = votes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.main_list_item,viewGroup,false);

        return new MyViewHolder(itemView);
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
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.bind(i);
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
            progressLinearLayout.setVisibility(View.VISIBLE);
        }

        public void bind(int position)
        {
            try {
                title.setText(polls.get(position).getTitle());
                desc.setText(polls.get(position).getDescr());
                optionO.setText(polls.get(position).getOptionO());
                optionT.setText(polls.get(position).getOptionT());
                optionO.setOnClickListener(null);
                optionO.setClickable(false);
                optionT.setOnClickListener(null);
                optionT.setClickable(false);
                calculatePercent(position);
            }catch (Exception e){}
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
    }
}
