package metro;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Command {
    protected Metro metro;
    protected int parametersCount;
    protected Matcher matcher;

    public Command(Metro metro) {
        this.metro = metro;
    }

    public abstract void execute(String input);

    public static Command newCommand(String commandName, Metro metro) {
        switch (commandName) {
            case "output":
                return new OutputCommand(metro);
            case "append":
                return new AppendCommand(metro);
            case "add-head":
                return new AddHeadCommand(metro);
            case "remove":
                return new RemoveCommand(metro);
            case "connect":
                return new ConnectCommand(metro);
            case "route":
                return new RouteCommand(metro);
            case "fastest-route":
                return new FastestRouteCommand(metro);
            default:
                throw new IllegalArgumentException("Invalid command");
        }
    }

    public static String getParametersPattern(int count) {
        return "/([\\w-]+)" + " (\"[\\w&-\\.\\s]+\"|[\\w&-\\.]+)".repeat(count);
    }

    protected void parseParameters(String input) {
        Pattern pattern = Pattern.compile(Command.getParametersPattern(parametersCount));
        matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            System.out.println("Invalid command");
            matcher = null;
        }
    }

    protected String getParameter(int parNum) {
        return matcher.group(parNum).replace("\"", "");
    }
}

class OutputCommand extends Command {

    public OutputCommand(Metro metro) {
        super(metro);
        parametersCount = 1;
    }

    @Override
    public void execute(String input) {
        parseParameters(input);
        if (matcher == null) {
            return;
        }
        metro.printLine(getParameter(2));
    }
}

class AppendCommand extends Command {

    public AppendCommand(Metro metro) {
        super(metro);
        parametersCount = 2;
    }

    @Override
    public void execute(String input) {
        parseParameters(input);
        if (matcher == null) {
            return;
        }
        String line = getParameter(2);
        String station = getParameter(3);
        metro.appendStation(line, station);
    }
}

class AddHeadCommand extends Command {

    public AddHeadCommand(Metro metro) {
        super(metro);
        parametersCount = 2;
    }

    @Override
    public void execute(String input) {
        parseParameters(input);
        if (matcher == null) {
            return;
        }
        String line = getParameter(2);
        String station = getParameter(3);
        metro.addHeadStation(line, station);
    }
}

class RemoveCommand extends Command {

    public RemoveCommand(Metro metro) {
        super(metro);
        parametersCount = 2;
    }

    @Override
    public void execute(String input) {
        parseParameters(input);
        if (matcher == null) {
            return;
        }
        String line = getParameter(2);
        String station = getParameter(3);
        metro.removeStation(line, station);
    }
}

class ConnectCommand extends Command {

    public ConnectCommand(Metro metro) {
        super(metro);
        parametersCount = 4;
    }

    @Override
    public void execute(String input) {
        parseParameters(input);
        if (matcher == null) {
            return;
        }
        String line1 = getParameter(2);
        String station1 = getParameter(3);
        String line2 = getParameter(4);
        String station2 = getParameter(5);
        metro.connectStations(line1, station1, line2, station2);
    }
}

class RouteCommand extends Command {

    public RouteCommand(Metro metro) {
        super(metro);
        parametersCount = 4;
    }

    @Override
    public void execute(String input) {
        parseParameters(input);
        if (matcher == null) {
            return;
        }
        String line1 = getParameter(2);
        String station1 = getParameter(3);
        String line2 = getParameter(4);
        String station2 = getParameter(5);
        metro.printRoute(line1, station1, line2, station2);
    }
}

class FastestRouteCommand extends Command {

    public FastestRouteCommand(Metro metro) {
        super(metro);
        parametersCount = 4;
    }

    @Override
    public void execute(String input) {
        parseParameters(input);
        if (matcher == null) {
            return;
        }
        String line1 = getParameter(2);
        String station1 = getParameter(3);
        String line2 = getParameter(4);
        String station2 = getParameter(5);
        metro.printFastestRoute(line1, station1, line2, station2);
    }
}