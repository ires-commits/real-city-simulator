package it.realcitysimulator;

/** Deterministic rules; sensors and police AI will call this later. */
public final class TrafficLaw {
    public enum Violation { NONE, SPEEDING, RED_LIGHT }
    private TrafficLaw() {}
    public static Violation check(boolean crossedStopLine, IntersectionController.Signal light,
                                  double speedKmh, double limitKmh) {
        if (crossedStopLine && light == IntersectionController.Signal.RED) return Violation.RED_LIGHT;
        if (speedKmh > limitKmh + 5.0) return Violation.SPEEDING;
        return Violation.NONE;
    }
    public static int fineEuros(Violation violation) {
        return switch (violation) {
            case NONE -> 0;
            case SPEEDING -> 100;
            case RED_LIGHT -> 150;
        };
    }
}
