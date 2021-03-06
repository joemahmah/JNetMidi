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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

/**
 *
 * @author mhrcek
 */
public class MidiWrapper {

    private Sequence sequence;
    private Sequencer sequencer;
    private Soundbank soundbank;
    private Synthesizer synth;
    private MidiDevice device;
    private boolean canPlay = false;

    public MidiWrapper() throws MidiUnavailableException {
        sequencer = MidiSystem.getSequencer();
        synth = MidiSystem.getSynthesizer();
        sequencer.open();
        synth.open();
    }

    public void loadMidi(String location) {
        try {
            sequence = MidiSystem.getSequence(new File(location));

            sequencer.setSequence(sequence);
            sequencer.getTransmitter().setReceiver(synth.getReceiver());
            canPlay = true;
        } catch (Exception e) {
            System.err.println("Unable to load MIDI at " + location);
            canPlay = false;
        }
    }

    public void loadMidi(Sequence sequence) {
        this.sequence = sequence;

        try {
            sequencer.setSequence(this.sequence);
            sequencer.getTransmitter().setReceiver(synth.getReceiver());
            canPlay = true;
        } catch (Exception ex) {
            System.err.println("Unable to load sequence.");
            canPlay = false;
        }

    }

//    public void loadMidi(String midiString, int bpm) throws MidiUnavailableException {
//        try {
//            sequence = new Sequence(0, 92);
//
//            Track t = sequence.createTrack();
//
//            MidiEvent[] midiEvents = NetMidi.parseMidiString(midiString);
//            for (MidiEvent me : midiEvents) {
//                t.add(me);
//            }
//
//            sequencer.setSequence(sequence);
//            sequencer.getTransmitter().setReceiver(synth.getReceiver());
//            canPlay = true;
//
//        } catch (InvalidMidiDataException ex) {
//            Logger.getLogger(MidiWrapper.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public void playMidi() {
        if (canPlay) {
            sequencer.start();
        } else {
            System.err.println("No MIDI loaded!");
        }
    }

    public void stopMidi() {
        if (canPlay) {
            sequencer.stop();
        } else {

        }
    }

    public void resetMidi() {
        if (canPlay) {
            sequencer.setTickPosition(0);
        } else {
            System.err.println("No MIDI loaded!");
        }
    }

    public void setTempo(int bpm) {
        if (canPlay) {
            sequencer.setTempoInBPM(bpm);
        } else {
            System.err.println("No MIDI loaded!");
        }
    }

    public Sequence getSequence() {
        return sequence;
    }

    public boolean canPlay() {
        return canPlay;
    }

    public Track[] getTracks() throws Exception {
        if (canPlay()) {
            return sequence.getTracks();
        } else {
            throw new Exception("No midi loaded!");
        }
    }

    public void setSoundfont(String location) {
        try {
            for (Instrument i : synth.getAvailableInstruments()) {
                //System.out.println(i);
                synth.unloadInstrument(i);
            }

            soundbank = MidiSystem.getSoundbank(new File(location));
            System.out.println("Soundfont Loaded");
            synth.loadAllInstruments(soundbank);
            /*for (Instrument i : synth.getLoadedInstruments()){
             System.out.println(i);
             }*/

            /*for(Instrument i : soundbank.getInstruments()){
             System.out.println(i);
             }*/
        } catch (InvalidMidiDataException ex) {
            Logger.getLogger(MidiWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MidiWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
