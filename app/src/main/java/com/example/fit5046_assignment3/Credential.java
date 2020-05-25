package com.example.fit5046_assignment3;

public class Credential {
   // private Users userid;

    private String credentialId;
    private String userName;
    private String passwordHash;
    private String signUpDate;
    private static int credentialIdTemp = 3;

    public Credential(){
        credentialIdTemp++;
        credentialId = Integer.toString(credentialIdTemp);
    }

    public String getCredentialId(){
        return credentialId;
    }

    public void setCredentialId(String credentialId){
        this.credentialId = credentialId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSignUpDate() {
        return signUpDate;
    }

    public void setSignUpDate(String signUpDate) {
        this.signUpDate = signUpDate;
    }


/*    public Users getUserid() {
        return userid;
    }

    public void setUserid(Person userid) {
        this.userid = userid;
    }*/
}
