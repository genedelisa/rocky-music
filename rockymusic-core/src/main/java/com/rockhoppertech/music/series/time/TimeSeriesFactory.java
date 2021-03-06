package com.rockhoppertech.music.series.time;

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

import com.rockhoppertech.collections.CircularArrayList;
import com.rockhoppertech.collections.CircularList;
import com.rockhoppertech.collections.ListUtils;
import com.rockhoppertech.music.DurationParser;
import com.rockhoppertech.music.Timed;

/**
 * The Factory for {@code TimeSeries} instances.
 * 
 * @author <a href="mailto:gene@rockhoppertech.com">Gene De Lisa</a>
 * 
 */
public class TimeSeriesFactory {

    private static double defaultDuration = 1d;
    static Logger logger = LoggerFactory.getLogger(TimeSeriesFactory.class);

    /**
     * Create n events with default duraiton of 1, and start increment of 1.
     * 
     * @param n
     *            number of events to create.
     * @return a {@code TimeSeries}
     */
    public static TimeSeries create(int n) {
        return create(n, defaultDuration, 1d);
    }

    /**
     * Create n events, each with specified duration, and start increment of 1.
     * 
     * @param n
     *            number of events to create.
     * @param duration
     *            the duration each event will have
     * @return a {@code TimeSeries}
     */
    public static TimeSeries create(int n, double duration) {
        return create(n, duration, 1d);
    }

    /**
     * Create n events, each with duration, and with the start time incremented
     * by startTimeIncrement.
     * 
     * @param n
     *            number of events to create.
     * @param duration
     *            the duration each event will have
     * @param startTimeIncrement
     *            the distance between start beats
     * @return a {@code TimeSeries}
     */
    public static TimeSeries create(int n, double duration,
            double startTimeIncrement) {
        TimeSeries timeSeries = new TimeSeries();
        double startTime = 1d;
        for (int i = 0; i < n; i++) {
            timeSeries.add(new TimeEvent(startTime, duration));
            startTime += startTimeIncrement;
        }
        return timeSeries;
    }

    public static TimeSeries create(int n, CircularList<Double> durations,
            double startTimeIncrement) {
        TimeSeries timeSeries = new TimeSeries();
        double startTime = 1d;
        for (int i = 0; i < n; i++) {
            timeSeries.add(new TimeEvent(startTime, durations.next()));
            startTime += startTimeIncrement;
        }
        return timeSeries;
    }

    // nah. there is a varargs version below
    // public static TimeSeries create(int n, CircularList<Double> durations) {
    // TimeSeries timeSeries = new TimeSeries();
    // double startTime = 1d;
    // double dur = 0d;
    // for (int i = 0; i < n; i++) {
    // dur = durations.next();
    // timeSeries.add(new TimeEvent(startTime, dur));
    // startTime += dur;
    // }
    // return timeSeries;
    // }

    /**
     * Create an event for each duration. The start times will be sequential.
     * The durations wrap.
     * 
     * @param durations
     * @return a {@code TimeSeries}
     */
    public static TimeSeries create(Double... durations) {
        CircularList<Double> durMask = new CircularArrayList<Double>();
        for (Double i : durations) {
            durMask.add(i);
        }
        TimeSeries timeSeries = new TimeSeries();
        double startTime = 1d;
        double dur = 0d;
        int n = durMask.size();
        for (int i = 0; i < n; i++) {
            dur = durMask.next();
            timeSeries.add(new TimeEvent(startTime, dur));
            startTime += dur;
        }
        return timeSeries;
    }

    // can't have both Double... and Double[]
    // public static TimeSeries create(Double[] durations) {
    // TimeSeries timeSeries = new TimeSeries();
    // double startTime = 1d;
    // double dur = 0d;
    // int n = durations.length;
    // for (int i = 0; i < n; i++) {
    // dur = durations[i];
    // timeSeries.add(new TimeEvent(startTime, dur));
    // startTime += dur;
    // }
    // return timeSeries;
    // }

    public static TimeSeries create(List<Double> durations) {
        TimeSeries timeSeries = new TimeSeries();
        double startTime = 1d;
        for (double dur : durations) {
            timeSeries.add(new TimeEvent(startTime, dur));
            startTime += dur;
        }
        return timeSeries;
    }

    public static TimeSeries create(CircularList<Double> startTimes,
            List<Double> durations) {
        // if (startTimes.size() != durations.size()) {
        // throw new IllegalArgumentException(String.format(
        // "start and duration list sizes must match %d != %d",
        // startTimes.size(), durations.size()));
        // }
        TimeSeries timeSeries = new TimeSeries();
        for (double dur : durations) {
            timeSeries.add(new TimeEvent(startTimes.next(), dur));
        }
        return timeSeries;
    }

    /**
     * Create n events sequentially. The durations will wrap.
     * 
     * @param n
     * @param durations
     * @return a {@code TimeSeries}
     */
    public static TimeSeries create(int n, Double... durations) {
        CircularList<Double> durList = new CircularArrayList<Double>();
        for (Double i : durations) {
            durList.add(i);
        }
        TimeSeries timeSeries = new TimeSeries();
        double startTime = 1d;
        double dur = 0d;
        for (int i = 0; i < n; i++) {
            dur = durList.next();
            timeSeries.add(new TimeEvent(startTime, dur));
            startTime += dur;
        }
        return timeSeries;
    }

    public static TimeSeries create(int n, CircularList<Double> durations,
            CircularList<Double> gaps) {
        TimeSeries timeSeries = new TimeSeries();
        double startTime = 1d;
        for (int i = 0; i < n; i++) {
            timeSeries.add(new TimeEvent(startTime, durations.next()));
            startTime += gaps.next();
        }
        return timeSeries;
    }

    // public static TimeSeries create(int n, CircularList<Double> durations) {
    // return create(n, durations, 1d);
    // }

    public static TimeSeries createRepeated(final TimeSeries original,
            final CircularList<Integer> mask) {
        TimeSeries repeated = new TimeSeries();
        mask.reset();
        int maskValue = 0;
        for (final Timed t : original) {
            maskValue = mask.next();
            logger.debug("mask value {}", maskValue);
            for (int i = 0; i < maskValue; i++) {
                // the start beat will be changed below
                TimeEvent clone = new TimeEvent(t.getStartBeat(), t
                        .getDuration());
                repeated.add(clone);
            }
        }
        return repeated.sequential();
    }

    public static TimeSeries createFromRepeatMask(List<Double> durations,
            final CircularList<Integer> mask) {
        TimeSeries repeated = new TimeSeries();
        mask.reset();
        int maskValue = 0;
        double startBeat = 1d;
        for (final Double dur : durations) {
            maskValue = mask.next();
            for (int i = 0; i < maskValue; i++) {
                TimeEvent clone = new TimeEvent(startBeat, dur);
                startBeat += dur;
                repeated.add(clone);
            }
        }
        return repeated;
    }

    public static TimeSeries createFromRepeatMask(String startTimes,
            String durations, final String repeats) {

        if (durations == null || durations.equals("")) {

        }
        if (repeats == null || repeats.equals("")) {

        }

        List<Double> durs = ListUtils.stringToDoubleList(durations);
        CircularList<Integer> mask = ListUtils
                .stringToIntegerCircularList(repeats);

        if (startTimes == null || startTimes.equals("")) {
            logger.debug("no start times");
            return createFromRepeatMask(durs, mask);
        }

        logger.debug("using start times");
        CircularList<Double> starts = ListUtils
                .stringToDoubleCircularList(startTimes);
        return createFromRepeatMask(starts, durs, mask);

    }

    public static TimeSeries createFromRepeatMask(
            CircularList<Double> startTimes,
            List<Double> durations, final CircularList<Integer> mask) {

        TimeSeries timeSeries = new TimeSeries();
        startTimes.reset();
        mask.reset();
        int maskValue = 0;
        for (final Double dur : durations) {
            maskValue = mask.next();
            for (int i = 0; i < maskValue; i++) {
                TimeEvent clone = new TimeEvent(startTimes.next(), dur);
                timeSeries.add(clone);
            }
        }
        return timeSeries;
    }

    /**
     * Create a {@code TimeSeries} from a duration string. dwhqestxo and dots.
     * 
     * @param durations
     *            duration string
     * @return a {@code TimeSeries}
     * @see DurationParser
     */
    public static TimeSeries createFromDurationString(String durations) {
        return DurationParser.getDurationAsTimeSeries(durations);
    }

    public static TimeSeries createFromDurationString(TimeSeries ts,
            String durations) {
        return DurationParser.getDurationAsTimeSeries(ts, durations);
    }

    public static double[] getStartTimeIntervals(TimeSeries timeSeries) {
        int len = timeSeries.getSize();
        double[] intervals = new double[len - 1];
        for (int i = 0; i < intervals.length; i++) {
            Timed i1 = (Timed) timeSeries.get(i);
            Timed i2 = (Timed) timeSeries.get(i + 1);
            intervals[i] = i2.getStartBeat() - i1.getStartBeat();
        }
        return intervals;
    }

    public static void setStartTimeIntervals(TimeSeries timeSeries,
            Double[] intervals) {
        TimeSeriesFactory.setStartTimeIntervals(timeSeries, intervals, 1d);
    }

    public static void setStartTimeIntervals(TimeSeries timeSeries,
            Double[] intervals, double start) {
        CircularList<Double> ci = new CircularArrayList<Double>(intervals);
        int len = timeSeries.getSize();
        timeSeries.setStart(start);
        for (int i = 0; i < len - 1; i += 2) {
            Timed te1 = (Timed) timeSeries.get(i);
            Timed te2 = (Timed) timeSeries.get(i + 1);
            te2.setStartBeat(te1.getStartBeat() + ci.next());
        }
    }

    /**
     * Create a new series from the start times and durations.
     * <p>
     * The durations are a duration string.
     * @param startTimes doubles in a string. comma delimited or not
     * @param durations duration string
     * @return a new {@code TimeSeries}
     * @see DurationParser
     */
    public static TimeSeries create(String startTimes, String durations) {
        CircularList<Double> starts = ListUtils
                .stringToDoubleCircularList(startTimes);

        List<Double> list = DurationParser.getDurationsAsList(durations);
        CircularList<Double> durs = new CircularArrayList<Double>(list);

        logger.debug("starts {}", starts);
        logger.debug("durs {}", durs);

        return TimeSeriesFactory.create(starts, durs);
    }
}
