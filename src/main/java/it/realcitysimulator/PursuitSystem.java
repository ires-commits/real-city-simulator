package it.realcitysimulator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Server-side gameplay-independent wanted-level state machine. */
public final class PursuitSystem {
    public enum Status { CLEAR, WANTED, PURSUIT, STOPPED }
    public record Snapshot(Status status, int wantedLevel, int fineEuros, int quietTicks) {}
    private static final int QUIET_TICKS_TO_CLEAR = 20 * 90;
    private final Map<String, Case> cases = new HashMap<>();
    private static final class Case {
        Status status = Status.CLEAR;
        int wantedLevel;
        int fineEuros;
        int quietTicks;
    }
    public Snapshot report(String playerId, TrafficLaw.Violation violation, boolean detected) {
        Case c = get(playerId);
        if (!detected || violation == TrafficLaw.Violation.NONE) return snapshot(c);
        c.fineEuros = Math.addExact(c.fineEuros, TrafficLaw.fineEuros(violation));
        c.wantedLevel = Math.min(5, c.wantedLevel + (violation == TrafficLaw.Violation.RED_LIGHT ? 2 : 1));
        c.status = Status.WANTED;
        c.quietTicks = 0;
        return snapshot(c);
    }
    public Snapshot patrolSpotted(String playerId) {
        Case c = get(playerId);
        if (c.status == Status.WANTED || c.status == Status.PURSUIT) {
            c.status = Status.PURSUIT;
            c.quietTicks = 0;
        }
        return snapshot(c);
    }
    public Snapshot evadeTick(String playerId, boolean visibleToPolice) {
        Case c = get(playerId);
        if (c.status == Status.WANTED || c.status == Status.PURSUIT) {
            c.quietTicks = visibleToPolice ? 0 : c.quietTicks + 1;
            if (c.quietTicks >= QUIET_TICKS_TO_CLEAR) {
                c.status = Status.CLEAR;
                c.wantedLevel = 0;
            }
        }
        return snapshot(c);
    }
    public Snapshot stop(String playerId) {
        Case c = get(playerId);
        if (c.status == Status.WANTED || c.status == Status.PURSUIT) {
            c.status = Status.STOPPED;
            c.wantedLevel = 0;
            c.quietTicks = 0;
        }
        return snapshot(c);
    }
    public Snapshot payFine(String playerId) {
        Case c = get(playerId);
        if (c.status == Status.STOPPED || c.status == Status.CLEAR) {
            c.fineEuros = 0;
            c.status = Status.CLEAR;
        }
        return snapshot(c);
    }
    public Snapshot status(String playerId) { return snapshot(get(playerId)); }
    private Case get(String id) {
        if (Objects.requireNonNull(id).isBlank()) throw new IllegalArgumentException("Blank player id");
        return cases.computeIfAbsent(id, ignored -> new Case());
    }
    private static Snapshot snapshot(Case c) {
        return new Snapshot(c.status, c.wantedLevel, c.fineEuros, c.quietTicks);
    }
}
