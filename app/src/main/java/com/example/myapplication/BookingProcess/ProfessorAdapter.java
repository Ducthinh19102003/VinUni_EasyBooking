package com.example.myapplication.BookingProcess;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ProfessorInfo;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Locale;

public class ProfessorAdapter extends RecyclerView.Adapter<ProfessorAdapter.MyViewHolder> implements Filterable{
    Context context;
    ArrayList<ProfessorInfo> professorItemArrayList;
    ArrayList<ProfessorInfo> professorItemArrayListOld;
    OnItemListener listener;

    public ProfessorAdapter(Context context, ArrayList<ProfessorInfo> professorItemArrayList, OnItemListener listener) {
        this.context = context;
        this.professorItemArrayList = professorItemArrayList;
        this.professorItemArrayListOld = professorItemArrayList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ProfessorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_professor,parent,false);
        return new MyViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfessorAdapter.MyViewHolder holder, int position) {
        ProfessorInfo professor = professorItemArrayList.get(position);
        if (professor == null) {
            return;
        }
        holder.name.setText(professor.getName());
        holder.email.setText(professor.getEmail());
        holder.subjects.setText(professor.getSubject());
        holder.profPicture.setImageResource(R.drawable.image1);
    }
    @Override
    public int getItemCount() {
        if (professorItemArrayList != null) return professorItemArrayList.size();
        else return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, email, subjects;
        ImageView profPicture;
        OnItemListener listener;

        public MyViewHolder(@NonNull View itemView, OnItemListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.name_professor);
            email = itemView.findViewById(R.id.prof_email);
            subjects = itemView.findViewById(R.id.prof_subjects);
            profPicture = itemView.findViewById(R.id.img_professor);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getBindingAdapterPosition());
        }


    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()){
                    professorItemArrayList = professorItemArrayListOld;
                } else {
                    ArrayList<ProfessorInfo> profList = new ArrayList<>();
                    for (ProfessorInfo prof: professorItemArrayListOld){
                        if (prof.getName().toLowerCase().contains(strSearch.toLowerCase())){
                            profList.add(prof);
                        }
                    }
                    professorItemArrayList = profList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = professorItemArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                professorItemArrayList = (ArrayList<ProfessorInfo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
