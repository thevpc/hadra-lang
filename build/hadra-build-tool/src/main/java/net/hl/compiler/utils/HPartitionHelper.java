package net.hl.compiler.utils;

import net.thevpc.jeep.JCompilerLog;
import net.thevpc.jeep.JToken;
import net.hl.lang.ComparableRange;

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
            log.jerror("S012", null, token, "literal already matched : " + o);
            return;
        }
        for (ComparableRange<T> range : ranges) {
            if (range.contains(o)) {
                log.jerror("S012", null, token, "literal already matched : " + o);
                return;
            }
        }
        set.add(o);
    }

    public void add(ComparableRange<T> r, JToken token) {
        for (T t : set) {
            if (r.contains(t)) {
                log.jerror("S012", null, token, "literal range already matched totally or partially : " + r);
                return;
            }
        }
        for (ComparableRange<T> range : ranges) {
            ComparableRange<T> z = range.intersect(r);
            if (z != null && !z.isEmpty()) {
                log.jerror("S012", null, token, "literal range already matched totally or partially : " + r);
            }
        }
        ranges.add(r);
    }

}
