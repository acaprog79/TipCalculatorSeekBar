package com.alejandria.tipcalculatorseekbar1;

import android.content.Context;
        import android.content.SharedPreferences;
        import android.icu.text.NumberFormat;
        import android.os.Build;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.KeyEvent;
        import android.view.View;
        import android.view.inputmethod.EditorInfo;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.EditText;
        import android.widget.SeekBar;
        import android.widget.TextView;

        import org.w3c.dom.Text;

        import static android.R.attr.progress;
        import static android.icu.text.NumberFormat.getCurrencyInstance;
        import static android.icu.text.NumberFormat.getPercentInstance;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnKeyListener, SeekBar.OnSeekBarChangeListener {

    private EditText billAmountEditText;
    private TextView percentTextView;
    private SeekBar percentSeekBar;
    private TextView tipTextView;
    private TextView totalTextView;

    private SharedPreferences savedValues;

    private String billAmountString="";
    private float tipPercent = 15f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        billAmountEditText = (EditText)findViewById(R.id.billAmountEditText);
        percentTextView = (TextView)findViewById(R.id.percentTextView);
        percentSeekBar = (SeekBar)findViewById(R.id.percentSeekBar);
        tipTextView = (TextView)findViewById(R.id.tipTextView);
        totalTextView = (TextView)findViewById(R.id.totalTextView);


        billAmountEditText.setOnEditorActionListener(this);
        billAmountEditText.setOnKeyListener(this);
        percentSeekBar.setOnSeekBarChangeListener(this);
        percentSeekBar.setOnKeyListener(this);

        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
    }

    @Override
    public void onPause(){

        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.commit();

        super.onPause();

    }

    @Override
    public void onResume(){
        super.onResume();

        billAmountString = savedValues.getString("billAmountString", "");
        tipPercent = savedValues.getFloat("tipPercent",0.15f);

        billAmountEditText.setText(billAmountString);

        int progress = Math.round(tipPercent * 100);
        percentSeekBar.setProgress(progress);
    }

    public void calculateAndDisplay(){

        billAmountString = billAmountEditText.getText().toString();
        float billAmount;
        if (billAmountString.equals("")){
            billAmount = 0;
        }
        else {
            billAmount = Float.parseFloat(billAmountString);
        }

        int progress = percentSeekBar.getProgress();
        tipPercent = (float) progress / 100;

        float tipAmount = 0;
        float totalAmount = 0;

        tipAmount = billAmount * tipPercent;
        totalAmount = billAmount + tipAmount;

        NumberFormat currency = getCurrencyInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tipTextView.setText(currency.format(tipAmount));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            totalTextView.setText(currency.format(totalAmount));
        }

        NumberFormat percent = getPercentInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            percentTextView.setText(percent.format(tipPercent));
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
            calculateAndDisplay();
        }
        return false;
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                calculateAndDisplay();

                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(
                        billAmountEditText.getWindowToken(), 0 );

                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (view.getId() == R.id.percentSeekBar) {
                    calculateAndDisplay();
                }
                break;

        }
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        percentTextView.setText( progress + "%");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        calculateAndDisplay();
    }
}
