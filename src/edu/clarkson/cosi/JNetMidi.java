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
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.MidiUnavailableException;

/**
 *
 * @author mhrcek
 */
public class JNetMidi {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Missing ");
            System.exit(1);
        }
        if (args[0].toLowerCase().equals("client")) {
            try {
                int port = Integer.parseInt(args[1]);
                Client c = new Client(port, "128.153.144.228");
                c.run();
            } catch (SocketException ex) {
                Logger.getLogger(JNetMidi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownHostException ex) {
                Logger.getLogger(JNetMidi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(JNetMidi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(JNetMidi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MidiUnavailableException ex) {
                Logger.getLogger(JNetMidi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(JNetMidi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (args[0].toLowerCase().equals("server")) {
            try {
                int port = Integer.parseInt(args[1]);
                Server s = new Server(port);
                Thread t = new Thread(s.searchForClients);
                t.start();
                s.run();
            } catch (SocketException ex) {
                Logger.getLogger(JNetMidi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownHostException ex) {
                Logger.getLogger(JNetMidi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(JNetMidi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(JNetMidi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MidiUnavailableException ex) {
                Logger.getLogger(JNetMidi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(JNetMidi.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.err.println("Please specify program to open in client or server mode.");
            System.exit(2);
        }
    }
}
