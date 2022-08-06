package com.mlt.wevote.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.firebase.auth.FirebaseAuth;
import com.mlt.wevote.Interfaces.VoterListener;
import com.mlt.wevote.Models.VoterModel;
import com.mlt.wevote.R;
import com.mlt.wevote.Singletons.Voter;

import org.json.JSONObject;

import java.security.Key;
import java.util.List;


public class QRCodeActivity extends AppCompatActivity {

    private Boolean openActivity = false;

    private FirebaseAuth auth;
    private JSONObject qrCodeData;
    private Voter voter;

    private Python python;
    private PyObject _decoder;
    private TextView errorMessage;

    private QRCodeReaderView qrCodeReaderView;
    private Intent faceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        auth = FirebaseAuth.getInstance();
        python = Python.getInstance();
        _decoder = python.getModule("Decoder");
        voter = Voter.getInstance();

        faceIntent = new Intent(QRCodeActivity.this, FaceDetectionActivity.class);

        voter.setListener(new VoterListener() {
            @Override
            public void onChanged(List<VoterModel> voterModelList) {
                faceIntent.putExtra("qrcode", voterModelList.get(0).getPhoto());
                if (openActivity)
                    startActivity(faceIntent);
                openActivity = !openActivity;
            }
        });

        errorMessage = findViewById(R.id.error_message);


        qrCodeReaderView = findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(new QRCodeReaderView.OnQRCodeReadListener() {
            @Override
            public void onQRCodeRead(String text, PointF[] points) {

                try {
                    qrCodeData = new JSONObject(_decoder.callAttr("decode", text, auth.getUid()).toString());
                    voter.setVoter(qrCodeData);
                    errorMessage.setTextColor(getResources().getColor(R.color.green));
                    errorMessage.setText("Successfully Verified.");
                } catch (Exception e) {
                    errorMessage.setText("Check your verification QR-Code.");
                }
            }
        });
    }
}