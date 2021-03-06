package model.server.server;
import model.server.DB.database;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
public class server {
    private ServerSocket serverSock;
    public server() {
        try {
            this.serverSock = new ServerSocket(10001);
            while (true) {
                System.out.println("-------------waitting client-------------");
                Socket sock = serverSock.accept();
                DBThread care = new DBThread(sock);
                care.start();
                System.out.println("-------------client accepted-------------");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

class DBThread extends Thread {
    private static final int INIT = 0, DIPOSIT = 1, WITHDRAW = 2, TRANSFER = 3; // for type
    private Socket sock;
    private database db;
    private BufferedReader br;
    private PrintWriter pw;

    public DBThread(Socket sock) {
        this.sock = sock;
        this.db = new database();
        try {
            pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void run() {
        String msg = null;
        int type;
        String accountFrom, accountTo, money;
        try {
            msg = br.readLine();
            if (msg == null) {
                return;
            }
            StringTokenizer st = new StringTokenizer(msg, "/");
            String[] ack = new String[1];
            ack[0] = "ACK";
            String[] nack = new String[1];
            nack[0] = "not enough money";
            type = Integer.parseInt(st.nextToken());
            accountFrom = st.nextToken();
            db.connect();
            db.rollback();
            switch (type) {
                case INIT:
                    String[] info = db.getInfo(accountFrom);
                    sendMsg(info);
                    break;
                case DIPOSIT:
                    money = st.nextToken();
                    db.putMoney(accountFrom, money);
                    sendMsg(ack);
                    break;
                case WITHDRAW:
                    money = st.nextToken();
                    if (db.getMoney(accountFrom, money)) {
                        sendMsg(ack);
                    } else {
                        sendMsg(nack);
                    }
                    break;
                case TRANSFER:
                    accountTo = st.nextToken();
                    money = st.nextToken();
                    if(db.getMoney(accountFrom, money)) {
                        db.putMoney(accountTo, money);
                        sendMsg(ack);
                    } else {
                        sendMsg(nack);
                    }
                    break;
            }
            db.commit();
            db.disconnect();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendMsg(String[] msg) {
        int size = msg.length;
        String send = msg[0];
        for (int i = 1; i < size; i++) {
            send = send + "/" + msg[i];
        }
        // 여기서 암호화하고 보낸다.

        pw.println(send);
        pw.flush();
    }
}
