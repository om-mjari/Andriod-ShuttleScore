package com.example.shuttlescore;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SingleMatches extends AppCompatActivity {

    Spinner points,matchFormat,tossWinner,choice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_matches);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        intialize();
        arraybind();
    }
    public void intialize(){
        points = findViewById(R.id.points);
        matchFormat = findViewById(R.id.matchFormat);
        tossWinner = findViewById(R.id.tossWinner);
        choice = findViewById(R.id.choice);
    }
    public void arraybind(){

        String player1 = getIntent().getStringExtra("player1");
        String player2 = getIntent().getStringExtra("player2");

        String[] players = {player1, player2};

        ArrayAdapter<String> tossWinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, players);
        tossWinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tossWinner.setAdapter(tossWinnerAdapter);

        ArrayAdapter<CharSequence> matchformatAdapter = ArrayAdapter.createFromResource(this, R.array.matchformat_options, android.R.layout.simple_spinner_item);
        matchformatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        matchFormat.setAdapter(matchformatAdapter );

        ArrayAdapter<CharSequence> choiceAdapter = ArrayAdapter.createFromResource(this, R.array.choice_options, android.R.layout.simple_spinner_item);
        choiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choice.setAdapter(choiceAdapter);

        ArrayAdapter<CharSequence> pointsAdapter = ArrayAdapter.createFromResource(this, R.array.points_options, android.R.layout.simple_spinner_item);
        pointsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        points.setAdapter(pointsAdapter);
    }
}