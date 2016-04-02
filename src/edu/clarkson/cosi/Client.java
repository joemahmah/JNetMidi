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

    public Client(int port, String serverName) throws SocketException, UnknownHostException, MidiUnavailableException {
        client = new DatagramSocket(port);
        host = InetAddress.getByName(serverName);
        midi = new MidiWrapper();
    }

    public void run() throws IOException, InterruptedException {
        try {
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
            DatagramPacket reply = new DatagramPacket(receiveData, receiveData.length);

            for (int i = 0; i < 10; i++) {

                sendData = "Dank Memes".getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, host, 8080);
                client.send(sendPacket);
                Thread.sleep(25);
            }

            while (true) {

                reply = new DatagramPacket(receiveData, receiveData.length);
                client.receive(reply);
                if (reply.getData()[0] == new Character('0')) {
                    System.out.println("Ping from host!");
                } else if (reply.getData()[0] == new Character('1')) {
                    midi.setSoundfont("/home/csguest/midi/GenesiSF.SF2");
                    midi.loadMidi("/home/csguest/midi/son1hill.mid");
                    midi.playMidi();
                }
            }
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

}
