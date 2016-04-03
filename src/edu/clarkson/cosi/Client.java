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
import java.net.UnknownHostException;
import javax.sound.midi.MidiUnavailableException;

/**
 *
 * @author mhrcek
 */
public class Client {

    DatagramSocket client;
    InetAddress host;
    MidiWrapper midi;
    int hostPort;
    NetMidi netMidi;

    public Client(int port, String serverName) throws SocketException, UnknownHostException, MidiUnavailableException {
        this(port, serverName, 8080);
    }

    public Client(int port, String serverName, int serverPort) throws SocketException, UnknownHostException, MidiUnavailableException {
        client = new DatagramSocket(port);
        host = InetAddress.getByName(serverName);
        midi = new MidiWrapper();
        hostPort = serverPort;
    }

    public void run() throws IOException, InterruptedException, MidiUnavailableException {
        try {
            byte[] receiveData = new byte[262144];
            byte[] sendData = new byte[32];
            DatagramPacket reply = new DatagramPacket(receiveData, receiveData.length);

            for (int i = 0; i < 10; i++) {

                sendData = "0join".getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, host, hostPort);
                client.send(sendPacket);
                Thread.sleep(25);
            }

            while (true) {

                reply = new DatagramPacket(receiveData, receiveData.length);
                client.receive(reply);
                byte replyType = receiveData[0];

                if (replyType == '0') {
                    System.out.println("Ping from host!");
                    sendData = "1ping".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, host, hostPort);
                    client.send(sendPacket);
                } else if (replyType == '1') {
                    System.out.println("Host sent midi data!");
                    midi.loadMidi(NetMidi.parseNetMidi(receiveData),receiveData[1]);
                    System.out.println("Midi data parsed.");
                    sendData = "1ready".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, host, hostPort);
                    client.send(sendPacket);
                    System.out.println("Replied to host!");
                } else if (replyType == '2') {
                    System.out.println("Server sent kill message! Exiting...");
                    sendData = "1quit".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, host, hostPort);
                    client.send(sendPacket);
                    System.exit(0);
                } else if (replyType == '3') {
                    System.out.println("Host sent stop message!");
                    midi.stopMidi();
                    midi.resetMidi();
                } else if (replyType == '4') {
                    System.out.println("Host sent play message!");
                    midi.playMidi();
                }

                sendData = "ping".getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, host, 8080);
                client.send(sendPacket);
            }
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

}
