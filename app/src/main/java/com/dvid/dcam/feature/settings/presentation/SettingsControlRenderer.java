package com.dvid.dcam.feature.settings.presentation;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import com.dvid.dcam.R;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/** Renders consistent settings controls for the settings presentation surface. */
public final class SettingsControlRenderer {
    private final Context context;

    public SettingsControlRenderer(Context context) {
        this.context = context;
    }

    public void render(
            LinearLayout parent,
            SettingsScreenModel model,
            BiConsumer<SettingId, Integer> onSelection,
            BiConsumer<SettingId, Integer> onNumber,
            BiConsumer<SettingId, Boolean> onBoolean) {
        for (SettingsSection section : model.getSections()) {
            section(parent, section.getTitle());
            for (SettingItem item : section.getItems()) {
                switch (item.getType()) {
                    case TEXT:
                        text(parent, item.getLabel(), item.getValue());
                        break;
                    case CHECKBOX:
                        checkbox(parent, item.getLabel(), item.isChecked(),
                                checked -> onBoolean.accept(item.getId(), checked));
                        break;
                    case CHOICE:
                        choice(parent, item.getLabel(), item.getOptions(), item.getSelectedIndex(),
                                selected -> onSelection.accept(item.getId(), selected));
                        break;
                    case SLIDER:
                        slider(parent, item.getLabel(), item.getMin(), item.getMax(),
                                item.getNumberValue(), item.getUnit(),
                                value -> onNumber.accept(item.getId(), value));
                        break;
                    case RADIO:
                        radio(parent, item.getLabel(), item.getOptions(), item.getSelectedIndex(),
                                selected -> onSelection.accept(item.getId(), selected));
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported setting type " + item.getType());
                }
            }
        }
    }

    public void section(LinearLayout parent, String title) {
        TextView heading = new TextView(context);
        heading.setText(title);
        heading.setTextColor(Color.rgb(142, 200, 255));
        heading.setTextSize(12);
        heading.setAllCaps(true);
        heading.setTypeface(null, android.graphics.Typeface.BOLD);
        heading.setPadding(dp(4), dp(18), dp(4), dp(6));
        parent.addView(heading, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void text(LinearLayout parent, String label, String value) {
        LinearLayout row = baseRow();
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        TextView labelView = label(label);
        TextView valueView = value(value);
        row.addView(labelView, weighted());
        row.addView(valueView, wrap());
        parent.addView(row);
    }

    public void checkbox(
            LinearLayout parent, String label, boolean checked, Consumer<Boolean> onChanged) {
        LinearLayout row = baseRow();
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        TextView labelView = label(label);
        CheckBox checkBox = new CheckBox(context);
        checkBox.setChecked(checked);
        checkBox.setOnCheckedChangeListener((button, isChecked) -> {
            if (onChanged != null) onChanged.accept(isChecked);
        });
        row.addView(labelView, weighted());
        row.addView(checkBox, wrap());
        parent.addView(row);
    }

    public void choice(
            LinearLayout parent,
            String label,
            List<String> options,
            int selectedIndex,
            IntConsumer onSelected) {
        LinearLayout row = baseRow();
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        TextView labelView = label(label);
        int initialSelection = clamp(selectedIndex, 0, options.size() - 1);
        int[] currentSelection = { initialSelection };
        TextView selectedValue = value(choiceText(options.get(initialSelection)));
        selectedValue.setSingleLine(true);
        selectedValue.setEllipsize(TextUtils.TruncateAt.END);
        selectedValue.setMinWidth(dp(48));
        selectedValue.setClickable(true);
        selectedValue.setFocusable(true);
        selectedValue.setOnClickListener(view -> showChoicePopup(
                selectedValue, options, currentSelection[0], position -> {
                    currentSelection[0] = position;
                    selectedValue.setText(choiceText(options.get(position)));
                    if (onSelected != null) onSelected.accept(position);
                }));
        row.addView(labelView, weighted());
        row.addView(selectedValue, wrap());
        parent.addView(row);
    }

    private void showChoicePopup(
            View anchor, List<String> options, int selectedIndex, IntConsumer onSelected) {
        ListView list = new ListView(context);
        list.setDivider(new ColorDrawable(Color.rgb(56, 69, 83)));
        list.setDividerHeight(1);
        list.setBackgroundColor(Color.rgb(27, 34, 43));
        list.setAdapter(new DarkChoiceAdapter(context, options, selectedIndex));

        int width = dp(176);
        PopupWindow popup = new PopupWindow(
                list, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popup.setBackgroundDrawable(new ColorDrawable(Color.rgb(27, 34, 43)));
        popup.setOutsideTouchable(true);
        list.setOnItemClickListener((parent, view, position, id) -> {
            onSelected.accept(position);
            popup.dismiss();
        });
        popup.showAsDropDown(anchor, anchor.getWidth() - width, 0);
    }

    public void slider(
            LinearLayout parent,
            String label,
            int min,
            int max,
            int value,
            String unit,
            IntConsumer onChanged) {
        LinearLayout row = baseRow();
        row.setOrientation(LinearLayout.VERTICAL);
        TextView valueView = value(formatValue(value, unit));
        LinearLayout titleLine = new LinearLayout(context);
        titleLine.setOrientation(LinearLayout.HORIZONTAL);
        titleLine.setGravity(android.view.Gravity.CENTER_VERTICAL);
        titleLine.addView(label(label), weighted());
        titleLine.addView(valueView, wrap());

        SeekBar seekBar = new SeekBar(context);
        seekBar.setMax(max - min);
        seekBar.setProgress(clamp(value, min, max) - min);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                int selected = min + progress;
                valueView.setText(formatValue(selected, unit));
                if (fromUser && onChanged != null) onChanged.accept(selected);
            }

            @Override public void onStartTrackingTouch(SeekBar bar) {}
            @Override public void onStopTrackingTouch(SeekBar bar) {}
        });

        row.addView(titleLine);
        row.addView(seekBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        parent.addView(row);
    }

    public void radio(
            LinearLayout parent,
            String label,
            List<String> options,
            int selectedIndex,
            IntConsumer onSelected) {
        LinearLayout row = baseRow();
        row.setOrientation(LinearLayout.VERTICAL);
        row.addView(label(label));
        RadioGroup group = new RadioGroup(context);
        group.setOrientation(RadioGroup.HORIZONTAL);
        int checkedIndex = clamp(selectedIndex, 0, options.size() - 1);
        for (int i = 0; i < options.size(); i++) {
            RadioButton button = new RadioButton(context);
            button.setText(options.get(i));
            button.setTextColor(Color.WHITE);
            button.setTextSize(14);
            button.setId(View.generateViewId());
            button.setTag(i);
            group.addView(button, wrap());
            if (i == checkedIndex) group.check(button.getId());
        }
        group.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            View checked = radioGroup.findViewById(checkedId);
            if (checked != null && onSelected != null) {
                onSelected.accept((Integer) checked.getTag());
            }
        });
        row.addView(group);
        parent.addView(row);
    }

    private LinearLayout baseRow() {
        LinearLayout row = new LinearLayout(context);
        row.setBackgroundResource(R.drawable.bg_setting_card);
        row.setMinimumHeight(dp(56));
        row.setPadding(dp(14), dp(10), dp(14), dp(10));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dp(8));
        row.setLayoutParams(params);
        return row;
    }

    private TextView label(String text) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextColor(Color.rgb(229, 237, 245));
        view.setTextSize(14);
        view.setGravity(android.view.Gravity.CENTER_VERTICAL);
        return view;
    }

    private TextView value(String text) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextColor(Color.rgb(204, 204, 204));
        view.setTextSize(13);
        view.setGravity(android.view.Gravity.CENTER_VERTICAL | android.view.Gravity.END);
        view.setPadding(dp(12), 0, 0, 0);
        return view;
    }

    private LinearLayout.LayoutParams weighted() {
        return new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
    }

    private LinearLayout.LayoutParams wrap() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private int dp(int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    private static int clamp(int value, int min, int max) {
        if (max < min) return min;
        return Math.max(min, Math.min(max, value));
    }

    private static String formatValue(int value, String unit) {
        return unit == null || unit.isBlank() ? String.valueOf(value) : value + " " + unit;
    }

    private static String choiceText(String value) {
        return value + "  \u25BE";
    }

    private static final class DarkChoiceAdapter extends ArrayAdapter<String> {
        private final int selectedIndex;

        private DarkChoiceAdapter(Context context, List<String> values, int selectedIndex) {
            super(context, android.R.layout.simple_list_item_1, values);
            this.selectedIndex = selectedIndex;
        }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTextColor(position == selectedIndex ? Color.rgb(142, 200, 255) : Color.WHITE);
            view.setTextSize(14);
            view.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            view.setBackgroundColor(Color.rgb(27, 34, 43));
            view.setPadding(16, 14, 16, 14);
            return view;
        }
    }
}
