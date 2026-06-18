package br.com.samuel.stockly;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.List;

public class LogActivity extends Activity {
    private static final String EXTRA_TABLE = "table";

    public static Intent intent(Context context, String table) {
        return new Intent(context, LogActivity.class).putExtra(EXTRA_TABLE, table);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        String table = getIntent().getStringExtra(EXTRA_TABLE);
        if (table == null) {
            table = "stock_movements";
        }

        DB db = new DB(this);
        List<Entry> entries = db.entries(table);
        buildLayout(table, entries);
    }

    private void buildLayout(String table, List<Entry> entries) {
        LinearLayout root = Ui.root(this);
        String title = "audit_logs".equals(table) ? "Auditoria" : "Movimentações";
        Ui.add(root, Ui.title(this, title));
        Ui.add(root, Ui.subtitle(this, entries.size() + " registros encontrados"));

        ScrollView scrollView = new ScrollView(this);
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(list);

        if (entries.isEmpty()) {
            Ui.addMargin(list, Ui.subtitle(this, "Nenhum registro encontrado."), 12);
        } else {
            for (Entry entry : entries) {
                LinearLayout card = Ui.card(this);
                Ui.add(card, Ui.tv(this, entry.title, 16, true, Ui.TEXT));
                Ui.add(card, Ui.subtitle(this, entry.subtitle));
                Ui.addMargin(list, card, 8);
            }
        }

        root.addView(scrollView, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);
    }
}
