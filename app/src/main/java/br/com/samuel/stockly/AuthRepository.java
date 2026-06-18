package br.com.samuel.stockly;

public class AuthRepository {
    private final DB db;
    private final Session session;

    public AuthRepository(DB db, Session session) {
        this.db = db;
        this.session = session;
    }

    public void register(String name, String email, String password) throws Exception {
        long id = db.register(name, email, password);
        session.save(id, name.trim());
    }

    public boolean login(String email, String password) throws Exception {
        DB.Login user = db.login(email, password);
        if (user == null) {
            return false;
        }

        session.save(user.id, user.name);
        return true;
    }
}
