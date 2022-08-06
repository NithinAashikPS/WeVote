package com.mlt.wevote.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.palette.graphics.Palette;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;
import com.google.android.material.imageview.ShapeableImageView;
import com.mlt.wevote.Handlers.FingerprintHandler;
import com.mlt.wevote.R;
import com.mlt.wevote.Singletons.Voter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class VoteActivity extends AppCompatActivity {

    private KeyguardManager keyguardManager;
    private FingerprintManager fingerprintManager;
    private KeyStore keyStore;
    private static final String KEY_NAME = "androidHive";
    private Cipher cipher;

    private ShapeableImageView profilePhoto;
    private ImageView symbol;
    private TextView name;
    private TextView place;
    private TextView shortName;
    private TextView previousElectionStatus;
    private TextView previousElectionVoteCount;
    private TextView statusText;
    private char[] c = new char[]{'K', 'M', 'B', 'T'};

    private Python python;
    private PyObject database;
    private String data;

    private Voter voter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        voter = Voter.getInstance();

        profilePhoto = findViewById(R.id.profile_image);
        symbol = findViewById(R.id.symbol);
        name = findViewById(R.id.name);
        place = findViewById(R.id.place);
        shortName = findViewById(R.id.short_name);
        previousElectionStatus = findViewById(R.id.status);
        previousElectionVoteCount = findViewById(R.id.no_of_vote);
        statusText = findViewById(R.id.status_text);

        Bundle ids = getIntent().getExtras();
        python = Python.getInstance();
        database = python.getModule("Database");
        data = database.callAttr("get_election_details", voter.getCandidateId(), voter.getPartyId()).toString();

        Log.i("MGHFHJFHJFH", data);
        try {
            JSONArray voteDetails = new JSONArray(data);
            JSONObject party = voteDetails.getJSONObject(0);
            JSONObject candidate = voteDetails.getJSONObject(1);
            voter.setParty(party);
            voter.setCandidate(candidate);
            shortName.setText(party.getString("shortName"));
            name.setText(candidate.getString("name"));
            place.setText(candidate.getString("place"));
            Glide.with(this)
                    .load(party.getString("symbol"))
                    .listener(
                            GlidePalette
                                    .with(candidate.getString("photo"))
                                    .use(BitmapPalette.Profile.MUTED)
                                    .intoTextColor(name)
                                    .intoTextColor(place)
                                    .intoTextColor(shortName)
                                    .use(GlidePalette.Profile.VIBRANT)
                                    .intoTextColor(name, GlidePalette.Swatch.RGB)
                                    .intoTextColor(place, GlidePalette.Swatch.RGB)
                                    .intoTextColor(shortName, GlidePalette.Swatch.RGB)
                                    .crossfade(false)
                    )
                    .into(symbol);
            Glide.with(this)
                    .load(candidate.getString("photo"))
                    .into(profilePhoto);
            JSONObject previousElection = candidate.getJSONObject("previousElection");
            if (previousElection.getBoolean("won")) {
                previousElectionStatus.setText("Won");
                previousElectionStatus.setTextColor(getResources().getColor(R.color.green));
            } else {
                previousElectionStatus.setText("Loss");
                previousElectionStatus.setTextColor(getResources().getColor(R.color.saffron));
            }
            previousElectionVoteCount.setText(String.format("No of Votes - %s", coolFormat(previousElection.getLong("votes"), 0)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!fingerprintManager.isHardwareDetected()) {
            statusText.setText("Your Device does not have a Fingerprint Sensor");
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                statusText.setText("Fingerprint authentication permission not enabled");
            } else {
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    statusText.setText("Register at least one fingerprint in Settings");
                } else {
                    if (!keyguardManager.isKeyguardSecure()) {
                        statusText.setText("Lock screen security not enabled in Settings");
                    } else {
                        generateKey();
                        if (cipherInit()) {
                            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                            FingerprintHandler helper = new FingerprintHandler(this);
                            helper.startAuth(fingerprintManager, cryptoObject);
                        }
                    }
                }
            }
        }

    }

    private String coolFormat(double n, int iteration) {
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) %10 == 0;
        return (d < 1000?
                ((d > 99.9 || isRound || (!isRound && d > 9.99)?
                        (int) d * 10 / 10 : d + ""
                ) + "" + c[iteration])
                : coolFormat(d, iteration+1));
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }
        try {
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }
}