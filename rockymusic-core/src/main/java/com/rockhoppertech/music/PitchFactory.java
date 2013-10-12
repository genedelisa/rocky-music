package com.rockhoppertech.music;

/*
 * $Id$
 *
 * Copyright 1998,1999,2000,2001,2002,2003 by Rockhopper Technologies,
 * Inc. (RTI), 75 Trueman Ave., Haddonfield, New Jersey, 08033-2529,
 * U.S.A.  All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Rockhopper Technologies, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use it
 * only in accordance with the terms of the license agreement you
 * entered into with RTI.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Uses the GoF flyweight pattern. Consider using this first to obtain Pitch
 * instances rather than creating them yourself.
 * </p>
 * 
 * @author <a href="mailto:gene@rockhoppertech.com">Gene De Lisa</a>
 * @version $Revision$, $Date$
 */
public class PitchFactory {
	private static final Logger logger = LoggerFactory
			.getLogger(PitchFactory.class);

	protected static boolean isTraceEnabled;
	protected static boolean isDebugEnabled;
	protected static boolean isInfoEnabled;
	protected static boolean isWarnEnabled;
	protected static boolean isErrorEnabled;
	protected static boolean isFatalEnabled;

	static {
		isTraceEnabled = logger.isTraceEnabled();
		isDebugEnabled = logger.isDebugEnabled();
		isInfoEnabled = logger.isInfoEnabled();
		isWarnEnabled = logger.isWarnEnabled();
		isErrorEnabled = logger.isErrorEnabled();
	}

	private static Map<Integer, Pitch> pitches = new HashMap<Integer, Pitch>();
	private static PitchFactory instance;

	public static synchronized PitchFactory getInstance() {
		if (instance == null) {
			instance = new PitchFactory();
		}
		return instance;
	}

	private PitchFactory() {
		if (isTraceEnabled) {
			logger.trace(new Throwable().getStackTrace()[0].getMethodName());
		}
	}

	public static Pitch getPitch(double midiNumber) {
		return getPitch((int) midiNumber);
	}

	/**
	 * <p>
	 * Retrieves a cached instance. If it doesn't yet exist in the cache it is
	 * created and cached.
	 * </p>
	 * 
	 * @param midiNumber
	 * @return the matching Pitch
	 */
	public static Pitch getPitch(int midiNumber) {
		if (isTraceEnabled) {
			logger.trace(new Throwable().getStackTrace()[0].getMethodName());
		}
		Integer i = new Integer(midiNumber);
		Pitch p = null;
		if (pitches.containsKey(i)) {
			p = pitches.get(i);
			if (isDebugEnabled) {
				logger.debug("retrieved existing pitch: " + p);
			}

		} else {
			p = new Pitch(midiNumber);
			pitches.put(i, p);
			if (isDebugEnabled) {
				logger.debug("created and cached new pitch: " + p);
			}
		}
		return p;
	}

	/*
	 * What about CS and DF? for key signatures they are different things! So,
	 * the midi number is not enough for uniqueness.
	 */
	public static final Map<String, Pitch> nameMap = new TreeMap<String, Pitch>();

	public static Pitch getPitchByName(String name) {
		name = name.trim();
		Pitch p = nameMap.get(name);
		if (p == null) {
			Integer i = Pitch.nameMap.get(name.toUpperCase());
			if (i == null) {
				throw new IllegalArgumentException(String.format(
						"That is one bad name dude: '%s'", name));
			}
			// so go ahead and get a shared instance.
			// cs, c# and df will point at the same pitch instance.
			p = getPitch(i);
			nameMap.put(name, p);
			p.setPreferredSpelling(name);
		}
		return p;
	}

	/**
	 * <p>
	 * Retrieves a cached instance. If it doesn't yet exist in the cache it is
	 * created and cached.
	 * 
	 * @param pitchString
	 *            name plus octave number e.g. C5
	 * @return the matching Pitch
	 */
	public static Pitch getPitch(String pitchString) {
		Integer i = new Integer(PitchFormat.stringToMidiNumber(pitchString));
		Pitch p = null;
		if (pitches.containsKey(i)) {
			p = (Pitch) pitches.get(i);
			if (isDebugEnabled) {
				logger.debug("retrieved existing pitch: " + p);
			}

		} else {
			p = new Pitch(i.intValue());
			pitches.put(i, p);
			if (isDebugEnabled) {
				logger.debug("created and cached new pitch: " + p);
			}
		}
		return p;
	}

	/**
	 * The frequency of C0
	 */
	// public static final double C0_FQ = 16.3515978312876;
	public static final double C0_FQ = 8.175798915643707;

	static final double LOG2 = Math.log(2d);

	/*
	 * see
	 * http://www.myriad-online.com/resources/docs/harmony/english/microtone.htm
	 * For example, if we need a frequency Z of 310 Hz: Y = 1200 x
	 * log(310/16.3515978312876)/log(2) Y = 5093.72 Pitch[midiNumber=51
	 * frequency=155.56349186104043 cents=8186] Octave (N) = integer part of
	 * Y/1200 = 5093.72/1200 = 4 We subtract 4 x 1200 from 5093.72, which gives
	 * Y' = 293.72 Semitone S = integer part of 293.72 / 100 = 2. The note to
	 * insert is a D (1=C#, 2=D, 3=D#). We subtract 100 x 2 from 293.72. The
	 * result is 93.72, rounded to M = 94 cents We will have to insert a D, 4th
	 * octave, with a microtonal adjustment of 94 cents. We can also obtain the
	 * same frequency by using a D#, 4th octave, with a microtonal adjustment of
	 * (94-100) = -6 cents.
	 */
	/**
	 * 
	 * @param freq
	 * @return a Pitch with midiNumber and cents set
	 */
	public static Pitch createFromFrequency(double freq) {
		if (EQUAL_TEMPERAMENT.containsValue(freq)) {
			if (logger.isDebugEnabled()) {
				logger.debug("is equal temperament");
			}
			for (Map.Entry<Integer, Double> entry : EQUAL_TEMPERAMENT
					.entrySet()) {
				if (entry.getValue().equals(freq)) {
					return getPitch(entry.getKey().intValue());
				}
			}
		}
		if (frequencyCacheMap.containsKey(freq)) {
			logger.debug("is cached");
			return frequencyCacheMap.get(freq);
		}

		double totalCents = 1200.0 * Math.log(freq / C0_FQ) / LOG2;
		double octave = Math.round(totalCents / 1200.0);
		double semitoneCents = totalCents - (octave * 1200.0);
		double semitone = Math.round(semitoneCents / 100.0);
		double cents = Math.round(semitoneCents - (semitone * 100d));
		double bend = 8192d + Math.round(semitoneCents - (semitone * 100d));
		// The two bytes of the pitch bend message form a 14 bit number, 0 to
		// 16383.
		// The value 8192 (sent, LSB first, as 0x00 0x40), is centered, or
		// "no pitch bend."
		// The value 0 (0x00 0x00) means, "bend as low as possible," and,
		// similarly, 16383 (0x7F 0x7F) is to "bend as high as possible."
		// The exact range of the pitch bend is specific to the synthesizer.

		double note = (octave * 12d) + semitone;
		if (note > 127)
			note = 127;

		Pitch p = new Pitch((int) note);
		p.setCents((short) cents);
		p.setFrequency(freq);
		p.setPitchBend((short) bend);
		frequencyCacheMap.put(freq, p);

		if (logger.isDebugEnabled()) {
			logger.debug("p.cents= " + p.getCents());
			logger.debug("p.midiNumber= " + p.getMidiNumber());
			logger.debug("octave= " + octave);
			logger.debug("semitone= " + semitone);
		}
		return p;
	}

	private static final Map<Double, Pitch> frequencyCacheMap = new HashMap<Double, Pitch>();

	/**
	 * <p>
	 * You can get a frequency based on midi number.
	 * <p>
	 * For use in the Gof flyweight design pattern.
	 * </p>
	 */
	public static final Map<Integer, Double> EQUAL_TEMPERAMENT;
	static {
		EQUAL_TEMPERAMENT = new HashMap<Integer, Double>();
		for (int i = 0; i < 128; i++) {
			EQUAL_TEMPERAMENT.put(i, Pitch.midiFq(i));
		}
	}

	// public static List<Pitch> getPitches(String string) {
	// MIDIStringParser p = new MIDIStringParser();
	// return p.parseString(string).getPitches();
	// }
}
