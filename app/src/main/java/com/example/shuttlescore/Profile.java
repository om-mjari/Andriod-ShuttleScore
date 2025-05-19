package com.example.shuttlescore;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Profile extends AppCompatActivity {

    Button btnEdit, btnDeleteAccount;
    TextView etName, etAddress, etContact, etEmail, etDOB;
    RadioGroup rgGender, rgHandedness;
    RadioButton rbMale, rbFemale, rbRightHanded, rbLeftHanded;
    ImageView backButton;
    private boolean isEditing = false;
    TextView dobTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing or show a toast
                // Toast.makeText(Login.this, "Back is disabled on login screen", Toast.LENGTH_SHORT).show();
            }
        });

        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        initialize();
        fetchdata();
        onclick();
    }

    private void initialize(){
        btnEdit = findViewById(R.id.btnEdit);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        etContact = findViewById(R.id.etContact);
        etEmail = findViewById(R.id.etEmail);
        etDOB = findViewById(R.id.etDOB);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbRightHanded = findViewById(R.id.rbRightHanded);
        rbLeftHanded = findViewById(R.id.rbLeftHanded);
        rgGender = findViewById(R.id.rgGender);
        rgHandedness = findViewById(R.id.rgHandedness);
        backButton = findViewById(R.id.backButton);
        dobTextView = findViewById(R.id.etDOB);
    }
    private void onclick(){
        btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!isEditing) {
                    etName.setEnabled(true);
                    etName.setFocusable(true);
                    etName.setFocusableInTouchMode(true);

                    etAddress.setEnabled(true);
                    etAddress.setFocusable(true);
                    etAddress.setFocusableInTouchMode(true);

                    etEmail.setEnabled(true);
                    etEmail.setFocusable(true);
                    etEmail.setFocusableInTouchMode(true);

                    etDOB.setEnabled(true);
                    etDOB.setFocusable(true);
                    etDOB.setFocusableInTouchMode(true);

                    rbMale.setEnabled(true);
                    rbMale.setFocusable(true);
                    rbMale.setClickable(true);

                    rbFemale.setEnabled(true);
                    rbFemale.setFocusable(true);
                    rbFemale.setClickable(true);

                    rbRightHanded.setEnabled(true);
                    rbRightHanded.setFocusable(true);
                    rbRightHanded.setClickable(true);

                    rbLeftHanded.setEnabled(true);
                    rbLeftHanded.setFocusable(true);
                    rbLeftHanded.setClickable(true);

                    btnEdit.setText("SAVE");
                    isEditing = true;
                }else{
                    saveUserDataToFirebase();
                }

            }
    });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new androidx.appcompat.app.AlertDialog.Builder(Profile.this)
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            deleteuser();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        etDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etDOB.isFocusable() && etDOB.isClickable()) {
                    showDatePickerDialog();
                }
            }
        });
    }

    private void fetchdata() {
        SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);
        String contactNumber = sh.getString("contact", null);

        if (contactNumber == null) {
            Toast.makeText(this, "No contact found", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("tbl_Users");

        usersRef.orderByChild("phone").equalTo(contactNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                                String name = userSnapshot.child("name").getValue(String.class);
                                String address = userSnapshot.child("address").getValue(String.class);
                                String contact = userSnapshot.child("phone").getValue(String.class);
                                String email = userSnapshot.child("email").getValue(String.class);
                                String gender = userSnapshot.child("gender").getValue(String.class);
                                String handedness = userSnapshot.child("handedness").getValue(String.class);

                                Long dobTimestamp = userSnapshot.child("dateOfBirth").getValue(Long.class);
                                String dob = convertTimestampToString(dobTimestamp);

                                if (dobTimestamp == null || dobTimestamp == 0) {
                                    Intent intent = getIntent();
                                    long dateOfBirth = intent.getLongExtra("dateOfBirth", 0); // Default is 0
                                    if (dateOfBirth != 0) {
                                        dob = convertMillisToDate(dateOfBirth);
                                    } else {
                                        dob = "Not Available";
                                    }
                                }

                                etName.setText(name);
                                etAddress.setText(address);
                                etContact.setText(contact);
                                etEmail.setText(email);
                                etDOB.setText(dob);

                                if ("Male".equalsIgnoreCase(gender)) {
                                    rbMale.setChecked(true);
                                } else if ("Female".equalsIgnoreCase(gender)) {
                                    rbFemale.setChecked(true);
                                }

                                if ("Right-Handed".equalsIgnoreCase(handedness)) {
                                    rbRightHanded.setChecked(true);
                                } else if ("Left-Handed".equalsIgnoreCase(handedness)) {
                                    rbLeftHanded.setChecked(true);
                                }
                            }
                        } else {
                            Toast.makeText(Profile.this, "User not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("Firebase", "Database Error: " + error.getMessage());
                        Toast.makeText(Profile.this, "Failed to load data!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String convertTimestampToString(Long timestamp) {
        if (timestamp == null) {
            return "N/A";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }

//    this
    private String convertMillisToDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    private void saveUserDataToFirebase() {
        SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);
        String contactNumber = sh.getString("contact", null);

        if (contactNumber == null) {
            Toast.makeText(this, "No contact found!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("tbl_Users");

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("name", etName.getText().toString().trim());
        updates.put("address", etAddress.getText().toString().trim());
        updates.put("email", etEmail.getText().toString().trim());
        updates.put("dob", convertDateToTimestamp(etDOB.getText().toString().trim()));

        if (rbMale.isChecked()) {
            updates.put("gender", "Male");
        } else if (rbFemale.isChecked()) {
            updates.put("gender", "Female");
        }


        if (rbRightHanded.isChecked()) {
            updates.put("handedness", "Right-Handed");
        } else if (rbLeftHanded.isChecked()) {
            updates.put("handedness", "Left-Handed");
        }

        usersRef.orderByChild("phone").equalTo(contactNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                userSnapshot.getRef().updateChildren(updates)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(Profile.this, "Profile Updated!", Toast.LENGTH_SHORT).show();

                                            etName.setFocusable(false);
                                            etName.setClickable(false);

                                            etAddress.setFocusable(false);
                                            etAddress.setClickable(false);

                                            etEmail.setFocusable(false);
                                            etEmail.setClickable(false);

                                            etDOB.setFocusable(false);
                                            etDOB.setClickable(false);

                                            rbMale.setFocusable(false);
                                            rbMale.setClickable(false);

                                            rbFemale.setFocusable(false);
                                            rbFemale.setClickable(false);

                                            rbRightHanded.setFocusable(false);
                                            rbRightHanded.setClickable(false);

                                            rbLeftHanded.setFocusable(false);
                                            rbLeftHanded.setClickable(false);

                                            btnEdit.setText("Edit");
                                            isEditing = false;
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(Profile.this, "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(Profile.this, "User not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(Profile.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Long convertDateToTimestamp(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            return (date != null) ? date.getTime() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showDatePickerDialog() {

        Calendar cc = Calendar.getInstance();
        int year = cc.get(Calendar.YEAR);
        int month = cc.get(Calendar.MONTH);
        int day = cc.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDOB.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(cc.getTimeInMillis());
        datePickerDialog.show();
    }
    private void deleteuser(){
        SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);
        String contactNumber = sh.getString("contact", null);

        if (contactNumber == null) {
            Toast.makeText(Profile.this, "No contact found!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("tbl_Users");

        usersRef.orderByChild("phone").equalTo(contactNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                userSnapshot.getRef().removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(Profile.this, "Account deleted successfully!", Toast.LENGTH_SHORT).show();

                                            SharedPreferences.Editor editor = sh.edit();
                                            editor.clear();
                                            editor.apply();


                                            Intent intent = new Intent(Profile.this, Login.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(Profile.this, "Failed to delete account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(Profile.this, "User not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(Profile.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }}