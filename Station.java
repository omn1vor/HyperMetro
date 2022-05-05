package metro;

import java.util.*;
import java.util.stream.Collectors;

public class Station {
    private final int TRANSFER_TIME = 5;
    private final String name;
    private final Line line;
    private final List<Station> transfers;
    private int timeToNext;
    private final List<Station> prev;
    private final List<Station> next;

    public Station(String name, Line line) {
        this.name = name;
        this.line = line;
        transfers = new ArrayList<>();
        next = new ArrayList<>();
        prev = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Station> getPrev() {
        return prev;
    }

    public List<Station> getNext() {
        return next;
    }

    public Line getLine() {
        return line;
    }

    public int getTimeToNext() {
        return timeToNext;
    }

    public void setTimeToNext(int timeToNext) {
        this.timeToNext = timeToNext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(name, station.name) && Objects.equals(line, station.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, line);
    }

    @Override
    public String toString() {
        return getName();
    }

    public void addNext(Station station) {
        next.add(station);
    }

    public void addPrev(Station station) {
        prev.add(station);
    }

    public void removeNext(Station station) {
        next.remove(station);
    }

    public void removePrev(Station station) {
        prev.remove(station);
    }

    public void addTransfer(Station station) {
        transfers.add(station);
    }

    public List<Station> getNeighbors() {
        return getNeighborWays().stream()
                .map(way -> way.station)
                .collect(Collectors.toList());
    }

    public List<WayToStation> getNeighborWays() {
        List<WayToStation> ways = getLocalNeighborWays();

        transfers.forEach(st -> {
            st.getPrev().forEach(s -> ways.add(new WayToStation(s, s.getTimeToNext() + TRANSFER_TIME)));
            st.getNext().forEach(s -> ways.add(new WayToStation(s, st.getTimeToNext() + TRANSFER_TIME)));
        });
        return ways;
    }

    public List<WayToStation> getLocalNeighborWays() {
        List<WayToStation> ways = new ArrayList<>();

        getPrev().forEach(st -> ways.add(new WayToStation(st, st.getTimeToNext())));
        getNext().forEach(st -> ways.add(new WayToStation(st, getTimeToNext())));

        return ways;
    }

    public String getFullName() {
        return String.format("%s (%s line)", getName(), line.getName());
    }

    public String getNameWithTransfers() {
        StringBuilder sb = new StringBuilder(getName());
        if (!transfers.isEmpty()) {
            sb.append(transfers.stream()
                    .map(Station::getFullName)
                    .collect(Collectors.joining(" - ", " - ", "")));
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    public Station getTransitStation(Line line) {
        return transfers.stream()
                .filter(s -> s.getLine().equals(line))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("There is no transfer to station %s from line %s",
                                this, line)));
    }

    public Map<Station, Route> getAllRoutes() {
        return getRoutes(true);
    }

    public Map<Station, Route> getLocalRoutes() {
        return getRoutes(false);
    }

    private Map<Station, Route> getRoutes(boolean global) {
        Map<Station, Route> routes = new HashMap<>();
        Set<Station> queue = new HashSet<>();
        Set<Station> visited = new HashSet<>();
        queue.add(this);
        routes.put(this, new Route(0, List.of(this)));

        while (!queue.isEmpty()) {
            Station closest = queue.stream()
                    .min(Comparator.comparingInt(st -> routes.getOrDefault(st, Route.empty()).time))
                    .get();
            List<WayToStation> ways = global ? closest.getNeighborWays() : closest.getLocalNeighborWays();
            ways.forEach(way -> {
                int oldTime = routes.getOrDefault(way.station, Route.empty()).time;
                int time = routes.get(closest).time + way.time;
                if (time < oldTime) {
                    List<Station> newPath = new ArrayList<>(routes.get(closest).path);
                    newPath.add(way.station);
                    routes.put(way.station, new Route(time, newPath));
                }
                if (!visited.contains(way.station)) {
                    queue.add(way.station);
                }
            });
            queue.remove(closest);
            visited.add(closest);
        }

        return routes;
    }
}

class Route {
    int time;
    List<Station> path;

    public Route(int time, List<Station> path) {
        this.time = time;
        this.path = path;
    }

    static Route empty() {
        return new Route(Integer.MAX_VALUE, List.of());
    }
}