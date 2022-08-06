package com.mlt.wevote.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.*;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mlt.wevote.Background.FaceRecognition;
import com.mlt.wevote.Background.UpdateVote;
import com.mlt.wevote.Contracts.WeVote;
import com.mlt.wevote.Handlers.CameraHandler;
import com.mlt.wevote.Interfaces.FaceMatch;
import com.mlt.wevote.Interfaces.VoteUpdated;
import com.mlt.wevote.R;
import com.mlt.wevote.Singletons.Voter;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class FaceDetectionActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraHandler mHandler;
    private FrameLayout preview;
    private Button voteNow;

    private ProgressBar faceProgress;
    private TextView faceMatchStatus;

//    private FirebaseStorage storage;
//    private StorageReference storageReference;

    private String qrCodeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);

        voteNow = findViewById(R.id.vote_now);
        preview = findViewById(R.id.camera_preview);
        faceMatchStatus = findViewById(R.id.face_match_status);
        faceProgress = findViewById(R.id.face_progress);

        qrCodeData = getIntent().getExtras().getString("qrcode");

//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference().child(String.format("Voters/%s/temp.jpg", auth.getUid()));

//        _database = python.getModule("Database");
//        voterPhoto = _database.callAttr("get_voter_photo", auth.getUid()).toString();

        initCamera();

        final PictureCallback mPicture = new PictureCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onPictureTaken(byte[] data, Camera camera) {
//                Log.i("ASDFG", String.valueOf(qrCodeData));
                faceMatchStatus.setText("Verifying...");
                faceProgress.setVisibility(View.VISIBLE);
                voteNow.setEnabled(false);
                voteNow.setBackground(getResources().getDrawable(R.drawable.disabled_button_background));
                new FaceRecognition(data, qrCodeData, new FaceMatch() {
                    @Override
                    public void onMatch(boolean match) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    if (match) {
//                                    if (true) {
                                        faceMatchStatus.setText("Successfully verified.\nWait for the vote to update.");
                                        new UpdateVote(FaceDetectionActivity.this, new VoteUpdated() {
                                            @Override
                                            public void onUpdated() {
                                                startActivity(new Intent(FaceDetectionActivity.this, ResultActivity.class));
                                            }

                                            @Override
                                            public void onError() {
                                                runOnUiThread(new Runnable() {
                                                    @SuppressLint("UseCompatLoadingForDrawables")
                                                    @Override
                                                    public void run() {
                                                        faceMatchStatus.setText("You have already voted.");
                                                        voteNow.setEnabled(true);
                                                        voteNow.setBackground(FaceDetectionActivity.this.getDrawable(R.drawable.button_background));
                                                    }
                                                });
                                            }
                                        }).execute();
                                    } else {
                                        faceMatchStatus.setText("Face verification failed.");
                                        voteNow.setEnabled(true);
                                        voteNow.setBackground(getResources().getDrawable(R.drawable.button_background));
                                    }

                                } catch (Exception e) {
                                    faceMatchStatus.setText(e.getMessage());
                                }
                                faceProgress.setVisibility(View.GONE);
                            }
                        });
                    }
                }).execute();
            }
        };

        Parameters params = mCamera.getParameters();
        List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes.contains(Parameters.FOCUS_MODE_AUTO)) {
            // Autofocus mode is supported
            params.setFocusMode(Parameters.FOCUS_MODE_AUTO);
            params.setFlashMode(Parameters.FLASH_MODE_ON);
            mCamera.setParameters(params);
        }

        voteNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceMatchStatus.setText("Wait for a moment.");
                voteNow.setEnabled(false);
                voteNow.setBackground(getResources().getDrawable(R.drawable.disabled_button_background));
                mCamera.autoFocus(new AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean arg0, Camera arg1) {
                        mCamera.takePicture(null, null, mPicture);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mCamera.startPreview();
                    }
                });
            }
        });

    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(findFrontFacingCamera());
            c.setDisplayOrientation(90);
        } catch (Exception e) {
        }
        return c;
    }

    private static int findFrontFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCamera();
    }

    private void initCamera() {
        mCamera = getCameraInstance();
//        mCamera = android.hardware.Camera.open(1);
        mHandler = new CameraHandler(this, mCamera);
        preview.addView(mHandler);
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

}