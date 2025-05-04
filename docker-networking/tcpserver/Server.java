import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Server extends Thread {

    private int port;
    private Counter counter;

    public Server(int port, Counter counter) {
        this.port = port;
        this.counter = counter;
    }

    @Override
    public void run() {

        // Treba da kreirame server
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                // Prifakjaj request od klienti
                Socket socket = serverSocket.accept();
                WorkerThread workerThread = new WorkerThread(socket, counter);
                workerThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class WorkerThread extends Thread {

        private Socket socket;
        private Counter counter;
        private static Semaphore semaphore = new Semaphore(1);

        public WorkerThread(Socket socket, Counter counter) {
            this.socket = socket;
            this.counter = counter;
        }

        @Override
        public void run() {
            InputStream inputStream = null;
            try {
                inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                Scanner scanner = new Scanner(inputStream);

                String line = scanner.nextLine();

                if (!line.equals("login")) {
                    socket.close();
                }

                outputStream.write(("logged in\n").getBytes(StandardCharsets.UTF_8));
                outputStream.flush();

                while (!(line = scanner.nextLine()).equals("logout")) {
                    System.out.println(line);
                    outputStream.write(("echo: " + line + "\n").getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    semaphore.acquire();
                    counter.counter++;
                    semaphore.release();
                }
                outputStream.write(("logged out\n").getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                socket.close();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public static class Counter {
        public int counter = 0;
    }




    public static void main(String[] args) {

        Counter counter = new Counter();

        Server server = new Server(9000, counter);
        server.start();
    }
}
