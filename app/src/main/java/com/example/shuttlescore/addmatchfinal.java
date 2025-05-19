package com.example.shuttlescore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class addmatchfinal extends AppCompatActivity {

    LinearLayout topLeft, topRight, bottomLeft, bottomRight;
    RadioGroup gamepoints,serveGroup;
    RadioButton sets1, sets3,points7,points11,points21;
    Button startGame;
    ImageView resetIcon, logoIcon, imgback;
    TextView txtTitle;
    EditText etxtplayer1,etxtplayer2,etxtplayer3,etxtplayer4;
    FirebaseHelper firebaseHelper;
    private String player1Contact, player2Contact, player3Contact, player4Contact,points,sets;

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
        setContentView(R.layout.activity_addmatchfinal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initializeViews();
        firebaseHelper = new FirebaseHelper();
        onclick();
    }
    private void initializeViews() {

        logoIcon = findViewById(R.id.imgback);
        resetIcon = findViewById(R.id.imgreset);
        txtTitle = findViewById(R.id.txtteam);
        imgback = findViewById(R.id.imgback);

        etxtplayer1 = findViewById(R.id.etxtplayer1);
        etxtplayer2 = findViewById(R.id.etxtplayer2);
        etxtplayer3 = findViewById(R.id.etxtplayer3);
        etxtplayer4 = findViewById(R.id.etxtplayer4);

        topLeft = findViewById(R.id.top_left);
        topRight = findViewById(R.id.top_right);
        bottomLeft = findViewById(R.id.bottom_left);
        bottomRight = findViewById(R.id.bottom_right);

        serveGroup = findViewById(R.id.gamepoints);
        points7 = findViewById(R.id.points7);
        points11 = findViewById(R.id.points11);
        points21 = findViewById(R.id.points21);

        gamepoints = findViewById(R.id.sets_group);
        sets1 = findViewById(R.id.sets_1);
        sets3 = findViewById(R.id.sets_3);

        startGame = findViewById(R.id.start_game);
    }

    public void onclick(){
        etxtplayer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(addmatchfinal.this, searchplayer.class);
                intent.putExtra("player", "one");
                intent.putExtra("exclude1", player1Contact);
                intent.putExtra("exclude2", player2Contact);
                intent.putExtra("exclude3", player3Contact);
                intent.putExtra("exclude4", player4Contact);
                startActivityForResult(intent, 1);
            }
        });
        etxtplayer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(addmatchfinal.this, searchplayer.class);
                intent.putExtra("player", "two");
                intent.putExtra("exclude1", player1Contact);
                intent.putExtra("exclude2", player2Contact);
                intent.putExtra("exclude3", player3Contact);
                intent.putExtra("exclude4", player4Contact);
                startActivityForResult(intent, 2);
            }
        });
        etxtplayer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(addmatchfinal.this, searchplayer.class);
                intent.putExtra("player", "three");
                intent.putExtra("exclude1", player1Contact);
                intent.putExtra("exclude2", player2Contact);
                intent.putExtra("exclude3", player3Contact);
                intent.putExtra("exclude4", player4Contact);
                startActivityForResult(intent, 3);
            }
        });
        etxtplayer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(addmatchfinal.this, searchplayer.class);
                intent.putExtra("player", "four");
                intent.putExtra("exclude1", player1Contact);
                intent.putExtra("exclude2", player2Contact);
                intent.putExtra("exclude3", player3Contact);
                intent.putExtra("exclude4", player4Contact);
                startActivityForResult(intent, 4);
            }
        });
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pl1 = etxtplayer1.getText().toString();
                String pl2 = etxtplayer2.getText().toString();
                String pl3 = etxtplayer3.getText().toString();
                String pl4 = etxtplayer4.getText().toString();
                points = "";
                if(points7.isChecked()){
                    points = points7.getText().toString();
                } else if (points11.isChecked()) {
                    points = points11.getText().toString();;
                } else if (points21.isChecked()) {
                    points = points21.getText().toString();
                }

                sets = "";
                if (sets1.isChecked()){
                    sets = "1";
                } else if (sets3.isChecked()) {
                    sets = "3";
                }

                boolean allPlayersSelected = !pl1.isEmpty() && !pl2.isEmpty() && !pl3.isEmpty() && !pl4.isEmpty();
                boolean onlyPl1AndPl4 = !pl1.isEmpty() && pl2.isEmpty() && pl3.isEmpty() && !pl4.isEmpty();
                boolean onlyPl2AndPl3 = pl1.isEmpty() && !pl2.isEmpty() && !pl3.isEmpty() && pl4.isEmpty();

                if (!(allPlayersSelected || onlyPl1AndPl4 || onlyPl2AndPl3)) {
                    Toast.makeText(getApplicationContext(), "Please select valid player combinations", Toast.LENGTH_SHORT).show();
                }else if(points.isEmpty() || sets.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please select serve and sets", Toast.LENGTH_SHORT).show();
                }else{
                    String matchFormat = sets.equals("1") ? "1 Set" : "3 Sets";
                    String matchStatus = "Scheduled";
                    String scheduleTime = String.valueOf(System.currentTimeMillis());

                    String gameMode;
                    if (!pl1.isEmpty() && pl4 != null && !pl4.isEmpty() && pl2.isEmpty() && pl3.isEmpty()) {
                        gameMode = "Singles";
                    } else if (!pl2.isEmpty() && !pl3.isEmpty() && pl1.isEmpty() && pl4.isEmpty()) {
                        gameMode = "Singles";
                    } else if (!pl1.isEmpty() && !pl2.isEmpty() && !pl3.isEmpty() && !pl4.isEmpty()) {
                        gameMode = "Doubles";
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid player combination", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    firebaseHelper.addMatch(
                            "null",
                            scheduleTime,
                            gameMode,
                            points,
                            player1Contact != null ? player1Contact : "",
                            player2Contact != null ? player2Contact : "",
                            player3Contact != null ? player3Contact : "",
                            player4Contact != null ? player4Contact : "",
                            matchFormat,
                            matchStatus,
                            new FirebaseHelper.MatchInsertCallback() {
                                @Override
                                public void onMatchInserted(String matchId) {
                                    if (matchId != null) {
                                        Intent intent = new Intent(addmatchfinal.this, scoring.class);
                                        intent.putExtra("matchId", matchId);

                                        intent.putExtra("pl1", pl1);
                                        intent.putExtra("pl2", pl2);
                                        intent.putExtra("pl3", pl3);
                                        intent.putExtra("pl4", pl4);

                                        intent.putExtra("cnt1", player1Contact);
                                        intent.putExtra("cnt2", player2Contact);
                                        intent.putExtra("cnt3", player3Contact);
                                        intent.putExtra("cnt4", player4Contact);

                                        intent.putExtra("points", points);
                                        intent.putExtra("sets", sets);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(addmatchfinal.this, "Failed to create match", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );

                }

            }
        });
        resetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etxtplayer1.setText("");
                etxtplayer2.setText("");
                etxtplayer3.setText("");
                etxtplayer4.setText("");

                player1Contact = "";
                player2Contact = "";
                player3Contact = "";
                player4Contact = "";

                gamepoints.clearCheck();
                serveGroup.clearCheck();

                Toast.makeText(addmatchfinal.this, "Reset successful", Toast.LENGTH_SHORT).show();
            }
        });
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(addmatchfinal.this);
                builder.setTitle("Are you sure?")
                        .setMessage("Are you sure you want to Exit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(addmatchfinal.this, MainActivity.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            String playerName = data.getStringExtra("playername");
            String playerCnt = data.getStringExtra("playercnt");

            if (requestCode == 1 && playerName != null) {
                etxtplayer1.setText(playerName);
                player1Contact = playerCnt;
            } else if (requestCode == 2 && playerName != null) {
                etxtplayer2.setText(playerName);
                player2Contact = playerCnt;
            }else if (requestCode == 3 && playerName != null) {
                etxtplayer3.setText(playerName);
                player3Contact = playerCnt;
            }else if (requestCode == 4 && playerName != null) {
                etxtplayer4.setText(playerName);
                player4Contact = playerCnt;
            }
        }
    }
}