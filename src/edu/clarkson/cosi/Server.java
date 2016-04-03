/*
 * Copyright 2016 Michael Hrcek.
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
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Track;

/**
 *
 * @author mhrcek
 */
public class Server {

    DatagramSocket server;
    List<InetAddress> clients;
    int port;
    int clientPort;
    MidiWrapper midi;
    volatile int repliesNeeded;

    public Server(int port) throws SocketException, MidiUnavailableException {
        this(port, 8090);
    }

    public Server(int port, int clientPort) throws SocketException, MidiUnavailableException {
        server = new DatagramSocket(port);
        clients = new ArrayList<InetAddress>();
        this.port = port;
        this.clientPort = clientPort;
        midi = new MidiWrapper();
    }

    public void run() throws IOException, InterruptedException, Exception {
        byte[] receiveData = new byte[32];
        byte[] sendData = new byte[262144];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        Scanner input = new Scanner(System.in);
        String[] command;

        while (true) {

            System.out.print("JNetMidi: ");
            command = input.nextLine().split(" ");
            String replyString;

            if (command[0].equals("ping")) {
                replyString = '0' + "";
                for (InetAddress address : clients) {
                    sendData = replyString.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, clientPort);
                    server.send(sendPacket);
                }
            } else if (command[0].equals("play")) {
                if (command.length <= 1) {
                    System.out.println("Must specify .mid file to play...");
                } else {

                    midi.loadMidi(command[1]);
                    if (midi.canPlay()) {

                        NetMidi netMidi = new NetMidi(midi.getTracks(), clients);
                        String[] tracks = netMidi.getTracks();

                        repliesNeeded = 0;

                        for (int i = 0; i < clients.size(); i++) {
                            if (i < tracks.length) {
                                replyString = '1' + tracks[i];
                                sendData = replyString.getBytes();
                                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clients.get(i), clientPort);
                                server.send(sendPacket);
                                repliesNeeded++;
                            }
                        }

                        System.out.println("Waiting for " + repliesNeeded + " replies!");

                        while (repliesNeeded > 0) {
                            Thread.sleep(50);
                        }

                        System.out.println("All replies in. Playing!");

                        for (InetAddress address : clients) {
                            replyString = '4' + "";
                            sendData = replyString.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, clientPort);
                            server.send(sendPacket);
                        }
                    }
                }
            } else if (command[0].equals("stop")) {
                replyString = '3' + "";
                for (InetAddress address : clients) {
                    sendData = replyString.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, clientPort);
                    server.send(sendPacket);
                }
            } else if (command[0].equals("quit")) {
                replyString = '2' + "";
                for (InetAddress address : clients) {
                    sendData = replyString.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, clientPort);
                    server.send(sendPacket);
                }
            }
        }
    }

    Runnable searchForClients = new Runnable() {

        @Override
        public void run() {
            try {
                byte[] receiveData = new byte[32];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                while (true) {
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    server.receive(receivePacket);
                    String sentence = new String(receivePacket.getData());
                    InetAddress IPAddress = receivePacket.getAddress();
                    if (!clients.contains(IPAddress)) {
                        clients.add(IPAddress);
                        System.out.print("Client at " + IPAddress.getHostAddress() + " has joined.\nJNetMidi: ");
                    } else {
                        if (receiveData[0] == '1') {
                            if (receiveData[1] == 'p') {
                                System.out.print("Ping reply from " + IPAddress.getHostAddress() + "\nJNetMidi: ");
                            } else if (receiveData[1] == 'q') {
                                clients.remove(IPAddress);
                            } else if (receiveData[1] == 'r') {
                                repliesNeeded--;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("IT BROKE!");;
            }
        }
    };

}
