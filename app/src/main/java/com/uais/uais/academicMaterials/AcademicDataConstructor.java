package com.uais.uais.academicMaterials;

public class AcademicDataConstructor {

    public String header, details;
    public int sms;

    public AcademicDataConstructor(String header, String Details){
        this.header = header;
        this.details = Details;
    }
    public AcademicDataConstructor(String header){
        this.header = header;
    }
    public AcademicDataConstructor(int message){
        this.sms = message;
    }

    public void setHeader(String h){
        this.header = h;
    }
    public void setDetails(String d){
        this.details = d;
    }
    public String getHeader(){return header;}
    public String getDetails(){return details;}
    public int getSms(){return sms;}
}
