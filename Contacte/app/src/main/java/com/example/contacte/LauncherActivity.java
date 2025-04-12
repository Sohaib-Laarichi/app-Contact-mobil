package com.example.contacte;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LauncherActivity extends AppCompatActivity {

    String deviceId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deviceId = getDeviceIdentifier();

        boolean isRegisteredLocal = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getBoolean("isRegistered", false);

        if (isRegisteredLocal) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            checkUserFromBackend();
        }
    }

    private String getDeviceIdentifier() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        }
    }

    private void checkUserFromBackend() {
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        // Log the IMEI being tested
        Log.d("LauncherActivity", "IMEI being tested: " + deviceId);

        api.checkUserExist(deviceId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    // User exists
                    getSharedPreferences("user_prefs", MODE_PRIVATE).edit()
                            .putBoolean("isRegistered", true).apply();

                    startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                } else {
                    // User does not exist
                    startActivity(new Intent(LauncherActivity.this, RegisterActivity.class));
                }
                finish();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(LauncherActivity.this, "Erreur connexion serveur", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
