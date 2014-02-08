package com.rockhoppertech.music.midi.js.function;

/*
 * #%L
 * Rocky Music Core
 * %%
 * Copyright (C) 1996 - 2014 Rockhopper Technologies
 * %%
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
 * #L%
 */

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rockhoppertech.music.midi.js.MIDINote;
import com.rockhoppertech.music.modifiers.AbstractModifier;

/**
 * A Guava Function to change {@code MIDINot}e's MIDI velocity. Uses a
 * CircularList of values.
 * 
 * @author <a href="http://genedelisa.com/">Gene De Lisa</a>
 * @see com.rockhoppertech.collections.CircularArrayList
 * @see AbstractMusicFunction
 * @see MIDINoteFunction
 */
public class VelocityFunction extends AbstractMusicFunction implements
        MIDINoteFunction {
    private static final Logger logger = LoggerFactory
            .getLogger(VelocityFunction.class);

    public VelocityFunction() {
        super();
    }

    /**
     * Initialize the function with these values.
     * 
     * @param list
     */
    public VelocityFunction(List<Number> list) {
        super(list);
    }

    /**
     * Initialize the function with these values.
     * 
     * @param numbers
     */
    public VelocityFunction(Number... numbers) {
        super(numbers);
    }

    /**
     * Initialize the function with these values.
     * 
     * @param op
     * @param list
     */
    public VelocityFunction(Operation op, List<Number> list) {
        super(op, list);
    }

    /**
     * Initialize the function with these values.
     * 
     * @param operation
     * @param numbers
     */
    public VelocityFunction(Operation operation, Number... numbers) {
        super(operation, numbers);
    }

    /*
     * @see com.google.common.base.Function#apply(java.lang.Object)
     */

    @Override
    public MIDINote apply(MIDINote note) {
        logger.debug("before: " + note);
        final int value = values.next().intValue();
        int midiNumber = 0;

        MIDINote returnedNote;
        if (isCreateDuplicate()) {
            returnedNote = note.duplicate();
        } else {
            returnedNote = note;
        }

        switch (operation) {
        case ADD:
            midiNumber = note.getVelocity() + value;
            midiNumber = validateValue(note, midiNumber);
            returnedNote.setVelocity(midiNumber);
            break;
        case SUBTRACT:
            midiNumber = note.getVelocity() - value;
            midiNumber = validateValue(note, midiNumber);
            returnedNote.setVelocity(midiNumber);
            break;
        case DIVIDE:
            midiNumber = note.getVelocity() / value;
            midiNumber = validateValue(note, midiNumber);
            returnedNote.setVelocity(midiNumber);
            break;
        case MULTIPLY:
            midiNumber = note.getVelocity() * value;
            midiNumber = validateValue(note, midiNumber);
            returnedNote.setVelocity(midiNumber);
            break;
        case MOD:
            midiNumber = note.getVelocity() % value;
            midiNumber = validateValue(note, midiNumber);
            returnedNote.setVelocity(midiNumber);
            break;
        case SET:
            returnedNote.setVelocity(midiNumber);
            break;
        case QUANTIZE:
            int d = (int) AbstractModifier.quantize(note.getVelocity(),
                    value);
            if ((d < 0) || (d > 127)) {
                d = note.getVelocity();
            }
            returnedNote.setVelocity(d);
        }
        logger.debug("returnedNote: " + returnedNote);
        return returnedNote;
    }

    private int validateValue(MIDINote note, int midiNumber) {
        if ((midiNumber < 0) || (midiNumber > 127)) {
            midiNumber = note.getVelocity();
        }
        return midiNumber;
    }
}
