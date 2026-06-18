package br.com.samuel.stockly;

import java.util.List;

public class ProductRepository {
    private final DB db;

    public ProductRepository(DB db) {
        this.db = db;
    }

    public List<Product> list() {
        return db.products();
    }

    public Product find(long id) {
        return db.findProduct(id);
    }

    public void save(long id, long userId, String name, double price, int quantity, int minimumQuantity, String category) {
        if (id > 0) {
            db.updateProduct(id, userId, name, price, quantity, minimumQuantity, category);
        } else {
            db.insertProduct(userId, name, price, quantity, minimumQuantity, category);
        }
    }

    public void delete(Product product, long userId) {
        db.deleteProduct(product.id, userId);
    }
}
