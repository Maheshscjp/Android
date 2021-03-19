package com.example.login.ui.leave_management;

public class LeaveDetailsPOJO {
    private String employee_name;
    private String employee_leaveCount;


    public LeaveDetailsPOJO() {
        this.employee_name = "";
        this.employee_leaveCount = "";
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }
    public String getEmployee_leaveCount() {
        return employee_leaveCount;
    }

    public void setEmployee_leaveCount(String employee_leaveCount) {
        this.employee_leaveCount = employee_leaveCount;
    }

}

