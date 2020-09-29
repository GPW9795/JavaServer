package com.nowcoder.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer {

    public static ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public static List<Socket> socketList = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
//        try {
//            ServerSocket serverSocket = new ServerSocket(9000);
//            while (true) {
//                Socket socket = serverSocket.accept(); // 没有客户端访问就阻塞
//                System.out.println("请求：" + socket.toString());
//                PrintStream ps = new PrintStream(socket.getOutputStream());
//                ps.println("Welcome " + socket.getInetAddress().getHostAddress());
//                socket.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            ServerSocket serverSocket = new ServerSocket(9000);
            while (true) {
                Socket socket = serverSocket.accept();
                socketList.add(socket);
                threadPool.submit(new ThreadTask(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class ThreadTask implements Runnable {

    private Socket socket;
    private BufferedReader reader;

    public ThreadTask(Socket socket) {
        this.socket = socket;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                for (Socket client : TcpServer.socketList) {
                    String from = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
                    String content = from + "说：" + line;
                    new PrintStream(client.getOutputStream()).println(content);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}