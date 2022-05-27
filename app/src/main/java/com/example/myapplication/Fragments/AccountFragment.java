package com.example.myapplication.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myapplication.Login;
import com.example.myapplication.MyAppGlideModule;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentAccountBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AccountFragment extends Fragment {
    private static final int GALLERY_INTENT_CODE = 1023 ;
    TextView fullName, email, info;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button logout, resetPassWord,changeImageBtn, changePro5;
    FirebaseUser fUser;
    ImageView image, notiBall;
    StorageReference storageReference;
    private FragmentAccountBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        fullName = binding.Name;
        email = binding.Email;
        info = binding.OtherInfo;
        logout = binding.LogOut;

        changeImageBtn = binding.changeProfileImage;
        image = binding.profileImage;
        notiBall = binding.notiBall;
        resetPassWord = binding.ResetPassword;
        changePro5 = binding.ChangePro5Btn;
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fUser = fAuth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference();

//        StorageReference profileRef = storageReference.child("users/"+fUser.getUid()+"/profile.jpg");
//        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Picasso.get().load(uri).into(profileImage);
//            }
//        });
        notiBall.setVisibility(View.GONE);
        if (Login.portal == 1) {



            fullName.setText(Login.currentStudent.getName());
            email.setText(Login.currentStudent.getEmail());
            info.setText(Login.currentStudent.getID());
            // Reference to pro5 image file in Cloud Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().
                    child("studentProfilePics/" + Login.currentStudent.getID() + ".jpg");
            
            Glide.with(this /* context */)
                    .load(storageReference)
                    .into(image);
        }
        else if (Login.portal == 2) {
            fullName.setText(Login.currentProfessor.getName());
            email.setText(Login.currentProfessor.getEmail());
            info.setText(Login.currentProfessor.getSubject());
            if (Login.currentProfessor.getLocation() == null) notiBall.setVisibility(View.VISIBLE);
        }
        changePro5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Login.portal == 2){
                    Dialog dialog = new Dialog(getActivity());
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.change_profile_prof);

                    Button ok = dialog.findViewById(R.id.okBtn_pro5prof);
                    Button cancel = dialog.findViewById(R.id.cancelBtn_pro5prof);

                    EditText editName = dialog.findViewById(R.id.editName);
                    EditText editEmail = dialog.findViewById(R.id.editEmail);
                    EditText editLoc = dialog.findViewById(R.id.editLocation);
                    EditText editOther = dialog.findViewById(R.id.editOther);

                    editName.setText(Login.currentProfessor.getName());
                    editEmail.setText(Login.currentProfessor.getEmail());
                    editOther.setText(Login.currentProfessor.getSubject());
                    editLoc.setText(Login.currentProfessor.getLocation());

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String newName, newEmail, newLoc, newOther;
                            newName = editName.getText().toString();
                            newEmail = editEmail.getText().toString();
                            newLoc = editLoc.getText().toString();
                            newOther = editOther.getText().toString();
                            //do other things with it if needed
                            Log.d("editinfoprof", newName + newEmail + newLoc + newOther);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    Window window = dialog.getWindow();
                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                }
            }
        });
        resetPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetPassword = new EditText(v.getContext());

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter New Password > 8 Characters long.");
                passwordResetDialog.setView(resetPassword);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        String newPassword = resetPassword.getText().toString();
                        fUser.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Password Reset Successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Password Reset Failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close
                    }
                });

                passwordResetDialog.create().show();

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(),Login.class));
            }
        });

//        changeProfileImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // open gallery
//                Intent i = new Intent(v.getContext(),EditProfile.class);
//                i.putExtra("fullName",fullName.getText().toString());
//                i.putExtra("email",email.getText().toString());
//                i.putExtra("phone",phone.getText().toString());
//                startActivity(i);
//            }
//        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}