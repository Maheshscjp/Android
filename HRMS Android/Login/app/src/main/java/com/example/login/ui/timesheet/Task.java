package com.example.login.ui.timesheet;

public class Task {

    private String assign_id;
    private String user_id;
    private String product_id;
    private String time_req;
    private String task_type_name;
    private String task_alt_type;
    private String task_code;
    private String status;
    private String start_time;
    private String end_time;
    private String sku_code;
    private String product_name;
    private String product_quantity;
    private String product_address;
    private String time_required;
    private String firstname;
    private String lastname;
    private char is_sub_task;
    private String contact;


    public Task() {
        super();
        // TODO Auto-generated constructor stub
    }

    public String getAssign_id() {
        return assign_id;
    }

    public void setAssign_id(String assign_id) {
        this.assign_id = assign_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getTime_req() {
        return time_req;
    }

    public void setTime_req(String time_req) {
        this.time_req = time_req;
    }

    public String getTask_type_name() {
        return task_type_name;
    }

    public void setTask_type_name(String task_type_name) {
        this.task_type_name = task_type_name;
    }

    public String getTask_alt_type() {
        return task_alt_type;
    }

    public void setTask_alt_type(String task_alt_type) {
        this.task_alt_type = task_alt_type;
    }

    public String getTask_code() {
        return task_code;
    }

    public void setTask_code(String task_code) {
        this.task_code = task_code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getSku_code() {
        return sku_code;
    }

    public void setSku_code(String sku_code) {
        this.sku_code = sku_code;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(String product_quantity) {
        this.product_quantity = product_quantity;
    }

    public String getProduct_address() {
        return product_address;
    }

    public void setProduct_address(String product_address) {
        this.product_address = product_address;
    }

    public String getTime_required() {
        return time_required;
    }

    public void setTime_required(String time_required) {
        this.time_required = time_required;
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

    public char getIs_sub_task() {
        return is_sub_task;
    }

    public void setIs_sub_task(char is_sub_task) {
        this.is_sub_task = is_sub_task;
    }

    @Override
    public String toString() {
        return "Task{" +
                "assign_id='" + assign_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", product_id='" + product_id + '\'' +
                ", time_req='" + time_req + '\'' +
                ", task_type_name='" + task_type_name + '\'' +
                ", task_alt_type='" + task_alt_type + '\'' +
                ", task_code='" + task_code + '\'' +
                ", status='" + status + '\'' +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", sku_code='" + sku_code + '\'' +
                ", product_name='" + product_name + '\'' +
                ", product_quantity='" + product_quantity + '\'' +
                ", product_address='" + product_address + '\'' +
                ", time_required='" + time_required + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", contact='" + contact + '\'' +
                ", is_sub_task=" + is_sub_task +
                '}';
    }
}
