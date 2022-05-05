package metro;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Util {
    private static Station getBufferedStation(String name, Line line, Map<Line, Map<String, Station>> buffer) {
        buffer.putIfAbsent(line, new HashMap<>());
        Map<String, Station> stations = buffer.get(line);
        if (stations.containsKey(name)) {
            return stations.get(name);
        }
        Station station = new Station(name, line);
        stations.put(name, station);
        return station;
    }

    public static void importLines(String filename, Metro metro) {
        Map<Line, Map<String, Station>> bufferedStations = new HashMap<>();
        Map<String, List<ImportStation>> importedLines = null;
        try {
            JsonReader reader = new JsonReader(Files.newBufferedReader(Path.of(filename)));
            Type importObjectType = new TypeToken<Map<String, List<ImportStation>>>() {}.getType();
            importedLines = new Gson().fromJson(reader, importObjectType);
        } catch (IOException e) {
            System.out.println("Error! Such a file doesn't exist!");
        } catch (JsonSyntaxException e) {
            System.out.println("Incorrect file");
        }
        if (importedLines == null) {
            return;
        }
        for (String lineName : importedLines.keySet()) {
            Line line = metro.addLineIfAbsent(lineName);
            List<ImportStation> importsStations = importedLines.get(lineName);
            for (ImportStation importStation : importsStations) {
                Station station = getBufferedStation(importStation.name, line, bufferedStations);
                station.setTimeToNext(importStation.time);
                for (String name : importStation.next) {
                    station.addNext(getBufferedStation(name, line, bufferedStations));
                }
                for (String name : importStation.prev) {
                    station.addPrev(getBufferedStation(name, line, bufferedStations));
                }
                for (ImportTransfer transfer : importStation.transfer) {
                    Line transferLine = metro.addLineIfAbsent(transfer.line);
                    station.addTransfer(getBufferedStation(transfer.station, transferLine, bufferedStations));
                }
                line.addStation(station);
            }
        }
    }

    static class ImportStation {
        String name;
        String[] prev;
        String[] next;
        ImportTransfer[] transfer;
        int time;
    }

    static class ImportTransfer {
        String line;
        String station;
    }

}
