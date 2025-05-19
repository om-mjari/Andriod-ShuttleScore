package com.example.shuttlescore;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddOfficials extends AppCompatActivity {

    private EditText addOfficalsContactNo;
    private Button addOfficialsButton;
    private ListView listViewSuggestions;
    private ListView listViewAddedOfficials;
    private String selectedPhone = null;

    ImageView back;
    private ArrayList<String> suggestionsList;
    private ArrayList<String> addedOfficialsList;
    private ArrayAdapter<String> suggestionsAdapter;
    private ArrayAdapter<String> addedOfficialsAdapter;

    private String tournamentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.applyTheme(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_officials);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing or show a toast
                // Toast.makeText(Login.this, "Back is disabled on login screen", Toast.LENGTH_SHORT).show();
            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = getIntent();
                String tournamentId = in.getStringExtra("tournamentId");
                String tournamentName = in.getStringExtra("tournamentName");
                String groundName = in.getStringExtra("groundName");
                String organizerContactNo = in.getStringExtra("organizerContactNo");
                Intent i = new Intent(getApplicationContext(), TournamentDetailActivity.class);
                i.putExtra("tournamentId", tournamentId);
                i.putExtra("tournamentName", tournamentName);
                i.putExtra("groundName", groundName);
                i.putExtra("organizerContactNo", organizerContactNo);
                startActivity(i);
            }
        });

        tournamentId = getIntent().getStringExtra("tournamentId");
        if (tournamentId == null) {
            Toast.makeText(this, "Error: No tournament selected", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        addOfficalsContactNo = findViewById(R.id.addOfficalsContactNo);
        addOfficialsButton = findViewById(R.id.addOfficialsButton);
        listViewSuggestions = findViewById(R.id.listViewSuggestions);
        listViewAddedOfficials = findViewById(R.id.listViewAddedOfficials);

        suggestionsList = new ArrayList<>();
        addedOfficialsList = new ArrayList<>();

        suggestionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestionsList);
        addedOfficialsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addedOfficialsList);

        listViewSuggestions.setAdapter(suggestionsAdapter);
        listViewAddedOfficials.setAdapter(addedOfficialsAdapter);

        fetchAddedOfficials(tournamentId);

        addOfficalsContactNo.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchSuggestions(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        listViewSuggestions.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = suggestionsAdapter.getItem(position);
            if (selectedItem != null) {
                String[] parts = selectedItem.split(" - ");
                if (parts.length == 2) {
                    selectedPhone = parts[1];
                    addOfficalsContactNo.setText(selectedItem);
                    listViewSuggestions.setVisibility(View.GONE);
                }
            }
        });

        addOfficialsButton.setOnClickListener(v -> {
            if (selectedPhone != null) {
                for (String official : addedOfficialsList) {
                    if (official.contains("📞 " + selectedPhone)) {
                        Toast.makeText(AddOfficials.this, "Official already added", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                addOfficialForPhone(selectedPhone);
                addOfficalsContactNo.setText(null);
            } else {
                Toast.makeText(this, "Please select a user from suggestions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSuggestions(String query) {
        if (query.isEmpty()) {
            suggestionsList.clear();
            suggestionsAdapter.notifyDataSetChanged();
            listViewSuggestions.setVisibility(View.GONE);
            return;
        }

        FirebaseDatabase.getInstance().getReference("tbl_Users")
                .orderByChild("phone")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        suggestionsList.clear();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String name = userSnapshot.child("name").getValue(String.class);
                            String phone = userSnapshot.child("phone").getValue(String.class);
                            if (name != null && phone != null) {
                                suggestionsList.add(name + " - " + phone);
                            }
                        }
                        suggestionsAdapter.notifyDataSetChanged();
                        listViewSuggestions.setVisibility(suggestionsList.isEmpty() ? View.GONE : View.VISIBLE);
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddOfficials.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addOfficialForPhone(String phone) {
        FirebaseDatabase.getInstance().getReference("tbl_Users")
                .orderByChild("phone")
                .equalTo(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(AddOfficials.this, "User not found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DataSnapshot child : snapshot.getChildren()) {
                            String userId = child.getKey();
                            String name = child.child("name").getValue(String.class);
                            String userPhone = child.child("phone").getValue(String.class);

                            if (userId != null && name != null && userPhone != null) {
                                FirebaseHelper helper = new FirebaseHelper();
                                helper.addOfficial(userId, tournamentId);

                                addedOfficialsList.add("\n👤 " + name + "\n\n📞 " + userPhone + "\n");
                                addedOfficialsAdapter.notifyDataSetChanged();
                                listViewAddedOfficials.setVisibility(View.VISIBLE);

                                Toast.makeText(AddOfficials.this, "Official added!", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddOfficials.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchAddedOfficials(String tournamentId) {
        FirebaseDatabase.getInstance().getReference("tbl_Officials")
                .orderByChild("tournamentId")
                .equalTo(tournamentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        addedOfficialsList.clear();
                        for (DataSnapshot officialSnapshot : snapshot.getChildren()) {
                            String userId = officialSnapshot.child("userId").getValue(String.class);
                            if (userId != null) {
                                FirebaseDatabase.getInstance().getReference("tbl_Users").child(userId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                                String name = userSnapshot.child("name").getValue(String.class);
                                                String phone = userSnapshot.child("phone").getValue(String.class);
                                                if (name != null && phone != null) {
                                                    for (String official : addedOfficialsList) {
                                                        if (official.contains("📞 " + phone)) {
                                                            return;
                                                        }
                                                    }
                                                    addedOfficialsList.add("\n👤 " + name + "\n\n📞 " + phone + "\n");
                                                    addedOfficialsAdapter.notifyDataSetChanged();
                                                    listViewAddedOfficials.setVisibility(View.VISIBLE);
                                                }
                                            }

                                            @Override public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(AddOfficials.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                        listViewAddedOfficials.setVisibility(addedOfficialsList.isEmpty() ? View.GONE : View.VISIBLE);
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddOfficials.this, "Error fetching officials", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
