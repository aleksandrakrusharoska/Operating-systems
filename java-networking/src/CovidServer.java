import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class CovidServer extends Thread {
    private int port;
    private String fileOutput;

    public CovidServer(int port, String fileOutput) {
        this.port = port;
        this.fileOutput = fileOutput;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            File file = new File(fileOutput);

            if (!file.exists()) {
                file.createNewFile();
            }

            while (true) {
                Socket socket = serverSocket.accept();
                WorkerThread workerThread = new WorkerThread(socket, fileOutput);
                workerThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CovidServer covidServer = new CovidServer(8888, "D:\\java-networking\\data.csv");
        covidServer.start();
    }
}

class WorkerThread extends Thread {

    private Socket socket;
    private String fileLocation;
    private static Semaphore semaphore = new Semaphore(1);

    public WorkerThread(Socket socket, String fileLocation) {
        this.socket = socket;
        this.fileLocation = fileLocation;
    }

    @Override
    public void run() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            Scanner scanner = new Scanner(inputStream);

            outputStream.write(("HELLO " + socket.getRemoteSocketAddress() + "\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            String message = scanner.nextLine();

            if (!message.startsWith("HELLO ")) {
                socket.close();
            }

            outputStream.write(("SEND DAILY DATA\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            // Poraka:
            // Number of new covid cases, Number of hospitalized cases, Number of recovered patients
            // 5, 10, 15
            message = scanner.nextLine();
            LocalDate date = LocalDate.now();

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileLocation, true));

            semaphore.acquire();
            bufferedWriter.write(date + "," + message + "\n");
            bufferedWriter.flush();
            semaphore.release();

            outputStream.write(("OK\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            message = scanner.nextLine();

            socket.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
