/*
 * $Id$
 *
 * Copyright 1998,1999,2000,2001 by Rockhopper Technologies, Inc.,
 * 75 Trueman Ave., Haddonfield, New Jersey, 08033-2529, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Rockhopper Technologies, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with RTI.
 */

package com.rockhoppertech.music.modifiers;

/*
 * #%L
 * Rocky Music Core
 * %%
 * Copyright (C) 1996 - 2013 Rockhopper Technologies
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

import com.rockhoppertech.collections.CircularArrayList;
import com.rockhoppertech.music.Note;
import com.rockhoppertech.music.Timed;

/**
 * Class <code>StartModifier</code> Modifies the note's start beat by the
 * amount. The series provided will wrap.
 * 
 * 
 * @author <a href="mailto:gene@rockhoppertech.com">Gene De Lisa</a>
 * @version $Revision$, $Date$
 * @since 1.0
 * @see NoteModifier
 * @see AbstractModifier
 * @see TimedModifier
 */
public class StartBeatModifier extends AbstractModifier implements
        TimedModifier, NoteModifier {

    private static final Logger logger = LoggerFactory
            .getLogger(StartBeatModifier.class);

   

    /**
     * 
     */
    public StartBeatModifier() {
    }

    /**
     * @param list
     */
    public StartBeatModifier(List<Number> list) {
        super(list);
    }

    /**
     * @param numbers
     */
    public StartBeatModifier(Number... numbers) {
        super(numbers);
    }

    /**
     * @param op
     * @param list
     */
    public StartBeatModifier(Operation op, List<Number> list) {
        super(op, list);
    }

    /**
     * @param operation
     * @param numbers
     */
    public StartBeatModifier(Operation operation, Number... numbers) {
        super(operation, numbers);
    }

    private void doit(final Timed timed) {
        double d = 0d;
        final double value = values.next().doubleValue();
        switch (operation) {
        case ADD:
            d = timed.getStartBeat() + value;
            if (d < 1d) {
                logger.debug("value {} is < 1, setting to 1",
                        d);
                d = 1d;
            }
            timed.setStartBeat(d);

            break;

        case SUBTRACT:
            d = timed.getStartBeat() - value;
            logger.debug("value {} start {} d {}", value, timed.getStartBeat(), d);            
            if (d < 1d) {
                logger.debug("value {} is < 1, setting to 1",
                        d);
                d = 1d;
            }
            timed.setStartBeat(d);
            break;

        case DIVIDE:
            d = timed.getStartBeat() / value;
            if (d < 1d) {
                logger.debug("Rounding to 1: ", d);
                d = 1d;
            }
            timed.setStartBeat(d);
            break;

        case MULTIPLY:
            d = timed.getStartBeat() * value;
            if (d < 1d) {
                logger.debug("Rounding to 1: ", d);
                d = 1d;
            }
            timed.setStartBeat(d);
            break;

        case SET:
            d = value;
            if (d < 1d) {
                logger.debug("Rounding to 1: ", d);
                d = 1d;
            }
            timed.setStartBeat(d);
            break;

        case MOD:
            d = timed.getStartBeat() % value;
            if (d < 1d) {
                logger.debug("Rounding to 1: ", d);
                d = 1d;
            }
            timed.setStartBeat(d);
            break;

        case QUANTIZE:
            d = AbstractModifier.quantize(timed.getStartBeat(),
                    value);
            if (d < 1d) {
                logger.debug("Rounding to 1: ", d);
                d = 1d;
            }
            timed.setStartBeat(d);
            break;
        }

    }

    /**
     * <code>getDescription</code>
     * 
     * @return a <code>String</code> value
     */
    @Override
    public String getDescription() {
        return "Start Beat modifier";
    }

    /**
     * <code>getName</code>
     * 
     * @return a <code>String</code> value
     */
    @Override
    public String getName() {
        return "Start Beat";
    }

  

    @Override
    public void modify(final Note note) {
        doit(note);
        if (successor != null) {
            successor.modify(note);
        }
    }

    @Override
    public void modify(final Timed timed) {
        doit(timed);
    }

}