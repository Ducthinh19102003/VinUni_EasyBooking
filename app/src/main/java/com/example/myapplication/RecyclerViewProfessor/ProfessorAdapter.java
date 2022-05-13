package com.example.myapplication.RecyclerViewProfessor;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class ProfessorAdapter extends RecyclerView.Adapter<ProfessorAdapter.MyViewHolder> {
    Context context;
    ArrayList<ProfessorItem> professorItemArrayList;

    public ProfessorAdapter(Context context, ArrayList<ProfessorItem> professorItemArrayList) {
        this.context = context;
        this.professorItemArrayList = professorItemArrayList;
    }

    public void setProfessorItemArrayList(ArrayList<ProfessorItem> professorItemArrayList) {
        this.professorItemArrayList = professorItemArrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProfessorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_professor,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfessorAdapter.MyViewHolder holder, int position) {
        ProfessorItem professor = professorItemArrayList.get(position);
        if (professor == null) {
            return;
        }
        holder.name.setText(professor.getName());
        holder.email.setText(professor.getEmail());
        holder.subjects.setText(professor.getSubject());
        holder.profPicture.setImageResource(professor.getResourceID());

    }

    @Override
    public int getItemCount() {
        if (professorItemArrayList != null) return professorItemArrayList.size();
        else return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, subjects;
        ImageView profPicture;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.prof_name);
            email = itemView.findViewById(R.id.prof_email);
            subjects = itemView.findViewById(R.id.prof_subjects);
            profPicture = itemView.findViewById(R.id.img_professor);
        }
    }
}
