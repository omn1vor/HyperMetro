package metro;

import java.util.*;

public class Line {
    private final String name;
    private Station head;

    public Line(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station getHead() {
        return head;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return name.equals(line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return getName();
    }

    public String stationList() {
        StringBuilder sb = new StringBuilder("depot");
        sb.append(System.lineSeparator());
        Map<Station, Route> stations = getHead().getLocalRoutes();
        stations.keySet().forEach(st -> sb.append(st.getNameWithTransfers()));
        sb.append("depot");
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    public void addStation(Station station) {
        if (head == null) {
            head = station;
        }
    }

    public void addHeadStation(Station station) {
        if (head != null) {
            head.addPrev(station);
            station.addNext(head);
        }
        head = station;
    }

    public void removeStation(String name) {
        Station station = getByName(name);
        if (station == null) {
            return;
        }

        List<Station> prev = station.getPrev();
        List<Station> next = station.getNext();

        if (prev.isEmpty()) {
            head = next.isEmpty() ? null : next.get(0);
        }

        for (Station prevSt : prev) {
            prevSt.removeNext(station);
            for (Station nextSt : next) {
                prevSt.addNext(nextSt);
            }

        }
        for (Station nextSt : next) {
            nextSt.removePrev(station);
            for (Station prevSt : prev) {
                nextSt.addPrev(prevSt);
            }
        }
    }

    public Station getByName(String name) {
        Map<Station, Route> stations = getHead().getLocalRoutes();
        return stations.keySet().stream()
                .filter(st -> name.equals(st.getName()))
                .findAny().orElse(null);
    }


}
