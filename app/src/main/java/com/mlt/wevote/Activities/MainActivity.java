package com.mlt.wevote.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mlt.wevote.Adapters.CandidateAdapter;
import com.mlt.wevote.Background.GetVoteDetails;
import com.mlt.wevote.Interfaces.AlreadyVoted;
import com.mlt.wevote.Models.CandidateModel;
import com.mlt.wevote.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private List<CandidateModel> candidateModels;
    private CandidateAdapter candidateAdapter;
    private RecyclerView recyclerView;

    private TextView welcomeWish;
    private TextView voterName;
    private View voted;
    private View notVoted;
    private TextView voteDate;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;

    private Python python;
    private PyObject _database;

    private ShapeableImageView profilePhoto;
    private ImageView symbol;
    private TextView name;
    private TextView place;
    private TextView shortName;
    private TextView voteMsg;
    private LinearLayout voteCountContainer;

    private TextView winnerStatus;
    private ProgressBar winnerData;
    private TextView myStatus;
    private ProgressBar myData;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        winnerStatus = findViewById(R.id.winner_status);
        winnerData = findViewById(R.id.winner_data);
        myStatus = findViewById(R.id.my_status);
        myData = findViewById(R.id.my_data);

        profilePhoto = findViewById(R.id.profile_image);
        symbol = findViewById(R.id.symbol);
        name = findViewById(R.id.name);
        place = findViewById(R.id.place);
        shortName = findViewById(R.id.short_name);
        voteDate = findViewById(R.id.vote_date);
        voteCountContainer = findViewById(R.id.vote_count_container);

        recyclerView = findViewById(R.id.candidate_recyclerview);
        welcomeWish = findViewById(R.id.welcome_wish);
        voterName = findViewById(R.id.voter_name);
        voted = findViewById(R.id.voted);
        notVoted = findViewById(R.id.not_voted);
        voteMsg = findViewById(R.id.vote_msg);

        python = Python.getInstance();
        _database = python.getModule("Database");
        auth = FirebaseAuth.getInstance();

        candidateModels = new ArrayList<>();
        candidateAdapter = new CandidateAdapter(candidateModels);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(candidateAdapter);
        welcomeWish.setText(getTimeFromAndroid());
        voterName.setText(_database.callAttr("get_voter_detail", auth.getUid()).toString());

        database = FirebaseDatabase.getInstance("https://wevote-2c7c4-default-rtdb.asia-southeast1.firebasedatabase.app");
        reference = database.getReference("candidates");

        new GetVoteDetails(MainActivity.this, new AlreadyVoted() {
            @Override
            public void voted(JSONObject voteData, String date) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notVoted.setVisibility(View.GONE);
                        voted.setVisibility(View.VISIBLE);
                        voteDate.setVisibility(View.VISIBLE);
                        voteCountContainer.setVisibility(View.GONE);
                        voteDate.setText(String.format("Result will be updated on %s.", date));
                        String data = null;
                        try {
                            data = _database.callAttr("get_election_details", voteData.getString("candidate"), voteData.getString("party")).toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadData("You Voted For.", data);

                    }
                });
            }

            @Override
            public void notVoted(String date) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notVoted.setVisibility(View.VISIBLE);
                        voted.setVisibility(View.GONE);
                        voteDate.setVisibility(View.VISIBLE);
                        voteCountContainer.setVisibility(View.GONE);
                        voteDate.setText(String.format("Result will be updated on %s.", date));
                        getCandidates();
                    }
                });
            }

            @Override
            public void winner(JSONObject winnerCandidate, JSONObject myCandidate, int totalVote) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notVoted.setVisibility(View.GONE);
                        voted.setVisibility(View.VISIBLE);
                        voteDate.setVisibility(View.GONE);
                        voteCountContainer.setVisibility(View.VISIBLE);
                        winnerData.setMax(totalVote);
                        myData.setMax(totalVote);
                        String winnerCandidateData = null;
                        String myCandidateData = null;
                        try {
                            winnerCandidateData = _database.callAttr("get_candidate_and_party", winnerCandidate.getString("id")).toString();
                            myCandidateData = _database.callAttr("get_candidate_and_party", myCandidate.getString("id")).toString();
                            JSONArray WCD = new JSONArray(winnerCandidateData);
                            JSONArray MCD = new JSONArray(myCandidateData);
                            winnerData.setProgress(winnerCandidate.getInt("voteCount"));
                            winnerStatus.setText(String.format(Locale.ENGLISH,
                                    "%d/%d\n%s\n%s",
                                    winnerCandidate.getInt("voteCount"),
                                    totalVote,
                                    WCD.getJSONObject(1).getString("name"),
                                    WCD.getJSONObject(0).getString("shortName")
                            ));
                            myData.setProgress(myCandidate.getInt("voteCount"));
                            myStatus.setText(String.format(Locale.ENGLISH,
                                    "%d/%d\n%s\n%s",
                                    myCandidate.getInt("voteCount"),
                                    totalVote,
                                    MCD.getJSONObject(1).getString("name"),
                                    MCD.getJSONObject(0).getString("shortName")
                            ));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadData("Winner of the Election.", winnerCandidateData);
                    }
                });
            }
        }).execute();
    }

    private void loadData(String msg, String data) {
        voteMsg.setText(msg);
        try {

            JSONArray voteDetails = new JSONArray(data);
            JSONObject party = voteDetails.getJSONObject(0);
            JSONObject candidate = voteDetails.getJSONObject(1);
            shortName.setText(party.getString("shortName"));
            name.setText(candidate.getString("name"));
            place.setText(candidate.getString("place"));
            Glide.with(MainActivity.this)
                    .load(party.getString("symbol"))
                    .into(symbol);
            Glide.with(MainActivity.this)
                    .load(candidate.getString("photo"))
                    .into(profilePhoto);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getTimeFromAndroid() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            return ("Good Morning");
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            return ("Good Afternoon");
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            return ("Good Evening");
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            return ("Good Night");
        }
        return null;
    }

    private void getCandidates() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                candidateModels.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        CandidateModel movieModel = snapshot.getValue(CandidateModel.class);
                        JSONObject party = new JSONObject(_database.callAttr("get_party", movieModel.getParty()).toString());
                        movieModel.setShortName(party.getString("shortName"));
                        movieModel.setSymbol(party.getString("symbol"));
                        movieModel.setCandidate(snapshot.getKey());
                        candidateModels.add(movieModel);
                    } catch (Exception e) {
                        Log.i("hfdggdjf", e.getMessage());
                        getCandidates();
                    }
                }
                candidateAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}