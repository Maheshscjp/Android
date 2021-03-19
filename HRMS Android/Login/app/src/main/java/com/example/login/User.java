package com.example.login;

public class User {


    private String userid,firstname, lastname, email, password,role,dob,tlid, tlname,tlEmailId,createdon,updatedon,isactive,joiningdate,gender;
   private String contactno,empcode,aadharno,panno, warehouse;

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getTlname() {
        return tlname;
    }

    public String getTlEmailId() {
        return tlEmailId;
    }

    public void setTlEmailId(String tlEmailId) {
        this.tlEmailId = tlEmailId;
    }

    public void setTlname(String tlname) {
        this.tlname = tlname;
    }

    public User(String userid, String firstname, String lastname, String email, String password, String role, String tlid, String tlname, String tlEmailId, String empcode, String warehouse) {
        this.userid = userid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.tlid = tlid;
        this.tlname = tlname;
        this.tlEmailId = tlEmailId;
        this.empcode = empcode;
        this.warehouse = warehouse;
    }

    public User(String userid, String firstname, String lastname, String email, String password, String role, String dob, String tlid, String tlname, String createdon, String updatedon, String isactive, String joiningdate, String gender, String contactno, String empcode, String aadharno, String panno) {
        this.userid = userid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;//
        this.role = role;
        this.dob = dob;//
        this.tlid = tlid;
        this.tlname = tlname;
        this.createdon = createdon;//
        this.updatedon = updatedon;//
        this.isactive = isactive;//
        this.joiningdate = joiningdate;//
        this.gender = gender;//
        this.contactno = contactno;//
        this.empcode = empcode;
        this.aadharno = aadharno;//
        this.panno = panno;//
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getTlid() {
        return tlid;
    }

    public void setTlid(String tlid) {
        this.tlid = tlid;
    }

    public String getCreatedon() {
        return createdon;
    }

    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }

    public String getUpdatedon() {
        return updatedon;
    }

    public void setUpdatedon(String updatedon) {
        this.updatedon = updatedon;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public String getJoiningdate() {
        return joiningdate;
    }

    public void setJoiningdate(String joiningdate) {
        this.joiningdate = joiningdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContactno() {
        return contactno;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

    public String getEmpcode() {
        return empcode;
    }

    public void setEmpcode(String empcode) {
        this.empcode = empcode;
    }

    public String getAadharno() {
        return aadharno;
    }

    public void setAadharno(String aadharno) {
        this.aadharno = aadharno;
    }

    public String getPanno() {
        return panno;
    }

    public void setPanno(String panno) {
        this.panno = panno;
    }


}
