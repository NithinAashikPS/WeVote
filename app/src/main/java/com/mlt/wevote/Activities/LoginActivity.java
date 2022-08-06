package com.mlt.wevote.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mlt.wevote.R;
import com.mlt.wevote.Singletons.Voter;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String verificationID;

    private Python python;
    private PyObject authenticator;
    private String phoneNumber;

    private Button loginButton;
    private TextView getOtp;
    private TextView otpMessage;
    private EditText voterId;
    private EditText otp;

    private Voter voter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_button);
        getOtp = findViewById(R.id.get_otp);
        voterId = findViewById(R.id.voter_id);
        otpMessage = findViewById(R.id.message);
        otp = findViewById(R.id.otp);

        mAuth = FirebaseAuth.getInstance();
        python = Python.getInstance();
        voter = Voter.getInstance();
        authenticator = python.getModule("Authenticator");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifycode(otp.getText().toString());
            }
        });

        otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (otp.getText().toString().matches("[0-9]{6}")) {
                    loginButton.setVisibility(View.VISIBLE);
                } else {
                    loginButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        voterId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (voterId.getText().toString().length() == 10) {
                    if (voterId.getText().toString().matches("[a-zA-Z]{3}+[0-9]{7}")) {
                        getOtp.setVisibility(View.VISIBLE);
                    } else {
                        voterId.setError("Invalid ID Format.");
                        getOtp.setVisibility(View.GONE);
                    }
                } else {
                    getOtp.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getOtp.setEnabled(false);
                otpMessage.setText("");
                getOtp.setTextColor(getResources().getColor(R.color.edittext_colour));
                try {
                    phoneNumber = authenticator.callAttr("get_phone_number", voterId.getText().toString()).toString();
                    voter.setPhone(phoneNumber);
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(phoneNumber)
                                    .setTimeout(60L, TimeUnit.SECONDS)
                                    .setActivity(LoginActivity.this)
                                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        @Override
                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                            final String code = phoneAuthCredential.getSmsCode();
                                            if (code != null) {
                                                verifycode(code);
                                            }
                                        }

                                        @Override
                                        public void onVerificationFailed(@NonNull FirebaseException e) {
                                            otpMessage.setVisibility(View.VISIBLE);
                                            otpMessage.setTextColor(getResources().getColor(R.color.saffron));
                                            otpMessage.setText("Verification Failed.");
                                            getOtp.setEnabled(true);
                                            getOtp.setTextColor(getResources().getColor(R.color.accent_colour));
                                            getOtp.setText("Resend");
                                        }

                                        @Override
                                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                            super.onCodeSent(s, forceResendingToken);
                                            verificationID = s;
                                            otpMessage.setVisibility(View.VISIBLE);
                                            otpMessage.setTextColor(getResources().getColor(R.color.accent_colour));
                                            otpMessage.setText("OTP sent to the number linked with your voter Id.");
                                            otp.setVisibility(View.VISIBLE);
                                            new CountDownTimer(60000, 1000) {

                                                public void onTick(long millisUntilFinished) {
                                                    getOtp.setText(String.format("Resend (%s)", millisUntilFinished / 1000));
                                                }

                                                public void onFinish() {
                                                    getOtp.setEnabled(true);
                                                    getOtp.setTextColor(getResources().getColor(R.color.accent_colour));
                                                    getOtp.setText("Resend");
                                                }

                                            }.start();
                                        }
                                    })
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                } catch (Exception e) {
                    voterId.setError("No Record Found.");
                    getOtp.setEnabled(true);
                    getOtp.setTextColor(getResources().getColor(R.color.accent_colour));
                    getOtp.setText("Get OTP");
                }
            }
        });
    }

    private void verifycode(String Code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, Code);
        signinbyCredentials(credential);
    }

    private void signinbyCredentials(PhoneAuthCredential credential) {

        otpMessage.setTextColor(getResources().getColor(R.color.green));
        otpMessage.setText("Verifying OTP...");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            voter.setEpicNumber(voterId.getText().toString());
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        otpMessage.setTextColor(getResources().getColor(R.color.saffron));
                        otpMessage.setText("Check the OTP you entered.");
                    }
                });

    }
}