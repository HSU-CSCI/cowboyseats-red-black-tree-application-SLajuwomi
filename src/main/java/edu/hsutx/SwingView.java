package edu.hsutx;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class SwingView extends JFrame {

    private BufferedImage stadiumImage;
    private JPanel stadiumPanel;
    private JScrollPane scrollPane;  // ScrollPane for tree panel
    private JPanel treePanel;  // Tree panel
    private CowboySeatTree tree;  // Store the CowboySeatTree reference here
    private double zoomFactor = 1.0;  // Zoom factor for the tree visualization
    private int updateCount = 0;

    // Constructor to set up the JFrame
    public SwingView() {
        setTitle("Stadium Seat Reservations");
        setSize(2200, 1100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add the stadium visualization on the right (East)
        stadiumImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 1000; x++) {
            for (int y = 0; y < 1000; y++) {
                stadiumImage.setRGB(x, y, Color.BLACK.getRGB());
            }
        }

        stadiumPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(stadiumImage, 0, 0, this);
            }
        };
        stadiumPanel.setPreferredSize(new Dimension(1000, 1000));
        add(stadiumPanel, BorderLayout.EAST);

        // Tree Panel: Set up the scrollable and zoomable tree visualization
        treePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.scale(zoomFactor, zoomFactor);  // Apply zoom factor

                // Check if tree exists and draw it
                if (tree != null && tree.getRoot() != null) {
                    drawTree(g2d, tree.getRoot(), getWidth() / 2, 50, getWidth() / 4);
                }
            }

            private void drawTree(Graphics2D g, CowboySeatTree.Node node, int x, int y, int xOffset) {
                if (node == null) return;

                g.setColor(node.color ? Color.RED : Color.BLACK);
                g.fillOval(x - 15, y - 15, 30, 30);  // Draw node as a circle
                g.setColor(Color.WHITE);
                g.drawString(node.key, x - 10, y + 5);  // Draw the key inside the node

                if (node.left != null) {
                    g.setColor(Color.BLACK);
                    g.drawLine(x, y, x - xOffset, y + 50);  // Draw left child line
                    drawTree(g, node.left, x - xOffset, y + 50, xOffset / 2);  // Recurse to left child
                }

                if (node.right != null) {
                    g.setColor(Color.BLACK);
                    g.drawLine(x, y, x + xOffset, y + 50);  // Draw right child line
                    drawTree(g, node.right, x + xOffset, y + 50, xOffset / 2);  // Recurse to right child
                }
            }
        };

        // Set initial size for the tree panel, will adjust based on zoom
        treePanel.setPreferredSize(new Dimension(1000, 600));

        // Wrap the tree panel in a scroll pane for scrolling
        scrollPane = new JScrollPane(treePanel);
        add(scrollPane, BorderLayout.CENTER);  // Add the scroll pane to the center of the layout

        // Add zoom functionality for the tree panel via mouse wheel
        treePanel.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getPreciseWheelRotation() < 0) {
                    zoomFactor *= 1.1;  // Zoom in
                } else {
                    zoomFactor /= 1.1;  // Zoom out
                }
                treePanel.revalidate();  // Revalidate and repaint to apply zoom
                treePanel.repaint();
            }
        });
    }

    // Method to show the GUI
    public void createAndShowGUI() {
        this.setVisible(true);
    }

    // Method to update the stadium visualization with colored seats
    public void updateStadiumVisualization(int start, int length, boolean colored) {
        Random rand = new Random();
        Color randomColor = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));

        for (int i = start; i < start + length; i++) {
            int row = i / 1000;
            int col = (row % 2 == 0) ? (i % 1000) : (999 - (i % 1000));

            if (colored) {
                stadiumImage.setRGB(col, row, randomColor.getRGB());
            } else {
                stadiumImage.setRGB(col, row, Color.BLACK.getRGB());
            }
        }

        stadiumPanel.revalidate();
        stadiumPanel.repaint();
        updateCount++;

        // Save the stadium image periodically (every 20 updates)
        if (updateCount % 20 == 0) {
            File outputfile = new File("image.jpg");
            try {
                ImageIO.write(stadiumImage, "jpg", outputfile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void updateTreeVisualization(CowboySeatTree tree) {
        // Store the tree for use in paintComponent
        this.tree = tree;

        // Dynamically resize the tree panel based on the depth of the tree
        int depth = calculateTreeDepth(tree.getRoot());
        treePanel.setPreferredSize(new Dimension((int) (1000 * zoomFactor), (depth * 100)));  // Adjust the panel size
        treePanel.revalidate();  // Revalidate to ensure scroll pane updates
        treePanel.repaint();  // Repaint to refresh the visualization
    }

    // Helper function to calculate tree depth
    private int calculateTreeDepth(CowboySeatTree.Node node) {
        if (node == null) return 0;
        int leftDepth = calculateTreeDepth(node.left);
        int rightDepth = calculateTreeDepth(node.right);
        return Math.max(leftDepth, rightDepth) + 1;
    }
}
