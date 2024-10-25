package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.model.ApiModels;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateEditLedgerActivity extends AppCompatActivity {

    ImageView returnButton, doneButton;
    TextView deleteTV;
    EditText ledgerNameEditText;

    private int selectedIcon = 0;
    private ImageView[] iconImageViews = new ImageView[10];

    private String mode = "create"; // 默认模式为创建
    private String ledgerId; // 编辑账本时的ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_ledger);

        initViews();

        returnButton.setOnClickListener(v -> finish());

        doneButton.setOnClickListener(v -> {
            String ledgerName = ledgerNameEditText.getText().toString();
            if (ledgerName.isEmpty()) {
                Toast.makeText(CreateEditLedgerActivity.this, "账本名称不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
            Map<String, String> map = Map.of("name", ledgerName, "image", String.valueOf(selectedIcon));

            if ("create".equals(mode)) {
                Call<ApiModels.ApiResponse<Objects>> call = apiService.createLedger(map);
                call.enqueue(new Callback<ApiModels.ApiResponse<Objects>>() {
                    @Override
                    public void onResponse(Call<ApiModels.ApiResponse<Objects>> call, retrofit2.Response<ApiModels.ApiResponse<Objects>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(CreateEditLedgerActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(CreateEditLedgerActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModels.ApiResponse<Objects>> call, Throwable t) {
                        Toast.makeText(CreateEditLedgerActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Call<ApiModels.ApiResponse<Objects>> call = apiService.updateLedger(ledgerId, map);

                call.enqueue(new Callback<ApiModels.ApiResponse<Objects>>() {
                    @Override
                    public void onResponse(Call<ApiModels.ApiResponse<Objects>> call, retrofit2.Response<ApiModels.ApiResponse<Objects>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(CreateEditLedgerActivity.this, "修改账本成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(CreateEditLedgerActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModels.ApiResponse<Objects>> call, Throwable t) {
                        Toast.makeText(CreateEditLedgerActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        deleteTV.setOnClickListener(v -> {
            ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
            Call<ApiModels.ApiResponse<Objects>> call = apiService.deleteLedger(ledgerId);
            call.enqueue(new Callback<ApiModels.ApiResponse<Objects>>() {
                @Override
                public void onResponse(Call<ApiModels.ApiResponse<Objects>> call, retrofit2.Response<ApiModels.ApiResponse<Objects>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CreateEditLedgerActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CreateEditLedgerActivity.this, "删除失败: 不能删除默认账本" , Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiModels.ApiResponse<Objects>> call, Throwable t) {
                    Toast.makeText(CreateEditLedgerActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private void initViews() {
        returnButton = findViewById(R.id.btn_back);
        doneButton = findViewById(R.id.btn_ok);
        deleteTV = findViewById(R.id.btn_delete);
        ledgerNameEditText = findViewById(R.id.name_tv);

        Intent intent = getIntent();
        mode = intent.getStringExtra("mode"); // "create" 或 "edit"

        if ("edit".equals(mode)) {
            deleteTV.setVisibility(View.VISIBLE);
            ledgerNameEditText.setText(intent.getStringExtra("ledger_name"));
            selectedIcon = intent.getIntExtra("ledger_icon", 0);
            ledgerId = intent.getStringExtra("ledger_id");
        } else {
            deleteTV.setVisibility(View.GONE);
        }

        initIconSelection();
        updateIconSelection();
    }

    private void initIconSelection() {
        iconImageViews[0] = findViewById(R.id.ledger_0);
        iconImageViews[1] = findViewById(R.id.ledger_1);
        iconImageViews[2] = findViewById(R.id.ledger_2);
        iconImageViews[3] = findViewById(R.id.ledger_3);
        iconImageViews[4] = findViewById(R.id.ledger_4);
        iconImageViews[5] = findViewById(R.id.ledger_5);
        iconImageViews[6] = findViewById(R.id.ledger_6);
        iconImageViews[7] = findViewById(R.id.ledger_7);
        iconImageViews[8] = findViewById(R.id.ledger_8);
        iconImageViews[9] = findViewById(R.id.ledger_9);

        for (int i = 0; i < iconImageViews.length; i++) {
            final int index = i;
            iconImageViews[i].setOnClickListener(v -> {
                selectedIcon = index;
                updateIconSelection();
            });
        }
    }

    // 更新图标选中的UI效果
    private void updateIconSelection() {
        for (int i = 0; i < iconImageViews.length; i++) {
            if (i == selectedIcon) {
                iconImageViews[i].setBackgroundResource(R.drawable.selected_icon_background);
            } else {
                iconImageViews[i].setBackgroundResource(0);
            }
        }
    }

}
