package com.cb200.minchessv8.search;

import com.cb200.minchessv8.Util.Value;
import com.cb200.minchessv8.board.Board;
import com.cb200.minchessv8.eval.Eval;
import com.cb200.minchessv8.piece.Piece;

public class Search {
    
    private static int counter;
    private static int qCounter;

    private Search() {}

    public static int bestMove(Board board, int depth) {
        if(!board.hasGenerated()) {
            return -1;
        }
        int player = board.player();
        int bestMove = -1;
        int bestEval = -99999;
        int moveNum = 0;
        for(int i = 0; i < board.moveSize(); i ++) {
            Board boardAfterMove = Board.makeMove(board, board.move(i));
            if(Board.isSquareAttacked(boardAfterMove, Long.numberOfTrailingZeros(boardAfterMove.bitboard(Piece.KING | (player << 3))), 1 ^ player)) {
                continue;
            }
            counter = 0;
            qCounter = 0;
            moveNum ++;
            System.out.print(moveNum + ": " + board.moveNotation(i) + " ");
            int eval = -alphaBeta(boardAfterMove, depth, 0, -99999, 99999, new int[0]);
            System.out.println(eval + " " + counter + " " + qCounter);
            if(eval > bestEval) {
                bestEval = eval;
                bestMove = i;
            }
        }
        return bestMove;
    }

    private static int alphaBeta(Board board, int depth, int ply, int alpha, int beta, int[] pv) {
        if(depth == 0) {
            return quiesce(board, ply, alpha, beta, pv);
        }
        Board boardGen = Board.gen(board, false);
        int player = boardGen.player();
        for(int i = 0; i < boardGen.moveSize(); i ++) {
            Board boardAfterMove = Board.makeMove(boardGen, boardGen.move(i));
            if(Board.isSquareAttacked(boardAfterMove, Long.numberOfTrailingZeros(boardAfterMove.bitboard(Piece.KING | (player << 3))), 1 ^ player)) {
                continue;
            }
            counter ++;
            int eval = -alphaBeta(boardAfterMove, depth - 1, ply + 1, -beta, -alpha, pv);
            if(eval >= beta) {
                return beta;
            }
            if(eval > alpha) {
                alpha = eval;
            }
        }
        return alpha;
    }

    private static int quiesce(Board board, int ply, int alpha, int beta, int[] pv) {
        int standPat = Eval.eval(board);
        if(standPat >= beta) {
			return beta;
		}
		if(standPat + 900 < alpha) {
			return alpha;
		}
		if(standPat > alpha) {
			alpha = standPat;
		}
        Board boardGen = Board.genTactical(board, false);
        int player = boardGen.player();
        for(int i = 0; i < boardGen.moveSize(); i ++) {
            Board boardAfterMove = Board.makeMove(boardGen, boardGen.move(i));
            if(Board.isSquareAttacked(boardAfterMove, Long.numberOfTrailingZeros(boardAfterMove.bitboard(Piece.KING | (player << 3))), 1 ^ player)) {
                continue;
            }
            qCounter ++;
            int eval = -quiesce(boardAfterMove, ply + 1, -beta, -alpha, pv);
            if(eval >= beta) {
                return beta;
            }
            if(eval > alpha) {
                alpha = eval;
            }
        }
        return alpha;
    }

}
