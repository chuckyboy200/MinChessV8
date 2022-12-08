package com.cb200.minchessv8.eval;

import com.cb200.minchessv8.Util.Value;
import com.cb200.minchessv8.board.Board;
import com.cb200.minchessv8.piece.Piece;

public class Eval {
    
    private Eval() {}

    public static int eval(Board board) {
        int player = board.player();
        int playerBit = player << 3;
        int otherBit = 8 ^ playerBit;
        int eval = Long.bitCount(board.bitboard(Piece.QUEEN | playerBit)) * Piece.VALUE[Piece.QUEEN] - Long.bitCount(board.bitboard(Piece.QUEEN | otherBit)) * Piece.VALUE[Piece.QUEEN]
        + Long.bitCount(board.bitboard(Piece.ROOK | playerBit)) * Piece.VALUE[Piece.ROOK]
        - Long.bitCount(board.bitboard(Piece.ROOK | otherBit)) * Piece.VALUE[Piece.ROOK]
        + Long.bitCount(board.bitboard(Piece.BISHOP | playerBit)) * Piece.VALUE[Piece.BISHOP]
        - Long.bitCount(board.bitboard(Piece.BISHOP | otherBit)) * Piece.VALUE[Piece.BISHOP]
        + Long.bitCount(board.bitboard(Piece.KNIGHT | playerBit)) * Piece.VALUE[Piece.KNIGHT]
        - Long.bitCount(board.bitboard(Piece.KNIGHT | otherBit)) * Piece.VALUE[Piece.KNIGHT]
        + Long.bitCount(board.bitboard(Piece.PAWN | playerBit)) * Piece.VALUE[Piece.PAWN]
        - Long.bitCount(board.bitboard(Piece.PAWN | otherBit)) * Piece.VALUE[Piece.PAWN];
        for(int pieceType = Piece.KING; pieceType <= Piece.PAWN; pieceType ++) {
            for(int p = Value.WHITE; p <= Value.BLACK; p ++) {
                int perspective = p == player ? 1 : -1;
                switch(pieceType) {
                    case Piece.KING: {
                        eval += evaluateKing(board.bitboard(Piece.KING | (p << 3)), p, perspective);
                    }
                    case Piece.QUEEN: {
                        eval += evaluateQueen(board.bitboard(Piece.QUEEN | (p << 3)), p, perspective);
                    }
                    case Piece.ROOK: {
                        eval += evaluateRook(board.bitboard(Piece.ROOK | (p << 3)), p, perspective);
                    }
                    case Piece.BISHOP: {
                        eval += evaluateBishop(board.bitboard(Piece.BISHOP | (p << 3)), p, perspective);
                    }
                    case Piece.KNIGHT: {
                        eval += evaluateKnight(board.bitboard(Piece.KNIGHT | (p << 3)), p, perspective);
                    }
                    case Piece.PAWN: {
                        eval += evaluatePawn(board.bitboard(Piece.PAWN | (p << 3)), p, perspective);
                    }
                }
            }
        }
        return eval;
    }

    private static int evaluateKing(long king, int p, int perspective) {
        int square = Long.numberOfTrailingZeros(king);
        int eval = Bonus.BONUS[Piece.KING][p][square][0] * perspective;

        return eval;
    }

    private static int evaluateQueen(long queens, int p, int perspective) {
        int eval = 0;
        while(queens != 0L) {
            int square = Long.numberOfTrailingZeros(queens);
            queens &= queens - 1;
            eval += Bonus.BONUS[Piece.QUEEN][p][square][0] * perspective;
        }
        return eval;
    }

    private static int evaluateRook(long rooks, int p, int perspective) {
        int eval = 0;
        while(rooks != 0L) {
            int square = Long.numberOfTrailingZeros(rooks);
            rooks &= rooks - 1;
            eval += Bonus.BONUS[Piece.ROOK][p][square][0] * perspective;
        }
        return eval;
    }

    private static int evaluateBishop(long bishops, int p, int perspective) {
        int eval = 0;
        while(bishops != 0L) {
            int square = Long.numberOfTrailingZeros(bishops);
            bishops &= bishops - 1;
            eval += Bonus.BONUS[Piece.BISHOP][p][square][0] * perspective;
        }
        return eval;
    }

    private static int evaluateKnight(long knights, int p, int perspective) {
        int eval = 0;
        while(knights != 0L) {
            int square = Long.numberOfTrailingZeros(knights);
            knights &= knights - 1;
            eval += Bonus.BONUS[Piece.KNIGHT][p][square][0] * perspective;
        }
        return eval;
    }

    private static int evaluatePawn(long pawns, int p, int perspective) {
        int eval = 0;
        while(pawns != 0L) {
            int square = Long.numberOfTrailingZeros(pawns);
            pawns &= pawns - 1;
            eval += Bonus.BONUS[Piece.PAWN][p][square][0] * perspective;
        }
        return eval;
    }

}
