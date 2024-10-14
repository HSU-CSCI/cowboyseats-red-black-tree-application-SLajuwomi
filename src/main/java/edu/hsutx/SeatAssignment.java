package edu.hsutx;

/**
 * Represents a seat assignment with a unique key based on the quantity and starting index of the seats,
 * along with an optional owner of the seat assignment.
 *
 * The key is a concatenation of the seat quantity and starting index, each formatted to be a fixed-length string.
 * The owner field is optional and can be null if no owner is assigned.
 *
 * @author Todd Dole
 * @version 1.0
 */

public class SeatAssignment {
    private final String key;
    private final String owner;
    private final int seatQuantity;
    private final int seatStartIndex;


    /**
     * Constructor to create a SeatAssignment without an owner.
     * This represents unassigned seats.
     * Generates a unique key based on the seat quantity and start index.
     *
     * @param seatQuantity   The number of seats being assigned.
     * @param seatStartIndex The starting index of the seat assignment.
     */
    public SeatAssignment(int seatQuantity, int seatStartIndex) {
        this.key = String.format("%06d", seatQuantity) + String.format("%07d",seatStartIndex);
        this.owner = null;
        this.seatQuantity = seatQuantity;
        this.seatStartIndex = seatStartIndex;

    }

    /**
     * Constructor to create a SeatAssignment with an owner.
     * Generates a unique key based on the seat quantity and start index.
     *
     * @param owner          The owner of the seat assignment.
     * @param seatQuantity   The number of seats being assigned.
     * @param seatStartIndex The starting index of the seat assignment.
     */
    public SeatAssignment(String owner, int seatQuantity, int seatStartIndex) {
        this.key = String.format("%06d", seatQuantity) + String.format("%07d",seatStartIndex);
        this.owner = owner;
        this.seatQuantity = seatQuantity;
        this.seatStartIndex = seatStartIndex;
    }

    /**
     * Gets the unique key representing this seat assignment.
     * The key is a concatenation of the seat quantity and seat start index,
     * formatted as 6 digits and 7 digits respectively.
     *
     * @return The unique key for the seat assignment.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the quantity of seats assigned.
     *
     * @return The number of seats in this assignment.
     */
    public int getSeatQuantity() {
        return seatQuantity;
    }

    /**
     * Gets the starting index of the seat assignment.
     *
     * @return The starting index of the seats in this assignment.
     */
    public int getSeatStartIndex() {
        return seatStartIndex;
    }

    /**
     * Gets the owner of the seat assignment.
     *
     * @return The owner of the seat assignment, or null if no owner is assigned.
     */
    public String getOwner() {
        return owner;
    }
}
