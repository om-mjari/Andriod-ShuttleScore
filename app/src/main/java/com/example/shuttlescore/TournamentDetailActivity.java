package com.example.shuttlescore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TournamentDetailActivity extends AppCompatActivity {

    private TextView textViewTournamentName, textViewGroundName;
    TextView EditTournamentItem, DeleteTournamentItem, AddOfficialsItem, RemoveOfficialsItem, deleteScheduleItem, AddSponsorsItem, EditSponsorsItem, DeleteSponsorsItem, FeedbacksItem;
    private ImageView imageViewTournamentLogo,back;
    DrawerLayout drawerLayout;
    String userId,contactNo;
    ArrayList<String> contactNos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_detail);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });

        textViewTournamentName = findViewById(R.id.textViewTournamentName);
        textViewGroundName = findViewById(R.id.textViewGroundName);
        imageViewTournamentLogo = findViewById(R.id.imageViewTournamentLogo);
        back = findViewById(R.id.back);
        EditTournamentItem = findViewById(R.id.EditTournamentItem);
        DeleteTournamentItem = findViewById(R.id.DeleteTournamentItem);
        AddOfficialsItem = findViewById(R.id.AddOfficialsItem);
        RemoveOfficialsItem = findViewById(R.id.RemoveOfficialsItem);
        deleteScheduleItem = findViewById(R.id.deleteScheduleItem);
        AddSponsorsItem = findViewById(R.id.AddSponsorsItem);
        EditSponsorsItem = findViewById(R.id.EditSponsorsItem);
        DeleteSponsorsItem = findViewById(R.id.DeleteSponsorsItem);
        FeedbacksItem = findViewById(R.id.FeedbacksItem);

        drawerLayout = findViewById(R.id.drawerLayout);
        ImageView menuIcon = findViewById(R.id.menuIcon);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        String tournamentId = getIntent().getStringExtra("tournamentId");
        String tournamentName = getIntent().getStringExtra("tournamentName");
        String groundName = getIntent().getStringExtra("groundName");
        String tournamentLogo = getIntent().getStringExtra("tournamentLogo");
        String cityName = getIntent().getStringExtra("cityName");
        long createdAt = getIntent().getLongExtra("createdAt", 0);
        long deletedAt = getIntent().getLongExtra("deletedAt", 0);
        String organizerContactNo = getIntent().getStringExtra("organizerContactNo");
        String organizerEmailId = getIntent().getStringExtra("organizerEmailId");
        String organizerName = getIntent().getStringExtra("organizerName");
        String shuttlecockType = getIntent().getStringExtra("shuttlecockType");
        String singlesDoubles = getIntent().getStringExtra("singlesDoubles");
        String tournamentCategory = getIntent().getStringExtra("tournamentCategory");
        long tournamentStartDate = getIntent().getLongExtra("tournamentStartDate", 0);
        long tournamentEndDate = getIntent().getLongExtra("tournamentEndDate", 0);

        fetchOfficialUserIds(tournamentId);

        EditTournamentItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
                String phoneNumber = prefs.getString("contact", null);
                if (phoneNumber != null && phoneNumber.equals(organizerContactNo))  {
                    Intent i = new Intent(TournamentDetailActivity.this, EditTournaments.class);
                    i.putExtra("tournamentId", tournamentId);
                    i.putExtra("tournamentName", tournamentName);
                    i.putExtra("groundName", groundName);
                    i.putExtra("tournamentLogo", tournamentLogo);
                    i.putExtra("cityName", cityName);
                    i.putExtra("createdAt", createdAt);
                    i.putExtra("deletedAt", deletedAt);
                    i.putExtra("organizerContactNo", organizerContactNo);
                    i.putExtra("organizerEmailId", organizerEmailId);
                    i.putExtra("organizerName", organizerName);
                    i.putExtra("shuttlecockType", shuttlecockType);
                    i.putExtra("singlesDoubles", singlesDoubles);
                    i.putExtra("tournamentCategory", tournamentCategory);
                    i.putExtra("tournamentStartDate", tournamentStartDate);
                    i.putExtra("tournamentEndDate", tournamentEndDate);
                    startActivity(i);
                }
                else{
                    Toast.makeText(getApplicationContext(),"You are not authorized to Edit this tournament",Toast.LENGTH_SHORT).show();
                }
            }
        });
        DeleteTournamentItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
                String phoneNumber = prefs.getString("contact", null);
                if (phoneNumber != null && phoneNumber.equals(organizerContactNo)) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete Tournament")
                            .setMessage("Are You Sure You Want To Delete This " + tournamentName + " Tournament?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseHelper firebaseHelper = new FirebaseHelper();
                                    firebaseHelper.deleteTournamentById(tournamentId);
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                    Toast.makeText(v.getContext(), "Tournament Deleted Successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("NO", null)
                            .show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"You are not authorized to Delete this tournament",Toast.LENGTH_SHORT).show();
                }
            }
        });
        AddOfficialsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
                String phoneNumber = prefs.getString("contact", null);
                if (phoneNumber != null && phoneNumber.equals(organizerContactNo)) {
                    Intent i = new Intent(getApplicationContext(), AddOfficials.class);
                    i.putExtra("tournamentId", tournamentId);
                    i.putExtra("tournamentName", tournamentName);
                    i.putExtra("groundName", groundName);
                    i.putExtra("organizerContactNo", organizerContactNo);
                    startActivity(i);
                }
                else{
                    Toast.makeText(getApplicationContext(),"You are not authorized to Add Officials for this tournament",Toast.LENGTH_SHORT).show();
                }
            }
        });
        RemoveOfficialsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
                String phoneNumber = prefs.getString("contact", null);
                if (phoneNumber != null && phoneNumber.equals(organizerContactNo)) {
                    Intent i = new Intent(getApplicationContext(), RemoveOfficials.class);
                    i.putExtra("tournamentId", tournamentId);
                    i.putExtra("tournamentName", tournamentName);
                    i.putExtra("groundName", groundName);
                    i.putExtra("organizerContactNo", organizerContactNo);
                    startActivity(i);
                }
                else{
                    Toast.makeText(getApplicationContext(),"You are not authorized to Remove Officials for this tournament",Toast.LENGTH_SHORT).show();
                }
            }
        });
        deleteScheduleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
                String phoneNumber = prefs.getString("contact", null);

                if (phoneNumber != null && phoneNumber.equals(organizerContactNo)) {
                    new AlertDialog.Builder(TournamentDetailActivity.this)
                            .setTitle("Confirm Deletion")
                            .setMessage("Are you sure you want to delete all scheduled matches for this tournament?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference("tbl_tournament_matches")
                                            .orderByChild("tournamentId")
                                            .equalTo(tournamentId)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    boolean matchFound = false;
                                                    for (DataSnapshot matchSnapshot : snapshot.getChildren()) {
                                                        String matchStatus = matchSnapshot.child("matchStatus").getValue(String.class);
                                                        if ("Scheduled".equalsIgnoreCase(matchStatus)) {
                                                            matchSnapshot.getRef().removeValue();
                                                            matchFound = true;
                                                        }
                                                    }
                                                    if (matchFound) {
                                                        Toast.makeText(getApplicationContext(), "Scheduled matches deleted", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "No scheduled matches found to delete", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(getApplicationContext(), "Failed to delete scheduled matches", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "You are not authorized to delete scheduled matches for this tournament", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AddSponsorsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
                String phoneNumber = prefs.getString("contact", null);
                if (phoneNumber != null && phoneNumber.equals(organizerContactNo)) {
                    Intent i = new Intent(getApplicationContext(), AddSponsors.class);
                    i.putExtra("tournamentId", tournamentId);
                    i.putExtra("tournamentName", tournamentName);
                    i.putExtra("groundName", groundName);
                    i.putExtra("organizerContactNo", organizerContactNo);
                    startActivity(i);
                }
                else{
                    Toast.makeText(getApplicationContext(),"You are not authorized to Add Sponsors for this tournament",Toast.LENGTH_SHORT).show();
                }
            }
        });
        EditSponsorsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
                String phoneNumber = prefs.getString("contact", null);
                if (phoneNumber != null && phoneNumber.equals(organizerContactNo)) {
                    Intent i = new Intent(getApplicationContext(),EditSponsors.class);
                    i.putExtra("tournamentId", tournamentId);
                    i.putExtra("tournamentName", tournamentName);
                    i.putExtra("groundName", groundName);
                    i.putExtra("organizerContactNo", organizerContactNo);
                    startActivity(i);
                }
                else{
                    Toast.makeText(getApplicationContext(),"You are not authorized to Edit Sponsors for this tournament",Toast.LENGTH_SHORT).show();
                }
            }
        });
        DeleteSponsorsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
                String phoneNumber = prefs.getString("contact", null);
                if (phoneNumber != null && phoneNumber.equals(organizerContactNo)) {
                    Intent i = new Intent(getApplicationContext(), RemoveSponsors.class);
                    i.putExtra("tournamentId", tournamentId);
                    i.putExtra("tournamentName", tournamentName);
                    i.putExtra("groundName", groundName);
                    i.putExtra("organizerContactNo", organizerContactNo);
                    startActivity(i);
                }
                else{
                    Toast.makeText(getApplicationContext(),"You are not authorized to Remove Sponsors for this tournament",Toast.LENGTH_SHORT).show();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });
        FeedbacksItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),TournamentFeedback.class);
                i.putExtra("tournamentId", tournamentId);
                i.putExtra("tournamentName", tournamentName);
                i.putExtra("groundName",groundName);
                i.putExtra("organizerContactNo", organizerContactNo);
                startActivity(i);
            }
        });


        textViewTournamentName.setText(tournamentName);
        textViewGroundName.setText(groundName);

        Glide.with(this)
                .load(tournamentLogo)
                .placeholder(R.drawable.tournamentimage)
                .into(imageViewTournamentLogo);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        TournamentPagerAdapter pagerAdapter = new TournamentPagerAdapter(this, tournamentId,tournamentName, groundName,contactNos);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("MATCHES");
                    else if (position == 1) tab.setText("POINT TABLE");
                    else if (position == 2) tab.setText("TEAMS");
                    else tab.setText("SPONSOR");
                }).attach();
    }

    private void fetchOfficialUserIds(String tournamentId) {
        DatabaseReference officialsRef = FirebaseDatabase.getInstance().getReference("tbl_Officials");
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("tbl_Users");

        officialsRef.orderByChild("tournamentId").equalTo(tournamentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        contactNos = new ArrayList<>();
                        int totalOfficials = (int) dataSnapshot.getChildrenCount();
                        final int[] processedCount = {0};

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            userId = snapshot.child("userId").getValue(String.class);

                            if (userId != null) {
                                usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                        String phone = userSnapshot.child("phone").getValue(String.class);
                                        if (phone != null) {
                                            contactNos.add(phone);
                                        }
                                        processedCount[0]++;
                                        if (processedCount[0] == totalOfficials) {
                                            setupViewPagerWithContacts(tournamentId, contactNos);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        processedCount[0]++;
                                        if (processedCount[0] == totalOfficials) {
                                            setupViewPagerWithContacts(tournamentId, contactNos);
                                        }
                                        Log.e("FirebaseError", "User fetch error: " + error.getMessage());
                                    }
                                });
                            } else {
                                processedCount[0]++;
                                if (processedCount[0] == totalOfficials) {
                                    setupViewPagerWithContacts(tournamentId, contactNos);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("FirebaseError", "Error fetching officials: " + databaseError.getMessage());
                    }
                });
    }

    private void setupViewPagerWithContacts(String tournamentId, ArrayList<String> contactNos) {
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        TournamentPagerAdapter pagerAdapter = new TournamentPagerAdapter(
                this,
                tournamentId,
                textViewTournamentName.getText().toString(),
                textViewGroundName.getText().toString(),
                contactNos // send the list
        );
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("MATCHES");
                    else if (position == 1) tab.setText("POINT TABLE");
                    else if (position == 2) tab.setText("TEAMS");
                    else tab.setText("SPONSOR");
                }).attach();
    }

}
