package com.example.ontrack_test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.example.ontrack_test.MainActivity.APP_PREFERENCES;


public class AddExpenseActivity extends AppCompatActivity {


    public static final String DAYS_ARRAY = "DaysArray";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* Loading array values from SP and applying them on TextView */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.AddExpToolbar);
        setSupportActionBar(myToolbar);
// my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.AddExpToolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        //addItemsOnSpinner();

        int days_OnC[] = new int[31];

        TextView textView = findViewById(R.id.history1);

        for (int i = 0; i < days_OnC.length; i++) {
            days_OnC[i] = GetDayValue(i + 1);
        }

        displayHistory(days_OnC,textView);

    }

    public void add_nec(View view) {

        /*Reading value of EditText after pressing a Necessary button and sending the value
        to MainActivity*/

        int input_nec;
        EditText editText = (EditText) findViewById(R.id.editText);
        String input_nec_str = editText.getText().toString();
        if (input_nec_str.matches("")) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Add Value First!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else {
            input_nec = Integer.parseInt(input_nec_str);
        }
        Intent intent = new Intent(AddExpenseActivity.this, MainActivity.class);
        intent.putExtra("InputNec", input_nec);
        startActivity(intent);
    }

    public void add_day(View view) {
    /* Getting value from edittext, checking it not to be null.
    Loading SP in array, then applying current input value to an item in an array
    Saving whole array and applying text
    */
        int[] days = new int[31];

        int input_daily_temp;
        int input_daily_sum = 0;


        SharedPreferences mSettings;
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        EditText editText = (EditText) findViewById(R.id.editText);
        String input_daily_str = editText.getText().toString();
        if (input_daily_str.matches("")) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Add Value First!", Toast.LENGTH_SHORT);
            toast.show();
            return;

        } else {
            input_daily_temp = Integer.parseInt(input_daily_str);
        }


        TextView textView = findViewById(R.id.history1);


        if (mSettings.contains(DAYS_ARRAY)) {
            input_daily_sum = GetDayValue(currentDay());
            input_daily_sum += input_daily_temp;
            for (int q = 0; q < 31; q++) {
                days[q] = GetDayValue(q + 1);
            }
        }

        if (input_daily_str.matches("66106610")) {
            for (int y = 0; y < 31; y++) {
                days[y] = 0;
            }
            input_daily_temp = 0;
            input_daily_sum = 0;
        }

        days[currentDay() - 1] = input_daily_sum;


        SaveDaysValues(days);




        displayHistory(days,textView);

        Intent intent = new Intent(AddExpenseActivity.this, MainActivity.class);

        startActivity(intent);
    }

    public int getDays() {

        /*
        Getting amount of days in a current month.
        Maybe could be done easier
         */
        Calendar calendar = Calendar.getInstance();
        int iYear = calendar.get(Calendar.YEAR);
        int iMonth = calendar.get(Calendar.MONTH); // 1 (months begin with 0)
        int iDay = calendar.get(Calendar.DATE);
        Calendar mycal = new GregorianCalendar(iYear, iMonth, iDay);
        return mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public int currentDay() {
        /*
        Getting int value of current day
         */
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DATE);
    }

    public int GetDayValue(int day) {
        /*
        Get value saved in SP for current day
        Calendar day input required, since array starts from zero,
        and decrement is included in this function
         */
        SharedPreferences mSettings;
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String temp_days_str = mSettings.getString(DAYS_ARRAY, null);
        String[] integerStrings = temp_days_str.split(", ");
        day--;
        return Integer.parseInt(integerStrings[day]);
    }

    public void SaveDaysValues(int[] days_temp) {

        /* преобразование int array в String для хранения, не работает без обрезки по краям */

        SharedPreferences SaveSettings;
        SaveSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        String days_str = Arrays.toString(days_temp);
        days_str = days_str.substring(1, days_str.length() - 1);

        SharedPreferences.Editor editor_arr = SaveSettings.edit();
        editor_arr.putString(DAYS_ARRAY, days_str);
        editor_arr.apply();
    }

    public void ChangeSpecificDay(View view) {

        int[] days_temp = new int[31];

        int amount_specific;
        int input_specific_sum = 0;
        int day_specific = 0;

        SharedPreferences mSettings;
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        EditText ET_Amount = (EditText) findViewById(R.id.ET_specific_amount);
        String input_specific_amount_str = ET_Amount.getText().toString();
        if (input_specific_amount_str.matches("")) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Add Value First!", Toast.LENGTH_SHORT);
            toast.show();
            return;

        } else {
            amount_specific = Integer.parseInt(input_specific_amount_str);
        }

        EditText ET_Day = (EditText) findViewById(R.id.ET_specific_day);
        String input_specific_day_str = ET_Day.getText().toString();
        if (input_specific_day_str.matches("")) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Add Value First!", Toast.LENGTH_SHORT);
            toast.show();
            return;

        } else {
            day_specific = Integer.parseInt(input_specific_day_str);
        }


        TextView textView = findViewById(R.id.history1);

        if (mSettings.contains(DAYS_ARRAY)) {

            for (int q = 0; q < 31; q++) {
                days_temp[q] = GetDayValue(q + 1);
            }
        }

        days_temp[day_specific - 1] = amount_specific;


        SaveDaysValues(days_temp);

displayHistory(days_temp, textView);
    }



    public void displayHistory(int [] days_temp, TextView textView) {
        String extra_0;
        textView.setText("");
        for (int i = 0; i < getDays(); i++) {
            if (i < 9) {
                extra_0 = "0";
            } else {
                extra_0 = "";
            }
            textView.append(extra_0 + (i + 1) + ".09.2018: " + String.valueOf(days_temp[i]) + " RUR.\n");
        }
    }

}
