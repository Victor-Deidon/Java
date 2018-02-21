import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class GameWindowV2 {
    private JFrame mainFrame;
    private JPanel controlPanel;
    private JPanel statusPanel;
    private JButton[][] buttonGrid = new JButton[13][13];
    private final Color[] colors = {Color.red, Color.orange, Color.yellow, Color.green,
            Color.blue, new Color(128,0,128)};
    private HashMap<Color, ColorDescriptionV2> colorsMap = new HashMap<>();
    private Color[] currentColorForPlayer = new Color[2];
    private List<TileV2> tilesControlledByPlayer1 = new ArrayList<>();
    private List<TileV2> tilesControlledByPlayer2 = new ArrayList<>();
    private JLabel gameStatus = new JLabel("Game In Progress", JLabel.CENTER);
    private JLabel playerScores[] = {new JLabel("Score :1", JLabel.LEFT), new JLabel("Score :1", JLabel.RIGHT)};
    private JLabel pointsEarned[] = {new JLabel("Points Earned :1", JLabel.LEFT), new JLabel("Points Earned :1", JLabel.RIGHT)};
    JLabel turn = new JLabel("Turn: Player 1", JLabel.CENTER);
    JPanel[] buttonsPannel = {new JPanel(new GridLayout(1, 1)), new JPanel(new GridLayout(1, 1))};
    private GameAlgoritm gameAlgoritm;

    public GameWindowV2() {
        initializeColorsToBeUsed();
        prepareGUI();
        gameAlgoritm = new GameAlgoritm(this);
    }

    private void initializeColorsToBeUsed() {
        colorsMap.put(Color.red, new ColorDescriptionV2("R", "RED"));
        colorsMap.put(Color.orange, new ColorDescriptionV2("O", "ORANGE"));
        colorsMap.put(Color.yellow, new ColorDescriptionV2("Y", "YELLOW"));
        colorsMap.put(Color.green, new ColorDescriptionV2("G", "GREEN"));
        colorsMap.put(Color.blue, new ColorDescriptionV2("B", "BLUE"));
        colorsMap.put(new Color(128,0,128), new ColorDescriptionV2("P", "PURPLE"));
    }

    private void prepareGUI() {
        setUpPlayersPanel();
        setUpGrid();
        initializePlayers();
        initializeMainFrame();
    }

    private void setUpPlayersPanel() {
        statusPanel = new JPanel(new GridLayout(4, 4));
        statusPanel.setBackground(Color.lightGray);
        statusPanel.add(new JLabel("Player 1", JLabel.LEFT));
        statusPanel.add(gameStatus);
        statusPanel.add(new JLabel("Player 2", JLabel.RIGHT));
        for (Entry<Color, ColorDescriptionV2> entry : colorsMap.entrySet()) {
            JButton button = new JButton("");
            button.setBackground(entry.getKey());
            button.setActionCommand("1," + entry.getValue().getColorCode());
            button.addActionListener(new ButtonClickListener());
            buttonsPannel[0].add(button);
        }
        statusPanel.add(buttonsPannel[0]);
        statusPanel.add(turn);
        for (Entry<Color, ColorDescriptionV2> entry : colorsMap.entrySet()) {
            JButton button = new JButton("");
            button.setBackground(entry.getKey());
            button.setActionCommand("2," + entry.getValue().getColorCode());
            button.addActionListener(new ButtonClickListener());
            buttonsPannel[1].add(button);
        }
        statusPanel.add(buttonsPannel[1]);
        statusPanel.add(pointsEarned[0]);
        statusPanel.add(new JLabel(""));
        statusPanel.add(pointsEarned[1]);
        statusPanel.add(playerScores[0]);
        statusPanel.add(new JLabel(""));
        statusPanel.add(playerScores[1]);
    }

    private void setUpGrid() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(13, 13));
        Random random = new Random();
        for (int i = 0; i < buttonGrid.length; i++) {
            for (int j = 0; j < buttonGrid.length; j++) {
                JButton button = new JButton("");
                button.setBackground(colors[random.nextInt(colors.length)]);
                button.setEnabled(false);
                buttonGrid[i][j] = button;
                controlPanel.add(buttonGrid[i][j]);
            }
        }
        if (isColorSame(buttonGrid[0][0], buttonGrid[12][12])) {
            for (Color color : colorsMap.keySet()) {
                if (!color.equals(buttonGrid[0][0].getBackground())) {
                    buttonGrid[12][12].setBackground(color);
                }
            }
        }
        buttonGrid[0][0].setText("1");
        buttonGrid[12][12].setText("2");
        List<TileV2> initialNeighboursOfPlayer1 = getNeighbours(0, 0);
        setNeighbourColorDifferentFromInitialControlledTile(initialNeighboursOfPlayer1, 0, 0);
        List<TileV2> initialNeighboursOfPlayer2 = getNeighbours(12, 12);
        setNeighbourColorDifferentFromInitialControlledTile(initialNeighboursOfPlayer2, 12, 12);
    }

    private void initializePlayers() {
        currentColorForPlayer[0] = buttonGrid[0][0].getBackground();
        currentColorForPlayer[1] = buttonGrid[buttonGrid.length - 1][buttonGrid.length - 1].getBackground();
        tilesControlledByPlayer1.add(new TileV2(0, 0));
        tilesControlledByPlayer2.add(new TileV2(buttonGrid.length - 1, buttonGrid.length - 1));
        disableRequiredButtonsForNextTurn(2);
        Component[] buttonsPanel1 = buttonsPannel[1].getComponents();
        for (Component button : buttonsPanel1) {
            ((JButton) button).setEnabled(false);
        }
        Component[] buttonsPanel2 = buttonsPannel[0].getComponents();
        for (Component button : buttonsPanel2) {
            if (button.getBackground().equals(getCurrentColorForPlayer(1))) {
                ((JButton) button).setEnabled(false);
                break;
            }
        }
    }

    private void initializeMainFrame() {
        mainFrame = new JFrame("Game");
        mainFrame.setSize(700, 700);
        mainFrame.setLayout(new GridBagLayout());
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridheight = 5;
        constraints.ipady = 170;
        constraints.fill = GridBagConstraints.BOTH;
        mainFrame.add(controlPanel, constraints);
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridheight = 1;
        constraints.ipady = 0;
        mainFrame.add(statusPanel, constraints);
        mainFrame.setVisible(true);
    }

    private void setNeighbourColorDifferentFromInitialControlledTile(List<TileV2> initialNeighboursOfPlayer, int x, int y) {
        for (TileV2 tile : initialNeighboursOfPlayer) {
            if (buttonGrid[x][y].getBackground().equals(buttonGrid[tile.getX()][tile.getY()].getBackground())) {
                for (Color c : colorsMap.keySet()) {
                    if (!c.equals(buttonGrid[x][y].getBackground())) {
                        buttonGrid[tile.getX()][tile.getY()].setBackground(c);
                    }
                }
            }
        }
    }

    public List<TileV2> getNeighbours(int x, int y) {
        List<TileV2> neighbours = new ArrayList<>();
        if ((x + 1) < buttonGrid.length) {
            neighbours.add(new TileV2(x + 1, y));
        }
        if ((x - 1) >= 0) {
            neighbours.add(new TileV2(x - 1, y));
        }
        if ((y + 1) < buttonGrid.length) {
            neighbours.add(new TileV2(x, y + 1));
        }
        if ((y - 1) >= 0) {
            neighbours.add(new TileV2(x, y - 1));
        }
        if ((x - 1 >= 0) && (y - 1 >= 0)) {
            neighbours.add(new TileV2(x - 1, y - 1));
        }
        if ((x + 1 < buttonGrid.length) && (y + 1 < buttonGrid.length)) {
            neighbours.add(new TileV2(x + 1, y + 1));
        }
        if ((x + 1 < buttonGrid.length) && (y - 1 >= 0)) {
            neighbours.add(new TileV2(x + 1, y - 1));
        }

        if ((x - 1 >= 0) && (y + 1 < buttonGrid.length)) {
            neighbours.add(new TileV2(x - 1, y + 1));
        }
        return neighbours;
    }

    private boolean isColorSame(JButton button1, JButton button2) {
        return button1.getBackground().equals(button2.getBackground());
    }

    public Color getCurrentColorForPlayer(int playerNo) {
        return currentColorForPlayer[playerNo - 1];
    }

    public Color[] getColors() {
        return colors;
    }

    public Map<Color, ColorDescriptionV2> getColorMap() {
        return colorsMap;
    }

    public JButton[][] getButtonGrid() {
        return buttonGrid;
    }

    public List<TileV2> getTilesControlledByPlayer(int i) {
        if (i == 1) {
            return tilesControlledByPlayer1;
        } else {
            return tilesControlledByPlayer2;
        }

    }

    public void updateUIforPlayer(List<TileV2> updatedListForTilesControlled, int currentPlayer, Color chosenColor, Integer pointsEarnedInChance) {
        setTilesControlledByPlayer(updatedListForTilesControlled, currentPlayer);
        updateColorForTilesControlled(currentPlayer, chosenColor);
        setCurrentColorForPlayer(chosenColor, currentPlayer);
        playerScores[currentPlayer - 1].setText("Score: " + updatedListForTilesControlled.size());
        pointsEarned[currentPlayer - 1].setText("Points Earned :" + pointsEarnedInChance);
        turn.setText("Turn: Player " + (currentPlayer == 1 ? 2 : 1));
        disableRequiredButtonsForNextTurn(currentPlayer);
    }

    public void setTilesControlledByPlayer(List<TileV2> tilesControlledByPlayer, int player) {
        if (player == 1) {
            this.tilesControlledByPlayer1 = tilesControlledByPlayer;
        } else {
            this.tilesControlledByPlayer2 = tilesControlledByPlayer;
        }
    }

    private void updateColorForTilesControlled(int currentPlayer, Color chosenColor) {
        JButton[][] grid = getButtonGrid();
        for (TileV2 tile : getTilesControlledByPlayer(currentPlayer)) {
            buttonGrid[tile.getX()][tile.getY()].setBackground(chosenColor);
            buttonGrid[tile.getX()][tile.getY()].setText(new Integer(currentPlayer).toString());
        }
    }

    public void setCurrentColorForPlayer(Color currentColorForPlayer, int player) {
        this.currentColorForPlayer[player - 1] = currentColorForPlayer;
    }

    public void updateGameSummary(int player) {
        gameStatus.setText("Game Concluded.");
        turn.setText("Winner : Player " + player);

    }

    private void disableRequiredButtonsForNextTurn(Integer player) {
        JPanel panel1 = buttonsPannel[player-1];
        for (Component component : panel1.getComponents()) {
            ((JButton) component).setEnabled(false);
        }
        Integer otherPlayer = (player == 1) ? 2 : 1;
        JPanel panel2 = buttonsPannel[otherPlayer-1];
        for (Component component : panel2.getComponents()) {
            if (((JButton) component).getBackground().equals(currentColorForPlayer[0])
                    || ((JButton) component).getBackground().equals(currentColorForPlayer[1])) {
                ((JButton) component).setEnabled(false);
            }
            else{
                ((JButton)component).setEnabled(true);
            }
        }
    }

    private void disableAllButtons() {
        for (JPanel panel : buttonsPannel) {
            for (Component component : panel.getComponents()) {
                ((JButton) component).setEnabled(false);
            }
        }
    }
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            String input[] = command.split(",");
            Integer player = Integer.parseInt(input[0].trim());
            String colorCode = input[1].trim();
            Color chosenColor = getChosenColor(colorCode);
            Integer otherPlayer = (player == 1) ? 2 : 1;
            List<TileV2> updatedListForTilesControlled = gameAlgoritm.getTilesControlledByPlayerAfterInputColor(player, otherPlayer, chosenColor);
            Integer pointsEarnedInChance = updatedListForTilesControlled.size() - getTilesControlledByPlayer(player).size();
            updateUIforPlayer(updatedListForTilesControlled, player, chosenColor, pointsEarnedInChance);

            if (getTilesControlledByPlayer(player).size() > GameAlgoritm.WINNING_POINTS) {
                updateGameSummary(player);
                disableAllButtons();
            }
        }

        private Color getChosenColor(String colorCode) {
            Color chosenColor=null;
            for (Entry<Color, ColorDescriptionV2> entry : colorsMap.entrySet()) {
                if (entry.getValue().getColorCode().equalsIgnoreCase(colorCode)) {
                    chosenColor = entry.getKey();
                }
            }
            return chosenColor;
        }
    }
}


