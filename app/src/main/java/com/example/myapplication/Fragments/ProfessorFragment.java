package com.example.myapplication.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.BookingProcess.OfficeHourBooking;
import com.example.myapplication.BookingProcess.ProfessorAdapter;
import com.example.myapplication.BookingProcess.SelectDate;
import com.example.myapplication.ProfessorInfo;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentViewProfessorsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProfessorFragment extends Fragment implements ProfessorAdapter.OnItemListener{
    RecyclerView professorRecView;
    ProfessorAdapter myAdapter;
    ArrayList<ProfessorInfo> professorItemArrayList;
    FirebaseFirestore fstore;
    SearchView profSearch;

    private FragmentViewProfessorsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentViewProfessorsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        professorRecView = binding.profList2;
        professorRecView.setHasFixedSize(true);
        professorRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
        profSearch = binding.profSearch;
        fstore = FirebaseFirestore.getInstance();
        professorItemArrayList = new ArrayList<ProfessorInfo>();
        myAdapter = new ProfessorAdapter(getActivity(), professorItemArrayList,this);
        professorRecView.setAdapter(myAdapter);
        profSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                myAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                myAdapter.getFilter().filter(s);
                return false;
            }
        });
        EventChangeListener();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void EventChangeListener() {
        fstore.collection("Professors").orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("OfficeHourBooking", document.getId() + " => " + document.getData());
                                ProfessorInfo professor = document.toObject(ProfessorInfo.class);
                                professorItemArrayList.add(professor);
                            }
                        } else {
                            Log.d("OfficeHourBooking", "Error getting documents: ", task.getException());
                        }
                        myAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        ProfessorInfo currentProfessor = professorItemArrayList.get(position);
        Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.professor_info_dialog);
        TextView currentProfName = dialog.findViewById(R.id.name_professor);
        TextView currentProfSubject = dialog.findViewById(R.id.prof_subjects);
        TextView currentProfEmail = dialog.findViewById(R.id.prof_email);
        Button done = dialog.findViewById(R.id.done_btn);

        currentProfName.setText(currentProfessor.getName());
        currentProfSubject.setText(currentProfessor.getSubject());
        currentProfEmail.setText(currentProfessor.getEmail());

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}