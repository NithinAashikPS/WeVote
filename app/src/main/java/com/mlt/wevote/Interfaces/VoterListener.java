package com.mlt.wevote.Interfaces;

import com.mlt.wevote.Models.VoterModel;

import java.util.ArrayList;
import java.util.List;

public interface VoterListener {
    void onChanged(List<VoterModel> voterModelList);
}
