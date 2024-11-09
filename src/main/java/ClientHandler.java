import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
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
        } else if (request.startsWith("register")) {
            return handleRegistration(request);
        } else if (request.startsWith("createMenu")) {
            return createMenu(request);
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

    private String handleRegistration(String request) {
        String[] parts = request.split(" ");
        if (parts.length == 4) {
            String username = parts[1];
            String password = parts[2];
            String role = parts[3];
            return registerUser(username, password, role) ? "Регистрация успешна" : "Ошибка при регистрации";
        }
        return "Неправильный формат запроса";
    }

    private boolean registerUser(String username, String password, String role) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String createMenu(String request) {
        String[] parts = request.split(" ");
        if (parts.length == 4) {
            int userId = Integer.parseInt(parts[1]);
            int productId = Integer.parseInt(parts[2]);
            double portionSize = Double.parseDouble(parts[3]);
            return addToMenu(userId, productId, portionSize) ? "Меню создано" : "Ошибка при создании меню";
        }
        return "Неправильный формат запроса";
    }

    private boolean addToMenu(int userId, int productId, double portionSize) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO menu (user_id, product_id, portion_size) VALUES (?, ?, ?)");
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            stmt.setDouble(3, portionSize);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
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
