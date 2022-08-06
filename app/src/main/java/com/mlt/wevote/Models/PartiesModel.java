package com.mlt.wevote.Models;

public class PartiesModel {

    private String fullName;
    private String shortName;
    private String symbol;

    public PartiesModel() {
    }

    public PartiesModel(String fullName, String shortName, String symbol) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.symbol = symbol;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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
}
