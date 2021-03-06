package com.ysered.customspinnerexample.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ysered.customspinnerexample.R;

import java.util.ArrayList;
import java.util.List;

public class CompoundSpinner extends FrameLayout {
    private static final String TAG = CompoundSpinner.class.getSimpleName();

    private static final int DRAWABLE_RIGHT = 2;

    private boolean isSelected = false;
    private TextView placeholderText;
    private Spinner spinner;
    private EditText customText;

    public CompoundSpinner(Context context) {
        super(context);
        init();
    }

    public CompoundSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        applyAttributes(attrs);
    }

    public CompoundSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        applyAttributes(attrs);
    }

    public String getSelectedItemText() {
        String text = null;
        final boolean isPlaceholderShown = placeholderText.getVisibility() == VISIBLE;
        if (!isPlaceholderShown && customText.getVisibility() == VISIBLE) {
            text = customText.getText().toString();
        } else if (!isPlaceholderShown && spinner.getVisibility() == VISIBLE) {
            text = spinner.getSelectedItem().toString();
        }
        return text;
    }

    private void init() {
        final View view = inflate(getContext(), R.layout.view_compound_spinner, this);
        placeholderText = (TextView) view.findViewById(R.id.placeholderText);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        customText = (EditText) view.findViewById(R.id.customText);

        // hide placeholder text and show spinner items
        placeholderText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                placeholderText.setVisibility(INVISIBLE);
                spinner.setVisibility(VISIBLE);
                spinner.performClick();
            }
        });
        // show EditText if last item selected or regular item otherwise
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 && !isSelected) {
                    placeholderText.setVisibility(VISIBLE);
                    spinner.setVisibility(INVISIBLE);
                } else {
                    isSelected = true;
                    customText.setVisibility(INVISIBLE);
                    if (position == parent.getCount() - 1) {
                        customText.setVisibility(VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // show spinner items when on right image of EditText clicked
        customText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (getRight() - customText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Log.d(TAG, "onTouch: touched on drawable");
                        spinner.performClick();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void applyAttributes(AttributeSet attrs) {
        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CompoundSpinner);
        final CharSequence[] textArray = typedArray.getTextArray(R.styleable.CompoundSpinner_android_entries);
        List<String> items = new ArrayList<>();
        if (textArray != null) {
            for (CharSequence textItem : textArray) {
                items.add(textItem.toString());
            }
        } else {
            throw new IllegalArgumentException("Spinner items not specified: declare 'android:entries' attribute");
        }
        spinner.setAdapter(new SpinnerAdapter(getContext(), items));
        final String placeholder = typedArray.getString(R.styleable.CompoundSpinner_placeholder_text);
        if (placeholder != null) {
            placeholderText.setText(placeholder);
        }
        typedArray.recycle();
    }

    private final class SpinnerAdapter extends ArrayAdapter<String> {

        public SpinnerAdapter(Context context, List<String> items) {
            super(context, R.layout.spinner_item, items);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
    }

}
