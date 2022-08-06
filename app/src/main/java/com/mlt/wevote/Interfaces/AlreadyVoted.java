package com.mlt.wevote.Interfaces;

import org.json.JSONObject;

import java.sql.Date;

public interface AlreadyVoted {
    void voted(JSONObject voteData, String date);
    void notVoted(String date);
    void winner(JSONObject winnerCandidate, JSONObject myCandidate, int totalVote);
}
