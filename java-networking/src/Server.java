import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Server extends Thread {

    private int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();
                WorkerThread workerThread = new WorkerThread(socket);
                workerThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class WorkerThread extends Thread {

        private Socket socket;

        public WorkerThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();
                Scanner scanner = new Scanner(inputStream);

                // Ja citame porakata: hello:221005
                String message = scanner.nextLine();

                int index = Integer.parseInt(message.split(":")[1]);

                outputStream.write((index + ":hello\n").getBytes(StandardCharsets.UTF_8));
                outputStream.flush();

                // Ja citame porakata: 221005:receive
                message = scanner.nextLine();

                if (!message.equals(index + ":receive")) {
                    socket.close();
                }

                // 221005:send:filename.txt
                // SODRZINA na fajlot
                // 221005:over

                File file = new File("D:\\server.txt");
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

                outputStream.write((index + ":send:" + file.getName() + "\n").getBytes(StandardCharsets.UTF_8));
                outputStream.flush();

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    outputStream.write((line + "\n").getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                }

                outputStream.write((index + ":over\n").getBytes(StandardCharsets.UTF_8));
                outputStream.flush();

                // Ja citame porakata 221005:size:fileSize
                message = scanner.nextLine();

                System.out.println(message);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public static void main(String[] args) {
        Server server = new Server(9000);
        server.start();
    }


}

