package com.nowcoder.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UdpServer {

    public static ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public static List<InetSocketAddress> addressList = new ArrayList<>();

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(9001);
            // 随时通知
            threadPool.submit(new SendTask(socket));
            // 接受注册
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while (true) {
                socket.receive(packet);
                addressList.add((InetSocketAddress) packet.getSocketAddress());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
}

}

class SendTask implements Runnable {

    private DatagramSocket socket;

    public SendTask(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            String line;
            while ((line = scanner.nextLine()) != null) {
                for (InetSocketAddress isa : UdpServer.addressList) {
                    byte[] buffer = line.getBytes();
                    DatagramPacket packet = new DatagramPacket(
                            buffer, buffer.length, isa.getAddress(),isa.getPort());
                    socket.send(packet);
            }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}