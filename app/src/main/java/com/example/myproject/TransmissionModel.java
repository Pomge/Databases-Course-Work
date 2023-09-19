package com.example.myproject;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class TransmissionModel implements Serializable {
    // Расчет D1: Начало
    public int method;              // метод нахождения диаметра меньшего шкива
    // Метод 1:
    public float N;                 // мощность
    public float n1;                // частота вращения меньшего шкива
    public int k1;                  // параметр допускаемого значения в диапазоне
    // =============================================================================================
    // Метод 2:
    public boolean material_1;      // материал ремня (прорезиненный или синтетический) Таблица 1.1
    public boolean type_1;          // тип синтетического ремня (Тип I или Тип II) Таблица 1.1
    // =============================================================================================
    // Метод 3:
    public int D1_int;              // диаметр меньшего шкива (int)
    public float D1_float;          // диаметр меньшего шкива (Float)
    // =============================================================================================
    // Метод 4:
    public boolean material_2;      // материал ремня (прорезиненный или синтетический) Таблица 1.2
    public boolean model;           // модель прорезиненного ремня (Б-... или БКНЛ-...) Таблица 1.2
    public float deltaSmall;        // толщина прорезиненного ремня
    public int g;                   // число прокладок
    public int g_forCalc;           // число прокладок для Views
    public int ro;                  // толщина синтетического ремня
    public boolean layers;          // с прослойками или без
    // =============================================================================================
    // Расчет D1: Конец

    // Расчет D2: Начало
    public float n2;                // частота вращения большего шкива
    public float u;                 // передаточное число
    public boolean type_2;          // тип передачи (понижающая или повышающая)
    public float ksi;               // коэффициент скольжения ремня
    public int D2_int;              // диаметр большего шкива (int)
    public float D2_float;          // диаметр большего шкива (Float)
    // Расчет D2: Конец (успешно)

    // Расчет a: Начало
    public float velocity;          // скорость ремня
    public boolean type_3;          // тип передачи (среднескоростная или быстроходная)
    public float a_min;             // минимальное межосевое расстояние
    public float a;                 // межосевое расстояние
    public int _i_max;              // максимальная частота пробега ремня в секунду
    public int _i;                  // частота пробега ремня в секунду
    public float L_min;             // минимальная длина ремня
    public int L_int;               // длина ремня (int)
    public float L_float;           // длина ремня (float)
    public float deltaBig;          // средняя разница между диаметрами шкива
    public float D_avg;             // средний диаметр шкива
    public float lambda;            // скорректированная длина ремня
    // Расчет a: Конец

    // Расчет alpha_1: Начало
    public float alpha_1;           // угол обхвата меньшего шкива
    // Расчет alpha_1: Конец

    public TransmissionModel() {
    }
}
