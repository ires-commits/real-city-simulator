package it.realcitysimulator;

/** Two-phase intersection: never grants conflicting green lights. */
public final class IntersectionController {
    public enum Signal { GREEN, YELLOW, RED }
    private static final int GREEN_TICKS = 200;
    private static final int YELLOW_TICKS = 40;
    private static final int ALL_RED_TICKS = 20;
    private int phase;
    private int elapsed;
    public void tick() {
        elapsed++;
        int limit = switch (phase) {
            case 0, 3 -> GREEN_TICKS;
            case 1, 4 -> YELLOW_TICKS;
            default -> ALL_RED_TICKS;
        };
        if (elapsed >= limit) { phase = (phase + 1) % 6; elapsed = 0; }
    }
    public Signal northSouth() {
        return switch (phase) {
            case 0 -> Signal.GREEN;
            case 1 -> Signal.YELLOW;
            default -> Signal.RED;
        };
    }
    public Signal eastWest() {
        return switch (phase) {
            case 3 -> Signal.GREEN;
            case 4 -> Signal.YELLOW;
            default -> Signal.RED;
        };
    }
}
