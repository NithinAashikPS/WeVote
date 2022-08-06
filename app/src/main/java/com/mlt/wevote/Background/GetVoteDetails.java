package com.mlt.wevote.Background;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.google.firebase.auth.FirebaseAuth;
import com.mlt.wevote.Contracts.WeVote;
import com.mlt.wevote.Interfaces.AlreadyVoted;
import com.mlt.wevote.R;
import com.mlt.wevote.Singletons.Voter;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

public class GetVoteDetails extends AsyncTask<Void, Void, Void> {

    private Web3j web3j;
    private Credentials credentials;
    private WeVote weVote;

    private Voter voter;
    private FirebaseAuth auth;
    private Python python;
    private PyObject _decoder;

    private AlreadyVoted alreadyVoted;

    private Timestamp timestamp;
    private String date;

    public GetVoteDetails(Activity activity, AlreadyVoted alreadyVoted) {

        this.alreadyVoted = alreadyVoted;
        credentials = Credentials.create("db0eddda965e7d7518818d75164f1bd28fce2a505b54444ae3e98d39e156f9dc");
        web3j = Web3j.build(new HttpService(activity.getString(R.string.infura_node)));
        weVote = WeVote.load(
                activity.getString(R.string.contract_address),
                web3j,
                credentials,
                ManagedTransaction.GAS_PRICE,
                Contract.GAS_LIMIT);

        python = Python.getInstance();
        _decoder = python.getModule("Decoder");

        voter = Voter.getInstance();
        auth = FirebaseAuth.getInstance();

        timestamp = new Timestamp(System.currentTimeMillis());
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected Void doInBackground(Void... voids) {

        try {
            Long resultDate = weVote.resultDate().sendAsync().get().longValue();
            String string = weVote.getVoter(voter.getEpicNumber()).sendAsync().get();
            date = new SimpleDateFormat("dd/MM/yyyy").format(new Date(resultDate*1000));
            JSONObject myCand = new JSONObject(_decoder.callAttr("decode", string, auth.getUid()).toString());
            if (timestamp.getTime()/1000 < resultDate) {
                try {
                    alreadyVoted.voted(
                            myCand,
                            date);
                } catch (Exception e) {
                    alreadyVoted.notVoted(date);
                }
            } else {
                JSONObject winnerCandidate = new JSONObject();
                JSONObject myCandidate = new JSONObject();
                String myCandidateId = myCand.getString("candidate");
                Tuple3<String, BigInteger, BigInteger> winner = weVote.getWinner().sendAsync().get();
                Tuple2<BigInteger, BigInteger> myCandi = weVote.getVoteCount(myCandidateId).sendAsync().get();
                winnerCandidate.put("id", winner.component1());
                myCandidate.put("id", myCandidateId);
                winnerCandidate.put("voteCount", winner.component2().intValue());
                myCandidate.put("voteCount",  myCandi.component1().intValue());
                alreadyVoted.winner(winnerCandidate, myCandidate, winner.component3().intValue());
            }
        } catch (Exception e) {
            alreadyVoted.notVoted(date);
            e.printStackTrace();
        }

        return null;
    }
}
