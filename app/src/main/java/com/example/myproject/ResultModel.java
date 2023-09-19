package com.example.myproject;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;

@IgnoreExtraProperties
public class ResultModel implements Serializable {
    public String id;
    public String resultName;
    public ArrayList<TransmissionModel> transmissionModels;

    public ResultModel() {
    }
}
