package com.mlt.wevote.Activities;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.LOCATION_HARDWARE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.mlt.wevote.Contracts.WeVote;
import com.mlt.wevote.R;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private Runnable runnable;
    public static final int REQUEST_CODE = 100;

    private String[] neededPermissions = new String[]{CAMERA};

    private ArrayList<String> permissionsNotGranted;

    private Web3j web3j;
    private Credentials credentials;

    private List<String> candidateId = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        candidateId.add("0");
        candidateId.add("1");
        candidateId.add("2");

        credentials = Credentials.create("db0eddda965e7d7518818d75164f1bd28fce2a505b54444ae3e98d39e156f9dc");
        web3j = Web3j.build(new HttpService(getString(R.string.infura_node)));

//        deployContract();
        runnable = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        };
        if (checkPermission()) {
            new Handler().postDelayed(runnable, 3000);
        }

    }

    private void deployContract() {
        try {
            WeVote etherCasino = WeVote.deploy(
                    web3j,
                    credentials,
                    ManagedTransaction.GAS_PRICE,
                    Contract.GAS_LIMIT,
                    candidateId,
                    BigInteger.valueOf(1655814600),
                    BigInteger.valueOf(1655814600)
            ).sendAsync().get();
            Log.i("_CONTRACT_ADDRESS : ", etherCasino.getContractAddress());
            Log.i("_CONTRACT_BINARY : ", etherCasino.getContractBinary());
        } catch (Exception e) {
            Log.i("ERROR_", e.getMessage());
        }
    }

    private boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            permissionsNotGranted = new ArrayList<>();
            for (String permission : neededPermissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsNotGranted.add(permission);
                }
            }
            if (permissionsNotGranted.size() > 0) {
                boolean shouldShowAlert = false;
                for (String permission : permissionsNotGranted) {
                    shouldShowAlert = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                }
                if (shouldShowAlert) {
                    showPermissionAlert(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]));
                } else {
                    requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]));
                }
                return false;
            }
        }
        return true;
    }

    private void showPermissionAlert(final String[] permissions) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission Required");
        alertBuilder.setMessage("You must grant permission to access camera to run this application.");
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(permissions);
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void requestPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(SplashActivity.this, permissions, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        showPermissionAlert(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]));
                        return;
                    }
                }
                new Handler().postDelayed(runnable, 3000);
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}