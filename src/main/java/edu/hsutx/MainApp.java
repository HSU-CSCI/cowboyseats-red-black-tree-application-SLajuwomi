package edu.hsutx;

public class MainApp {

    public static void main(String[] args) {
        // Initialize the model (CowboySeatTree)
        CowboySeatTree seatTree = new CowboySeatTree();

        // Initialize the view (Swing-based GUI)
        SwingView view = new SwingView();

        // Use SwingUtilities to ensure GUI is created on the EDT
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Create and display the GUI
            view.createAndShowGUI();

            // Initialize the controller with the model and view
            ReservationController controller = new ReservationController(seatTree, view);

            // Delay the start of CSV processing to ensure the GUI is fully initialized
            // You can use a small delay here to ensure everything is in place
            new Thread(() -> {
                try {
                    Thread.sleep(500); // Add a small delay to ensure the GUI is visible
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Start processing the CSV file after the GUI is visible
                controller.processCSV("data/reservations.csv");
            }).start();
        });
    }
}
