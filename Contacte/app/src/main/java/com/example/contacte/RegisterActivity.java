package com.example.contacte;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText nameInput, emailInput, phoneInput;
    Button submitBtn;
    String deviceId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        submitBtn = findViewById(R.id.submitBtn);

        deviceId = getDeviceIdentifier();

        submitBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String email = emailInput.getText().toString();
            String phone = phoneInput.getText().toString();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show();
            } else {
                saveUser(name, phone, email, deviceId);
            }
        });
    }

    private String getDeviceIdentifier() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        }
    }

    private void saveUser(String name, String phone, String email, String imei) {
        UserModel user = new UserModel(name, phone, email, imei);

        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
        api.addUser(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(RegisterActivity.this, "Utilisateur enregistré", Toast.LENGTH_SHORT).show();

                getSharedPreferences("user_prefs", MODE_PRIVATE).edit()
                        .putBoolean("isRegistered", true).apply();

                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Erreur d'envoi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
