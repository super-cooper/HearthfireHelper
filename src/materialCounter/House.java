package materialCounter;

import materialCounter.Room.FurnitureType;
import materialCounter.Room.RoomType;

import java.util.*;

/**
 * Class to represent the construction of a single house
 *
 * @author Adam Cooper
 */
public class House {

    //fields
    private LinkedHashSet<Room> rooms;
    private Room outside;
    private Location location;
    private static final String BUILD_PROMPT = "Do you want to build ";
    private static final Scanner in = new Scanner(System.in);


    /**
     * Constructor
     *
     * @param location The location of this House
     */
    House(Location location) {
        this.rooms = new LinkedHashSet<>();
        this.outside = new Room(RoomType.Outside);
        this.location = location;
        this.addRooms();
    }


    /**
     * Adds appropriate rooms
     */
    private void addRooms() {
        if (this.isHomestead()) {
            this.buildHomestead();
            this.determineExpenses();
        } else
            this.rooms.add(Room.getRoomByName(this.location.name()));
    }


    /**
     * Determines appropriate expenses for this home based on user input
     */
    private void determineExpenses() {
        for (Expense expense : Expense.values()) {
            if (expense != Expense.Chicken) {
                System.out.print("Do you want to buy a " + expense.name() + " for " + expense.cost() + " gold? (y/n) ");
                if (getUserInputPositive()) {
                    expense.add(1);
                }
            } else {
                System.out.print("How many chickens would you like to buy for 25 gold each?" + " (0-3) ");
                boolean status = true;
                while (status) {
                    int x = -1;
                    try {
                        x = in.nextInt();
                    } catch (InputMismatchException e) {
                        in.reset();
                    } finally {
                        if (x >= 0 && x <= 3) {
                            Expense.Chicken.add(x);
                            in.nextLine();
                            status = false;
                        }
                    }
                }
                in.reset();
            }
        }
    }


    /**
     * Asks the user which rooms and features they are adding and then
     * adds them accordingly
     */
    private void buildHomestead() {
        this.rooms.add(this.outside);
        while (true) {
            System.out.print("Enter 0 for small cottage, enter 1 for full house: ");
            String line = in.nextLine().trim();
            if (line.contains("1")) {
                this.buildFullManor();
                break;
            } else if (line.contains("0")) {
                this.buildSmallCottage();
                break;
            }
        }
        this.buildOutside();
    }


    /**
     * Builds the outside furniture of the house
     */
    private void buildOutside() {
        FurnitureType[] types = {
                FurnitureType.Animal_Pen,
                FurnitureType.Garden,
                FurnitureType.Stable
        };
        // for each type of outside furniture
        for (int i = 0; i < types.length; i++) {
            FurnitureType type = types[i];
            System.out.print(BUILD_PROMPT + (i == 0 ? "an " : "a ") + formatName(type.name()) + "? (y/n) ");
            if (getUserInputPositive()) {
                this.outside.addPiece(this.outside.new Furniture(type, 1));
            }
        }
        this.assignOutsideFeature();
    }


    /**
     * Helper method for homestead building if the user just wants to build
     * a small cottage
     */
    private void buildSmallCottage() {
        this.rooms.add(new Room(RoomType.Small_House));
    }


    /**
     * Helper method for homestead building if the user wants to build a full
     * manor
     */
    private void buildFullManor() {
        this.rooms.add(new Room(RoomType.Entryway));
        Room mainHall = new Room(RoomType.Main_Hall);
        this.rooms.add(mainHall);
        String[] westWing = {"West Wing", "Enchanter's Tower", "Bedrooms", "Greenhouse"};
        String[] northWing = {"North Wing", "Trophy Room", "Storage Room", "Alchemy Laboratory"};
        String[] eastWing = {"East Wing", "Library", "Armory", "Kitchen"};
        String[][] wings = {westWing, northWing, eastWing};
        // ask about magic items in main hall
        System.out.print("Do you want an arcane enchanter on the first floor" + " of your main hall? (y/n) ");
        if (getUserInputPositive()) {
            mainHall.addPiece(mainHall.new Furniture(FurnitureType.Arcane_Enchanter, 1));
        }
        System.out.print("Do you want an alchemy lab on the first floor" + " of your main hall? (y/n) ");
        if (getUserInputPositive()) {
            mainHall.addPiece(mainHall.new Furniture(FurnitureType.Alchemy_Lab, 1));
        }
        // build cellar
        this.buildCellar();
        // build wings
        for (String[] wing : wings) {
            // wing[0] = wing name
            System.out.println("Select room for " + wing[0] + ":");
            for (int i = 1; i < 4; i++) {
                System.out.println(i + ": " + wing[i]);
            }
            // get input until it's valid
            int input;
            do {
                try {
                    input = in.nextInt(); //TODO potentially catch blank input
                } catch (InputMismatchException e) {
                    input = -1;
                }
            } while (input < 0 || input > wings.length);
            in.nextLine();
            in.reset();
            Room newRoom = Room.getRoomByName(formatToEnum(wing[input]));
            if (newRoom != null) {
                if (newRoom.getType().equals(RoomType.Trophy_Room)) {
                    this.buildTrophies(newRoom);
                }
                this.rooms.add(newRoom);
            }
        }
    }


    /**
     * Asks the user what types of trophies they want for their trophy room
     *
     * @param trophyRoom The trophy room
     */
    private void buildTrophies(Room trophyRoom) {
        if (!trophyRoom.getType().equals(RoomType.Trophy_Room)) {
            return;
        }
        int largeTrophies = 3, smallTrophies = 4;
        FurnitureType[] largeOptions = {
                FurnitureType.Trophy_Bear, FurnitureType.Trophy_Chaurus,
                FurnitureType.Trophy_Cow, FurnitureType.Trophy_Deer,
                FurnitureType.Trophy_Dragon_Skull, FurnitureType.Trophy_Draugr,
                FurnitureType.Trophy_Dwarven_Sphere, FurnitureType.Trophy_Falmer,
                FurnitureType.Trophy_Frost_Troll,
                FurnitureType.Trophy_Frostbite_Spider,
                FurnitureType.Trophy_Horker, FurnitureType.Trophy_Sabre_Cat,
                FurnitureType.Trophy_Snow_Bear, FurnitureType.Trophy_Troll,
                FurnitureType.Trophy_Wolf
        };
        FurnitureType[] smallOptions = {
                FurnitureType.Trophy_Draugr_Small,
                FurnitureType.Trophy_Dwarven_Spider,
                FurnitureType.Trophy_Falmer_Small, FurnitureType.Trophy_Goat,
                FurnitureType.Trophy_Hagraven, FurnitureType.Trophy_Ice_Wolf,
                FurnitureType.Trophy_Mudcrab, FurnitureType.Trophy_Skeever,
                FurnitureType.Trophy_Skeleton, FurnitureType.Trophy_Slaughterfish,
                FurnitureType.Trophy_Spriggan
        };

        // choose large trophies
        String prompt = "Please pick " + largeTrophies + " large trophies." + " (Separated by spaces)";
        for (int i = 0; i < largeOptions.length; i++) {
            FurnitureType large = largeOptions[i];
            System.out.println(i + ": " + large.name().replaceAll("Trophy_", "").replaceAll("_", " "));
        }
        FurnitureType[] picks = getTrophyInput(largeOptions, largeTrophies, prompt);
        for (FurnitureType trophy : picks) {
            if (trophy != null) {
                trophyRoom.addPiece(trophyRoom.new Furniture(trophy, 1));
            }
        }
        // chose small trophies
        prompt = "Please pick " + smallTrophies + " small trophies." + " (Separated by spaces)";
        for (int i = 0; i < smallOptions.length; i++) {
            FurnitureType small = smallOptions[i];
            System.out.println(i + ": " + small.name().replaceAll("Trophy_", "").replaceAll("_", " "));
        }
        picks = getTrophyInput(smallOptions, smallTrophies, prompt);
        for (FurnitureType trophy : picks) {
            if (trophy != null) {
                trophyRoom.addPiece(trophyRoom.new Furniture(trophy, 1));
            }
        }
    }


    /**
     * Helper method for handily getting user input about trophies
     *
     * @param largeOptions The trophies to pick from
     * @param largeTrophies The number of trophies to pick
     * @param prompt The prompt to print
     */
    private FurnitureType[] getTrophyInput(FurnitureType[] largeOptions, int largeTrophies, String prompt) {
        FurnitureType[] picks = null;
        while (picks == null) {
            System.out.println(prompt);
            String input = in.nextLine();
            picks = parseTrophyChoices(input, largeOptions, largeTrophies);
            if (picks != null) {
                System.out.println("Is this correct? (y/n)");
                System.out.println(Arrays.toString(picks));
                if (!getUserInputPositive()) {
                    picks = null;
                }
            }
        }
        return picks;
    }


    /**
     * Helper method for determining what trophies the user wants
     *
     * @param input   The input string to parse
     * @param options The trophies the user chose from
     * @param limit   The max number of trophies to pick
     * @return An array of FurnitureTypes as designated by the input string
     */
    private static FurnitureType[] parseTrophyChoices(String input, FurnitureType[] options, int limit) {
        FurnitureType[] result = new FurnitureType[limit];
        String[] args = Room.fixSplit(input.split(" "));
        for (int i = 0; i < limit; i++) {
            FurnitureType trophy = null;
            try {
                int j = Integer.parseInt(args[i]);
                trophy = j >= 0 && j < options.length ? options[j] : null;
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                /* do nothing */
            } finally {
                result[i] = trophy;
            }
        }
        return result;
    }


    /**
     * Helper method for building a full manor. Focuses on the cellar and
     * its parts
     */
    private void buildCellar() {
        System.out.print(BUILD_PROMPT + "a cellar? (y/n) ");
        if (!getUserInputPositive()) {
            return; // if user doesn't want to build a cellar
        }
        this.rooms.add(new Room(RoomType.Cellar));
        // build smithing items
        System.out.print(BUILD_PROMPT + "smithing equipment in your cellar? " + "(y/n) ");
        if (getUserInputPositive()) {
            this.rooms.add(new Room(RoomType.Cellar_Smithing));
        }
        // build shrine
        System.out.print(BUILD_PROMPT + "shrines in your cellar?" + " (y/n) ");
        if (getUserInputPositive()) {
            Room religious = new Room(RoomType.Cellar_Religious);
            this.rooms.add(religious);
            this.buildShrines(religious);
        }
        // build safes
        System.out.print(BUILD_PROMPT + "10 safes in your cellar? " + "(y/n) ");
        if (getUserInputPositive()) {
            this.rooms.add(new Room(RoomType.Cellar_Safes));
        }
    }


    /**
     * Helper method to determine which shrines to build in the cellar
     *
     * @param base The base on which to build the shrines
     */
    private void buildShrines(Room base) {
        String[] divines = {"Akatosh", "Arkay", "Dibella", "Julianos", "Kynareth", "Mara", "Stendarr", "Talos", "Zenithar"};

        for (String divine : divines) {
            System.out.print(BUILD_PROMPT + "a shrine to " + divine + "? (y/n) ");
            if (getUserInputPositive()) {
                Room.Furniture shrine = Room.getFurnitureByName("Shrine_of_" + divine, base);
                base.addPiece(shrine);
            } else { // lil joke :)
                if (divine.equals("Talos")) {
                    System.out.println("Skyrim belongs to the Nords!");
                }
            }
        }
    }


    /**
     * Formats output strings to look like they should in the source code <br>
     * I.E. a translator from user-friendly text to programmer-friendly text
     *
     * @param str The string to reformat
     * @return str with all possessive modifiers removed and all spaces replaced
     * with underscores
     */
    static String formatToEnum(String str) {
        return str.trim().replaceAll("\'s", "").replaceAll(" ", "_");
    }


    /**
     * @return true if this house is a homestead, false otherwise
     */
    private boolean isHomestead() {
        return this.location == Location.Windstad_Manor || this.location == Location.Heljarchen_Hall || this.location == Location.Lakeview_Manor;
    }


    /**
     * Assigns an appropriate outside feature if the house has one
     */
    private void assignOutsideFeature() {
        switch (this.location) {
            case Windstad_Manor:
                System.out.print(BUILD_PROMPT + "a fish hatchery? (y/n) ");
                if (getUserInputPositive()) {
                    this.outside.addPiece(Room.getFurnitureByName("Fish_Hatchery", outside));
                }
                break;
            case Lakeview_Manor:
                System.out.print(BUILD_PROMPT + "an apiary? (y/n) ");
                if (getUserInputPositive()) {
                    this.outside.addPiece(Room.getFurnitureByName("Apiary", outside));
                }
                break;
            case Heljarchen_Hall:
                System.out.print(BUILD_PROMPT + "a grain mill? (y/n) ");
                if (getUserInputPositive()) {
                    this.outside.addPiece(Room.getFurnitureByName("Grain_Mill", outside));
                }
                break;
            default:
                break;
        }
    }


    /**
     * @return True if positive or ambiguous response, false otherwise
     */
    private static boolean getUserInputPositive() {
        return !in.nextLine().trim().toLowerCase().startsWith("n");
    }


    private HashMap<String, Integer> maxLength = new HashMap<>();

    /**
     * Formats strings properly for printing
     *
     * @param material The material being used
     * @param amount   The amount of that material
     * @param category The category of the material, to keep track of appropriate length
     * @return A formatted string in the form material: amount
     */
    private String formatString(String material, int amount, String category) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatName(material)).append(": ");
        // append appropriate spaces to keep all values aligned
        sb.append(" ".repeat(Math.max(0, maxLength.get(category) - material.length())));
        sb.append(amount);
        return new String(sb);
    }

    /**
     * Builds up max lengths for each category using recurrent calls
     *
     * @param material The string whose length we are testing
     * @param category The category of the string
     */
    private void buildMaxLength(String material, String category) {
        if (!maxLength.containsKey(category)) {
            maxLength.put(category, 0);
        }
        if (material.length() > maxLength.get(category)) {
            maxLength.put(category, material.length());
        }
    }


    /**
     * Creates a string that neatly describes the materials and expenses
     * necessary to build this home
     *
     * @return A string representation of this home
     */
    @Override
    public String toString() {
        String indent = "";
        @SuppressWarnings("RegExpRepeatedSpace") String tab = "    ";
        StringBuilder sb = new StringBuilder();
        HashMap<String, Integer> houseTotals = new HashMap<>();
        this.buildMaxLengths();
        sb.append(formatName(this.location.name())).append(":\n");
        indent += tab;
        // for each room in this house
        for (Room room : this.rooms) {
            // in case there is nothing outside
            if (room.getTotalMaterials().keySet().size() == 0) {
                continue;
            }
            String val = room.getName().equals(location.name()) ? "Upgrade" : room.getName();
            sb.append(indent).append(formatName(val)).append(":\n");
            indent += tab;
            // for all the furniture in each room in this house
            for (Room.Furniture piece : room.getPieces()) {
                sb.append(indent).append(formatName(piece.getName())).append(": ").append(piece.amount()).append("\n");
                indent += tab;
                // for all the materials to build each furniture in each room in this house
                for (String material : piece.materials()) {
                    sb.append(indent).append(formatString(material, piece.getMaterialCount(material), room.getName() + piece.getName())).append('\n');
                }
                indent = indent.replaceFirst("\\s{4}+", "");
            }
            HashMap<String, Integer> roomMaterials = room.getMaterials();
            for (String material : roomMaterials.keySet()) {
                sb.append(indent).append(formatString(material, roomMaterials.get(material), room.getName())).append('\n');
            }
            indent = indent.replaceFirst("\\s{4}+", "");
            sb.append(indent).append("TOTALS:\n");
            indent += tab;
            // for all the materials required to build the room and everything in it
            HashMap<String, Integer> totalMaterials = room.getTotalMaterials();
            for (String material : totalMaterials.keySet()) {
                sb.append(indent).append(formatString(material, totalMaterials.get(material), room.getName() + "total")).append('\n');
                MaterialCounter.tally(material, totalMaterials.get(material), houseTotals);
            }
            indent = indent.replaceFirst("\\s{4}+", "");
        }

        sb.append(indent).append("EXPENSES:\n");
        indent += tab;
        for (Expense expense : Expense.values()) {
            if (expense.count() <= 0) {
                continue;
            }
            sb.append(indent).append(formatString(expense.name(), expense.count(), expense.getClass().getName())).append('\n');
            indent += tab;
            sb.append(indent).append("Gold: ").append(expense.totalCost()).append('\n');
            indent = indent.replaceFirst(tab, "");
            MaterialCounter.tally("Gold", expense.totalCost(), houseTotals);
            indent = indent.replaceFirst("\\s{4}+", "");
        }
        sb.append(indent).append(formatName(location.name())).append(this.isHomestead() ? " plot:\n" : " deed:\n");
        indent += tab;
        sb.append(indent).append("Gold: ").append(location.cost()).append('\n');
        MaterialCounter.tally("Gold", location.cost(), houseTotals);
        indent = indent.replaceFirst("\\s{4}+", "").replaceFirst("\\s{4}+", "");
        sb.append("TOTALS:\n");
        //indent += tab;
        for (String material : houseTotals.keySet()) {
            sb.append(indent).append(formatString(material, houseTotals.get(material), location.name())).append('\n');
        }
        return new String(sb);
    }


    private static String formatName(String name) {
        return name.replaceAll("[_][0-9]", "").replaceAll("[_]", " ");
    }


    /**
     * Builds max lengths for each category
     */
    private void buildMaxLengths() {
        for (Room room : this.rooms) {
            for (Room.Furniture piece : room.getPieces()) {
                for (String material : piece.materials()) {
                    this.buildMaxLength(material, room.getName() + piece.getName());
                }
            }
            for (String material : room.getMaterials().keySet()) {
                this.buildMaxLength(material, room.getName());
            }
            for (String material : room.getTotalMaterials().keySet()) {
                this.buildMaxLength(material, room.getName() + "total");
                this.buildMaxLength(material, this.location.name());
            }
        }
        for (Expense expense : Expense.values()) {
            if (expense.count() > 0) {
                this.buildMaxLength(expense.totalCost() + ' ' + expense.name(), expense.getClass().getName());
            }
        }
    }


    /**
     * Gets a location by name
     *
     * @param name The name to look up
     * @return The Location that matches name, or null if it doesn't exist
     */
    static Location getLocationByName(String name) {
        for (Location location : Location.values()) {
            if (location.name().equals(name)) {
                return location;
            }
        }
        return null;
    }


    /**
     * Describes different types of expenses for a home that aren't rooms or
     * furniture
     */
    private enum Expense {
        Bard(1500),
        Carriage(500),
        Cow(200),
        Chicken(25),
        Horse(1000);

        //fields
        private int cost;
        private int count;

        Expense(int cost) {
            this.cost = cost;
            this.count = 0;
        }

        public int cost() {
            return cost;
        }

        public int count() {
            return count;
        }

        public void add(int amt) {
            count += amt;
        }

        public int totalCost() {
            return cost() * count();
        }
    }


    /**
     * Saves specific information for locations
     */
    public enum Location {
        Breezehome(5000),
        Hjerim(12000),
        Honeyside(8000),
        Proudspire_Manor(25000),
        Severin_Manor(0),
        Vlindrel_Hall(8000),
        Windstad_Manor(5000),
        Lakeview_Manor(5000),
        Heljarchen_Hall(5000);

        //field
        private int cost;

        Location(int cost) {
            this.cost = cost;
        }

        public int cost() {
            return this.cost;
        }
    }
}
