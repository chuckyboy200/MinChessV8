import com.cb200.minchessv8.game.Game;
import com.cb200.minchessv8.testing.Perft;

public class App {

    public static void main(String[] args) throws Exception {
        Perft.all();
        Game game = new Game.Builder().build();
        game.drawBoard();
        game.showMoves();
        game.generateMoves(false);
        game.showMoves();
        //Perft.some(6, 18);
        /*
        Board board = Board.fromFen("qqqk4/8/8/8/8/8/8/3K4 w - - 0 1");
        //Board board = Board.fromFen("r2qk2r/8/8/8/8/8/8/R2QK2R w KQkq - 0 1");
        boolean gameOver = false;
        while(!gameOver) {
            board = Board.gen(board, true);
            if(board.moveSize() != 0) {
                int randomMove = (int) (Math.random() * board.moveSize());
                System.out.println(board.moveNotation(randomMove));
                board = Board.makeMove(board, board.move(randomMove));
                System.out.println(board.toString());
                //Thread.sleep(200);
            } else {
                System.out.println("GAME OVER");
                gameOver = true;
            }
        }
        */
    }
}
