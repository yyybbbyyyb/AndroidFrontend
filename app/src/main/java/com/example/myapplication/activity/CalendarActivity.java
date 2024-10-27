package com.example.myapplication.activity;

import static com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.model.ApiModels;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.ViewContainer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class CalendarActivity extends AppCompatActivity {
    private CalendarView calendarView;
    ImageView back;
    TextView date;

    private Map<LocalDate, Pair<Double, Double>> dailyData = new HashMap<>();
    private int year;
    private int month;
    private int ledger_id;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        back = findViewById(R.id.btn_back);
        date = findViewById(R.id.tv_date);

        Intent intent = getIntent();
        year = intent.getIntExtra("year", 0);
        month = intent.getIntExtra("month", 0);
        ledger_id = intent.getIntExtra("ledger_id", 0);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        date.setText(year + "年" + month + "月");

        // 设置日历的开始和结束月份
        YearMonth startMonth = YearMonth.of(year, month);
        YearMonth endMonth = YearMonth.of(year, month);
        DayOfWeek firstDayOfWeek = daysOfWeek(DayOfWeek.SUNDAY).get(0);

        // 初始化日历
        calendarView.setup(startMonth, endMonth, firstDayOfWeek);
        calendarView.scrollToMonth(startMonth);

        // 加载数据
        loadDataFromApi();

        // 设置自定义的 MonthDayBinder
        calendarView.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @Override
            public DayViewContainer create(View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(DayViewContainer container, CalendarDay day) {
                // 仅绑定当月日期
                if (day.getPosition() == DayPosition.MonthDate) {
                    container.dayText.setText(String.valueOf(day.getDate().getDayOfMonth()));

                    // 根据日期数据设置收入和支出的显示
                    Pair<Double, Double> data = dailyData.get(day.getDate());
                    Double income = data != null ? data.first : 0;
                    Double expense = data != null ? data.second : 0;
                    if (income > 0) {
                        container.income.setText("+" + income);
                        container.income.setVisibility(View.VISIBLE);
                    } else {
                        container.income.setVisibility(View.GONE);
                    }
                    if (expense > 0) {
                        container.expense.setText("-" + expense);
                        container.expense.setVisibility(View.VISIBLE);
                    } else {
                        container.expense.setVisibility(View.GONE);
                    }
                } else {
                    // 非当前月的日期，不显示数据
                    container.dayText.setText("");
                    container.income.setVisibility(View.GONE);
                    container.expense.setVisibility(View.GONE);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadDataFromApi() {
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        Map<String, String> map = new HashMap<>();
        map.put("ledger_id", String.valueOf(ledger_id));
        map.put("year", String.valueOf(year));
        map.put("month", String.valueOf(month));
        Call<ApiModels.ApiResponse<List<ApiModels.DailyReportResponse>>> call = apiService.getDailyReport(map);

        call.enqueue(new retrofit2.Callback<ApiModels.ApiResponse<List<ApiModels.DailyReportResponse>>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<List<ApiModels.DailyReportResponse>>> call, retrofit2.Response<ApiModels.ApiResponse<List<ApiModels.DailyReportResponse>>> response) {
                if (response.isSuccessful()) {
                    List<ApiModels.DailyReportResponse> dailyReportResponses = response.body().getData();
                    for (ApiModels.DailyReportResponse dailyReportResponse : dailyReportResponses) {
                        int day = dailyReportResponse.getDay();
                        LocalDate date = LocalDate.of(year, month, day);
                        dailyData.put(date, new Pair<>(dailyReportResponse.getIncome(), dailyReportResponse.getExpense()));
                    }
                    calendarView.notifyCalendarChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<List<ApiModels.DailyReportResponse>>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public static class DayViewContainer extends ViewContainer {
        TextView dayText;
        TextView income;
        TextView expense;

        public DayViewContainer(View view) {
            super(view);
            dayText = view.findViewById(R.id.dayText);
            income = view.findViewById(R.id.income);
            expense = view.findViewById(R.id.expense);
        }
    }

    public static class Pair<F, S> {
        public final F first;
        public final S second;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
    }
}
