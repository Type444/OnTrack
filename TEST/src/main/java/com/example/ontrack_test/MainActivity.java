package com.example.ontrack_test;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
//android.support.v7.widget.Toolbar
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.example.ontrack_test.AddExpenseActivity.DAYS_ARRAY;

public class MainActivity extends AppCompatActivity {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_ALL = "AllIncome";
    public static final String APP_PREFERENCES_Necessary = "Necessary";
    public static final String APP_PREFERENCES_Percent = "Percent";
    public static final String FIRST_LAUNCH = "firstLaunch";

    int Nec_Add; /* probably this is bad, but necessary since it is used in both onCreate and
    GetNecessary methods*/


    SharedPreferences mSettings; // maybe is ok, since it's the only SP file in whole app


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(myToolbar);

        SeekBar seekBar;
        final TextView SavePercent;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean(FIRST_LAUNCH, false)) {
            // <---- run your one time code here
            CreateSPArray();
            // mark first time has ran.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(FIRST_LAUNCH, true);
            editor.commit();

            ShowDialog();
        }


        Nec_Add = getIntent().getIntExtra("InputNec", 0);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        SavePercent = (TextView) findViewById(R.id.savings_percent);
        seekBar.setProgress(GetPercent());
        SavePercent.setText(String.valueOf(GetPercent()) + "%");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SavePercent.setText(String.valueOf(progress) + "%");
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putInt(APP_PREFERENCES_Percent, progress);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Put_Values();
        Nec_Add = 0;
        //HelloThere("lol", 5);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_help: {
                ShowDialog();
                break;
            }
            case R.id.settings_main:{
                Intent settings_intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settings_intent);
            }
            // case blocks for other MenuItems (if any)
        }
        return false;
    }

    public void AddExpense(View view) {
        Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
        startActivity(intent);
    } //ok.

    public void Put_Values() {
        /*
        Calculates all other values, that don't have methods for them,
        and sends it in ApplyText method as int values
         */
        int All_Income_put = GetAll();
        int Necessary = GetNecessary();
        int Savings = GetSavings(All_Income_put);
        //int Leftover = All_Income_put - (Savings + Necessary);
        ApplyText(Savings, All_Income_put, GetDailyAvg(), GetDailyLeft(), Necessary);
    }

    public void Apply(View view) {

        /*
        Activates when button is pressed, maybe is redundant
         */
        Put_Values();

        Toast toast = Toast.makeText(getApplicationContext(),
                "Changes Applied!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public int GetAll() {
        EditText All_txt = (EditText) findViewById(R.id.AllIncomeEdit);
        String All_str_txt = All_txt.getText().toString();

        int All_Income_temp;

        if (All_str_txt.matches("")) {
            if (mSettings.contains(APP_PREFERENCES_ALL)) {
                All_Income_temp = mSettings.getInt(APP_PREFERENCES_ALL, 15);
            } else {
                All_Income_temp = 0;
            }
        } else {
            All_Income_temp = Integer.parseInt(All_str_txt);
        }

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_ALL, All_Income_temp);
        editor.apply();

        return All_Income_temp;
    }

    public void ApplyText(int Savings, int AllIncome, int Daily_avg, int Daily_Left, int Necessary_appl) {

        /*
        Gets int values and applies them on TextViews
        TextViews differ only by number after them, too lazy to fix it, tho :(
         */

        TextView textView = (TextView) findViewById(R.id.amount_all);
        String All_Income_str = String.valueOf(AllIncome);
        textView.setText(All_Income_str + "RUR");


        TextView textView1 = (TextView) findViewById(R.id.amount_savings);
        String Savings_str = String.valueOf(Savings);
        textView1.setText(Savings_str + "RUR");


        TextView textView2 = (TextView) findViewById(R.id.amount_necessary);
        String Necessary_str = String.valueOf(Necessary_appl);
        textView2.setText(Necessary_str + "RUR");


      /*  TextView textView3 = (TextView) findViewById(R.id.amount_Leftover);
        String Leftover_str = String.valueOf(Leftover);
        textView3.setText(Leftover_str + "RUR"); */


        TextView textView4 = (TextView) findViewById(R.id.amount_dailyAvg);
        String Daily_avg_str = String.valueOf(Daily_avg);
        textView4.setText(Daily_avg_str + "RUR");


        TextView textView5 = (TextView) findViewById(R.id.amount_dailyLeft);
        String Daily_Left_str = String.valueOf(Daily_Left);
        textView5.setText(Daily_Left_str + "RUR");


    }

    public int getDays() {
        /*
        Gets amount of days in a month, similar to method in AddExpenseActivity
        Maybe is redundant
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
        Get current day
         */
        Calendar calendar1 = Calendar.getInstance();
        return calendar1.get(Calendar.DATE);
    }

    public int GetDayValue(int day) {
        /*
        Method is similar to one in AddExpenseActivity
        Loads String from SP, transforms it into array and returns only 1 value of requested day
         */
        SharedPreferences mSettings;
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String temp_days_str = mSettings.getString(DAYS_ARRAY, "0");
        String[] integerStrings = temp_days_str.split(", ");
        day--;
        return Integer.parseInt(integerStrings[day]);
    }

    public int GetDailyLeft() {
        /*
        Calculates amount of money left for current day
        Accounts for unused money in previous days
         */
        int ExpSum = 0;
        for (int r = 1; r < currentDay() + 1; r++) {
            ExpSum = GetDayValue(r) + ExpSum;
        }

        return ((GetDailyAvg() * currentDay()) - ExpSum);
    }

    public int GetDailyAvg() {
        /*
        Calculates Daily avg sum of expenses to be used
        */

        int All_Income_DAVG = GetAll();
        return (All_Income_DAVG - (GetSavings(All_Income_DAVG) + GetNecessary())) / getDays();
    }

    public void HelloThere(String name, int kek) {
        /*
        Redundant to program, can be used to display value of variable in toast
         */
        Toast toast1 = Toast.makeText(getApplicationContext(),
                "Value of " + name + " is " + kek, Toast.LENGTH_SHORT);
        toast1.show();
    }

    public int GetNecessary() {

        /*
        Calculates Necessary Expenses, cost of which is spread across all days
        Checks the EditText box, adds global variable Nec_Add, which is defined in OnCreate method
        from activity AddExpenseActivity
        Saves final value in SP and sets Nec_Add to zero
        Probably Nec_Add should be managed otherwise
         */
        EditText Nec_txt_edit = (EditText) findViewById(R.id.NecessaryEdit);
        String Nec_str_txt = Nec_txt_edit.getText().toString();

        int Nec_temp;

        if (Nec_str_txt.matches("")) {
            if (mSettings.contains(APP_PREFERENCES_Necessary)) {
                Nec_temp = mSettings.getInt(APP_PREFERENCES_Necessary, 15);
            } else {
                Nec_temp = 0;
            }
        } else {
            Nec_temp = Integer.parseInt(Nec_str_txt);
        }

        Nec_temp += Nec_Add;

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_Necessary, Nec_temp);
        editor.apply();

        Nec_Add = 0;
        return Nec_temp;
    }

    public int GetPercent() {

        /*
        Loads amount of percents 0-100 stored in SP
        SP containing percents is ONLY changed when seekBar is moved
         */
        int percent_temp;
        if (mSettings.contains(APP_PREFERENCES_Percent)) {
            percent_temp = mSettings.getInt(APP_PREFERENCES_Percent, 0);
        } else {
            percent_temp = 0;
        }
        return percent_temp;
    }

    public int GetSavings(int All_inc_savings) {
        /*
        Applies percent value to AllIncome to calculate amount of savings in current month
         */
        return Math.round(All_inc_savings * GetPercent() / 100);
    }

    public void CreateSPArray() {

        /* преобразование int array в String для хранения, не работает без обрезки по краям */
        int[] days_temp = new int[31];

        SharedPreferences SaveSettings;
        SaveSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        String days_str = Arrays.toString(days_temp);
        days_str = days_str.substring(1, days_str.length() - 1);

        SharedPreferences.Editor editor_arr = SaveSettings.edit();
        editor_arr.putString(DAYS_ARRAY, days_str);
        editor_arr.apply();
    }

    public void ShowDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_window);
        dialog.setTitle("Dialog box");

        Button button = (Button) dialog.findViewById(R.id.Button01);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}