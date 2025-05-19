package com.example.shuttlescore;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditTournaments extends AppCompatActivity {

    private EditText nameField, groupField, cityField, organizerField, contactField, emailField, startDateField, endDateField;
    private Spinner categorySpinner, shuttleSpinner, formatSpinner;
    private ImageView logoView, backButton;
    Button EditTournamentButton,submitButton;
    private DatabaseReference databaseRef;
    private String tournamentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tournaments);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing or show a toast
                // Toast.makeText(Login.this, "Back is disabled on login screen", Toast.LENGTH_SHORT).show();
            }
        });

        initializeViews();
        setupSpinners();
        buttonClicks();

        tournamentId = getIntent().getStringExtra("tournamentId");
        Log.d("EditTournaments", "Tournament ID: " + tournamentId);

        if (tournamentId != null) {
            fetchTournamentData(tournamentId);
        } else {
            showError("Invalid tournament ID");
        }



    }

    public void buttonClicks()
    {
        submitButton = findViewById(R.id.submitButton);
        submitButton.setBackgroundColor(getColor(R.color.gray));
        submitButton.setEnabled(false);
        submitButton.setBackgroundResource(R.drawable.rounded_button);
        backButton = findViewById(R.id.back);

        Intent i = getIntent();
        String tournamentName = i.getStringExtra("tournamentName");
        String groundName = i.getStringExtra("groundName");
        String organizerContactNo = i.getStringExtra("organizerContactNo");
        String cityName = i.getStringExtra("cityName");

        String organizerName = i.getStringExtra("organizerName");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),TournamentDetailActivity.class);
                i.putExtra("tournamentId",tournamentId);
                i.putExtra("tournamentName",tournamentName);
                i.putExtra("groundName",groundName);
                i.putExtra("cityName",cityName);
                i.putExtra("organizerContactNo",organizerContactNo);
                i.putExtra("organizerName",organizerName);
                startActivity(i);
            }
        });
        EditTournamentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableAllFields();
                EditTournamentButton.setBackgroundColor(getColor(R.color.gray));
                EditTournamentButton.setEnabled(false);
                EditTournamentButton.setBackgroundResource(R.drawable.rounded_button);
                submitButton.setBackgroundColor(getColor(com.google.android.material.R.color.design_default_color_primary));
                submitButton.setEnabled(true);
                submitButton.setBackgroundResource(R.drawable.rounded_button);
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAndSubmit()) {
                    updateTournamentDetails();
                    disableAllFields();
                    Intent i = new Intent(getApplicationContext(),TournamentDetailActivity.class);
                    i.putExtra("tournamentId",tournamentId);
                    i.putExtra("tournamentName",nameField.getText().toString());
                    i.putExtra("groundName",groupField.getText().toString());
                    i.putExtra("cityName",cityField.getText().toString());
                    i.putExtra("organizerContactNo",contactField.getText().toString());
                    i.putExtra("organizerName",organizerField.getText().toString());
                    startActivity(i);
                    EditTournamentButton.setBackgroundColor(getColor(com.google.android.material.R.color.design_default_color_primary));
                    EditTournamentButton.setEnabled(true);
                    EditTournamentButton.setBackgroundResource(R.drawable.rounded_button);
                    submitButton.setBackgroundColor(getColor(R.color.gray));
                    submitButton.setEnabled(false);
                    submitButton.setBackgroundResource(R.drawable.rounded_button);
                }
            }
        });
    }
    private void initializeViews() {
        nameField = findViewById(R.id.EditTournamentName);
        groupField = findViewById(R.id.EditGroupName);
        cityField = findViewById(R.id.EditCityName);
        organizerField = findViewById(R.id.EditOrganizerName);
        contactField = findViewById(R.id.EditContactNo);
        emailField = findViewById(R.id.EditEmailId);
        startDateField = findViewById(R.id.TournamentStartDate);
        endDateField = findViewById(R.id.TournamentEndDate);
        categorySpinner = findViewById(R.id.TournamentCategory);
        shuttleSpinner = findViewById(R.id.ShuttlecockType);
        formatSpinner = findViewById(R.id.SinglesDoubles);
        logoView = findViewById(R.id.tournamentLogo);
        backButton = findViewById(R.id.back);
        EditTournamentButton = findViewById(R.id.EditTournamentButton);

        startDateField.setOnClickListener(v -> openDatePicker(startDateField));
        endDateField.setOnClickListener(v -> openDatePicker(endDateField));
        backButton.setOnClickListener(v -> navigateBack());
    }

    private void setupSpinners() {
        setupSpinner(categorySpinner, R.array.tournament_categories);
        setupSpinner(shuttleSpinner, R.array.shuttlecock_types);
        setupSpinner(formatSpinner, R.array.singles_doubles);
    }

    private void setupSpinner(Spinner spinner, int arrayResId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void openDatePicker(EditText field) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) ->
                field.setText(String.format(Locale.US, "%02d/%02d/%04d", day, month + 1, year)),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void fetchTournamentData(String id) {
        databaseRef = FirebaseDatabase.getInstance().getReference("tbl_Tournaments").child(id);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    TournamentModel tournament = snapshot.getValue(TournamentModel.class);
                    if (tournament != null) populateFields(tournament);
                    else showError("Failed to load tournament details");
                } else {
                    showError("Tournament not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                showError("Database error: " + error.getMessage());
            }
        });
    }

    private void populateFields(TournamentModel t) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        nameField.setText(t.getTournamentName());
        groupField.setText(t.getGroundName());
        cityField.setText(t.getCityName());
        organizerField.setText(t.getOrganizerName());
        contactField.setText(t.getOrganizerContactNo());
        emailField.setText(t.getOrganizerEmailId());
        startDateField.setText(sdf.format(new Date(t.getTournamentStartDate())));
        endDateField.setText(sdf.format(new Date(t.getTournamentEndDate())));

        selectSpinnerItem(categorySpinner, t.getTournamentCategory());
        selectSpinnerItem(shuttleSpinner, t.getShuttlecockType());
        selectSpinnerItem(formatSpinner, t.getSinglesDoubles());

        Glide.with(this)
                .load(t.getTournamentPhoto())
                .placeholder(R.drawable.placeholder_image)
                .into(logoView);

        disableAllFields();
    }

    private void selectSpinnerItem(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void disableAllFields() {
        nameField.setEnabled(false);
        groupField.setEnabled(false);
        cityField.setEnabled(false);
        organizerField.setEnabled(false);
        contactField.setEnabled(false);
        emailField.setEnabled(false);
        startDateField.setEnabled(false);
        endDateField.setEnabled(false);
        categorySpinner.setEnabled(false);
        shuttleSpinner.setEnabled(false);
        formatSpinner.setEnabled(false);
    }
    private void enableAllFields() {
        nameField.setEnabled(true);
        groupField.setEnabled(true);
        cityField.setEnabled(true);
        organizerField.setEnabled(true);
        emailField.setEnabled(true);
        startDateField.setEnabled(true);
        endDateField.setEnabled(true);
        categorySpinner.setEnabled(true);
        shuttleSpinner.setEnabled(true);
        formatSpinner.setEnabled(true);
    }

    private boolean validateAndSubmit() {
        String name = nameField.getText().toString().trim();
        String group = groupField.getText().toString().trim();
        String city = cityField.getText().toString().trim();
        String organizer = organizerField.getText().toString().trim();
        String contact = contactField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String startDate = startDateField.getText().toString().trim();
        String endDate = endDateField.getText().toString().trim();
        String selectedCategory = categorySpinner.getSelectedItem().toString().trim();
        String selectedShuttleType = shuttleSpinner.getSelectedItem().toString().trim();
        String selectedSinglesDoubles = formatSpinner.getSelectedItem().toString().trim();

        if (name.isEmpty() || group.isEmpty() || city.isEmpty() || organizer.isEmpty() ||
                contact.isEmpty() || email.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!name.matches("^[a-zA-Z ]+$")) {
            nameField.setError("Enter a valid Tournament Name (Only letters allowed)");
            return false;
        }

        if (!group.matches("^[a-zA-Z ]+$")) {
            groupField.setError("Enter a valid Group Name (Only letters allowed)");
            return false;
        }

        if (!city.matches("^[a-zA-Z ]+$")) {
            cityField.setError("Enter a valid City Name (Only letters allowed)");
            return false;
        }

        if (!organizer.matches("^[a-zA-Z ]+$")) {
            organizerField.setError("Enter a valid Organizer Name (Only letters allowed)");
            return false;
        }

        if (!contact.matches("^[0-9]{10}$")) {
            contactField.setError("Enter a valid 10-digit Contact Number");
            return false;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            emailField.setError("Enter a valid Email Address");
            return false;
        }

        long startMillis = parseDate(startDate);
        long endMillis = parseDate(endDate);

        if (startMillis == 0 || endMillis == 0) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (endMillis < startMillis) {
            Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
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

        return true; // All validations passed
    }

    private void navigateBack() {
        Intent intent = new Intent(this, TournamentDetailActivity.class);
        intent.putExtra("tournamentId", tournamentId);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.e("EditTournaments", message);
    }

    private long parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dateStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void updateTournamentDetails() {
        String tournamentId = getIntent().getStringExtra("tournamentId");
        if (tournamentId == null) {
            Toast.makeText(this, "No tournament ID!", Toast.LENGTH_SHORT).show();
            return;
        }

        String name       = nameField.getText().toString().trim();
        String ground     = groupField.getText().toString().trim();
        String city       = cityField.getText().toString().trim();
        String orgName    = organizerField.getText().toString().trim();
        String orgContact = contactField.getText().toString().trim();
        String orgEmail   = emailField.getText().toString().trim();
        String category   = categorySpinner.getSelectedItem().toString();
        String shuttle    = shuttleSpinner.getSelectedItem().toString();
        String format     = formatSpinner.getSelectedItem().toString();

        long startMillis = parseDate(startDateField.getText().toString());
        long endMillis   = parseDate(endDateField.getText().toString());

        Map<String, Object> updates = new HashMap<>();
        updates.put("tournamentName",     name);
        updates.put("groundName",         ground);
        updates.put("cityName",           city);
        updates.put("organizerName",      orgName);
        updates.put("organizerContactNo", orgContact);
        updates.put("organizerEmailId",   orgEmail);
        updates.put("tournamentStartDate", startMillis);
        updates.put("tournamentEndDate",   endMillis);
        updates.put("tournamentCategory", category);
        updates.put("shuttlecockType",    shuttle);
        updates.put("singlesDoubles",     format);
        updates.put("updatedAt",          System.currentTimeMillis());

        FirebaseDatabase
                .getInstance()
                .getReference("tbl_Tournaments")
                .child(tournamentId)
                .updateChildren(updates)
                .addOnSuccessListener(unused ->
                        Toast.makeText(EditTournaments.this,
                                "Tournament details updated!",
                                Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(EditTournaments.this,
                                "Update failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

}