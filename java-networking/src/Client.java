import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client extends Thread {
    private String ipAddress;
    private int port;

    public Client(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public void run() {
        // Konektiranje do serverot
        try {

            Socket socket = new Socket(ipAddress, port);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            Scanner scanner = new Scanner(inputStream);

            // Isprakjame poraka do serverot: hello:221005
            outputStream.write(("hello:221005\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            // Client objektot ke e blokiran na 32 linija kod se dodeka ne dobie poraka od serverot
            // Ako ne dobieme poraka 5 minuti, avtomatski da se prekine konekcijata
            // Serverot treba da ni vrati: 221005:hello
            String message = scanner.nextLine();

            if (!message.equals("221005:hello")) {
                socket.close();
            }

            // Isprakjame poraka do serverot: 221005:receive
            outputStream.write(("221005:receive\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            // Serverot treba da ni vrati:
            // 221005:send:filename.txt (POCNUVA DA GO ISPRAKJA FAJLOT)
            // SODRZINA na fajlot
            // 221005:over (KRAJ SO ISPRAKJANJE NA FAJLOT)

            // 221005:send:filename.txt
            message = scanner.nextLine();

            String fileName = message.split(":")[2];
            String filePath = "D:\\java-networking\\";

            File file = new File(filePath + fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

//            while(!(message = scanner.nextLine()).equals("221005:over")) {
//
//            }

            while (true) {
                message = scanner.nextLine();

                if (message.equals("221005:over")) {
                    break;
                }

                bufferedWriter.write(message + "\n");
                bufferedWriter.flush();
            }


            // Isprakjame poraka: 221005:size:fileSize, kaj sto fileSize e goleminata na fajlot vo bajti

            long fileSize = file.length();

            outputStream.write(("221005:size:" + fileSize + "\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 9000);
        client.start();
    }
}
