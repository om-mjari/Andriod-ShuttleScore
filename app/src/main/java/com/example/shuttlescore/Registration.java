package com.example.shuttlescore;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Registration extends AppCompatActivity {

    TextView txtregister, regitxtgender, txtregihandchoice,regilogin;
    EditText reginame, regicontact, regiemail, regiaddress, regidob;
    RadioGroup rggender;
    RadioButton rbmale, rbfemale;
    Button regibtnregister;
    Spinner regihandpreference;
    long dateOfBirth, createdAt;
    Long deletedAt;
    String name, address, contactNo, emailId, gender, handedness,userId,otpString;
    String generatedOTP = "";
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.regimain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        intialize();
        firebaseHelper = new FirebaseHelper();
        onclick();

        String[] handOptions = getResources().getStringArray(R.array.hand_preference_array);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, handOptions);
        regihandpreference.setAdapter(adapter);

    }

    private void intialize() {
        txtregister = findViewById(R.id.txtregister);
        reginame = findViewById(R.id.reginame);
        regicontact = findViewById(R.id.regicontact);
        regiemail = findViewById(R.id.regiemail);
        regiaddress = findViewById(R.id.regiaddress);
        rggender = findViewById(R.id.rggender);
        rbmale = findViewById(R.id.rbmale);
        rbfemale = findViewById(R.id.rbfemale);
        regihandpreference = findViewById(R.id.regihandpreference);
        regidob = findViewById(R.id.regidob);
        regilogin = findViewById(R.id.regilogin);
        regibtnregister = findViewById(R.id.regibtnregister);
    }

    private void onclick() {
        regibtnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference database = FirebaseDatabase.getInstance().getReference("tbl_Users");

                userId = database.push().getKey();
                name = reginame.getText().toString();
                address = regiaddress.getText().toString();
                contactNo = regicontact.getText().toString().trim();
                emailId = regiemail.getText().toString().trim();
                gender = rbmale.getText().toString().trim();
                handedness = regihandpreference.getSelectedItem().toString();
                if (rbmale.isChecked()) {
                    gender = rbmale.getText().toString();
                } else if (rbfemale.isChecked()) {
                    gender = rbfemale.getText().toString();
                } else {
                    gender = "Not Specified";
                }
                String ss = regidob.getText().toString();
                dateOfBirth = 0;
                SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
                sdf.setLenient(false);
                try {
                    Date date = sdf.parse(ss);
                    if (date != null) {
                        dateOfBirth = date.getTime();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Please Enter all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                createdAt = System.currentTimeMillis();
                deletedAt = null;

                if (name.isEmpty() || address.isEmpty() || contactNo.isEmpty() || emailId.isEmpty() || dateOfBirth == 0 || handedness.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show();

                }else{
                    if (contactNo.length() < 10) {
                        regicontact.setError("Please Enter valid phone number");
                    }else {
                        checkIfContactExists(contactNo);
                    }
                }
            }

        });
        regidob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        regilogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Registration.this,Login.class);
                startActivity(i);
            }
        });

    }

    private void showDatePickerDialog() {

        Calendar cc = Calendar.getInstance();
        int year = cc.get(Calendar.YEAR);
        int month = cc.get(Calendar.MONTH);
        int day = cc.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    regidob.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(cc.getTimeInMillis());
        datePickerDialog.show();
    }
    private void loadFragment(Fragment ff) {

        FragmentManager fr = getSupportFragmentManager();
        FragmentTransaction ft = fr.beginTransaction();
        ft.replace(R.id.regiframe, ff);
        ft.addToBackStack(null);
        ft.commit();
    }
    private void checkIfContactExists(String contactNo) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tbl_Users");

        databaseReference.orderByChild("phone").equalTo(contactNo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Contact number already registered!", Toast.LENGTH_SHORT).show();
                        } else {
                            generatedOTP = generateOTP();
                            if (ContextCompat.checkSelfPermission(Registration.this, Manifest.permission.SEND_SMS) ==
                                    PackageManager.PERMISSION_GRANTED) {
                                sendSMS();
                            } else {
                                ActivityCompat.requestPermissions(Registration.this,
                                        new String[]{Manifest.permission.SEND_SMS}, 100);
                            }
                            Intent i = new Intent(Registration.this,otp.class);

                            i.putExtra("userId", userId);
                            i.putExtra("name", name);
                            i.putExtra("address", address);
                            i.putExtra("contactNo", contactNo);
                            i.putExtra("emailId", emailId);
                            i.putExtra("gender", gender);
                            i.putExtra("handedness", handedness);
                            i.putExtra("dateOfBirth", dateOfBirth);
                            i.putExtra("createdAt", createdAt);

                            i.putExtra("generatedotp",otpString);

                            startActivity(i);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendSMS();
        } else {
            Toast.makeText(this, "Permission Denied!!", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);

        otpString = String.valueOf(otp);

        return otpString;
    }

    private void sendSMS() {
        String phone = regicontact.getText().toString().trim();
        String message = "ShuttleScore \nYour OTP is:\n" + otpString + "\nPlease do not share this with anyone!! \n Thanks for registering with ShuttleScore ";

        if (!phone.isEmpty()) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, message, null, null);
                Toast.makeText(this, "OTP Sent Successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
        }
    }


}