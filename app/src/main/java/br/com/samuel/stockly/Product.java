package br.com.samuel.stockly;

public class Product {
    public final long id;
    public final long userId;
    public final String name;
    public final String category;
    public final double price;
    public final int quantity;
    public final int minimumQuantity;

    public Product(long id, long userId, String name, double price, int quantity, int minimumQuantity, String category) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.minimumQuantity = minimumQuantity;
        this.category = category;
    }

    public boolean critical() {
        return quantity <= minimumQuantity;
    }

    public String status() {
        if (quantity <= 0) {
            return "SEM ESTOQUE";
        }
        return critical() ? "CRÍTICO" : "OK";
    }
}
