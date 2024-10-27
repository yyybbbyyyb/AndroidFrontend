package com.example.myapplication.network;

import com.example.myapplication.model.ApiModels;
import com.example.myapplication.model.ApiModels.ApiResponse;
import com.example.myapplication.model.ApiModels.LoginRequest;
import com.example.myapplication.model.ApiModels.LoginResponse;
import com.example.myapplication.model.MyBillData;
import com.example.myapplication.model.MyBudgetData;
import com.example.myapplication.model.MyLedgerData;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import kotlin.ParameterName;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


public interface ApiService {


    @POST("login/")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest loginRequest);

    @POST("token/refresh/")
    Call<ApiResponse<LoginResponse>> refresh(@Body Map<String, String> refreshToken);

    @GET("token/check/")
    Call<ApiResponse<Objects>> checkToken();

    @POST("register/")
    Call<ApiResponse<Objects>> register(@Body LoginRequest loginRequest);

    @GET("user/")
    Call<ApiResponse<ApiModels.UserInfoResponse>> getUserInfo();

    @PUT("user/")
    Call<ApiResponse<Objects>> updateUserInfo(@Body Map<String, String> map);

    @Multipart
    @PUT("user/")
    Call<ApiResponse<Objects>> updateUserInfo(@Part MultipartBody.Part avatar);

    @POST("logout/")
    Call<ApiResponse<Objects>> logout(@Body Map<String, String> map);

    // ----------------------------

    @GET("ledgers/")
    Call<ApiResponse<List<MyLedgerData>>> getLedgers();

    @GET("ledgers/{id}/")
    Call<ApiResponse<MyLedgerData>> getLedger(@Path("id") String ledgerId);

    @PUT("ledgers/{id}/")
    Call<ApiResponse<Objects>> updateLedger(@Path("id") String ledgerId, @Body Map<String, String> map);

    @POST("ledgers/")
    Call<ApiResponse<Objects>> createLedger(@Body Map<String, String> map);

    @DELETE("ledgers/{id}/")
    Call<ApiResponse<Objects>> deleteLedger(@Path("id") String ledgerId);

    // ----------------------------

    @GET("bills/")
    Call<ApiResponse<List<MyBillData>>> getBills(@QueryMap() Map<String, String> map);

    @GET("bills/{id}/")
    Call<ApiResponse<MyBillData>> getBill(@Path("id") String billId);

    @DELETE("bills/{id}/")
    Call<ApiResponse<Objects>> deleteBill(@Path("id") String billId);

    @POST("bills/")
    Call<ApiResponse<Objects>> createBill(@Body ApiModels.BillCreateRequest billCreateRequest);

    @PUT("bills/{id}/")
    Call<ApiResponse<Objects>> updateBill(@Path("id") String billId, @Body ApiModels.BillCreateRequest billCreateRequest);

    // ----------------------------

    @GET("budgets/")
    Call<ApiResponse<List<MyBudgetData>>> getBudgets(@QueryMap() Map<String, String> map);

    @GET("budgets/{id}/")
    Call<ApiResponse<MyBudgetData>> getBudget(@Path("id") String budgetId);

    @POST("budgets/")
    Call<ApiResponse<Objects>> createBudget(@Body ApiModels.BudgetCreateRequest budgetCreateRequest);

    @PUT("budgets/{id}/")
    Call<ApiResponse<Objects>> updateBudget(@Path("id") String budgetId, @Body ApiModels.BudgetCreateRequest budgetCreateRequest);

    @DELETE("budgets/{id}/")
    Call<ApiResponse<Objects>> deleteBudget(@Path("id") String budgetId);

    // ----------------------------

    @GET("monthly-report/")
    Call<ApiResponse<ApiModels.ReportResponse>> getMonthlyReport(@QueryMap() Map<String, String> map);

    @GET("daily-report/")
    Call<ApiResponse<List<ApiModels.DailyReportResponse>>> getDailyReport(@QueryMap() Map<String, String> map);

    @GET("total-budget/")
    Call<ApiResponse<ApiModels.TotalBudgetResponse>> getTotalBudget(@QueryMap() Map<String, String> map);

    @GET("total-expense-by-category/")
    Call<ApiResponse<ApiModels.TotalExpenseByCategoryResponse>> getTotalExpenseByCategory(@QueryMap() Map<String, String> map);

}
