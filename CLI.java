package metro;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CLI {
    Metro metro;
    Scanner scanner = new Scanner(System.in);

    public CLI(Metro metro) {
        this.metro = metro;
    }

    public void mainMenu() {
        while (true) {
            String input = scanner.nextLine();
            if ("/exit".equalsIgnoreCase(input)) {
                break;
            }
            String commandName = parseCommandName(input);
            try {
                Command command = Command.newCommand(commandName, metro);
                command.execute(input);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private String parseCommandName(String input) {
        Pattern pattern = Pattern.compile("/([\\w-]+) .*");
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return "";
        }
        return matcher.group(1);
    }
}