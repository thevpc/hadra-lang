package net.vpc.hadralang.compiler.utils;

import net.vpc.common.jeep.JCompilerLog;
import net.vpc.common.jeep.JToken;
import net.vpc.hadralang.stdlib.ComparableRange;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HPartitionHelper<T extends Comparable> {
    private List<ComparableRange<T>> ranges = new ArrayList<>();
    private Set<T> set = new HashSet<>();
    private JCompilerLog log;

    public HPartitionHelper(JCompilerLog log) {
        this.log = log;
    }

    public void add(T o, JToken token) {
        if (set.contains(o)) {
            log.error("S012", null, "literal already matched : " + o, token);
            return;
        }
        for (ComparableRange<T> range : ranges) {
            if (range.contains(o)) {
                log.error("S012", null, "literal already matched : " + o, token);
                return;
            }
        }
        set.add(o);
    }

    public void add(ComparableRange<T> r, JToken token) {
        for (T t : set) {
            if (r.contains(t)) {
                log.error("S012", null, "literal range already matched totally or partially : " + r, token);
                return;
            }
        }
        for (ComparableRange<T> range : ranges) {
            ComparableRange<T> z = range.intersect(r);
            if (z != null && !z.isEmpty()) {
                log.error("S012", null, "literal range already matched totally or partially : " + r, token);
            }
        }
        ranges.add(r);
    }

}
