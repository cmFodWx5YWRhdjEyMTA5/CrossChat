package com.example.kamaloli.crosschat.visulizationOfPeopleToEachOther;

/**
 * Created by KAMAL OLI on 02/03/2017.
 */

public class ParameterAnalysis {
    public  String url;
    public int radius;
    public String authenticationToken;
    public float latitude,longitude;
   public ParameterAnalysis(String url, int distance, String authenticationToken,float latitude,float longitude){
       this.url=url;
       radius=distance;
       this.latitude=latitude;
       this.longitude=longitude;
       this.authenticationToken=authenticationToken;
    }
    public ParameterAnalysis(){}
}
