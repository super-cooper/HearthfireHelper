package materialCounter;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Class to represent a room and its properties for a single house
 *
 * @author Adam Cooper
 */
public class Room {

    //fields
    public enum FurnitureType {
        Barrels, Chest, Cupboard, Desk,
        Display_Case_and_Cupboard, Display_Case_And_Small_Wardrobe,
        Dresser, End_Table_1, End_Table_2, Hanging_Rack, Safe_1, Safe_2,
        Tall_Wardrobe, Wardrobe_Small, Chair, Child_Bed, Dining_Table_and_Chairs,
        Display_Case, Display_Case_on_Low_Table, Double_Bed, Large_Table_with_Chest,
        Low_Table, Night_Table_1, Night_Tables_2, Round_Table,
        Round_Table_and_Chairs, Single_Bed, Square_Table, Square_Table_and_Chairs,
        Table_with_Display_Cases, Washbasin_on_Stand, Armor_Mannequin,
        Armor_Mannequin_with_Cupboard, Weapon_Plaque, Weapon_Racks_1, Weapon_Racks_2,
        Weapon_Racks_3, Bench, Table_With_Benches, Bookshelf, Corner_Shelf,
        Display_Case_and_Shelf, Small_Shelf, Tall_Bookshelf, Tall_Shelf,
        Tall_Shelf_with_Display_Case, Wall_Shelves_1, Wall_Shelves_2, Alchemy_Lab,
        Arcane_Enchanter, Brazer, Archery_Target_1, Archery_Targets_2, Armorer_Workbench,
        Blacksmith_Forge, Blacksmith_Anvil, Chandelier_Large, Chandelier_Small,
        Child_Practice_Dummy, Fireplace, Firepit, Grindstone, Lamp_Stand, Mead_Barrels,
        Coffin, Mounted_Bear_Head, Mounted_Elk_Antlers, Mounted_Elk_Head_1,
        Mounted_Elk_Head_2, Mounted_Goat_Head, Mounted_Horker_Head, Mounted_Mudcrab,
        Mounted_Sabre_Cat_Head, Mounted_Slaughterfish, Mounted_Snowy_Sabre_Cat_Head,
        Mounted_Wolf_Head, Large_Planter, Small_Planter_with_Cupboard, Oven,
        Shrine_Base, Shrine_of_Akatosh, Shrine_of_Arkay, Shrine_of_Dibella,
        Shrine_of_Julianos, Shrine_of_Kynareth, Shrine_of_Mara,
        Shrine_of_Stendarr, Shrine_of_Talos, Shrine_of_Zenithar, Smelter,
        Tanning_Rack, Trophy_Base_Large, Trophy_Base_Small, Wall_Sconce, Trophy_Bear,
        Trophy_Chaurus, Trophy_Cow, Trophy_Deer, Trophy_Dragon_Skull, Trophy_Draugr,
        Trophy_Dwarven_Sphere, Trophy_Falmer, Trophy_Frost_Troll, Trophy_Frostbite_Spider,
        Trophy_Horker, Trophy_Sabre_Cat, Trophy_Snow_Bear, Trophy_Troll, Trophy_Wolf,
        Trophy_Draugr_Small, Trophy_Dwarven_Spider, Trophy_Falmer_Small, Trophy_Goat,
        Trophy_Hagraven, Trophy_Ice_Wolf, Trophy_Mudcrab, Trophy_Skeever, Trophy_Skeleton,
        Trophy_Slaughterfish, Trophy_Spriggan, Fish_Hatchery, Apiary, Grain_Mill,
        Animal_Pen, Garden, Stable
    }

    public enum RoomType {
        Small_House, Entryway, Main_Hall, Cellar,
        Cellar_Smithing, Cellar_Safes, Cellar_Religious, Armory, Kitchen,
        Library, Bedrooms, Enchanter_Tower, Greenhouse, Alchemy_Laboratory,
        Trophy_Room, Storage_Room, Breezehome, Hjerim, Honeyside,
        Proudspire_Manor, Severin_Manor, Vlindrel_Hall, Outside
    }

    private RoomType type;
    private LinkedHashSet<Furniture> pieces;
    private HashMap<String, Integer> roomMaterials;
    private static final String ROOM_FILE = "rooms.info";
    private static final String ROOM_TYPE_NOTATOR = "-";
    private static final String ROOM_FMT_ERROR = "Improperly formatted room file \'" + ROOM_FILE + "\'!";

    /**
     * Constructor
     *
     * @param type The type of room being created
     */
    Room(RoomType type) {
        this.type = type;
        this.pieces = new LinkedHashSet<>();
        this.roomMaterials = new HashMap<>();
        this.buildProperties();
    }


    /**
     * @return The furniture pieces in this room
     */
    LinkedHashSet<Furniture> getPieces() {
        return this.pieces;
    }


    /**
     * Builds a list of furniture and materials
     * needed to create a room
     */
    private void buildProperties() {
        Scanner scan = null;
        try {
            scan = new Scanner(new File(Paths.get(getResourceDirectory(), ROOM_FILE).toString()));
        } catch (FileNotFoundException e) {
            System.err.println("Missing room info file \'" + ROOM_FILE + "\'!");
            System.exit(1);
        }
        String line;
        while (scan.hasNextLine()) {
            line = scan.nextLine();
            if (line.startsWith(ROOM_TYPE_NOTATOR)) {
                // search for this room's type
                if (line.trim().replaceFirst(ROOM_TYPE_NOTATOR, "").equals(this.getName())) {
                    if (scan.hasNextLine()) {
                        line = scan.nextLine();
                    } else {
                        return;
                    }
                    // read furniture and material for this room type
                    while (scan.hasNextLine() && !line.startsWith(ROOM_TYPE_NOTATOR)) {
                        if (line.contains(Furniture.FURNITURE_TYPE_NOTATOR)) {
                            String[] tokens = line.trim().replaceFirst(Furniture.FURNITURE_TYPE_NOTATOR, " ").split(" ");
                            tokens = fixSplit(tokens);
                            try {
                                // check if amount given
                                String amt = "1";
                                if (tokens.length >= 2) {
                                    amt = tokens[1];
                                }
                                // add a new piece of furniture to the furniture list
                                this.addPiece(new Furniture(FurnitureType.valueOf(tokens[0]), Integer.parseInt(amt)));
                            } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                                System.err.println(ROOM_FMT_ERROR);
                                e.printStackTrace();
                                System.exit(1);
                            }
                        } else { //add raw materials instead of furniture
                            String amt = "1";
                            String[] tokens = fixSplit(line.split(" "));
                            if (tokens.length >= 2) {
                                amt = tokens[1];
                            }
                            try {
                                this.roomMaterials.put(tokens[0], Integer.parseInt(amt));
                            } catch (NumberFormatException e) {
                                System.err.println(ROOM_FMT_ERROR);
                                e.printStackTrace();
                                System.exit(1);
                            }
                        }
                        line = scan.nextLine();
                    }
                    scan.close();
                    return;
                }
            }
        }
        // if we got this far, the given room type was not in the file
        System.err.println("Give room type \'" + this.getName() + "\' not found in room file \'" + ROOM_FILE + "\'!");
        System.exit(1);
    }


    /**
     * @return The directory with all resources in it as a string
     */
    private static String getResourceDirectory() {
        return Paths.get(System.getProperty("user.dir"), "info").toString();
    }


    /**
     * Removes empty spaces because String.split() is incredibly bad.
     * Where's my strtok???
     *
     * @param badArray The array which has been ruined by String.split()
     * @return An array with no instances of ""
     */
    static String[] fixSplit(String[] badArray) {
        int newLen = 0;
        for (String s : badArray) {
            if (!s.equals("")) {
                newLen++;
            }
        }
        String[] goodArray = new String[newLen];
        int i = 0;
        for (String s : badArray) {
            if (!s.equals("")) {
                goodArray[i] = s;
                i++;
            }
        }
        return goodArray;
    }


    /**
     * @return A HashMap with a keyset of this room's materials,
     * and associated values of their respected counts
     */
    HashMap<String, Integer> getMaterials() {
        return this.roomMaterials;
    }


    /**
     * Builds a set of all materials involved in building this room,
     * including furniture, and tallies up the total amount needed
     * of each material
     *
     * @return A HashMap containing a keyset of all the materials
     * needed to build this room with each key's value corresponding
     * to the amount of each material needed
     */
    HashMap<String, Integer> getTotalMaterials() {
        HashMap<String, Integer> totals = new HashMap<>();
        for (String material : this.roomMaterials.keySet()) {
            MaterialCounter.tally(material, this.roomMaterials.get(material), totals);
        }
        for (Furniture piece : this.pieces) {
            for (String material : piece.materials()) {
                MaterialCounter.tally(material, piece.getTotalMaterialCount(material), totals);
            }
        }
        return totals;
    }


    /**
     * @return The name of this room
     */
    String getName() {
        return this.type.name();
    }


    /**
     * @return The type of this room
     */
    RoomType getType() {
        return this.type;
    }


    /**
     * Gets a furniture piece by name
     *
     * @param furnitureName The name of the piece
     * @param room          The room to add the furniture to
     * @return A new furniture object of the requested type, or null if type DNE
     */
    static Furniture getFurnitureByName(String furnitureName, Room room) {
        for (FurnitureType item : FurnitureType.values()) {
            if (item.name().equals(furnitureName)) {
                return room.new Furniture(item, 1);
            }
        }
        return null;
    }


    /**
     * Creates a new Room object of the given type
     *
     * @param roomName The type of room to create
     * @return A new Room of type roomName or null if type roomName DNE
     */
    static Room getRoomByName(String roomName) {
        for (RoomType room : RoomType.values()) {
            if (room.name().equals(roomName)) {
                return new Room(room);
            }
        }
        return null;
    }


    /**
     * Adds a piece of furniture to this room,
     * or updates its amount if it's already in this room
     *
     * @param piece The furniture to add
     */
    void addPiece(Furniture piece) {
        if (this.pieces.contains(piece)) {
            for (Furniture old : this.pieces) {
                if (old.equals(piece)) {
                    old.amount += piece.amount();
                    return;
                }
            }
        } else {
            this.pieces.add(piece);
        }
    }


    /**
     * Tells if a furniture group of a particular type has already been
     * added to this room
     *
     * @param type The type to check for
     * @return True if the type has already been added, false otherwise
     */
    public boolean containsFurnitureType(FurnitureType type) {
        for (Furniture piece : this.pieces) {
            if (piece.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Represents a single type of furniture and its
     * properties for this room
     */
    public class Furniture {

        //fields
        private FurnitureType type;
        private int amount;
        private HashMap<String, Integer> materials;
        private static final String FURNITURE_FILE = "furniture.info";
        private static final String FURNITURE_TYPE_NOTATOR = "~";

        /**
         * Constructor <br>
         * Builds a furniture group for this particular room with specified properties
         *
         * @param type The type of furniture this is
         * @throws IllegalArgumentException if initialized with repeated type
         */
        Furniture(FurnitureType type, int amount) throws IllegalArgumentException {
            this.type = type;
            this.amount = amount;
            this.materials = new HashMap<>();
            this.buildMaterialsList();
        }

        /**
         * Builds the list of materials for this furniture's type
         */
        private void buildMaterialsList() {
            Scanner scan = null;
            try {
                scan = new Scanner(new File(Paths.get(getResourceDirectory(), FURNITURE_FILE).toString()));
            } catch (FileNotFoundException e) {
                System.err.println("Furniture material file \"" + FURNITURE_FILE + "\" missing!");
                System.exit(1);
            }
            String line;
            while (scan.hasNextLine()) {
                line = scan.nextLine();
                if (line.contains(FURNITURE_TYPE_NOTATOR)) {
                    // search for this group's furniture type
                    if (line.replaceAll(FURNITURE_TYPE_NOTATOR, "").trim().equals(this.type.name())) {
                        line = scan.nextLine();
                        // gather materials below
                        while (scan.hasNextLine() && !line.contains(FURNITURE_TYPE_NOTATOR)) {
                            String[] tokens = fixSplit(line.split(" "));
                            try {
                                MaterialCounter.tally(tokens[0], Integer.parseInt(tokens[1]), this.materials);
                            } catch (ClassCastException | NumberFormatException e) {
                                System.err.println("Improperly formatted file!");
                                e.printStackTrace();
                                System.exit(1);
                            }
                            line = scan.nextLine();
                        }
                        scan.close();
                        return;
                    }
                }
            }
            // if we got this far, the furniture type wasn't found in the file
            System.err.println("Furniture type " + this.getName() + " not found in file \'" + FURNITURE_FILE + "\'!");
            System.exit(1);
        }

        /**
         * @return This group's type
         */
        FurnitureType getType() {
            return this.type;
        }

        /**
         * @return The name of the type of furniture represented by this group
         */
        String getName() {
            return this.type.name();
        }

        /**
         * @return The amount of pieces in this group
         */
        int amount() {
            return this.amount;
        }

        /**
         * Gets the total amount of one material needed to build a single
         * piece of this furniture
         *
         * @param material The material to get
         * @return < 0 if the material isn't needed, else the amount of it
         * needed to build a single piece of this furniture
         */
        int getMaterialCount(String material) {
            return this.materials.getOrDefault(material, -1);
        }

        /**
         * Gets the total amount of one material needed to build all
         * the pieces of this furniture needed for this room
         *
         * @param material The material to get
         * @return < 0 if the material isn't needed, else the amount of it
         * needed to build a single piece of this furniture * the amount of
         * pieces of this furniture type needed to build the room
         */
        int getTotalMaterialCount(String material) {
            return this.getMaterialCount(material) * this.amount();
        }

        /**
         * @return A set of all the material types needed to build this
         * type of furniture
         */
        Set<String> materials() {
            return this.materials.keySet();
        }

        /**
         * Tells of two furniture pieces are of the same type
         */
        @Override
        public boolean equals(Object o) {
            if (!o.getClass().getName().equals(this.getClass().getName()))
                return false;
            return this.getName().equals(((Furniture) o).getName());
        }
    }
}
