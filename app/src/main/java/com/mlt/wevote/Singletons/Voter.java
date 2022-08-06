package com.mlt.wevote.Singletons;

import android.util.Log;

import com.mlt.wevote.Interfaces.VoterListener;
import com.mlt.wevote.Models.VoterModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Voter {

    private static Voter voter = null;
    private static List<VoterModel> voterModelList;
    private static JSONObject party;
    private static JSONObject candidate;
    private static JSONObject _voter;

    private static JSONObject voterVoteData = new JSONObject();

    private VoterListener voterListener;
    private static String candidateId;
    private static String partyId;
    private static String epicNumber;

    private static String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        Voter.phone = phone;
    }

    public String getEpicNumber() {
        return epicNumber;
    }

    public void setEpicNumber(String epicNumber) {
        Voter.epicNumber = epicNumber;
    }

    public static Voter getInstance() {
        if (voter == null)
            voter = new Voter();
        return voter;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        Voter.candidateId = candidateId;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        Voter.partyId = partyId;
    }

    public Voter setListener(VoterListener voterListener) {
        this.voterListener = voterListener;
        return this;
    }

    public void setVoter(JSONObject voterData) {
        voterModelList = new ArrayList<>();
        Voter._voter = voterData;
        try {
            voterModelList.add(new VoterModel(
                    voterData.getString("photo"),
                    voterData.getString("epicNumber"),
                    voterData.getString("voterName")
            ));
            voterListener.onChanged(voterModelList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<VoterModel> getVoterModelList() {
        return voterModelList;
    }

    public JSONObject getVoter() {
        return _voter;
    }

    public JSONObject getParty() {
        return party;
    }

    public void setParty(JSONObject party) {
        Voter.party = party;
    }

    public JSONObject getCandidate() {
        return candidate;
    }

    public void setCandidate(JSONObject candidate) {
        Voter.candidate = candidate;
    }

    public JSONObject getVoterVoteData() {
        try {
            voterVoteData.put("photo", _voter.getString("photo"));
            voterVoteData.put("epicNumber", _voter.getString("epicNumber"));
            voterVoteData.put("voterName", _voter.getString("voterName"));
            voterVoteData.put("candidate", candidateId);
            voterVoteData.put("party", partyId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return voterVoteData;
    }
}
