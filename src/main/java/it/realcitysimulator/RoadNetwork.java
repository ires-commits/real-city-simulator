package it.realcitysimulator;

import java.util.*;

/** Directed, weighted road graph. */
public final class RoadNetwork {
    public record Node(String id, double x, double z) {}
    public record Edge(String destination, double distance, double speedLimitKmh) {}
    private final Map<String, Node> nodes = new LinkedHashMap<>();
    private final Map<String, List<Edge>> edges = new LinkedHashMap<>();
    public void addNode(String id, double x, double z) {
        if (id == null || id.isBlank() || !Double.isFinite(x) || !Double.isFinite(z))
            throw new IllegalArgumentException("Invalid road node");
        if (nodes.putIfAbsent(id, new Node(id, x, z)) != null)
            throw new IllegalArgumentException("Duplicate node: " + id);
        edges.put(id, new ArrayList<>());
    }
    public void addRoad(String from, String to, double speedLimitKmh) {
        Node a = require(from), b = require(to);
        if (from.equals(to) || !Double.isFinite(speedLimitKmh) || speedLimitKmh <= 0)
            throw new IllegalArgumentException("Invalid road");
        double distance = Math.hypot(a.x() - b.x(), a.z() - b.z());
        if (distance == 0) throw new IllegalArgumentException("Zero length road");
        edges.get(from).add(new Edge(to, distance, speedLimitKmh));
    }
    public List<String> fastestRoute(String start, String goal) {
        require(start); require(goal);
        Map<String, Double> best = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        record Step(String id, double seconds) {}
        PriorityQueue<Step> queue = new PriorityQueue<>(Comparator.comparingDouble(Step::seconds));
        best.put(start, 0.0);
        queue.add(new Step(start, 0));
        while (!queue.isEmpty()) {
            Step current = queue.remove();
            if (current.seconds() > best.getOrDefault(current.id(), Double.POSITIVE_INFINITY)) continue;
            if (current.id().equals(goal)) break;
            for (Edge edge : edges.get(current.id())) {
                double time = current.seconds() + edge.distance() / (edge.speedLimitKmh() / 3.6);
                if (time < best.getOrDefault(edge.destination(), Double.POSITIVE_INFINITY)) {
                    best.put(edge.destination(), time);
                    previous.put(edge.destination(), current.id());
                    queue.add(new Step(edge.destination(), time));
                }
            }
        }
        if (!best.containsKey(goal)) return List.of();
        LinkedList<String> route = new LinkedList<>();
        for (String at = goal; at != null; at = previous.get(at)) route.addFirst(at);
        return List.copyOf(route);
    }
    public double routeSeconds(List<String> route) {
        double seconds = 0;
        for (int i = 1; i < route.size(); i++) {
            String from = route.get(i - 1), to = route.get(i);
            require(from); require(to);
            Edge edge = edges.get(from).stream().filter(e -> e.destination().equals(to))
                .min(Comparator.comparingDouble(e -> e.distance() / e.speedLimitKmh()))
                .orElseThrow(() -> new IllegalArgumentException("Not a road: " + from + " -> " + to));
            seconds += edge.distance() / (edge.speedLimitKmh() / 3.6);
        }
        return seconds;
    }
    public Node node(String id) { return require(id); }
    public int nodeCount() { return nodes.size(); }
    private Node require(String id) {
        Node n = nodes.get(id);
        if (n == null) throw new IllegalArgumentException("Unknown node: " + id);
        return n;
    }
}
