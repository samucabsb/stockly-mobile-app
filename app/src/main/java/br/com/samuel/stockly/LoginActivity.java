package br.com.samuel.stockly;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private Button primaryButton;
    private Button toggleButton;
    private TextView title;
    private ProgressBar loading;
    private boolean registerMode;
    private AuthRepository repository;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Session session = new Session(this);
        if (session.logged()) {
            openDashboard();
            return;
        }

        repository = new AuthRepository(new DB(this), session);
        buildLayout();
    }

    private void buildLayout() {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout root = Ui.root(this);
        scrollView.addView(root);

        title = Ui.tv(this, "Stockly", 28, true, Ui.TEXT);
        Ui.add(root, title);
        Ui.add(root, Ui.subtitle(this, "Controle de estoque com acesso completo para todos os usuários."));
        Ui.add(root, Ui.subtitle(this, "Usuário de teste: demo@stockly.com • Senha: 1234"));

        LinearLayout card = Ui.card(this);
        nameInput = Ui.input(this, "Nome completo");
        emailInput = Ui.input(this, "E-mail");
        passwordInput = Ui.input(this, "Senha");
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        primaryButton = Ui.primaryButton(this, "Entrar");
        toggleButton = Ui.outlineButton(this, "Criar nova conta");
        loading = Ui.progress(this);
        nameInput.setVisibility(View.GONE);

        Ui.addMargin(card, nameInput, 4);
        Ui.addMargin(card, emailInput, 8);
        Ui.addMargin(card, passwordInput, 8);
        Ui.addMargin(card, primaryButton, 12);
        Ui.addMargin(card, toggleButton, 8);
        Ui.addMargin(card, loading, 8);
        Ui.addMargin(root, card, 18);

        setContentView(scrollView);

        toggleButton.setOnClickListener(view -> toggleMode());
        primaryButton.setOnClickListener(view -> submit());
    }

    private void toggleMode() {
        registerMode = !registerMode;
        nameInput.setVisibility(registerMode ? View.VISIBLE : View.GONE);
        title.setText(registerMode ? "Criar conta" : "Stockly");
        primaryButton.setText(registerMode ? "Cadastrar" : "Entrar");
        toggleButton.setText(registerMode ? "Já tenho conta" : "Criar nova conta");
    }

    private void submit() {
        try {
            setLoading(true);
            String name = nameInput.getText().toString();
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (!email.contains("@") || !email.contains(".")) {
                showMessage("Informe um e-mail válido.");
                return;
            }

            if (password.length() < 4) {
                showMessage("A senha precisa ter pelo menos 4 caracteres.");
                return;
            }

            if (registerMode) {
                if (name.trim().isEmpty()) {
                    showMessage("Informe seu nome.");
                    return;
                }
                repository.register(name, email, password);
                showMessage("Conta criada com sucesso.");
            } else if (!repository.login(email, password)) {
                showMessage("Credenciais inválidas ou usuário inativo.");
                return;
            }

            openDashboard();
        } catch (Exception exception) {
            showMessage("Erro: " + exception.getMessage());
        } finally {
            setLoading(false);
        }
    }

    private void setLoading(boolean enabled) {
        loading.setVisibility(enabled ? View.VISIBLE : View.GONE);
        primaryButton.setEnabled(!enabled);
        toggleButton.setEnabled(!enabled);
    }

    private void openDashboard() {
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
