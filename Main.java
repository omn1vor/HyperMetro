package metro;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            return;
        }

        Metro metro = new Metro();
        if (!metro.importLines(args[0])) {
            return;
        }

        CLI cli = new CLI(metro);
        cli.mainMenu();
    }
}

