package com.example.kamaloli.crosschat.visulizationOfPeopleToEachOther;

/**
 * Created by KAMAL OLI on 03/03/2017.
 */

public class UserPropertiesToBeDisplayedOnMap {
    public String fullName;
    public String highestQualification;
    public String emailAddress;
    public String userName;
    public String currentAddress;
    public String permanentAddress;
    public String fieldOfInterest;
    public String specialization;
    public String latitude;
    public String longitude;
    public UserPropertiesToBeDisplayedOnMap(String fullName,String highestQualification,
                                            String emailAddress,String userName,String currentAddress,
                                            String permanentAddress,String fieldOfInterest,String specialization,
                                            String latitude,String longitude){
        this.fullName=fullName;
        this.highestQualification=highestQualification;
        this.emailAddress=emailAddress;
        this.userName=userName;
        this.currentAddress=currentAddress;
        this.permanentAddress=permanentAddress;
        this.fieldOfInterest=fieldOfInterest;
        this.specialization=specialization;
        this.latitude=latitude;
        this.longitude=longitude;
    }
    public UserPropertiesToBeDisplayedOnMap(){}
}
