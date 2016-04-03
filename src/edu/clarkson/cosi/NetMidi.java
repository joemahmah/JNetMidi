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

import java.net.InetAddress;
import java.util.List;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

/**
 *
 * @author mhrcek
 */
public class NetMidi {

    String[] tracks;
    int numClients;

    public NetMidi(Track[] tracks, List<InetAddress> clients) {
        this.tracks = new String[tracks.length];
        this.numClients = clients.size();

        for (int track = 0; track < tracks.length; track++) {
            for (int i = 0; i < tracks[track].size(); i++) {
                for (Byte b : tracks[track].get(i).getMessage().getMessage()) {
                    this.tracks[track] += (char) b.byteValue();
                }
                this.tracks[track] += "\ueeee";
            }
            System.out.println(this.tracks[track].length());
        }
    }

    public String[] getTracks() {
        return tracks;
    }

    public static String parseNetMidi(byte[] bytes) {
        StringBuilder midiString = new StringBuilder();
        for (Byte b : bytes) {
            midiString.append(b);
        }
        midiString.deleteCharAt(0);
        return midiString.toString();
    }

    public static MidiEvent[] parseMidiString(String midiString) {
        String[] notes = midiString.split("\ueeee");
        MidiEvent[] messages = new MidiEvent[notes.length];
        for (int i = 0; i < notes.length; i++) {
            byte[] bytes = new byte[notes[i].toCharArray().length];
            char[] chars = notes[i].toCharArray();
            int size = chars.length;
            for (int j = 0; j < size; j++) {
                bytes[j] = (byte) chars[j];
            }
            messages[i] = new MidiEvent(new MidiMessage(bytes) {

                @Override
                public Object clone() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            }, 2);
        }
        return messages;
    }

}
