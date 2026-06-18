package br.com.samuel.stockly;

    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;

    import java.security.MessageDigest;
    import java.security.SecureRandom;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.List;

    public class DB extends SQLiteOpenHelper {
        public static final String DB_NAME = "stockly_sem_perfis.db";
        private static final int DB_VERSION = 2;

        public DB(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "email TEXT NOT NULL UNIQUE," +
                    "password_hash TEXT NOT NULL," +
                    "salt TEXT NOT NULL," +
                    "status TEXT NOT NULL DEFAULT 'ACTIVE'," +
                    "created_at INTEGER NOT NULL DEFAULT (strftime('%s','now'))," +
                    "last_login INTEGER" +
                    ")");

            db.execSQL("CREATE TABLE products (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "name TEXT NOT NULL," +
                    "price REAL NOT NULL DEFAULT 0," +
                    "quantity INTEGER NOT NULL DEFAULT 0," +
                    "minimum_quantity INTEGER NOT NULL DEFAULT 1," +
                    "category TEXT NOT NULL DEFAULT 'Geral'," +
                    "created_at INTEGER NOT NULL DEFAULT (strftime('%s','now'))," +
                    "updated_at INTEGER NOT NULL DEFAULT (strftime('%s','now'))" +
                    ")");

            db.execSQL("CREATE TABLE stock_movements (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "product_id INTEGER," +
                    "user_id INTEGER," +
                    "type TEXT NOT NULL," +
                    "quantity INTEGER NOT NULL," +
                    "note TEXT," +
                    "created_at INTEGER NOT NULL DEFAULT (strftime('%s','now'))" +
                    ")");

            db.execSQL("CREATE TABLE audit_logs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "action TEXT NOT NULL," +
                    "entity TEXT NOT NULL," +
                    "details TEXT," +
                    "created_at INTEGER NOT NULL DEFAULT (strftime('%s','now'))" +
                    ")");

            seed(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS audit_logs");
            db.execSQL("DROP TABLE IF EXISTS stock_movements");
            db.execSQL("DROP TABLE IF EXISTS products");
            db.execSQL("DROP TABLE IF EXISTS users");
            onCreate(db);
        }

        private void seed(SQLiteDatabase db) {
            try {
                long userId = createUser(db, "Usuário Demo", "demo@stockly.com", "1234");
                insertProduct(db, userId, "Notebook Dell", 3500.00, 8, 2, "Eletrônicos");
                insertProduct(db, userId, "Mouse Sem Fio", 89.90, 3, 5, "Acessórios");
                insertProduct(db, userId, "Cadeira Ergonômica", 799.00, 10, 2, "Escritório");
                log(db, userId, "SEED", "database", "Dados iniciais criados para demonstração");
            } catch (Exception ignored) {
                // Seed é auxiliar. A aplicação continua funcionando mesmo se algum dado de teste falhar.
            }
        }

        public long register(String name, String email, String password) throws Exception {
            long id = createUser(getWritableDatabase(), name, email, password);
            log(id, "CREATE_USER", "users", "Usuário cadastrado: " + normalizeEmail(email));
            return id;
        }

        private long createUser(SQLiteDatabase db, String name, String email, String password) throws Exception {
            String salt = salt();
            ContentValues values = new ContentValues();
            values.put("name", name.trim());
            values.put("email", normalizeEmail(email));
            values.put("password_hash", sha(password + salt));
            values.put("salt", salt);
            values.put("status", "ACTIVE");
            return db.insertOrThrow("users", null, values);
        }

        public Login login(String email, String password) throws Exception {
            Cursor cursor = getReadableDatabase().query(
                    "users",
                    null,
                    "email = ? AND status = 'ACTIVE'",
                    new String[]{normalizeEmail(email)},
                    null,
                    null,
                    null
            );

            try {
                if (!cursor.moveToFirst()) {
                    return null;
                }

                String salt = cursor.getString(cursor.getColumnIndexOrThrow("salt"));
                String expectedHash = cursor.getString(cursor.getColumnIndexOrThrow("password_hash"));
                if (!sha(password + salt).equals(expectedHash)) {
                    return null;
                }

                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));

                ContentValues values = new ContentValues();
                values.put("last_login", System.currentTimeMillis() / 1000);
                getWritableDatabase().update("users", values, "id = ?", new String[]{String.valueOf(id)});
                log(id, "LOGIN", "users", "Login realizado");

                return new Login(id, name);
            } finally {
                cursor.close();
            }
        }

        public List<UserAccount> users() {
            ArrayList<UserAccount> users = new ArrayList<>();
            Cursor cursor = getReadableDatabase().query("users", null, null, null, null, null, "name");

            try {
                while (cursor.moveToNext()) {
                    users.add(new UserAccount(
                            cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("email")),
                            cursor.getString(cursor.getColumnIndexOrThrow("status"))
                    ));
                }
            } finally {
                cursor.close();
            }

            return users;
        }

        public void toggleStatus(long id) {
            Cursor cursor = getReadableDatabase().query(
                    "users",
                    new String[]{"status"},
                    "id = ?",
                    new String[]{String.valueOf(id)},
                    null,
                    null,
                    null
            );

            try {
                if (cursor.moveToFirst()) {
                    String newStatus = "ACTIVE".equals(cursor.getString(0)) ? "INACTIVE" : "ACTIVE";
                    ContentValues values = new ContentValues();
                    values.put("status", newStatus);
                    getWritableDatabase().update("users", values, "id = ?", new String[]{String.valueOf(id)});
                    log(id, "CHANGE_STATUS", "users", "Novo status: " + newStatus);
                }
            } finally {
                cursor.close();
            }
        }

        public long insertProduct(long userId, String name, double price, int quantity, int minimumQuantity, String category) {
            return insertProduct(getWritableDatabase(), userId, name, price, quantity, minimumQuantity, category);
        }

        private long insertProduct(SQLiteDatabase db, long userId, String name, double price, int quantity, int minimumQuantity, String category) {
            ContentValues values = productValues(userId, name, price, quantity, minimumQuantity, category);
            long id = db.insert("products", null, values);
            move(db, id, userId, "ENTRADA", quantity, "Cadastro inicial");
            log(db, userId, "CREATE_PRODUCT", "products", name);
            return id;
        }

        public void updateProduct(long id, long userId, String name, double price, int quantity, int minimumQuantity, String category) {
            Product oldProduct = findProduct(id);
            getWritableDatabase().update(
                    "products",
                    productValues(userId, name, price, quantity, minimumQuantity, category),
                    "id = ?",
                    new String[]{String.valueOf(id)}
            );

            if (oldProduct != null && oldProduct.quantity != quantity) {
                String type = quantity > oldProduct.quantity ? "ENTRADA" : "SAIDA";
                int difference = Math.abs(quantity - oldProduct.quantity);
                move(id, userId, type, difference, "Ajuste por edição");
            }

            log(userId, "UPDATE_PRODUCT", "products", name);
        }

        public void deleteProduct(long id, long userId) {
            Product product = findProduct(id);
            getWritableDatabase().delete("products", "id = ?", new String[]{String.valueOf(id)});
            log(userId, "DELETE_PRODUCT", "products", product == null ? "id=" + id : product.name);
        }

        public Product findProduct(long id) {
            Cursor cursor = getReadableDatabase().query(
                    "products",
                    null,
                    "id = ?",
                    new String[]{String.valueOf(id)},
                    null,
                    null,
                    null
            );

            try {
                return cursor.moveToFirst() ? productFrom(cursor) : null;
            } finally {
                cursor.close();
            }
        }

        public List<Product> products() {
            ArrayList<Product> products = new ArrayList<>();
            Cursor cursor = getReadableDatabase().query("products", null, null, null, null, null, "name");

            try {
                while (cursor.moveToNext()) {
                    products.add(productFrom(cursor));
                }
            } finally {
                cursor.close();
            }

            return products;
        }

        private ContentValues productValues(long userId, String name, double price, int quantity, int minimumQuantity, String category) {
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("name", name.trim());
            values.put("price", price);
            values.put("quantity", quantity);
            values.put("minimum_quantity", minimumQuantity);
            values.put("category", normalizeCategory(category));
            values.put("updated_at", System.currentTimeMillis() / 1000);
            return values;
        }

        private Product productFrom(Cursor cursor) {
            return new Product(
                    cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("minimum_quantity")),
                    cursor.getString(cursor.getColumnIndexOrThrow("category"))
            );
        }

        public void move(long productId, long userId, String type, int quantity, String note) {
            move(getWritableDatabase(), productId, userId, type, quantity, note);
        }

        private void move(SQLiteDatabase db, long productId, long userId, String type, int quantity, String note) {
            ContentValues values = new ContentValues();
            values.put("product_id", productId);
            values.put("user_id", userId);
            values.put("type", type);
            values.put("quantity", quantity);
            values.put("note", note);
            db.insert("stock_movements", null, values);
        }

        public void log(long userId, String action, String entity, String details) {
            log(getWritableDatabase(), userId, action, entity, details);
        }

        private void log(SQLiteDatabase db, long userId, String action, String entity, String details) {
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("action", action);
            values.put("entity", entity);
            values.put("details", details);
            db.insert("audit_logs", null, values);
        }

        public List<Entry> entries(String table) {
            ArrayList<Entry> entries = new ArrayList<>();
            String sql;

            if ("audit_logs".equals(table)) {
                sql = "SELECT action || ' • ' || entity, IFNULL(details, '') || ' • usuário: ' || IFNULL(user_id, '-') " +
                        "FROM audit_logs ORDER BY id DESC";
            } else {
                sql = "SELECT type || ' • produto ' || IFNULL(product_id, '-'), " +
                        "'Qtd: ' || quantity || ' • usuário: ' || user_id || ' • ' || IFNULL(note, '') " +
                        "FROM stock_movements ORDER BY id DESC";
            }

            Cursor cursor = getReadableDatabase().rawQuery(sql, null);
            try {
                while (cursor.moveToNext()) {
                    entries.add(new Entry(cursor.getString(0), cursor.getString(1)));
                }
            } finally {
                cursor.close();
            }

            return entries;
        }

        public String dump(String table) {
            if (!Arrays.asList("users", "products", "stock_movements", "audit_logs").contains(table)) {
                return "Tabela inválida";
            }

            Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + table + " ORDER BY id DESC", null);
            StringBuilder output = new StringBuilder();

            try {
                String[] columns = cursor.getColumnNames();

                for (String column : columns) {
                    output.append(column).append('\t');
                }

                output.append("\n");

                while (cursor.moveToNext()) {
                    for (String column : columns) {
                        output.append(cursor.getString(cursor.getColumnIndexOrThrow(column))).append('\t');
                    }

                    output.append("\n");
                }

                if (cursor.getCount() == 0) {
                    output.append("Nenhum registro encontrado.");
                }
            } finally {
                cursor.close();
            }

            return output.toString();
        }

        public int rows(String table) {
            Cursor cursor = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + table, null);
            try {
                return cursor.moveToFirst() ? cursor.getInt(0) : 0;
            } finally {
                cursor.close();
            }
        }

        private static String normalizeEmail(String email) {
            return email.trim().toLowerCase();
        }

        private static String normalizeCategory(String category) {
            if (category == null || category.trim().isEmpty()) {
                return "Geral";
            }
            return category.trim();
        }

        private static String salt() {
            byte[] bytes = new byte[16];
            new SecureRandom().nextBytes(bytes);
            return hex(bytes);
        }

        private static String sha(String value) throws Exception {
            return hex(MessageDigest.getInstance("SHA-256").digest(value.getBytes("UTF-8")));
        }

        private static String hex(byte[] bytes) {
            StringBuilder builder = new StringBuilder();
            for (byte current : bytes) {
                builder.append(String.format("%02x", current));
            }
            return builder.toString();
        }

        public static class Login {
            public final long id;
            public final String name;

            public Login(long id, String name) {
                this.id = id;
                this.name = name;
            }
        }
    }
