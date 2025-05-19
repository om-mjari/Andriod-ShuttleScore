    package com.example.shuttlescore;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ArrayAdapter;
    import android.widget.ImageView;
    import android.widget.ListView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.OnBackPressedCallback;
    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;

    import com.google.android.material.button.MaterialButton;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;

    public class RemoveOfficials extends AppCompatActivity {

        ImageView back;
        ListView listViewRemoveOfficials;
        MaterialButton removeOfficialsButton;
        private ArrayList<String> addedOfficialsList;
        private ArrayAdapter<String> addedOfficialsAdapter;
        private int selectedPosition = -1;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            ThemeUtils.applyTheme(this);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_remove_officials);
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // Do nothing or show a toast
                    // Toast.makeText(Login.this, "Back is disabled on login screen", Toast.LENGTH_SHORT).show();
                }
            });

            Intent i = getIntent();
            String organizerContactNo = i.getStringExtra("organizerContactNo");
            String tournamentName = i.getStringExtra("tournamentName");
            String groundName = i.getStringExtra("groundName");
            String tournamentId = i.getStringExtra("tournamentId");

            back = findViewById(R.id.back);
            listViewRemoveOfficials = findViewById(R.id.listViewRemoveOfficials);
            removeOfficialsButton = findViewById(R.id.removeOfficialsButton);

            back.setOnClickListener(v -> {
                Intent in = new Intent(getApplicationContext(), TournamentDetailActivity.class);
                in.putExtra("tournamentId", tournamentId);
                in.putExtra("tournamentName", tournamentName);
                in.putExtra("groundName", groundName);
                in.putExtra("organizerContactNo", organizerContactNo);
                startActivity(in);
            });


            addedOfficialsList = new ArrayList<>();
            addedOfficialsAdapter = new ArrayAdapter<String>(this, R.layout.list_item_official, addedOfficialsList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View row = convertView != null ? convertView : getLayoutInflater().inflate(R.layout.list_item_official, parent, false);

                    TextView tv = row.findViewById(R.id.tvOfficialInfo);
                    ImageView iv = row.findViewById(R.id.removeIcon);
                    View container = row.findViewById(R.id.itemContainer);

                    tv.setText(addedOfficialsList.get(position));
                    if (position == selectedPosition) {
                        container.setBackgroundResource(R.drawable.item_selected_background); // Border with color
                        iv.setVisibility(View.VISIBLE); // Show remove icon
                    } else {
                        container.setBackgroundResource(R.drawable.item_default_background); // Default background
                        iv.setVisibility(View.GONE); // Hide remove icon
                    }

                    return row;
                }
            };

            listViewRemoveOfficials.setAdapter(addedOfficialsAdapter);
            fetchAddedOfficials(tournamentId);


            listViewRemoveOfficials.setOnItemClickListener((parent, view, position, id) -> {
                selectedPosition = position; // Select item
                addedOfficialsAdapter.notifyDataSetChanged(); // Refresh the list
            });

            removeOfficialsButton.setOnClickListener(v -> {
                if (selectedPosition != -1) {
                    String officialInfo = addedOfficialsList.get(selectedPosition);
                    String phone = officialInfo.split("\n\n📞 ")[1].split("\n")[0]; // Extract phone number

                    FirebaseHelper.removeOfficialByPhone(phone, tournamentId, new FirebaseHelper.FirebaseCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(RemoveOfficials.this, "Official removed successfully", Toast.LENGTH_SHORT).show();
                            addedOfficialsList.remove(selectedPosition);
                            addedOfficialsAdapter.notifyDataSetChanged();

                            Intent i = new Intent(getApplicationContext(),TournamentDetailActivity.class);
                            i.putExtra("tournamentId",tournamentId);
                            i.putExtra("tournamentName",tournamentName);
                            i.putExtra("groundName",groundName);
                            i.putExtra("organizerContactNo",organizerContactNo);
                            startActivity(i);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(RemoveOfficials.this, "Error removing official: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }

                    });
                } else {
                    Toast.makeText(RemoveOfficials.this, "Please select an official", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void fetchAddedOfficials(String tournamentId) {
            FirebaseDatabase.getInstance().getReference("tbl_Officials")
                    .orderByChild("tournamentId").equalTo(tournamentId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            addedOfficialsList.clear();
                            for (DataSnapshot officialSnapshot : snapshot.getChildren()) {
                                String userId = officialSnapshot.child("userId").getValue(String.class);
                                if (userId != null) {
                                    FirebaseDatabase.getInstance().getReference("tbl_Users").child(userId)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                                    String name = userSnapshot.child("name").getValue(String.class);
                                                    String phone = userSnapshot.child("phone").getValue(String.class);
                                                    if (name != null && phone != null) {
                                                        addedOfficialsList.add("\n👤 " + name + "\n\n📞 " + phone + "\n");
                                                        addedOfficialsAdapter.notifyDataSetChanged();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(RemoveOfficials.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(RemoveOfficials.this, "Error fetching officials", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
