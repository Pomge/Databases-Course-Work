package com.example.myproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import io.github.kexanie.library.MathView;

public class TransmissionActivity extends AppCompatActivity {

    private TransmissionModel transmissionModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transmission_result);

        transmissionModel = (TransmissionModel) getIntent().getSerializableExtra("transmissionModel");
        getViewByMethod();
    }

    private void getViewByMethod() {
        int method = transmissionModel.method;
        TextView textView_2 = findViewById(R.id.textView_2);
        textView_2.setText(getResources().getStringArray(R.array.methods)[method]);

        MathView mathView = findViewById(R.id.formula_one);
//        String b = "When \\(a \\ne 0\\), there are two solutions to \\(ax^2 + bx + c = 0\\)" +
//                "and they are $$x = {-b \\pm \\sqrt{b^2-4ac} \\over 2a}.$$";

        String result = "\\(D_1=" + transmissionModel.D1_float + "=" + transmissionModel.D1_int + "\\) (ГОСТ)\n" +
                "\\(D_2=" + transmissionModel.D2_float + "=" + transmissionModel.D2_int + "\\) (ГОСТ)\n" +
                "\\(v=" + transmissionModel.velocity + "\\)\n";
        mathView.setText(result);

//        switch (method) {
//            case 0:
//                String method_0 =
//                        //"\\(D_1=k_1*\\sqrt[3]{N*{10^3\\over n_1}}=" +
//                        //transmissionModel.k1 + "*\\sqrt[3]{" + transmissionModel.N + "*{10^3\\over" + transmissionModel.n1 + "}}=" +
//                        "\\(D_1=" + transmissionModel.D1_float + "=" + transmissionModel.D1_int + "\\) (ГОСТ)";
//                mathView.setText(method_0);
//                break;
//            case 1:
//                String method_1 = "";
//                break;
//            case 2:
//                break;
//            case 3:
//                break;
//        }
    }
}
