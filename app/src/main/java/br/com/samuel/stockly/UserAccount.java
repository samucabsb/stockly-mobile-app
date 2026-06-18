package br.com.samuel.stockly;

public class UserAccount {
    public final long id;
    public final String name;
    public final String email;
    public final String status;

    public UserAccount(long id, String name, String email, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
    }

    public boolean active() {
        return "ACTIVE".equals(status);
    }
}
