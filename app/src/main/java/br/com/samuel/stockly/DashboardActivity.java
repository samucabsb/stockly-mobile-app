package br.com.samuel.stockly;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends Activity {
    private Session session;
    private ProductRepository repository;
    private LinearLayout listContainer;
    private TextView statsText;
    private EditText searchInput;
    private List<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        session = new Session(this);
        if (!session.logged()) {
            openLogin();
            return;
        }

        repository = new ProductRepository(new DB(this));
        buildLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    private void buildLayout() {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout root = Ui.root(this);
        scrollView.addView(root);

        Ui.add(root, Ui.tv(this, "Olá, " + session.name() + " 👋", 24, true, Ui.TEXT));
        Ui.add(root, Ui.subtitle(this, "Acesso completo • Stockly Enterprise"));

        statsText = Ui.tv(this, "Carregando resumo...", 15, true, Ui.GREEN);
        Ui.addMargin(root, statsText, 8);

        LinearLayout actionsPrimary = Ui.row(this);
        Button newButton = Ui.primaryButton(this, "Novo produto");
        Button movementsButton = Ui.outlineButton(this, "Movimentos");
        actionsPrimary.addView(newButton, Ui.weight(this));
        actionsPrimary.addView(movementsButton, Ui.weight(this));
        Ui.addMargin(root, actionsPrimary, 10);

        LinearLayout actionsSecondary = Ui.row(this);
        Button sqlButton = Ui.outlineButton(this, "SQLite");
        Button usersButton = Ui.outlineButton(this, "Usuários");
        Button auditButton = Ui.outlineButton(this, "Auditoria");
        actionsSecondary.addView(sqlButton, Ui.weight(this));
        actionsSecondary.addView(usersButton, Ui.weight(this));
        actionsSecondary.addView(auditButton, Ui.weight(this));
        Ui.addMargin(root, actionsSecondary, 8);

        Button logoutButton = Ui.dangerButton(this, "Sair");
        Ui.addMargin(root, logoutButton, 8);

        searchInput = Ui.input(this, "Buscar produto ou categoria");
        Ui.addMargin(root, searchInput, 12);

        listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        Ui.addMargin(root, listContainer, 12);
        setContentView(scrollView);

        newButton.setOnClickListener(view -> startActivity(new Intent(this, ProductFormActivity.class)));
        movementsButton.setOnClickListener(view -> startActivity(LogActivity.intent(this, "stock_movements")));
        sqlButton.setOnClickListener(view -> startActivity(new Intent(this, SqlActivity.class)));
        usersButton.setOnClickListener(view -> startActivity(new Intent(this, UsersActivity.class)));
        auditButton.setOnClickListener(view -> startActivity(LogActivity.intent(this, "audit_logs")));
        logoutButton.setOnClickListener(view -> confirmLogout());

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { renderProducts(); }
            @Override public void afterTextChanged(Editable editable) {}
        });
    }

    private void loadProducts() {
        products = repository.list();
        renderProducts();
    }

    private void renderProducts() {
        listContainer.removeAllViews();
        String query = searchInput == null ? "" : searchInput.getText().toString().toLowerCase();
        int count = 0;
        int units = 0;
        int critical = 0;
        double total = 0;

        for (Product product : products) {
            count++;
            units += product.quantity;
            total += product.price * product.quantity;
            if (product.critical()) {
                critical++;
            }
        }

        statsText.setText("Produtos: " + count + " • Unidades: " + units + " • Críticos: " + critical + " • Total: " + money(total));

        for (Product product : products) {
            boolean matches = product.name.toLowerCase().contains(query) || product.category.toLowerCase().contains(query);
            if (query.isEmpty() || matches) {
                listContainer.addView(productCard(product));
            }
        }

        if (listContainer.getChildCount() == 0) {
            Ui.add(listContainer, Ui.tv(this, "📦 Nenhum produto encontrado", 18, false, Ui.MUTED));
        }
    }

    private View productCard(Product product) {
        LinearLayout card = Ui.card(this);
        Ui.add(card, Ui.tv(this, product.name + " • " + product.status(), 17, true, product.critical() ? Ui.DANGER : Ui.TEXT));
        Ui.add(card, Ui.subtitle(this, product.category + " • Qtd: " + product.quantity + " • Min: " + product.minimumQuantity + " • " + money(product.price)));

        LinearLayout row = Ui.row(this);
        Button editButton = Ui.outlineButton(this, "Editar");
        Button deleteButton = Ui.dangerButton(this, "Excluir");
        row.addView(editButton, Ui.weight(this));
        row.addView(deleteButton, Ui.weight(this));
        Ui.addMargin(card, row, 8);

        editButton.setOnClickListener(view -> startActivity(ProductFormActivity.intent(this, product.id)));
        deleteButton.setOnClickListener(view -> confirmDelete(product));
        return card;
    }

    private void confirmDelete(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir produto")
                .setMessage("Deseja excluir " + product.name + "?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Excluir", (dialog, which) -> {
                    repository.delete(product, session.id());
                    Toast.makeText(this, "Produto excluído.", Toast.LENGTH_SHORT).show();
                    loadProducts();
                })
                .show();
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Sair")
                .setMessage("Deseja encerrar a sessão?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Sair", (dialog, which) -> {
                    session.clear();
                    openLogin();
                })
                .show();
    }

    private String money(double value) {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(value);
    }

    private void openLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
