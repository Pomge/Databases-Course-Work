package com.example.myproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Button button = findViewById(R.id.button_main);
        button.getBackground().setColorFilter(
                getResources().getColor(R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY);
        button.setTextColor(Color.WHITE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LinearLayout linearLayout = findViewById(R.id.linearly_main);
                linearLayout.removeAllViews();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ResultModel resultModel = dataSnapshot.getValue(ResultModel.class);
                    addChildren(resultModel);
                }

                ProgressBar progressBar = findViewById(R.id.progressBar_main);
                ScrollView scrollView = findViewById(R.id.scrollView_main);
                Button button = findViewById(R.id.button_main);

                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addChildren(final ResultModel resultModel) {
        final LinearLayout linearLayout = findViewById(R.id.linearly_main);
        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int height = (int) (64 * scale + 0.5f);

        final Button button = new Button(getApplicationContext());
        button.getBackground().setColorFilter(
                getResources().getColor(R.color.not_selected),
                PorterDuff.Mode.MULTIPLY);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);
        button.setLayoutParams(layoutParams);
        button.setText(resultModel.resultName);
        button.setTextColor(Color.BLACK);
        linearLayout.addView(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("resultModel", resultModel);
                startActivity(intent);
            }
        });

        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.delete));
                builder.setMessage(getString(R.string.choose));
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mDatabase.child(resultModel.id).removeValue();
                    }
                });
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.setCancelable(true);
                builder.create();
                builder.show();
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_main) {
            startActivity(new Intent(this, CalcActivity.class));
        }
    }
}