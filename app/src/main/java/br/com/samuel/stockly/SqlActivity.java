package br.com.samuel.stockly;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SqlActivity extends Activity {
    private DB db;
    private TextView summaryText;
    private TextView tableText;
    private String currentTable = "products";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        db = new DB(this);
        buildLayout();
        render();
    }

    private void buildLayout() {
        LinearLayout root = Ui.root(this);
        Ui.add(root, Ui.title(this, "SQLite do Stockly"));
        Ui.add(root, Ui.subtitle(this, "Visualização local para apresentação e depuração."));

        summaryText = Ui.subtitle(this, "");
        Ui.add(root, summaryText);

        LinearLayout buttons = Ui.row(this);
        String[] tables = {"products", "users", "stock_movements", "audit_logs"};
        for (String table : tables) {
            Button button = Ui.outlineButton(this, table);
            button.setOnClickListener(view -> {
                currentTable = table;
                render();
            });
            buttons.addView(button, Ui.weight(this));
        }
        Ui.addMargin(root, buttons, 8);

        Button copyButton = Ui.primaryButton(this, "Copiar tabela");
        Ui.addMargin(root, copyButton, 8);

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
        ScrollView scrollView = new ScrollView(this);
        tableText = Ui.tv(this, "", 13, false, Ui.TEXT);
        tableText.setTypeface(android.graphics.Typeface.MONOSPACE);
        tableText.setTextIsSelectable(true);
        scrollView.addView(tableText);
        horizontalScrollView.addView(scrollView);
        root.addView(horizontalScrollView, new LinearLayout.LayoutParams(-1, 0, 1));

        copyButton.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("Stockly SQL", tableText.getText()));
            Toast.makeText(this, "Tabela copiada.", Toast.LENGTH_SHORT).show();
        });

        setContentView(root);
    }

    private void render() {
        summaryText.setText("Banco: " + DB.DB_NAME + " • " + currentTable + " • Registros: " + db.rows(currentTable));
        tableText.setText(db.dump(currentTable));
    }
}
