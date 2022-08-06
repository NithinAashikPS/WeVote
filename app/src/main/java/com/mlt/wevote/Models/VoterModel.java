package com.mlt.wevote.Models;

import org.json.JSONArray;

public class VoterModel {
    private String photo;
    private String epicNumber;
    private String voterName;

    public VoterModel(String photo, String epicNumber, String voterName) {
        this.photo = photo;
        this.epicNumber = epicNumber;
        this.voterName = voterName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEpicNumber() {
        return epicNumber;
    }

    public void setEpicNumber(String epicNumber) {
        this.epicNumber = epicNumber;
    }

    public String getVoterName() {
        return voterName;
    }

    public void setVoterName(String voterName) {
        this.voterName = voterName;
    }
}
