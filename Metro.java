package metro;

import java.util.*;

public class Metro {
    List<Line> lines = new ArrayList<>();

    public void printLine(String lineName) {
        Line line = getLine(lineName);
        System.out.print(line.stationList());
    }

    public void appendStation(String lineName, String stationName) {
        Line line = addLineIfAbsent(lineName);
        line.addStation(new Station(stationName, line));
    }

    public void addHeadStation(String lineName, String stationName) {
        Line line = addLineIfAbsent(lineName);
        line.addHeadStation(new Station(stationName, line));
    }

    public void removeStation(String lineName, String stationName) {
        Line line = getLine(lineName);
        line.removeStation(stationName);
    }

    public void connectStations(String lineName1, String stationName1, String lineName2, String stationName2) {
        Line line1 = getLine(lineName1);
        Station station1 = line1.getByName(stationName1);
        Line line2 = getLine(lineName2);
        Station station2 = line2.getByName(stationName2);

        station1.addTransfer(station2);
        station2.addTransfer(station1);
    }

    public void printRoute(String lineName1, String stationName1, String lineName2, String stationName2) {
        // for backward compatibility
        Line line1 = getLine(lineName1);
        Station station1 = line1.getByName(stationName1);
        Line line2 = getLine(lineName2);
        Station station2 = line2.getByName(stationName2);

        Map<Station, Route> routes = station1.getAllRoutes();
        List<Station> path = routes.getOrDefault(station2, Route.empty()).path;
        if (path.isEmpty()) {
            System.out.println("No such route!");
            return;
        }

        Station prevStation = path.get(0);
        for (Station station : path) {
            Line line = station.getLine();
            Line prevLine = prevStation.getLine();
            if (!line.equals(prevLine)) {
                System.out.printf("Transition to line %s%n", line);
                System.out.println(prevStation.getTransitStation(line));
            }
            System.out.println(station.getName());
            prevStation = station;
        }
    }

    public void printFastestRoute(String lineName1, String stationName1, String lineName2, String stationName2) {
        Line line1 = getLine(lineName1);
        Station station1 = line1.getByName(stationName1);
        Line line2 = getLine(lineName2);
        Station station2 = line2.getByName(stationName2);

        Map<Station, Route> routes = station1.getAllRoutes();
        List<Station> path = routes.getOrDefault(station2, Route.empty()).path;
        if (path.isEmpty()) {
            System.out.println("No such route!");
            return;
        }

        Station prevStation = path.get(0);
        for (Station station : path) {
            Line line = station.getLine();
            Line prevLine = prevStation.getLine();
            if (!line.equals(prevLine)) {
                System.out.printf("Transition to line %s%n", line);
                System.out.println(prevStation.getTransitStation(line));
            }
            System.out.println(station.getName());
            prevStation = station;
        }
        System.out.printf("Total: %d minutes in the way%n", routes.get(station2).time);
    }

    public boolean importLines(String filename) {
        Util.importLines(filename, this);
        return !lines.isEmpty();
    }

    Line addLineIfAbsent(String name) {
        return findLine(name)
                .orElseGet(() -> {
                    Line newLine = new Line(name);
                    lines.add(newLine);
                    return newLine;
                });
    }

    private Line getLine(String name) {
        return findLine(name)
                .orElseThrow(() -> new IllegalArgumentException("Invalid line name"));
    }

    private Optional<Line> findLine(String name) {
        return lines.stream()
                .filter(i -> name.equalsIgnoreCase(i.getName()))
                .findAny();
    }
}

class WayToStation {
    Station station;
    int time;

    public WayToStation(Station station, int time) {
        this.station = station;
        this.time = time;
    }
}