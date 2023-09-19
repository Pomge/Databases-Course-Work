package com.example.myproject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class CalcActivity extends AppCompatActivity implements
        View.OnClickListener,
        View.OnKeyListener,
        SeekBar.OnSeekBarChangeListener,
        AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {

    private boolean editing;

    private View view_AlertDialog_save;
    private Dialog alertDialog_save;
    private DatabaseReference mDatabase;

    private TextView mTextView_seekBar_k1_value;    // отображение значения для seekBar_k1
    private TextView mTextView_seekBar_g_value;     // отображение значения для seekBar_g
    private TextView mTextView_seekBar_ksi_value;   // отображение значения для seekBar_ksi
    private TextView mTextView_seekBar_i_max_value; // отображение значения для seekBar_ksi
    private TextView mTextView_seekBar_i_value;     // отображение значения для seekBar_ksi

    // Расчет D1: Начало
    private int method = 0;             // метод нахождения диаметра меньшего шкива
    // Метод 1:
    private float N = 0;                // мощность
    private float n1 = 0;               // частота вращения меньшего шкива
    private int k1 = 120;               // параметр допускаемого значения в диапазоне
    // =============================================================================================
    // Метод 2:
    private boolean material_1 = true;  // материал ремня (прорезиненный или синтетический) Таблица 1.1
    private boolean type_1 = true;      // тип синтетического ремня (Тип I или Тип II) Таблица 1.1
    // =============================================================================================
    // Метод 3:
    private int D1_int = -1;            // диаметр меньшего шкива (int)
    private float D1_float = -1;        // диаметр меньшего шкива (Float)
    // =============================================================================================
    // Метод 4:
    private boolean material_2 = true;  // материал ремня (прорезиненный или синтетический) Таблица 1.2
    private boolean model = false;      // модель прорезиненного ремня (Б-... или БКНЛ-...) Таблица 1.2
    private float deltaSmall = 0;       // толщина прорезиненного ремня
    private int g = 0;                  // число прокладок
    private int g_forCalc = 2;          // число прокладок для Views
    private int ro = 0;                 // толщина синтетического ремня
    private boolean layers = false;     // с прослойками или без
    // =============================================================================================
    // Расчет D1: Конец

    // Расчет D2: Начало
    private float n2 = 0;               // частота вращения большего шкива
    private float u = 0;                // передаточное число
    private boolean type_2 = false;     // тип передачи (понижающая или повышающая)
    private float ksi = 0.015f;         // коэффициент скольжения ремня
    private int D2_int = -1;            // диаметр большего шкива (int)
    private float D2_float = -1;        // диаметр большего шкива (Float)
    // Расчет D2: Конец (успешно)

    // Расчет a: Начало
    private float velocity = 0;         // скорость ремня
    private boolean type_3 = false;     // тип передачи (среднескоростная или быстроходная)
    private float a_min = 0;            // минимальное межосевое расстояние
    private float a = -1;               // межосевое расстояние
    private int _i_max = 1;             // максимальная частота пробега ремня в секунду
    private int _i = 1;                 // частота пробега ремня в секунду
    private float L_min = 0;            // минимальная длина ремня
    private int L_int = -1;             // длина ремня (int)
    private float L_float = -1;         // длина ремня (float)
    private float deltaBig = 0;         // средняя разница между диаметрами шкива
    private float D_avg = 0;            // средний диаметр шкива
    private float lambda = 0;           // скорректированная длина ремня
    // Расчет a: Конец

    // Расчет alpha_1: Начало
    private float alpha_1 = -1;         // угол обхвата меньшего шкива
    // Расчет alpha_1: Конец


    // массив значений скоростей ('v') из Таблицы 1.1
    private final ArrayList<Integer> D1_velocityValues = new ArrayList<>(
            Arrays.asList(30, 100, 50));

    // массив значений диаметров меньшего шкива ('D1') из Таблицы 1.2 для синтетических ремней
    private final ArrayList<Integer> D1_syntheticValues = new ArrayList<>(
            Arrays.asList(28, 36, 45, 56, 63, 71, 80, 90, 100));

    // массив значений толщины ремня ('deltaSmall') из Таблицы 1.3 для прорезиненных ремней
    private final ArrayList<Float> D1_deltaValues = new ArrayList<>(
            Arrays.asList(
                    4.5f, 6.0f, 7.5f, 9.0f,
                    3.75f, 5.0f, 6.25f, 7.5f,
                    3.6f, 4.8f, 6.0f, 7.2f,
                    3.0f, 4.0f, 5.0f, 6.0f));

    // массив значений диаметров меньшего шкива ('D1') из Таблицы 1.2 для прорезиненных ремней
    private final ArrayList<Integer> D1_rubberValues = new ArrayList<>(
            Arrays.asList(
                    180, 224, 315, 355,
                    140, 200, 250, 315,
                    140, 180, 224, 280,
                    125, 160, 200, 224));

    // массив значений по ГОСТУ для D1
    private final ArrayList<Integer> D_GOST_standard = new ArrayList<>(
            Arrays.asList(
                    40, 45, 50, 56, 63, 71, 80, 90, 100, 112, 125, 140,
                    160, 180, 200, 224, 250, 280, 315, 355, 400, 450, 500,
                    560, 630, 710, 800, 900, 1000, 1120, 1250, 1400, 1600, 1800, 2000));


    private String switch_typeOfTransmission_1_on;  // тип передачи 1ый switch_on
    private String switch_typeOfTransmission_1_off; // тип передачи 1ый switch_off
    private String switch_typeOfTransmission_2_on;  // тип передачи 2ой switch_on
    private String switch_typeOfTransmission_2_off; // тип передачи 2ой switch_off

    private Thread myThread;                        // отдельный поток, в котором происходит расчет 'a'
    private int selectedColor;                      // цвет, если выбрано
    private int notSelectedColor;                   // цвет, если не выбрано
    private ArrayList<TransmissionModel> transmissionModels;  // рассчитанные передачи
    private final int dividerForKsi = (int) Math.pow(10, 5); // делитель для значения ksi
    private final ArrayList<View> views = new ArrayList<>(); // массив для быстрого скрытия View
    private final int minimumValue_k1 = 110;        // минимальное значение для seekBar_k1
    private final int minimumValue_g = 3;           // минимальное значение для seekBar_g
    private final double minimumValue_ksi = 0.01;   // минимальное значение для seekBar_ksi
    private final int minimumValue_i_max = 1;       // минимальное значение для seekBar_i_max
    private final int minimumValue_i = 1;           // минимальное значение для seekBar_i
    private final PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY; // метод рисования
    private final DecimalFormat decimalFormatBig = new DecimalFormat("#.####");
    private final DecimalFormat decimalFormatSmall = new DecimalFormat("#.##");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // инициализация статических переменных
        initializeConstantVariables();
        // добавление компонентов View с изменяемой видимостью
        addViewsWithVariableVisibility();

        // установка слушателя на SeekBar компоненты
        setListenerOnSeekBars();
        // инициализация TextViews для SeekBars, отображающих их значение
        initializeSeekBars_Value_TextViews();
        // установка начальных значений на SeekBar компоненты
        setDefaultProgressToSeekBars();

        // инициализация Button компоненты
        initializeButtons();

        // установка слушателя на EditText компоненты
        setListenerOnEditTexts();
        // установка слушателя на Spinner компонент
        setListenerOnSpinner();
        // установка слушателя на Switch компоненты
        setListenerOnSwitch();

        // программное создание таблицы для синтетического ремня
        createSyntheticsTable();
    }

    @Override
    protected void onStart() {
        super.onStart();
        transmissionModels = (ArrayList<TransmissionModel>) getIntent()
                .getSerializableExtra("transmissions");
        editing = getIntent().getBooleanExtra("editing", false);

        if (editing) {
            Button button_finish = findViewById(R.id.button_finish);
            button_finish.setText(getResources().getString(R.string.save));
        }
        if (transmissionModels == null) transmissionModels = new ArrayList<>();
    }

    // функция инициализации константных переменных
    private void initializeConstantVariables() {
        switch_typeOfTransmission_1_on = getResources().getString(R.string.switch_typeOfTransmission_1_text_on);
        switch_typeOfTransmission_1_off = getResources().getString(R.string.switch_typeOfTransmission_1_text_off);
        switch_typeOfTransmission_2_on = getResources().getString(R.string.switch_typeOfTransmission_2_text_on);
        switch_typeOfTransmission_2_off = getResources().getString(R.string.switch_typeOfTransmission_2_text_off);

        selectedColor = getResources().getColor(R.color.selected);
        notSelectedColor = getResources().getColor(R.color.not_selected);
    }

    // функция добавления компонентов View с изменяемой видимостью
    private void addViewsWithVariableVisibility() {
        RelativeLayout relativeLayout_method_1 = findViewById(R.id.relativeLayout_method_1);
        RelativeLayout relativeLayout_method_2 = findViewById(R.id.relativeLayout_method_2);
        EditText editTextNumberDecimal_D1 = findViewById(R.id.editText_D1);
        RelativeLayout relativeLayout_method_4 = findViewById(R.id.relativeLayout_method_4);
        EditText editTextNumberDecimal_n1_3 = findViewById(R.id.editText_n1_method_3_4);

        views.add(relativeLayout_method_1);
        views.add(relativeLayout_method_2);
        views.add(editTextNumberDecimal_D1);
        views.add(relativeLayout_method_4);
        views.add(editTextNumberDecimal_n1_3);
    }

    // функция установки слушателя на SeekBar компоненты
    private void setListenerOnSeekBars() {
        SeekBar seekBar_k1_param_choose = findViewById(R.id.seekBar_k1_choose);
        SeekBar seekBar_g_param_choose = findViewById(R.id.seekBar_g_param_choose);
        SeekBar seekBar_ksi_param_choose = findViewById(R.id.seekBar_ksi_choose);
        SeekBar seekBar_i_max_param_choose = findViewById(R.id.seekBar_i_max_choose);
        SeekBar seekBar_i_param_choose = findViewById(R.id.seekBar_i_choose);

        seekBar_k1_param_choose.setOnSeekBarChangeListener(this);
        seekBar_g_param_choose.setOnSeekBarChangeListener(this);
        seekBar_ksi_param_choose.setOnSeekBarChangeListener(this);
        seekBar_i_max_param_choose.setOnSeekBarChangeListener(this);
        seekBar_i_param_choose.setOnSeekBarChangeListener(this);
    }

    // функция инициализации TextViews для SeekBars, отображающих их значение
    private void initializeSeekBars_Value_TextViews() {
        mTextView_seekBar_k1_value = findViewById(R.id.seekBar_k1_value);
        mTextView_seekBar_g_value = findViewById(R.id.seekBar_g_value);
        mTextView_seekBar_ksi_value = findViewById(R.id.seekBar_ksi_value);
        mTextView_seekBar_i_max_value = findViewById(R.id.seekBar_i_max_value);
        mTextView_seekBar_i_value = findViewById(R.id.seekBar_i_value);
    }

    // функция установки начальных значений на SeekBar компоненты
    private void setDefaultProgressToSeekBars() {
        SeekBar seekBar_k1_param_choose = findViewById(R.id.seekBar_k1_choose);
        SeekBar seekBar_g_param_choose = findViewById(R.id.seekBar_g_param_choose);
        SeekBar seekBar_ksi_param_choose = findViewById(R.id.seekBar_ksi_choose);
        SeekBar seekBar_i_max_param_choose = findViewById(R.id.seekBar_i_max_choose);
        SeekBar seekBar_i_param_choose = findViewById(R.id.seekBar_i_choose);

        int seekBar_k1_value = seekBar_k1_param_choose.getProgress() + minimumValue_k1;
        int seekBar_g_value = seekBar_g_param_choose.getProgress() + minimumValue_g;
        int seekBar_i_max_value = seekBar_i_max_param_choose.getProgress() + minimumValue_i_max;
        int seekBar_i_value = seekBar_i_param_choose.getProgress() + minimumValue_i;
        float seekBar_ksi_value = (float) seekBar_ksi_param_choose.getProgress() / dividerForKsi;
        String ksiText = decimalFormatBig.format(seekBar_ksi_value + minimumValue_ksi);

        mTextView_seekBar_k1_value.setText(String.valueOf(seekBar_k1_value));
        mTextView_seekBar_g_value.setText(String.valueOf(seekBar_g_value));
        mTextView_seekBar_ksi_value.setText(ksiText);
        mTextView_seekBar_i_max_value.setText(String.valueOf(seekBar_i_max_value));
        mTextView_seekBar_i_value.setText(String.valueOf(seekBar_i_value));
    }

    // функция инициализации Button компоненты
    private void initializeButtons() {
        Button button_material_synthetics_1 = findViewById(R.id.button_material_synthetics_1);
        Button button_type_2 = findViewById(R.id.button_type_2);
        Button button_model_rubber_1 = findViewById(R.id.button_model_rubber_1);
        Button button_layers_rubber_1 = findViewById(R.id.button_layers_rubber_1);
        Button button_material_synthetics_2 = findViewById(R.id.button_material_synthetics_2);

        button_material_synthetics_1.performClick();
        button_type_2.performClick();
        button_model_rubber_1.performClick();
        button_layers_rubber_1.performClick();
        button_material_synthetics_2.performClick();

        int color = getResources().getColor(R.color.colorPrimary);
        Button button_finish = findViewById(R.id.button_finish);
        button_finish.getBackground().setColorFilter(color, mode);
        button_finish.setTextColor(Color.WHITE);
    }

    // функция установки слушателя на EditText компоненты
    private void setListenerOnEditTexts() {
        EditText editTextNumberDecimal_N = findViewById(R.id.editText_N);
        EditText editTextNumberDecimal_n1_1 = findViewById(R.id.editText_n1_method_1);
        EditText editTextNumberDecimal_n1_2 = findViewById(R.id.editText_n1_method_2);
        EditText editTextNumberDecimal_D1 = findViewById(R.id.editText_D1);
        EditText editTextNumberDecimal_n1_3 = findViewById(R.id.editText_n1_method_3_4);
        EditText editTextNumberDecimal_n2 = findViewById(R.id.editText_n2);

        editTextNumberDecimal_N.setOnKeyListener(this);
        editTextNumberDecimal_n1_1.setOnKeyListener(this);
        editTextNumberDecimal_n1_2.setOnKeyListener(this);
        editTextNumberDecimal_D1.setOnKeyListener(this);
        editTextNumberDecimal_n1_3.setOnKeyListener(this);
        editTextNumberDecimal_n2.setOnKeyListener(this);
    }

    // функция установки слушателя на Spinner компонент
    private void setListenerOnSpinner() {
        Spinner spinner_D1_method_choose = findViewById(R.id.spinner_D1_method_choose);
        spinner_D1_method_choose.setOnItemSelectedListener(this);
    }

    // функция установки слушателя на Switch компоненты
    private void setListenerOnSwitch() {
        Switch switch_typeOfTransmission_1 = findViewById(R.id.switch_typeOfTransmission_1);
        Switch switch_typeOfTransmission_2 = findViewById(R.id.switch_typeOfTransmission_2);

        switch_typeOfTransmission_1.setOnCheckedChangeListener(this);
        switch_typeOfTransmission_2.setOnCheckedChangeListener(this);
    }

    // функция программного создания таблицы для синтетического ремня
    private void createSyntheticsTable() {
        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int width = (int) (64 * scale + 0.5f);
        int height = (int) (48 * scale + 0.5f);

        LinearLayout linearlyRo_choose = findViewById(R.id.linearly_ro_choose);

        for (int i = 0; i < D1_syntheticValues.size(); i++) {
            Button button = new Button(getApplicationContext());
            button.setLayoutParams(new LinearLayout.LayoutParams(width, height));
            button.getBackground().setColorFilter(notSelectedColor, mode);
            button.setGravity(Gravity.CENTER);
            button.setOnClickListener(this);
            button.setText(String.valueOf((4 + (float) i) / 10));
            linearlyRo_choose.addView(button);
        }
        linearlyRo_choose.getChildAt(0).performClick();
    }


    private void calculate_D1() {
        switch (method) {
            case 0:
                calculate_D1_1();
                break;
            case 1:
                calculate_D1_2();
                break;
            case 2:
                calculate_D1_3();
                break;
            case 3:
                calculate_D1_4();
                break;
        }
        D1_int = get_D_from_GOST_standard(D1_float);
        calculate_velocity();
        calculate_D2();
    }

    private void calculate_D1_1() {
        N = getFloatNumberFromEditText_byId(R.id.editText_N);
        n1 = getFloatNumberFromEditText_byId(R.id.editText_n1_method_1);

        if (N > 0 && n1 > 0) {
            D1_float = (float) ((float) k1 * Math.pow(N * Math.pow(10, 3) / n1, (float) 1 / 3));
        } else D1_float = -1;
    }

    private void calculate_D1_2() {
        n1 = getFloatNumberFromEditText_byId(R.id.editText_n1_method_2);

        if (n1 > 0) {
            if (material_1) {
                velocity = D1_velocityValues.get(1 + (type_1 ? 1 : 0));
            } else velocity = D1_velocityValues.get(0);

            D1_float = (float) ((velocity * Math.pow(10, 3) * 60) / (Math.PI * n1));
        } else D1_float = -1;
    }

    private void calculate_D1_3() {
        D1_float = getFloatNumberFromEditText_byId(R.id.editText_D1);
    }

    private void calculate_D1_4() {
        if (material_2) {
            D1_float = D1_syntheticValues.get(ro);
        } else {
            get_D1_ForRubberTable();
            get_delta_ForRubberTable();
        }
    }


    private void calculate_D2() {
        if (D1_float > 0 && n2 > 0) {
            u = n1 / n2;
            D2_float = D1_int * u;

            if (type_2) {
                D2_float /= ((float) 1 - ksi);
            } else D2_float *= ((float) 1 - ksi);

            D2_int = get_D_from_GOST_standard(D2_float);
            calculate_a_min();
        } else D2_float = -1;
    }


    private void calculate_velocity() {
        if (D1_float > 0 && method != 1) {
            velocity = (float) ((Math.PI * D1_int * n1) / (Math.pow(10, 3) * 60));
            calculate_L_min();
        }
    }

    private void calculate_a_min() {
        if (D2_float > 0) {
            if (type_3) {
                a_min = (float) (2.0 * (float) (D1_int + D2_int));
            } else a_min = (float) (1.5 * (float) (D1_int + D2_int));
            a = a_min;
            calculate_L();
            find_a();
        } else a = -1;
    }

    private void calculate_L_min() {
        L_min = velocity * 1000 / _i;
        calculate_a_min();
    }

    private void calculate_L() {
        if (D2_float > 0 && a > 0) {
            L_float = (float) (2.0 * a + Math.PI * (D1_float + D2_float) / 2.0 + Math.pow(D2_float - D1_float, 2) / (4.0 * a));
        } else L_float = -1;
    }

    private void startThread() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (L_min >= L_float) {
                    a += 1;
                    calculate_L();
                }
                L_int = get_L_from_GOST_standard(L_float);
            }
        };
        myThread = new Thread(runnable);
        myThread.start();
    }

    private void find_a() {
        if (myThread != null) {
            Thread dummy = myThread;
            myThread = null;
            dummy.interrupt();
        }
        startThread();
    }

    private void calculate_a() {
        if (L_float > 0 && D2_float > 0) {
            deltaBig = (float) ((D2_float - D1_float) / 2.0);
            D_avg = (float) ((D1_float + D2_float) / 2.0);
            lambda = (float) (L_float - Math.PI * D_avg);

            a = (float) ((lambda + Math.sqrt(Math.pow(lambda, 2) - 8.0 * Math.pow(deltaBig, 2))) / 4);
        } else a = -1;
    }


    private void calculate_alpha_1() {
        if (a > 0) {
            alpha_1 = (float) (180 - (D2_float - D1_float) / a * 57.0);
        } else alpha_1 = -1;
    }


    private int get_D_from_GOST_standard(float D_input) {
        int D_new = 0;
        int D_round = Math.round(D_input);
        int delta = D_GOST_standard.get(D_GOST_standard.size() - 1);

        for (Integer i : D_GOST_standard) {
            if (Math.abs(D_round - i) < delta) {
                delta = Math.abs(D_round - i);
                D_new = i;
            }
        }

        return D_new;
    }

    private int get_L_from_GOST_standard(float L_input) {
        return (int) Math.ceil(L_input / 50.0) * 50;
    }


    // функция определения deltaSmall по данным из таблицы для прорезиненного ремня
    private void get_delta_ForRubberTable() {
        int resultID = g_forCalc;
        if (model) resultID += D1_rubberValues.size() / 2;
        if (layers) resultID += D1_rubberValues.size() / 4;

        deltaSmall = D1_deltaValues.get(resultID);
    }

    // функция определения D1 по данным из таблицы для прорезиненного ремня
    private void get_D1_ForRubberTable() {
        int resultID = g_forCalc;
        if (model) resultID += D1_rubberValues.size() / 2;
        if (layers) resultID += D1_rubberValues.size() / 4;

        D1_float = D1_rubberValues.get(resultID);
    }


    // функция обновления компонентов View с изменяемой видимостью
    private void updateMethodViews(int methodNumber) {
        for (int i = 0; i < views.size(); i++) {
            if (i != methodNumber) {
                views.get(i).setVisibility(View.GONE);
            } else views.get(i).setVisibility(View.VISIBLE);
        }

        if (methodNumber > 1) {
            views.get(views.size() - 1).setVisibility(View.VISIBLE);
        } else views.get(views.size() - 1).setVisibility(View.GONE);
    }

    // функция преобразования текста из EditText компонента в Float число
    private float getFloatNumberFromEditText_byId(int editTextId) {
        EditText editText = findViewById(editTextId);
        String input = editText.getText().toString();

        return input.isEmpty() ? -1 : Float.parseFloat(input);
    }

    // функция смены цвета Button компонента
    private void changeColorForButton_byId(int buttonId, boolean isPressed) {
        Button button = findViewById(buttonId);
        button.getBackground().setColorFilter(isPressed ? selectedColor : notSelectedColor, mode);
        button.setTextColor(isPressed ? Color.WHITE : Color.BLACK);
    }


    private void addNewDataToResultArray() {
        TransmissionModel transmissionModel = new TransmissionModel();
        transmissionModel.method = method;
        transmissionModel.N = N;
        transmissionModel.n1 = n1;
        transmissionModel.k1 = k1;
        transmissionModel.material_1 = material_1;
        transmissionModel.type_1 = type_1;
        transmissionModel.D1_int = D1_int;
        transmissionModel.D1_float = D1_float;
        transmissionModel.material_2 = material_2;
        transmissionModel.model = model;
        transmissionModel.deltaSmall = deltaSmall;
        transmissionModel.g = g;
        transmissionModel.g_forCalc = g_forCalc;
        transmissionModel.ro = ro;
        transmissionModel.layers = layers;
        transmissionModel.n2 = n2;
        transmissionModel.u = u;
        transmissionModel.type_2 = type_2;
        transmissionModel.ksi = ksi;
        transmissionModel.D2_int = D2_int;
        transmissionModel.D2_float = D2_float;
        transmissionModel.velocity = velocity;
        transmissionModel.type_3 = type_3;
        transmissionModel.a_min = a_min;
        transmissionModel.a = a;
        transmissionModel._i_max = _i_max;
        transmissionModel._i = _i;
        transmissionModel.L_min = L_min;
        transmissionModel.L_int = L_int;
        transmissionModel.L_float = L_float;
        transmissionModel.deltaBig = deltaBig;
        transmissionModel.D_avg = D_avg;
        transmissionModel.lambda = lambda;
        transmissionModel.alpha_1 = alpha_1;

        transmissionModels.add(transmissionModel);
    }

    private void restartActivity() {
        Intent intent = new Intent(CalcActivity.this, CalcActivity.class);
        intent.putExtra("transmissions", transmissionModels);

        startActivity(intent);
        finish();
    }

    private void backToMainActivity() {
        EditText editText = view_AlertDialog_save.findViewById(R.id.editText_name);
        String name = editText.getText().toString();

        if (name.isEmpty()) {
            name = editText.getHint().toString();
            editText.setText(name);
        }
        ProgressBar progressBar_save = findViewById(R.id.progressBar_save);
        ScrollView scrollView = findViewById(R.id.scrollView_calc);
        progressBar_save.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        String key = FirebaseDatabase.getInstance().getReference().push().getKey();
        ResultModel resultModel = new ResultModel();
        resultModel.id = key;
        resultModel.resultName = name;
        resultModel.transmissionModels = transmissionModels;
        mDatabase.child(key).setValue(resultModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        });
    }

    private String getDialogMessage() {
        String message_1 = getResources().getString(R.string.message_1) + "\n" +
                getResources().getStringArray(R.array.methods)[method] + "\n\n";
        String message_2 = getResources().getString(R.string.message_2) + " " +
                D1_int + " " + getResources().getString(R.string.mm) + "\n";
        String message_3 = getResources().getString(R.string.message_3) + " " +
                D2_int + " " + getResources().getString(R.string.mm) + "\n";
        String message_4 = getResources().getString(R.string.message_4) + " " +
                decimalFormatSmall.format(velocity) + " " + getResources().getString(R.string.m_s) + "\n";
        String message_5 = getResources().getString(R.string.message_5) + " " +
                L_int + " " + getResources().getString(R.string.mm) + "\n";
        String message_6 = getResources().getString(R.string.message_6) + " " +
                _i + " " + getResources().getString(R.string.hz) + "\n";
        String message_7 = getResources().getString(R.string.message_7) + " " +
                decimalFormatSmall.format(a) + " " + getResources().getString(R.string.mm) + "\n";
        String message_8 = getResources().getString(R.string.message_8) + " " +
                decimalFormatSmall.format(alpha_1) + getResources().getString(R.string.grad);

        return message_1 + message_2 + message_3 + message_4 +
                message_5 + message_6 + message_7 + message_8;
    }

    private void requestFocusToEditText_byId(int editText_Id) {
        EditText editText = findViewById(editText_Id);
        editText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private boolean areAllFormsFilled() {
        switch (method) {
            case 0:
                if (getFloatNumberFromEditText_byId(R.id.editText_N) == -1) {
                    requestFocusToEditText_byId(R.id.editText_N);
                    return false;
                } else if (getFloatNumberFromEditText_byId(R.id.editText_n1_method_1) == -1) {
                    requestFocusToEditText_byId(R.id.editText_n1_method_1);
                    return false;
                }
                break;
            case 1:
                if (getFloatNumberFromEditText_byId(R.id.editText_n1_method_2) == -1) {
                    requestFocusToEditText_byId(R.id.editText_n1_method_2);
                    return false;
                }
                break;
            case 2:
                if (getFloatNumberFromEditText_byId(R.id.editText_D1) == -1) {
                    requestFocusToEditText_byId(R.id.editText_D1);
                    return false;
                }
                if (getFloatNumberFromEditText_byId(R.id.editText_n1_method_3_4) == -1) {
                    requestFocusToEditText_byId(R.id.editText_n1_method_3_4);
                    return false;
                }
                break;
            case 3:
                if (getFloatNumberFromEditText_byId(R.id.editText_n1_method_3_4) == -1) {
                    requestFocusToEditText_byId(R.id.editText_n1_method_3_4);
                    return false;
                }
                break;
        }
        if (getFloatNumberFromEditText_byId(R.id.editText_n2) == -1) {
            requestFocusToEditText_byId(R.id.editText_n2);
            return false;
        }
        return true;
    }

    private void finishCalculatingDialog() {
        String alertDialog_title = getResources().getString(R.string.alertDialog_title_1);
        String alertDialog_finish = getResources().getString(R.string.alertDialog_finish);
        String alertDialog_new = getResources().getString(R.string.alertDialog_new);

        AlertDialog.Builder builder = new AlertDialog.Builder(CalcActivity.this);
        builder.setTitle(alertDialog_title);
        builder.setMessage(getDialogMessage());
        builder.setPositiveButton(alertDialog_finish, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                saveDialog();
            }
        });
        builder.setNegativeButton(alertDialog_new, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                addNewDataToResultArray();
                restartActivity();
            }
        });
        builder.setCancelable(true);
        builder.create();
        builder.show();
    }

    private void saveDialog() {
        String alertDialog_title = getResources().getString(R.string.alertDialog_title_2);
        LayoutInflater inflater = getLayoutInflater();
        view_AlertDialog_save = inflater.inflate(R.layout.alert_dialog_name, null);
        EditText editText = view_AlertDialog_save.findViewById(R.id.editText_name);
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        String timeText = timeFormat.format(currentDate);
        String alertDialog_save_name = getResources().getString(R.string.new_calc) + " " + dateText + " " + timeText;
        editText.setHint(alertDialog_save_name);

        AlertDialog.Builder builder = new AlertDialog.Builder(CalcActivity.this);
        builder.setTitle(alertDialog_title);
        builder.setCancelable(true);
        builder.setView(view_AlertDialog_save);
        alertDialog_save = builder.create();
        alertDialog_save.show();
    }


    // слушатель для Button компонентов
    @Override
    public void onClick(View v) {
        Button buttonType_1 = findViewById(R.id.button_type_1);
        Button buttonType_2 = findViewById(R.id.button_type_2);

        RelativeLayout relativeLayoutMethod4_rubber = findViewById(R.id.relativeLayout_method_4_rubber);
        RelativeLayout relativeLayoutMethod4_synthetic = findViewById(R.id.relativeLayout_method_4_synthetic);

        switch (v.getId()) {
            case R.id.button_material_rubber_1:
                material_1 = false;

                changeColorForButton_byId(R.id.button_material_rubber_1, true);
                changeColorForButton_byId(R.id.button_material_synthetics_1, false);

                buttonType_1.setVisibility(View.GONE);
                buttonType_2.setVisibility(View.GONE);
                break;

            case R.id.button_material_synthetics_1:
                material_1 = true;

                changeColorForButton_byId(R.id.button_material_rubber_1, false);
                changeColorForButton_byId(R.id.button_material_synthetics_1, true);

                buttonType_1.setVisibility(View.VISIBLE);
                buttonType_2.setVisibility(View.VISIBLE);
                break;

            case R.id.button_type_1:
                type_1 = false;

                changeColorForButton_byId(R.id.button_type_1, true);
                changeColorForButton_byId(R.id.button_type_2, false);
                break;

            case R.id.button_type_2:
                type_1 = true;

                changeColorForButton_byId(R.id.button_type_1, false);
                changeColorForButton_byId(R.id.button_type_2, true);
                break;

            case R.id.button_material_rubber_2:
                material_2 = false;

                changeColorForButton_byId(R.id.button_material_rubber_2, true);
                changeColorForButton_byId(R.id.button_material_synthetics_2, false);

                relativeLayoutMethod4_rubber.setVisibility(View.VISIBLE);
                relativeLayoutMethod4_synthetic.setVisibility(View.GONE);
                break;

            case R.id.button_material_synthetics_2:
                material_2 = true;

                changeColorForButton_byId(R.id.button_material_rubber_2, false);
                changeColorForButton_byId(R.id.button_material_synthetics_2, true);

                relativeLayoutMethod4_rubber.setVisibility(View.GONE);
                relativeLayoutMethod4_synthetic.setVisibility(View.VISIBLE);
                break;

            case R.id.button_model_rubber_1:
                model = false;

                changeColorForButton_byId(R.id.button_model_rubber_1, true);
                changeColorForButton_byId(R.id.button_model_rubber_2, false);
                break;

            case R.id.button_model_rubber_2:
                model = true;

                changeColorForButton_byId(R.id.button_model_rubber_1, false);
                changeColorForButton_byId(R.id.button_model_rubber_2, true);
                break;

            case R.id.button_layers_rubber_1:
                layers = false;

                changeColorForButton_byId(R.id.button_layers_rubber_1, true);
                changeColorForButton_byId(R.id.button_layers_rubber_2, false);
                break;

            case R.id.button_layers_rubber_2:
                layers = true;

                changeColorForButton_byId(R.id.button_layers_rubber_1, false);
                changeColorForButton_byId(R.id.button_layers_rubber_2, true);
                break;

            case R.id.button_finish:
                calculate_a();
                calculate_alpha_1();

                if (areAllFormsFilled()) {
                    if (editing) {
                        Intent intent = new Intent();
                        TransmissionModel transmissionModel = new TransmissionModel();
                        transmissionModel.method = method;
                        transmissionModel.N = N;
                        transmissionModel.n1 = n1;
                        transmissionModel.k1 = k1;
                        transmissionModel.material_1 = material_1;
                        transmissionModel.type_1 = type_1;
                        transmissionModel.D1_int = D1_int;
                        transmissionModel.D1_float = D1_float;
                        transmissionModel.material_2 = material_2;
                        transmissionModel.model = model;
                        transmissionModel.deltaSmall = deltaSmall;
                        transmissionModel.g = g;
                        transmissionModel.g_forCalc = g_forCalc;
                        transmissionModel.ro = ro;
                        transmissionModel.layers = layers;
                        transmissionModel.n2 = n2;
                        transmissionModel.u = u;
                        transmissionModel.type_2 = type_2;
                        transmissionModel.ksi = ksi;
                        transmissionModel.D2_int = D2_int;
                        transmissionModel.D2_float = D2_float;
                        transmissionModel.velocity = velocity;
                        transmissionModel.type_3 = type_3;
                        transmissionModel.a_min = a_min;
                        transmissionModel.a = a;
                        transmissionModel._i_max = _i_max;
                        transmissionModel._i = _i;
                        transmissionModel.L_min = L_min;
                        transmissionModel.L_int = L_int;
                        transmissionModel.L_float = L_float;
                        transmissionModel.deltaBig = deltaBig;
                        transmissionModel.D_avg = D_avg;
                        transmissionModel.lambda = lambda;
                        transmissionModel.alpha_1 = alpha_1;

                        intent.putExtra("index", getIntent().getIntExtra("index", -1));
                        intent.putExtra("transmissionModel", transmissionModel);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else finishCalculatingDialog();
                } else Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.fillingError),
                        Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_save:
                alertDialog_save.dismiss();
                addNewDataToResultArray();
                backToMainActivity();
                break;

            default:
                LinearLayout linearLayout = findViewById(R.id.linearly_ro_choose);

                for (int i = 0; i < D1_syntheticValues.size(); i++) {
                    Button button = (Button) linearLayout.getChildAt(i);

                    if (v == button) {
                        ro = i;
                        button.getBackground().setColorFilter(selectedColor, mode);
                        button.setTextColor(Color.WHITE);
                    } else {
                        button.getBackground().setColorFilter(notSelectedColor, mode);
                        button.setTextColor(Color.BLACK);
                    }
                }
                break;
        }
        calculate_D1();
    }

    // слушатель для EditText компонентов
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (v.getId()) {
            case R.id.editText_N:
                N = getFloatNumberFromEditText_byId(R.id.editText_N);
                //calculate_D1();
                break;
            case R.id.editText_n1_method_1:
                n1 = getFloatNumberFromEditText_byId(R.id.editText_n1_method_1);
                //calculate_D1();
                break;
            case R.id.editText_n1_method_2:
                n1 = getFloatNumberFromEditText_byId(R.id.editText_n1_method_2);
                //calculate_D1();
                break;
            case R.id.editText_D1:
                D1_float = getFloatNumberFromEditText_byId(R.id.editText_D1);
                //calculate_D1();
                break;
            case R.id.editText_n1_method_3_4:
                n1 = getFloatNumberFromEditText_byId(R.id.editText_n1_method_3_4);
                break;
            case R.id.editText_n2:
                n2 = getFloatNumberFromEditText_byId(R.id.editText_n2);
                break;
        }
        calculate_D1();
        return false;
    }

    // слушатель для SeekBar компонентов
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekBar_k1_choose:
                k1 = progress + minimumValue_k1;
                mTextView_seekBar_k1_value.setText(String.valueOf(k1));
                calculate_D1();
                break;

            case R.id.seekBar_g_param_choose:
                g_forCalc = progress;
                g = progress + minimumValue_g;
                mTextView_seekBar_g_value.setText(String.valueOf(g));
                calculate_D1();
                break;

            case R.id.seekBar_ksi_choose:
                ksi = (float) ((float) progress / dividerForKsi + minimumValue_ksi);
                String ksiText = decimalFormatBig.format(ksi);
                mTextView_seekBar_ksi_value.setText(ksiText);
                calculate_D2();
                break;

            case R.id.seekBar_i_max_choose:
                _i_max = progress + minimumValue_i_max;
                mTextView_seekBar_i_max_value.setText(String.valueOf(_i_max));
                SeekBar seekBar_i_param_choose = findViewById(R.id.seekBar_i_choose);
                seekBar_i_param_choose.setMax(progress);
                break;

            case R.id.seekBar_i_choose:
                _i = progress + minimumValue_i;
                calculate_L_min();
                mTextView_seekBar_i_value.setText(String.valueOf(_i));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    // слушатель для Switch компонентов
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_typeOfTransmission_1:
                type_2 = isChecked;
                calculate_D2();

                if (isChecked) {
                    buttonView.setText(switch_typeOfTransmission_1_on);
                } else buttonView.setText(switch_typeOfTransmission_1_off);
                break;

            case R.id.switch_typeOfTransmission_2:
                type_3 = isChecked;
                calculate_a_min();
                SeekBar seekBar_i_max_param_choose = findViewById(R.id.seekBar_i_max_choose);

                if (isChecked) {
                    seekBar_i_max_param_choose.setMax(4);
                    buttonView.setText(switch_typeOfTransmission_2_on);
                } else {
                    seekBar_i_max_param_choose.setMax(49);
                    buttonView.setText(switch_typeOfTransmission_2_off);
                }
                break;
        }
    }

    // слушатель для Spinner компонентов
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        method = position;
        calculate_D1();
        updateMethodViews(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (editing) setResult(RESULT_CANCELED);
    }
}