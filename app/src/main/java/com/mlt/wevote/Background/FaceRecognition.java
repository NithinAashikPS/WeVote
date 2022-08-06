package com.mlt.wevote.Background;

import android.os.AsyncTask;
import android.util.Log;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.mlt.wevote.Interfaces.FaceMatch;

public class FaceRecognition extends AsyncTask<Void, Void, Void> {

    private Python python;
    private PyObject _faceRecognition;

    private byte[] data;
    private String qrCodeData;

    private FaceMatch faceMatch;

    public FaceRecognition(byte[] data, String qrCodeData, FaceMatch faceMatch) {
        python = Python.getInstance();
        _faceRecognition = python.getModule("FaceRecognition");
        this.data = data;
        this.qrCodeData = qrCodeData;
        this.faceMatch = faceMatch;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        faceMatch.onMatch(_faceRecognition.callAttr("match_face", data, qrCodeData).toBoolean());
        return null;
    }
}
