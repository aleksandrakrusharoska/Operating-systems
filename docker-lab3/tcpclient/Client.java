import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client extends Thread{
    private int port;
    private String ipAdd;

    public Client(String ipAdd,int port) {
        this.port = port;
        this.ipAdd = ipAdd;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(ipAdd, port);

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            Scanner scanner = new Scanner(inputStream);

            outputStream.write(("log in\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            Scanner scanner1=new Scanner(System.in);
            String line;
            while(!(line=scanner1.nextLine()).equals("log out")){
                outputStream.write((line+"\n").getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }
            outputStream.write((line+"\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();



            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        Client client=new Client("tcpserver",1004);
        client.start();
    }
}
