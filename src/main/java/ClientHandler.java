import java.net.Socket;
import java.sql.Connection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Connection connection;

    public ClientHandler(Socket socket, Connection connection) {
        this.clientSocket = socket;
        this.connection = connection;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Получено: " + inputLine);
                String response = processRequest(inputLine);
                out.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String processRequest(String request) {
        if (request.startsWith("login")) {
            return handleLogin(request);
        } else if (request.equals("getProducts")) {
            return getProducts();
        } else {
            return "Неизвестный запрос";
        }
    }

    private String handleLogin(String request) {
        String[] parts = request.split(" ");
        if (parts.length == 3) {
            String username = parts[1];
            String password = parts[2];
            return authenticateUser(username, password) ? "Авторизация успешна" : "Неверный логин или пароль";
        }
        return "Неправильный формат запроса";
    }

    private boolean authenticateUser(String username, String password) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getProducts() {
        StringBuilder response = new StringBuilder();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name, calories FROM products");
            while (rs.next()) {
                response.append(rs.getString("name")).append(": ").append(rs.getInt("calories")).append(" калорий\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return response.toString();
    }
}
