package com.example.mortgagecalculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText homeValueInput;
    private EditText downPaymentInput;
    private EditText aprInput;
    private EditText termsInput;
    private EditText taxrateInput;
    private Toolbar toolbar;

    private TextView totalTaxPaidOutput;
    private TextView totalInterestPaidOutput;
    private TextView monthlyPaymentOutput;
    private TextView payOffDateOutput;

    private Button calculate;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeValueInput = (EditText) findViewById(R.id.homevalueinput);
        downPaymentInput = (EditText) findViewById(R.id.downpaymentinput);
        aprInput = (EditText) findViewById(R.id.aprinput);
        termsInput = (EditText) findViewById(R.id.termsinput);
        taxrateInput = (EditText) findViewById(R.id.taxrateinput);
        toolbar = (Toolbar) findViewById(R.id.app_bar);

        totalTaxPaidOutput = (TextView) findViewById(R.id.totaltaxpaidoutput);
        totalInterestPaidOutput = (TextView) findViewById(R.id.totalintpaidoutput);
        monthlyPaymentOutput = (TextView) findViewById(R.id.monthlypaymentoutput);
        payOffDateOutput = (TextView) findViewById(R.id.payoffdateoutput);

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        calculate = (Button) findViewById(R.id.calculatebutton);

        //disabling text on textview
        disableEditText(totalTaxPaidOutput);
        disableEditText(totalInterestPaidOutput);
        disableEditText(monthlyPaymentOutput);
        disableEditText(payOffDateOutput);

        //activating the custom toolbar
        setSupportActionBar(toolbar);

        //monetary input
        homeValueInput.addTextChangedListener(new NumberTextWatcher(homeValueInput, "#,###"));
        downPaymentInput.addTextChangedListener(new NumberTextWatcher(downPaymentInput, "#,###"));

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean validInputs = true;
                String toastMessage = " Please fill in the following box:";
                double downPayment = 0;
                double homeValue = 0;
                double rate = 0;
                int terms = 0;
                double tax = 0;

                String homeValueText = homeValueInput.getText().toString().replace("$", "").replace(",", "");
                if (homeValueText.isEmpty()) {
                    toastMessage += "\n- Home Value";
                    validInputs = false;
                } else {
                    homeValue = Double.parseDouble(homeValueText);
                }

                String downPaymentText = downPaymentInput.getText().toString().replace("$", "").replace(",", "");
                if (downPaymentText.isEmpty()) {
                    toastMessage += "\n- Down Payment";
                    validInputs = false;
                } else {
                    downPayment = Double.parseDouble(downPaymentText);
                }

                String rateText = aprInput.getText().toString();
                if (rateText.isEmpty()) {
                    toastMessage += "\n- APR";
                    validInputs = false;
                } else {
                    rate = Double.parseDouble(rateText);
                }

                String termsText = termsInput.getText().toString();
                if (termsText.isEmpty()) {
                    toastMessage += "\n- Terms";
                    validInputs = false;
                } else {
                    terms = Integer.parseInt(termsText);
                }

                String taxText = taxrateInput.getText().toString();
                if (taxText.isEmpty()) {
                    toastMessage += "\n- Tax Rate";
                    validInputs = false;
                } else {
                    tax = Double.parseDouble(taxText);
                }

                if (!validInputs) {
                    showToast(toastMessage);
                    return;
                }

                //monetary format
                //https://stackoverflow.com/questions/20571759/need-to-format-currency-for-textview
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator(',');
                symbols.setDecimalSeparator('.');
                DecimalFormat form = new DecimalFormat("$#,###.00", symbols);

                //calculation of monthly payment
                double adjustedTax = tax / 100;
                double adjustedRate = (rate / 100) / 12;
                int adjustedTerms = 12 * terms;
                double adjustedHomeValue = homeValue - downPayment;
                double annualTaxPropertyPayment = adjustedTax * adjustedHomeValue;

                double numerator = adjustedHomeValue * (adjustedRate * Math.pow((1 + adjustedRate), 180));
                double denominator = Math.pow((1 + adjustedRate), 180) - 1;
                double monthlyPayment = numerator / denominator;

                monthlyPaymentOutput.setText(form.format(monthlyPayment));

                //calculation of total total interest paid
                double totalInterestPaid = (monthlyPayment * adjustedTerms) - adjustedHomeValue;
                totalInterestPaidOutput.setText(form.format(totalInterestPaid));

                //pay off date calculation
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");

                Calendar cal = Calendar.getInstance();
                int currentMonthNumber = cal.get(Calendar.MONTH);
                int currentYearNumber = cal.get(Calendar.YEAR);
                cal.set(Calendar.MONTH, currentMonthNumber + adjustedTerms);
                currentMonthNumber = cal.get(Calendar.MONTH);
                currentYearNumber = cal.get(Calendar.YEAR);
                String[] month = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

                String currentMonthText = month[currentMonthNumber];
                payOffDateOutput.setText(currentMonthText + " " + Integer.toString(currentYearNumber));

                //calculating total tax paid
                double totalTaxPropertyPayment = annualTaxPropertyPayment * terms;
                totalTaxPaidOutput.setText(form.format(totalTaxPropertyPayment));
            }
        });
    }

    //menu inflator
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //implement reset button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.resetbutton) {
            Toast.makeText(this, "Reset has been clicked", Toast.LENGTH_SHORT).show();

            //resetting every input
            homeValueInput.setText("");
            downPaymentInput.setText("");
            aprInput.setText("");
            termsInput.setText("");
            taxrateInput.setText("");

            totalTaxPaidOutput.setText("");
            totalInterestPaidOutput.setText("");
            monthlyPaymentOutput.setText("");
            payOffDateOutput.setText("");
        }

        return super.onOptionsItemSelected(item);
    }

    //layout inflator for custom toast
    //reference https://www.youtube.com/watch?v=sZ1fLi4QZ-g
    public void showToast(String toastMessage) {
        //prevent overlapping toasts
        if (toast != null) {
            toast.cancel();
        }

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_root));

        //custom input for the toast
        TextView toastText = layout.findViewById(R.id.toast_text);
        ImageView toastImage = layout.findViewById(R.id.toast_image);

        toastText.setText(toastMessage);
        toastImage.setImageResource(R.drawable.warning);

        toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(toast.LENGTH_LONG);
        toast.setView(layout);

        toast.show();
    }

    //disabling disable text
    //https://stackoverflow.com/questions/4297763/disabling-of-edittext-in-android
    private void disableEditText(TextView editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
    }
}