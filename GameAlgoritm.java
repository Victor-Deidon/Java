import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class GameAlgoritm {
    private GameWindowV2 gameWindow;
    public static final int WINNING_POINTS = ((13 * 13) / 2) + 1;

    public GameAlgoritm(GameWindowV2 gameWindow) {
        this.gameWindow = gameWindow;
    }

    public List<TileV2> getTilesControlledByPlayerAfterInputColor(int currentPlayer, int otherPlayer, Color chosenColor) {
        List<TileV2> updatedListForTilesControlled = new ArrayList<>(gameWindow.getTilesControlledByPlayer(currentPlayer));
        List<TileV2> tempList = new ArrayList<>(updatedListForTilesControlled);
        updateTilesContolledByPlayerAfterColorInput(updatedListForTilesControlled, tempList, chosenColor, otherPlayer);
        return updatedListForTilesControlled;
    }

    private void updateTilesContolledByPlayerAfterColorInput(List<TileV2> updatedListForTilesControlled, List<TileV2> tempList,
                                                             Color color, int otherPlayer) {
        if (tempList.isEmpty()) {
            return;
        } else {
            List<TileV2> recursion = new ArrayList<>();
            for (TileV2 tile : tempList) {
                List<TileV2> tiles = gameWindow.getNeighbours(tile.getX(), tile.getY());
                for (TileV2 t : tiles) {
                    if (gameWindow.getButtonGrid()[t.getX()][t.getY()].getBackground().equals(color)
                            && !updatedListForTilesControlled.contains(t)
                            && !gameWindow.getTilesControlledByPlayer(otherPlayer).contains(t)) {
                        updatedListForTilesControlled.add(t);
                        recursion.add(t);
                    }
                }
            }
            updateTilesContolledByPlayerAfterColorInput(updatedListForTilesControlled, recursion, color, otherPlayer);
        }
    }
}
