package com.example.bookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.bookapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);




        binding.noAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateData();

            }
        });

    }

    private String email="",password= "";

    private void validateData() {

//        before login validation
//                        get data
            email = binding.emailEdt.getText().toString().trim();
            password =binding.passwordEdt.getText().toString().trim();
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            Toast.makeText(this,"Invalid email pattern",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(password)){

            Toast.makeText(this,"Confirm Password...!",Toast.LENGTH_SHORT).show();

            }else{

                loginUser();

            }
    }

    private void loginUser() {
            progressDialog.setMessage("Logging In...");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                    // Login succes

                            checkUser();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure( Exception e) {

                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });


    }

    private void checkUser() {
//                checking wheater the user is Admin or User
//        get current User
        progressDialog.setMessage("Checking User...");
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        progressDialog.dismiss();
                        String userType =""+snapshot.child("userType").getValue();
                        if(userType.equals("user")){
//                            open user dashboard

                                startActivity(new Intent(LoginActivity.this,DashboardUserActivity.class));
                                finish();
                        }else if(userType.equals("admin")){
                                startActivity(new Intent(LoginActivity.this,DashboardAdminActivity.class));
                                finish();
//                            open admin dashboard
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }

}