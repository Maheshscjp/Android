package com.example.login;


import com.google.gson.JsonObject;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface NodeJS {
    //RxJAVA
    @POST("userDetails/authenticate")
    Observable<JsonObject> loginUser(@Body JsonObject object);

    @POST("userDetails/register")
    Observable<JsonObject> registerUser(@Body JsonObject object);

    @POST("userDetails/getUser")
    Observable<JsonObject> getSuperUserList(@Body JsonObject object);

    @GET("taskDetails/getTaskType")
    Observable<JsonObject>  getTaskType();

    @POST("userDetails/getUserDetailsByEmpName")
    Observable<JsonObject> getEmployeeDetail(@Body JsonObject object);

    @POST("userDetails/UpdateEmployeeDetails")
    Observable<JsonObject> UpdateEmployeeDetails(@Body JsonObject object);

    @POST("userDetails/deleteUser")
    Observable<JsonObject> deleteUser(@Body JsonObject object);

    @GET("userDetails/getWarehouseMaster")
    Observable<JsonObject> getWarehouseList();

    @GET("userDetails/getShiftList")
    Observable<JsonObject> getWorkingShiftList();

    @POST("leaveDetails/pendingLeave")
    Observable<JsonObject> getPendingLeaveCount(@Body JsonObject object);

    @POST("leaveDetails/leaveRegister")
    Observable<JsonObject> registerLeaveDetail(@Body JsonObject object);

    @POST("leaveDetails/getEmployeeLeaves")
    Observable<JsonObject> getEmployeeLeaves(@Body JsonObject object);

    @POST("leaveDetails/getLeaveByYear")
    Observable<JsonObject> getLeaveByYear(@Body JsonObject object);

    @POST("leaveDetails/getLeaveStatus")
    Observable<JsonObject> getLeaveStatus(@Body JsonObject object);

    @POST("leaveDetails/updateLeaveReq")
    Observable<JsonObject> updateLeaveReq(@Body JsonObject object);

    //PDF service
    @POST("paySlipDetails/getPDF")
    Observable<JsonObject> downloadPDF(@Body JsonObject object);

    //Forget password services
    @POST("forgetPassword/verifyUser")
    Observable<JsonObject> verifyUser(@Body JsonObject object);//email id , dob

    @POST("forgetPassword/verifyOTP")
    Observable<JsonObject> verifyOTP(@Body JsonObject object);//emaial id, otp

    @POST("forgetPassword/updatePassword")
    Observable<JsonObject> updatePassword(@Body JsonObject object);//email id , password

    @POST("userDetails/resetpassword")
    Observable<JsonObject> resetpassword(@Body JsonObject object);

    @GET("timesheetDetails/getProjectList")
    Observable<JsonObject> getProjectList();

    @POST("timesheetDetails/addTimesheetData")
    Observable<JsonObject> addTimesheetData(@Body JsonObject object);

    @POST("timesheetDetails/getTimesheetData")
    Observable<JsonObject> getTimesheetData(@Body JsonObject object);


    //assetManagement
    @GET("assetDetails/getCategory")
    Observable<JsonObject> getAssetCategory();

    @POST("assetDetails/submitAsset")
    Observable<JsonObject> submitAsset(@Body JsonObject object);

    @POST("assetDetails/getAllAsset")
    Observable<JsonObject> getAllAsset(@Body JsonObject object);


    @POST("assetDetails/addSerial")
    Observable<JsonObject>  addSerial(@Body JsonObject object);


    @POST("assetDetails/updateAsset")
    Observable<JsonObject>  updateAsset(@Body JsonObject object);


    @POST("assetDetails/getAssignedAssetToUser")
    Observable<JsonObject>  getAssignedAssetToUser(@Body JsonObject object);



    @POST("assetDetails/getAllAvailableAsset")
    Observable<JsonObject>  getAllAvailableAsset(@Body JsonObject object);


    @POST("assetDetails/assignAsset")
    Observable<JsonObject>  assignAsset(@Body JsonObject object);



    @POST("assetDetails/updateAssignedAssetToUser")
    Observable<JsonObject>  updateAssignedAssetToUser(@Body JsonObject object);


    //Task


    @POST("taskDetails/getAvailTask")
    Observable<JsonObject>  getTaskList(@Body JsonObject jsonObject);

    @GET("taskDetails/getAllTaskForTrack")
    Observable<JsonObject>  getAllTaskForTrack();

    @POST("taskDetails/assignTask")
    Observable<JsonObject>  assignTask(@Body JsonObject object);

    @POST("taskDetails/getAssignedTaskByUserId")
    Observable<JsonObject>  getAssignedTaskByUserId(@Body JsonObject object);

    @POST("taskDetails/updateTaskDetails")
    Observable<JsonObject>  updateTaskDetails(@Body JsonObject object);

    @POST("taskDetails/getstatusCount")
    Observable<JsonObject>  getstatusCount(@Body JsonObject object);

    @POST("taskDetails/taskReport")
    Observable<JsonObject>  getTaskReport(@Body JsonObject object);

    @POST("taskDetails/getPendingTaskDetails")
    Observable<JsonObject> getPendingTaskDetails(@Body JsonObject object);

    @POST("taskDetails/submitScanData")
    Observable<JsonObject> submitScanData(@Body JsonObject object);

}
