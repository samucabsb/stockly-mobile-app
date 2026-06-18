package br.com.samuel.stockly;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class ProductFormActivity extends Activity {
    private static final String EXTRA_ID = "id";

    private long id = -1;
    private EditText nameInput;
    private EditText priceInput;
    private EditText quantityInput;
    private EditText minimumInput;
    private EditText categoryInput;
    private ProductRepository repository;
    private Session session;

    public static Intent intent(Context context, long id) {
        return new Intent(context, ProductFormActivity.class).putExtra(EXTRA_ID, id);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        session = new Session(this);
        repository = new ProductRepository(new DB(this));
        id = getIntent().getLongExtra(EXTRA_ID, -1);
        buildLayout();

        if (id > 0) {
            loadProduct();
        }
    }

    private void buildLayout() {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout root = Ui.root(this);
        scrollView.addView(root);

        Ui.add(root, Ui.title(this, id > 0 ? "Editar produto" : "Novo produto"));
        Ui.add(root, Ui.subtitle(this, "Preencha os dados do produto e salve as alterações."));

        nameInput = Ui.input(this, "Nome");
        priceInput = Ui.input(this, "Preço");
        quantityInput = Ui.input(this, "Quantidade");
        minimumInput = Ui.input(this, "Estoque mínimo");
        categoryInput = Ui.input(this, "Categoria");

        priceInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        quantityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        minimumInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        Button saveButton = Ui.primaryButton(this, "Salvar");
        Button backButton = Ui.outlineButton(this, "Voltar");

        Ui.addMargin(root, nameInput, 10);
        Ui.addMargin(root, priceInput, 8);
        Ui.addMargin(root, quantityInput, 8);
        Ui.addMargin(root, minimumInput, 8);
        Ui.addMargin(root, categoryInput, 8);
        Ui.addMargin(root, saveButton, 14);
        Ui.addMargin(root, backButton, 8);

        setContentView(scrollView);

        saveButton.setOnClickListener(view -> save());
        backButton.setOnClickListener(view -> finish());
    }

    private void loadProduct() {
        Product product = repository.find(id);
        if (product == null) {
            showMessage("Produto não encontrado.");
            finish();
            return;
        }

        nameInput.setText(product.name);
        priceInput.setText(String.valueOf(product.price));
        quantityInput.setText(String.valueOf(product.quantity));
        minimumInput.setText(String.valueOf(product.minimumQuantity));
        categoryInput.setText(product.category);
    }

    private void save() {
        try {
            String name = nameInput.getText().toString();
            String category = categoryInput.getText().toString();
            double price = Double.parseDouble(priceInput.getText().toString().replace(',', '.'));
            int quantity = Integer.parseInt(quantityInput.getText().toString());
            int minimum = minimumInput.getText().toString().trim().isEmpty()
                    ? 1
                    : Integer.parseInt(minimumInput.getText().toString());

            if (name.trim().isEmpty() || price < 0 || quantity < 0 || minimum < 0) {
                showMessage("Verifique os campos informados.");
                return;
            }

            repository.save(id, session.id(), name, price, quantity, minimum, category);
            showMessage("Produto salvo com sucesso.");
            finish();
        } catch (Exception exception) {
            showMessage("Valores inválidos. Revise os campos.");
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
