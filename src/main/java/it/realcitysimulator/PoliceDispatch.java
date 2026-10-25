package it.realcitysimulator;

import java.util.*;

/** Assigns fastest road-route police patrol to suspect. */
public final class PoliceDispatch {
    public record Patrol(String id, String roadNode, boolean available) {}
    public record Assignment(String patrolId, List<String> route) {}
    public static Optional<Assignment> dispatch(RoadNetwork roads, Collection<Patrol> patrols, String suspectNode) {
        Objects.requireNonNull(roads);
        roads.node(suspectNode);
        Assignment winner = null;
        double shortestTime = Double.POSITIVE_INFINITY;
        for (Patrol patrol : patrols) {
            if (!patrol.available()) continue;
            List<String> route = roads.fastestRoute(patrol.roadNode(), suspectNode);
            if (route.isEmpty()) continue;
            double travelTime = roads.routeSeconds(route);
            if (travelTime < shortestTime) {
                shortestTime = travelTime;
                winner = new Assignment(patrol.id(), route);
            }
        }
        return Optional.ofNullable(winner);
    }
    private PoliceDispatch() {}
}
