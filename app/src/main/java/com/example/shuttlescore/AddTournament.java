package com.example.shuttlescore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AddTournament extends AppCompatActivity {
    private FirebaseHelper firebaseHelper;
    private ImageView tournamentLogo,back;
    private EditText tournamentName, groupName, cityName, organizerName, contactNo, emailId;
    private TextView tournamentStartDate, tournamentEndDate;
    private Button submitButton;
    private Spinner tournamentCategory, shuttlecockType, singlesDoubles;
    private String tournamentPhoto = "";
    private static final int PICK_IMAGE = 1;
    private static final int CAPTURE_IMAGE = 2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_addtournament);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return insets;
        });

        initializeUI();
        setupSpinners();
        onclick();
    }

    private void initializeUI() {
        back = findViewById(R.id.back);
        tournamentLogo = findViewById(R.id.tournamentLogo);
        tournamentName = findViewById(R.id.tournamentName);
        groupName = findViewById(R.id.groupName);
        cityName = findViewById(R.id.cityName);
        organizerName = findViewById(R.id.organizerName);
        contactNo = findViewById(R.id.contactNo);
        emailId = findViewById(R.id.emailId);
        tournamentStartDate = findViewById(R.id.tournamentStartDate);
        tournamentEndDate = findViewById(R.id.tournamentEndDate);
        submitButton = findViewById(R.id.submitButton);
        tournamentCategory = findViewById(R.id.tournamentCategory);
        shuttlecockType = findViewById(R.id.shuttlecockType);
        singlesDoubles = findViewById(R.id.singlesDoubles);

        firebaseHelper = new FirebaseHelper();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddTournament.this);
                builder.setTitle("Are you sure?")
                        .setMessage("Are you sure you want to Exit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(AddTournament.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.tournament_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tournamentCategory.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> shuttleAdapter = ArrayAdapter.createFromResource(this, R.array.shuttlecock_types, android.R.layout.simple_spinner_item);
        shuttleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shuttlecockType.setAdapter(shuttleAdapter);

        ArrayAdapter<CharSequence> singlesDoublesAdapter = ArrayAdapter.createFromResource(this, R.array.singles_doubles, android.R.layout.simple_spinner_item);
        singlesDoublesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        singlesDoubles.setAdapter(singlesDoublesAdapter);


    }

    private void onclick() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSubmit();

            }
        });

        tournamentLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        tournamentStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStartDatePicker();
            }
        });

        tournamentEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEndDatePicker();
            }
        });
    }



    private boolean validateAndSubmit() {
        String name = tournamentName.getText().toString().trim();
        String ground = groupName.getText().toString().trim();
        String city = cityName.getText().toString().trim();
        String organizer = organizerName.getText().toString().trim();
        String contact = contactNo.getText().toString().trim();
        String email = emailId.getText().toString().trim();
        String startDate = tournamentStartDate.getText().toString().trim();
        String endDate = tournamentEndDate.getText().toString().trim();
        String selectedCategory = tournamentCategory.getSelectedItem().toString().trim();
        String selectedShuttleType = shuttlecockType.getSelectedItem().toString().trim();
        String selectedSinglesDoubles = singlesDoubles.getSelectedItem().toString().trim();

        if (startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Please select both start and end dates", Toast.LENGTH_SHORT).show();
            return false;
        }

        long startMillis = parseDateToMillis(startDate);
        long endMillis = parseDateToMillis(endDate);

        if (endMillis < startMillis) {
            Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(tournamentPhoto.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please select a Tournament Logo", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!name.matches("^[a-zA-Z ]+$")) {
            tournamentName.setError("Enter a valid Tournament Name (Only letters allowed)");
            return false;
        }

        if (!ground.matches("^[a-zA-Z ]+$")) {
            groupName.setError("Enter a valid Group Name (Only letters allowed)");
            return false;
        }

        if (!city.matches("^[a-zA-Z ]+$")) {
            cityName.setError("Enter a valid City Name (Only letters allowed)");
            return false;
        }

        if (!organizer.matches("^[a-zA-Z ]+$")) {
            organizerName.setError("Enter a valid Organizer Name (Only letters allowed)");
            return false;
        }

        if (contact.isEmpty() || contact.length() != 10 || !contact.matches("^[0-9]+$")) {
            contactNo.setError("Enter a valid 10-digit Contact Number");
            return false;
        }

        if (!email.contains("@") || !email.contains(".")) {
            emailId.setError("Enter a valid Email Address");
            return false;
        }

        if (selectedCategory.equalsIgnoreCase("Select Category")) {
            Toast.makeText(this, "Please select a Tournament Category", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedShuttleType.equalsIgnoreCase("Select Shuttlecock Type")) {
            Toast.makeText(this, "Please select a Shuttlecock Type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedSinglesDoubles.equalsIgnoreCase("Select SinglesDoubles")) {
            Toast.makeText(this, "Please select Singles or Doubles", Toast.LENGTH_SHORT).show();
            return false;
        }

        firebaseHelper.addTournament(
                name, tournamentPhoto, ground, city, organizer, contact, email,
                startMillis, endMillis, selectedCategory, selectedShuttleType,
                selectedSinglesDoubles, System.currentTimeMillis(), 0,
                new FirebaseHelper.TournamentCallback() {
                    @Override
                    public void onTournamentAdded(String tournamentId) {
                        Toast.makeText(AddTournament.this, "Tournament added successfully!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(AddTournament.this, TournamentDetailActivity.class);
                        i.putExtra("tournamentId", tournamentId);
                        i.putExtra("tournamentName", name);
                        i.putExtra("groundName", ground);
                        i.putExtra("organizerContactNo",contact);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onTournamentAddFailed(String errorMessage) {
                        Toast.makeText(AddTournament.this, "Failed to add tournament: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Toast.makeText(this, "Tournament added successfully!", Toast.LENGTH_SHORT).show();
        return true;
    }

    private long parseDateToMillis(String date) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void selectImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setItems(new CharSequence[]{"Choose from Gallery", "Take Photo", "Cancel"}, (dialog, which) -> {
            if (which == 0) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, PICK_IMAGE);
            } else if (which == 1) {
                Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePhoto, CAPTURE_IMAGE);
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private long startDateMillis = 0;

    private void openStartDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(AddTournament.this, (view, year1, monthOfYear, dayOfMonth) ->
        {
            String formattedDate = String.format(Locale.US, "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year1);
            tournamentStartDate.setText(formattedDate);

            startDateMillis = new GregorianCalendar(year1, monthOfYear, dayOfMonth).getTimeInMillis();
        }, year, month, day);

        dpd.getDatePicker().setMinDate(System.currentTimeMillis());

        dpd.show();
    }

    private void openEndDatePicker() {
        if (startDateMillis == 0) {
            Toast.makeText(this, "Please select a start date first", Toast.LENGTH_SHORT).show();
            return;
        }

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(AddTournament.this, (view, year1, monthOfYear, dayOfMonth) ->
        {
            String formattedDate = String.format(Locale.US, "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year1);
            tournamentEndDate.setText(formattedDate);
        }
        , year, month, day);

        dpd.getDatePicker().setMinDate(startDateMillis);

        dpd.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    tournamentLogo.setImageBitmap(bitmap);
                    tournamentPhoto = selectedImage.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAPTURE_IMAGE && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                tournamentLogo.setImageBitmap(photo);
                tournamentPhoto = "Captured Image";
            }
        }
    }
}