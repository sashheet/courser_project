import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NutritionServer {
    private static final int PORT = 12345;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private static Connection connection;

    public static void main(String[] args) {
        try {
            connection = DatabaseConnection.getConnection();
            System.out.println("Соединение с базой данных установлено.");

            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер запущен на порту " + PORT);


            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Новое соединение: " + clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket, connection));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
