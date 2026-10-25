package it.realcitysimulator;

/** Server-side traffic light state machine. Durations are in Minecraft ticks. */
public final class TrafficLight {
    public enum State { GREEN, YELLOW, RED }
    private State state = State.GREEN;
    private int elapsedTicks;
    public void tick() {
        elapsedTicks++;
        int duration = switch (state) {
            case GREEN -> 600;
            case YELLOW -> 60;
            case RED -> 400;
        };
        if (elapsedTicks >= duration) {
            state = switch (state) {
                case GREEN -> State.YELLOW;
                case YELLOW -> State.RED;
                case RED -> State.GREEN;
            };
            elapsedTicks = 0;
        }
    }
    public State state() { return state; }
    public int elapsedTicks() { return elapsedTicks; }
    public boolean canEnterIntersection() { return state == State.GREEN; }
}
