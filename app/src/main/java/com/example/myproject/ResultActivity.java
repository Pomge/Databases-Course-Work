package com.example.myproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;

public class ResultActivity extends AppCompatActivity {

    private ResultModel resultModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultModel = (ResultModel) getIntent().getSerializableExtra("resultModel");
        EditText editText = findViewById(R.id.editText_resultName);
        editText.setText(resultModel.resultName);

        Button button = findViewById(R.id.button_saveAllChanges);
        button.getBackground().setColorFilter(
                getResources().getColor(R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY);
        button.setTextColor(Color.WHITE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.editText_resultName);
                resultModel.resultName = editText.getText().toString();

                FirebaseDatabase.getInstance().getReference()
                        .child(resultModel.id).setValue(resultModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        addChildrenViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    int index = data.getIntExtra("index", -1);
                    TransmissionModel editedTransmissionModel = (TransmissionModel) data.getSerializableExtra("transmissionModel");
                    resultModel.transmissionModels.set(index, editedTransmissionModel);
                }
                break;
            case 1:
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void addChildrenViews() {
        LinearLayout linearLayout_result = findViewById(R.id.linearly_result);
        linearLayout_result.removeAllViews();
        for (int i = 0; i < resultModel.transmissionModels.size(); i++) {
            linearLayout_result.addView(addChildren(i + 1, resultModel.transmissionModels.get(i)));
        }
    }

    private View addChildren(final int i, final TransmissionModel transmissionModel) {
        LayoutInflater inflater = getLayoutInflater();
        DecimalFormat decimalFormat = new DecimalFormat("#.####");

        View view = inflater.inflate(R.layout.linear_layout_child, null);
        TextView textView_0 = view.findViewById(R.id.textView_0);
        TextView textView_1 = view.findViewById(R.id.textView_1);
        TextView textView_2 = view.findViewById(R.id.textView_2);
        TextView textView_3 = view.findViewById(R.id.textView_3);
        TextView textView_4 = view.findViewById(R.id.textView_4);
        TextView textView_5 = view.findViewById(R.id.textView_5);

        String textView_0_string = textView_0.getText().toString() + " " + i;
        String textView_1_string = textView_1.getText().toString() +
                " " + transmissionModel.D1_int + " " + getResources().getString(R.string.mm);
        String textView_2_string = textView_2.getText().toString() +
                " " + transmissionModel.D2_int + " " + getResources().getString(R.string.mm);
        String textView_3_string = textView_3.getText().toString() +
                " " + decimalFormat.format(transmissionModel.velocity) + " " + getResources().getString(R.string.m_s);
        String textView_4_string = textView_4.getText().toString() +
                " " + transmissionModel.L_int + " " + getResources().getString(R.string.mm);
        String textView_5_string = textView_5.getText().toString() +
                " " + decimalFormat.format(transmissionModel.alpha_1) + " " + getResources().getString(R.string.grad);

        textView_0.setText(textView_0_string);
        textView_1.setText(textView_1_string);
        textView_2.setText(textView_2_string);
        textView_3.setText(textView_3_string);
        textView_4.setText(textView_4_string);
        textView_5.setText(textView_5_string);

//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ResultActivity.this, TransmissionActivity.class);
//                intent.putExtra("transmissionModel", resultModel.transmissionModels.get(i - 1));
//                startActivity(intent);
//            }
//        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                builder.setTitle(getString(R.string.transmission) + " " + i);
                builder.setMessage(getString(R.string.choose));
                builder.setPositiveButton(getString(R.string.edit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ResultActivity.this, CalcActivity.class);
                        intent.putExtra("editing", true);
                        intent.putExtra("index", i - 1);
                        intent.putExtra("transmissionModel", transmissionModel);
                        startActivityForResult(intent, 0);
                    }
                });
                builder.setNegativeButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resultModel.transmissionModels.remove(i - 1);
                        addChildrenViews();
                    }
                });
                builder.setCancelable(true);
                builder.create();
                builder.show();
                return false;
            }
        });

        return view;
    }
}
