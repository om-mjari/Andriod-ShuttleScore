package com.example.shuttlescore;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class searchplayer extends AppCompatActivity {

    private EditText Etxtsearch;
    private ListView listViewPlayers;
    private Button btndone;

    private DatabaseReference databaseReference;
    private ArrayList<String> playerList;
    private ArrayAdapter<String> adapter;

    private String exclude1, exclude2, exclude3, exclude4;
    private String selectedPhone = null;
    private String selectedName = null;

    // Keep a reference to the single TextWatcher
    private TextWatcher searchWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.applyTheme(this);
        setContentView(R.layout.activity_searchplayer);

        // Apply window insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get excluded phone numbers from intent
        exclude1 = getIntent().getStringExtra("exclude1");
        exclude2 = getIntent().getStringExtra("exclude2");
        exclude3 = getIntent().getStringExtra("exclude3");
        exclude4 = getIntent().getStringExtra("exclude4");

        // Initialize views
        Etxtsearch = findViewById(R.id.Etxtsearch);
        listViewPlayers = findViewById(R.id.lvPlayers);
        btndone = findViewById(R.id.btndone);

        // Setup Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("tbl_Users");
        playerList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playerList);

        // Set up ListView adapter
        listViewPlayers.setAdapter(adapter);

        // Create and assign the one TextWatcher
        searchWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Reset selection when text changes
                selectedName = null;
                selectedPhone = null;

                if (s.length() > 0) {
                    searchPlayer(s.toString());
                } else {
                    playerList.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        };
        Etxtsearch.addTextChangedListener(searchWatcher);

        // List item click listener
        listViewPlayers.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = playerList.get(position);
            // Only proceed if format is correct
            if (selectedItem == null || !selectedItem.contains(" - ")) {
                Toast.makeText(this, "Please select a valid player", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] parts = selectedItem.split(" - ");
            if (parts.length == 2) {
                selectedName = parts[0];
                selectedPhone = parts[1];

                // Temporarily remove watcher, update text, then re-add
                Etxtsearch.removeTextChangedListener(searchWatcher);
                Etxtsearch.setText(selectedPhone);
                Etxtsearch.setSelection(selectedPhone.length());
                Etxtsearch.addTextChangedListener(searchWatcher);

                // Clear suggestions
                playerList.clear();
                adapter.notifyDataSetChanged();
            }
        });

        // Done button click listener
        btndone.setOnClickListener(v -> {
            if (selectedName == null || selectedPhone == null) {
                Toast.makeText(this, "Please select a player from the list", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("playername", selectedName);
            resultIntent.putExtra("playercnt", selectedPhone);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    // Search player in Firebase based on contact
    private void searchPlayer(String contact) {
        if (contact.isEmpty()) {
            playerList.clear(); // Clear list only if search term is empty
            adapter.notifyDataSetChanged();
            return;
        }

        databaseReference.orderByChild("phone")
                .startAt(contact)
                .endAt(contact + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(searchplayer.this,
                                "Error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        playerList.clear(); // Clear the list before adding new results

                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String name = userSnapshot.child("name").getValue(String.class);
                            String phone = userSnapshot.child("phone").getValue(String.class);

                            if (name != null && phone != null &&
                                    !phone.equals(exclude1) &&
                                    !phone.equals(exclude2) &&
                                    !phone.equals(exclude3) &&
                                    !phone.equals(exclude4)) {
                                String playerItem = name + " - " + phone;
                                // Add only if not already in the list
                                if (!playerList.contains(playerItem)) {
                                    playerList.add(playerItem);
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }

}
