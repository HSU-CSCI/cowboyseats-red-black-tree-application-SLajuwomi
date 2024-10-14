package edu.hsutx;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;

public class ReservationController {
    private static final double DELAY_SECONDS = 5.0;  // Set this to the number of seconds to pause after updating each entry in the csv file
    private CowboySeatTree seatTree;
    private SwingView view;

    // Constructor to link model and view
    public ReservationController(CowboySeatTree seatTree, SwingView view) {
        this.seatTree = seatTree;
        this.view = view;
    }

    // Method to process the CSV file
    public void processCSV(String filePath) {
        // Use a SwingWorker to handle the CSV processing in the background
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                String line;
                HashMap<String, String> keyNamePairs = new HashMap<>();
                BufferedReader br;

                seatTree.insert(new SeatAssignment(999999, 1));  // Add starting empty seats to the tree
                System.out.println("Inserted starting empty seats");

                try {
                    File file = new File(filePath);
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-16"));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                try {
                    // Read the file line by line
                    while ((line = br.readLine()) != null) {
                        System.out.println("Read " + line);
                        String[] values = line.split(",");

                        char command = values[0].charAt(0);
                        String name = values[1];
                        System.out.println("Command: " + command + ", Name: " + name);

                        if (command == 'a') {
                            String seatStr = values[2];
                            int preference = Integer.parseInt(values[3]);
                            int seatsNeeded = Integer.parseInt(seatStr);

                            System.out.println("Seats needed: " + seatsNeeded + ", Preference: " + preference);

                            SeatAssignment emptyBlock = seatTree.getUnassignedBlockOverThreshold(seatsNeeded);
                            if (emptyBlock == null) {
                                System.out.println("Error adding " + seatsNeeded + " seats for " + name + " -- no sufficient free block found.");
                                continue;
                            }

                            int openSeats = emptyBlock.getSeatQuantity();
                            int openSeatStart = emptyBlock.getSeatStartIndex();
                            System.out.println("Open seats found: " + openSeats + ", Start at: " + openSeatStart);

                            int startSeat = (int) ((openSeats - seatsNeeded) * (preference / 100.0)) + openSeatStart;
                            System.out.println("Start seat for reservation: " + startSeat);

                            // Update the tree
                            seatTree.delete(emptyBlock);
                            System.out.println("Deleted block: " + emptyBlock.getKey());

                            SeatAssignment addAsg = new SeatAssignment(name, seatsNeeded, startSeat);
                            seatTree.insert(addAsg);
                            System.out.println("Inserted new reservation: " + addAsg.getKey());
                            keyNamePairs.put(name, addAsg.getKey());

                            // Update the stadium image
                            SwingUtilities.invokeLater(() -> view.updateStadiumVisualization(startSeat, seatsNeeded, true));
                            System.out.println("Updated stadium visualization for seats: " + startSeat + " to " + (startSeat + seatsNeeded));

                            // Handle left and right unassigned blocks
                            int leftSeats = startSeat - openSeatStart;
                            if (leftSeats > 0) {
                                SeatAssignment addLeft = new SeatAssignment(leftSeats, openSeatStart);
                                seatTree.insert(addLeft);
                                System.out.println("Inserted left unassigned block: " + addLeft.getKey());
                            }

                            if (leftSeats + seatsNeeded < openSeats) {
                                SeatAssignment addRight = new SeatAssignment(openSeats - leftSeats - seatsNeeded, startSeat + seatsNeeded);
                                seatTree.insert(addRight);
                                System.out.println("Inserted right unassigned block: " + addRight.getKey());
                            }

                            SwingUtilities.invokeLater(() -> view.updateTreeVisualization(seatTree));
                            System.out.println("Updated tree visualization");
                        } else if (command == 'd') {
                            // Deletion command
                            String key = keyNamePairs.get(name);
                            seatTree.delete(key);
                            System.out.println("Deleted reservation: " + key);
                            keyNamePairs.remove(name);

                            int leftSeats = Integer.parseInt(key.substring(0, 6));
                            int openSeatStart = Integer.parseInt(key.substring(6));

                            SeatAssignment addEmpty = new SeatAssignment(leftSeats, openSeatStart);
                            seatTree.insert(addEmpty);
                            System.out.println("Inserted empty block: " + addEmpty.getKey());

                            SwingUtilities.invokeLater(() -> view.updateStadiumVisualization(openSeatStart, leftSeats, false));
                            System.out.println("Updated stadium visualization for deletion: " + openSeatStart + " to " + (openSeatStart + leftSeats));
                        }

                        // Sleep for the delay between operations
                        System.out.println("Sleeping for " + DELAY_SECONDS + " seconds");
                        Thread.sleep((long) (DELAY_SECONDS * 1000));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("File is fully read.");
                return null;
            }

            @Override
            protected void done() {
                System.out.println("Processing complete");
                // Code that runs after the background task is complete (if any cleanup is needed)
            }
        };

        // Start the SwingWorker thread
        worker.execute();
    }
}
