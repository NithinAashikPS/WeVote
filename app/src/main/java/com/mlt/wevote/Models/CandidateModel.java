package com.mlt.wevote.Models;

public class CandidateModel {

    private String name;
    private int party;
    private String candidate;
    private String photo;

    private String shortName;
    private String symbol;

    public CandidateModel() {
    }

    public CandidateModel(int party, String candidate) {
        this.party = party;
        this.candidate = candidate;
    }

    public CandidateModel(String name, int party, String photo) {
        this.name = name;
        this.party = party;
        this.photo = photo;
    }

    public CandidateModel(String name, int party, String candidate, String photo, String shortName, String symbol) {
        this.name = name;
        this.party = party;
        this.candidate = candidate;
        this.photo = photo;
        this.shortName = shortName;
        this.symbol = symbol;
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getParty() {
        return party;
    }

    public void setParty(int party) {
        this.party = party;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
