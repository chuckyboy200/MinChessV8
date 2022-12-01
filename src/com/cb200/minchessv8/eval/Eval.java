package com.cb200.minchessv8.eval;

import com.cb200.minchessv8.board.Board;
import com.cb200.minchessv8.piece.Piece;

public class Eval {
    
    private Eval() {}

    public static int eval(Board board) {
        int player = board.player();
        int playerBit = player << 3;
        int otherBit = 8 ^ playerBit;
        int eval = Long.bitCount(board.bitboard(Piece.QUEEN | playerBit)) * 900 - Long.bitCount(board.bitboard(Piece.QUEEN | otherBit)) * 900
        + Long.bitCount(board.bitboard(Piece.ROOK | playerBit)) * 500
        - Long.bitCount(board.bitboard(Piece.ROOK | otherBit)) * 500
        + Long.bitCount(board.bitboard(Piece.BISHOP | playerBit)) * 300
        - Long.bitCount(board.bitboard(Piece.BISHOP | otherBit)) * 300
        + Long.bitCount(board.bitboard(Piece.KNIGHT | playerBit)) * 300
        - Long.bitCount(board.bitboard(Piece.KNIGHT | otherBit)) * 300
        + Long.bitCount(board.bitboard(Piece.PAWN | playerBit)) * 100
        - Long.bitCount(board.bitboard(Piece.PAWN | otherBit)) * 100;
        return eval;
    }

}
