package br.com.samuel.stockly;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class UsersActivity extends Activity {
    private DB db;
    private LinearLayout listContainer;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        db = new DB(this);
        buildLayout();
        loadUsers();
    }

    private void buildLayout() {
        LinearLayout root = Ui.root(this);
        Ui.add(root, Ui.title(this, "Usuários"));
        Ui.add(root, Ui.subtitle(this, "Todos os usuários possuem acesso completo. Esta tela controla apenas status ativo/inativo."));

        ScrollView scrollView = new ScrollView(this);
        listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(listContainer);
        root.addView(scrollView, new LinearLayout.LayoutParams(-1, 0, 1));

        setContentView(root);
    }

    private void loadUsers() {
        listContainer.removeAllViews();

        for (UserAccount user : db.users()) {
            LinearLayout card = Ui.card(this);
            Ui.add(card, Ui.tv(this, user.name, 16, true, Ui.TEXT));
            Ui.add(card, Ui.subtitle(this, "ID: " + user.id + " • " + user.email + " • " + label(user.status)));
            card.setOnClickListener(view -> confirmStatusChange(user));
            Ui.addMargin(listContainer, card, 8);
        }
    }

    private void confirmStatusChange(UserAccount user) {
        String nextAction = user.active() ? "inativar" : "ativar";
        new AlertDialog.Builder(this)
                .setTitle(user.name)
                .setMessage("Deseja " + nextAction + " este usuário?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    db.toggleStatus(user.id);
                    Toast.makeText(this, "Status atualizado.", Toast.LENGTH_SHORT).show();
                    loadUsers();
                })
                .show();
    }

    private String label(String status) {
        return "ACTIVE".equals(status) ? "Ativo" : "Inativo";
    }
}
