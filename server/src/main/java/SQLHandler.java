import java.sql.*;

public class SQLHandler {
    private static Connection connection;
    private static Statement statement;
    private static Driver driver;
    private static final String URL = "jdbc:mysql://localhost:3306/dropbox?autoReconnect=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "********";

    public static void connect() throws ClassNotFoundException, SQLException {
        driver = new com.mysql.cj.jdbc.Driver();
        DriverManager.registerDriver(driver);

        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        statement = connection.createStatement();
        if (!connection.isClosed()) System.out.println("connection is established");
    }

    public static String getUserByLoginAndPass(String login, String pass) {
        try {
            ResultSet rs = statement.executeQuery(String.format("SELECT id FROM users WHERE login = '%s' AND pass = '%s';", login, pass));
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean addNewUser(String login, String pass) {
        try {
            statement.executeUpdate(String.format("INSERT INTO users (login, password) VALUES ('%s', '%s');", login, pass));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
