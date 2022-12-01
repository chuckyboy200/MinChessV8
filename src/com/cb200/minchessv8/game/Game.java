package com.cb200.minchessv8.game;

import com.cb200.minchessv8.board.Board;

public class Game {
    
    private Board board;
    private int searchDepth;
    private int numPlayers;

    public static class Builder {

        private String fenString;
        private int searchDepth;
        private int numPlayers;

        public Builder() {
            this.fenString = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
            this.searchDepth = 1;
            this.numPlayers = 2;
        }

        public Builder fenString(String fen) {
            this.fenString = fen;
            return this;
        }

        public Builder searchDepth(int _searchDepth) {
            this.searchDepth = _searchDepth;
            return this;
        }

        public Builder numPlayers(int _numPlayers) {
            this.numPlayers = _numPlayers;
            return this;
        }

        public Game build() {
            return new Game(this);
        }

    }

    private Game() {}

    private Game(Builder builder) {
        this.board = Board.fromFen(builder.fenString);
        this.searchDepth = builder.searchDepth;
        this.numPlayers = builder.numPlayers;
    }

    public void drawBoard() {
        Board.draw(this.board);
    }

    public void showMoves() {
        Board.drawMoveList(this.board);
    }

    public void generateMoves(boolean legal) {
        this.board = Board.gen(this.board, legal);
    }

    public int moveListSize() {
        return this.board.moveSize();
    }

    public int move(int index) {
        return this.board.move(index);
    }

    public String moveNotation(int index) {
        return this.board.moveNotation(index);
    }

    public void makeMove(int index) {
        this.board = Board.makeMove(this.board, this.board.move(index));
    }

    public int player() {
        return this.board.player();
    }

    public int fullMoveCount() {
        return this.board.fullMoveCount();
    }

    @Override
    public String toString() {
        return this.board.toString();
    }

}
