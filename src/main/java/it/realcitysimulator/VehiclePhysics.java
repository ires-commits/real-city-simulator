package it.realcitysimulator;

/** Engine-independent 2D vehicle dynamics. Distances are Minecraft blocks (metres). */
public final class VehiclePhysics {
    public record State(double x, double z, double headingRadians, double speedMetresPerSecond) {}
    public record Controls(double throttle, double steering, boolean brake) {}
    private static final double MAX_FORWARD = 22.22;
    private static final double MAX_REVERSE = 4.17;
    private static final double ACCELERATION = 4.0;
    private static final double BRAKING = 9.0;
    private static final double DRAG = 0.65;
    private static final double MAX_TURN_RATE = 0.8;
    private VehiclePhysics() {}
    public static State step(State state, Controls input, double dtSeconds) {
        if (!Double.isFinite(dtSeconds) || dtSeconds <= 0 || dtSeconds > 0.1)
            throw new IllegalArgumentException("Expected simulation step between 0 and 0.1 seconds");
        double throttle = clamp(input.throttle(), -1, 1);
        double steer = clamp(input.steering(), -1, 1);
        double speed = state.speedMetresPerSecond();
        double acceleration = throttle * ACCELERATION - DRAG * speed;
        if (input.brake()) {
            if (Math.abs(speed) <= BRAKING * dtSeconds) speed = 0;
            else speed -= Math.copySign(BRAKING * dtSeconds, speed);
        } else speed += acceleration * dtSeconds;
        speed = clamp(speed, -MAX_REVERSE, MAX_FORWARD);
        if (Math.abs(speed) < 0.02 && throttle == 0) speed = 0;
        double turnFactor = Math.min(1.0, Math.abs(speed) / 5.0);
        double heading = state.headingRadians() + steer * MAX_TURN_RATE * turnFactor
            * Math.signum(speed) * dtSeconds;
        return new State(state.x() + Math.sin(heading) * speed * dtSeconds,
                         state.z() + Math.cos(heading) * speed * dtSeconds,
                         heading, speed);
    }
    public static double speedKmh(State state) { return Math.abs(state.speedMetresPerSecond()) * 3.6; }
    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
