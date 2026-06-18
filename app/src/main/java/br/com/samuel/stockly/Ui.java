package br.com.samuel.stockly;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public final class Ui {
    public static final int BLUE = Color.rgb(37, 99, 235);
    public static final int GREEN = Color.rgb(16, 185, 129);
    public static final int BG = Color.rgb(248, 250, 252);
    public static final int TEXT = Color.rgb(15, 23, 42);
    public static final int MUTED = Color.rgb(100, 116, 139);
    public static final int DANGER = Color.rgb(220, 38, 38);
    public static final int WHITE = Color.WHITE;
    public static final int BORDER = Color.rgb(226, 232, 240);

    private Ui() {}

    public static int dp(android.content.Context context, int value) {
        return (int) (value * context.getResources().getDisplayMetrics().density);
    }

    public static LinearLayout root(Activity activity) {
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(activity, 18), dp(activity, 18), dp(activity, 18), dp(activity, 18));
        root.setBackgroundColor(BG);
        return root;
    }

    public static TextView title(Activity activity, String text) {
        return tv(activity, text, 24, true, TEXT);
    }

    public static TextView subtitle(Activity activity, String text) {
        return tv(activity, text, 14, false, MUTED);
    }

    public static TextView tv(Activity activity, String text, int sp, boolean bold, int color) {
        TextView view = new TextView(activity);
        view.setText(text == null ? "" : text);
        view.setTextSize(sp);
        view.setTextColor(color);
        view.setLineSpacing(dp(activity, 2), 1.0f);
        if (bold) {
            view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }
        view.setPadding(0, dp(activity, 4), 0, dp(activity, 4));
        return view;
    }

    public static Button primaryButton(Activity activity, String text) {
        return button(activity, text, BLUE, WHITE);
    }

    public static Button outlineButton(Activity activity, String text) {
        return button(activity, text, WHITE, BLUE);
    }

    public static Button dangerButton(Activity activity, String text) {
        return button(activity, text, DANGER, WHITE);
    }

    public static Button button(Activity activity, String text, int background, int foreground) {
        Button button = new Button(activity);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextColor(foreground);
        button.setMinHeight(dp(activity, 48));
        button.setPadding(dp(activity, 12), 0, dp(activity, 12), 0);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(background);
        drawable.setCornerRadius(dp(activity, 14));
        drawable.setStroke(dp(activity, 1), background == WHITE ? BORDER : background);
        button.setBackground(drawable);
        return button;
    }

    public static EditText input(Activity activity, String hint) {
        EditText input = new EditText(activity);
        input.setHint(hint);
        input.setSingleLine(true);
        input.setTextColor(TEXT);
        input.setHintTextColor(MUTED);
        input.setPadding(dp(activity, 12), dp(activity, 8), dp(activity, 12), dp(activity, 8));

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(WHITE);
        drawable.setCornerRadius(dp(activity, 14));
        drawable.setStroke(dp(activity, 1), BORDER);
        input.setBackground(drawable);
        return input;
    }

    public static LinearLayout card(Activity activity) {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(activity, 14), dp(activity, 14), dp(activity, 14), dp(activity, 14));

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(WHITE);
        drawable.setCornerRadius(dp(activity, 18));
        drawable.setStroke(dp(activity, 1), BORDER);
        card.setBackground(drawable);
        return card;
    }

    public static LinearLayout row(android.content.Context context) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        return row;
    }

    public static ProgressBar progress(Activity activity) {
        ProgressBar progressBar = new ProgressBar(activity);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);
        return progressBar;
    }

    public static void add(ViewGroup parent, View view) {
        parent.addView(view, new LinearLayout.LayoutParams(-1, -2));
    }

    public static void addMargin(ViewGroup parent, View view, int topDp) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dp(view.getContext(), topDp);
        parent.addView(view, params);
    }

    public static LinearLayout.LayoutParams weight(android.content.Context context) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1);
        params.setMargins(dp(context, 4), 0, dp(context, 4), 0);
        return params;
    }
}
