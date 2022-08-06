package com.mlt.wevote.Background;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.google.firebase.auth.FirebaseAuth;
import com.mlt.wevote.Contracts.WeVote;
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

import java.util.concurrent.ExecutionException;

public class UpdateVote extends AsyncTask<Void, Void, Void> {

    private Web3j web3j;
    private Credentials credentials;
    private WeVote weVote;

    private FirebaseAuth auth;
    private JSONObject voterVoteData;

    private Voter voter;

    private Python python;
    private PyObject _encoder;
    private PyObject authenticator;

    private PyObject sendSms;

    private VoteUpdated voteUpdated;


    public UpdateVote(Activity activity, VoteUpdated voteUpdated) {

        this.voteUpdated = voteUpdated;

        credentials = Credentials.create("db0eddda965e7d7518818d75164f1bd28fce2a505b54444ae3e98d39e156f9dc");
        web3j = Web3j.build(new HttpService(activity.getString(R.string.infura_node)));
        weVote = WeVote.load(
                activity.getString(R.string.contract_address),
                web3j,
                credentials,
                ManagedTransaction.GAS_PRICE,
                Contract.GAS_LIMIT);


        python = Python.getInstance();
        _encoder = python.getModule("Decoder");
        authenticator = python.getModule("Authenticator");
        sendSms = python.getModule("SendSMS");
        voter = Voter.getInstance();
        voterVoteData = voter.getVoterVoteData();

        auth = FirebaseAuth.getInstance();

    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            TransactionReceipt transactionReceipt = weVote.writeVote(
                    voterVoteData.getString("epicNumber"),
                    _encoder.callAttr("encode", String.valueOf(voterVoteData), auth.getUid()).toString(),
                    voter.getCandidateId()).sendAsync().get();
            sendSms.callAttr("send",
                    voter.getPhone(),
                    String.format("Your vote transaction link is https://ropsten.etherscan.io/tx/%s", transactionReceipt.getTransactionHash()));
//            Message message = Message.creator(
//                    new PhoneNumber(authenticator.callAttr("get_phone_number", voterVoteData.getString("epicNumber")).toString()),
//                    new PhoneNumber("+17579977060"),
//                    String.format("https://ropsten.etherscan.io/tx/%s",c)
//            ).create();
            voteUpdated.onUpdated();
        } catch (Exception e) {
            e.printStackTrace();
            voteUpdated.onError();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
    }
}
