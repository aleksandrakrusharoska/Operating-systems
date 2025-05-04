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
        try {
            ServerSocket serverSocket=new ServerSocket(port);
            while (true){
                Socket socket=serverSocket.accept();
                WorkerThread workerThread=new WorkerThread(socket,counter);
                workerThread.start();
                System.out.println(counter.counter);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class WorkerThread extends Thread{
        private Socket socket;
        private Counter counter;
        private static Semaphore semaphore=new Semaphore(1);

        public WorkerThread(Socket socket, Counter counter) {
            this.socket = socket;
            this.counter = counter;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                Scanner scanner = new Scanner(inputStream);

                String line= scanner.nextLine();
                if(!line.equals("log in"))
                    socket.close();

                outputStream.write(("logged in\n").getBytes(StandardCharsets.UTF_8));
                outputStream.flush();

                //ne znaeme kolku poraki ke isprati client
                while (!(line = scanner.nextLine()).equals("log out")) {
                    outputStream.write(("echo: "+line+"\n").getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    System.out.println(line);
                    semaphore.acquire();
                    counter.counter++;
                    semaphore.release();
                }

                outputStream.write(("logged out\n").getBytes(StandardCharsets.UTF_8));
                outputStream.flush();



                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
    static class Counter{
        public int counter=0;
    }

    public static void main(String[] args) {
        Counter counter=new Counter();
        Server server=new Server(1004,counter);
        server.start();

    }
}
