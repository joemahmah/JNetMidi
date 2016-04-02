/*
 * Copyright 2016 mhrcek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.clarkson.cosi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author mhrcek
 */
public class Server {

    DatagramSocket server;
    List<InetAddress> clients;
    int port;

    public Server(int port) throws SocketException {
        server = new DatagramSocket(port);
        clients = new ArrayList<InetAddress>();
        this.port = port;
    }

    public void run() throws IOException, InterruptedException {
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        Scanner input = new Scanner(System.in);
        String command;

        while (true) {

            System.out.print("JNetMidi: ");
            command = input.nextLine();

            if (command.equals("ping")) {
                for (InetAddress address : clients) {
                    String capitalizedSentence = '0' + "MEMES";
                    sendData = capitalizedSentence.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, 8090);
                    server.send(sendPacket);
                }
            } else if (command.equals("play")) {
                for (InetAddress address : clients) {
                    String capitalizedSentence = '1' + "MEMES";
                    sendData = capitalizedSentence.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, 8090);
                    server.send(sendPacket);
                }
            }
        }
    }

    Runnable searchForClients = new Runnable() {

        @Override
        public void run() {
            try {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                while (true) {
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    server.receive(receivePacket);
                    String sentence = new String(receivePacket.getData());
                    InetAddress IPAddress = receivePacket.getAddress();
                    if (!clients.contains(IPAddress)) {
                        clients.add(IPAddress);
                        System.out.println("Client at " + IPAddress.getHostAddress() + " has joined!");
                    }
                }
            } catch (Exception e) {
                System.err.println("IT BROKE!");;
            }
        }
    };

}
