package cb200.minchessv8.gen;

import java.util.Arrays;

import cb200.minchessv8.Util.B;
import cb200.minchessv8.board.Board;

public class Gen {
    
    private Gen() {}

	/**
	 * Generate all moves for the current board state
	 *
	 * @param board An immutable representation of the current board state
	 * @param legal true if generating legal moves, false if generating pseudo legal moves
	 * @return A 3xN array containing the requested moves ([0][] = start squares, [1][] = target squares, [2][] = promotion piece)
	 */
	public static int[] moves(Board board, boolean legal) {
		//System.out.println("Gen.moves: Generating...");
		int player = board.player();
		int playerBit = player << 3;
		long king = board.bitboard(playerBit | 1);
		long queens = board.bitboard(playerBit | 2);
		long rooks = board.bitboard(playerBit | 3);
		long bishops = board.bitboard(playerBit | 4);
		long knights = board.bitboard(playerBit | 5);
		long pawns = board.bitboard(playerBit | 6);
		long allOccupancy = board.bitboard(0) | board.bitboard(8);
		int[] moves = new int[127];
		int moveListLength = 0;
		moveListLength = getKingMoves(board, moves, king, player, moveListLength, allOccupancy);
		if(queens != 0L) {
			moveListLength = getQueenMoves(board, moves, queens, player, moveListLength, allOccupancy);
		}
		if(rooks != 0L) {
			moveListLength = getRookMoves(board, moves, rooks, player, moveListLength, allOccupancy);
		}
		if(bishops != 0L) {
			moveListLength = getBishopMoves(board, moves, bishops, player, moveListLength, allOccupancy);
		}
		if(knights != 0L) {
			moveListLength = getKnightMoves(board, moves, knights, player, moveListLength);
		}
		if(pawns != 0L) {
			moveListLength = getPawnMoves(board, moves, pawns, player, moveListLength, allOccupancy);
		}
		return legal ? purgeIllegalMoves(board, moves, moveListLength) : Arrays.copyOf(moves, moveListLength);
	}

	private static int getKingMoves(Board board, int[] moves, long king, int player, int moveListLength, long allOccupancy) {
		int square = Long.numberOfTrailingZeros(king);
		long result = B.KING_ATTACK[square] & ~board.bitboard(player << 3);
		while(result != 0L) {
			addMove(moves, square, Long.numberOfTrailingZeros(result), moveListLength);
			moveListLength ++;
			result &= result - 1;
		}
		if(!(board.kingSide(player) || board.queenSide(player))) {
			return moveListLength;
		}
		if(Board.isSquareAttacked(board, square, 1 ^ player)) {
			return moveListLength;
		}
		if(board.kingSide(player)) {
			if((allOccupancy & B.CASTLE[player][0]) == 0L && !Board.isSquareAttacked(board, square + 1, 1 ^ player)) {
				addMove(moves, square, square + 2, moveListLength);
				moveListLength ++;
			}
		}
		if(board.queenSide(player)) {
			if((allOccupancy & B.CASTLE[player][1]) == 0L && !Board.isSquareAttacked(board, square - 1, 1 ^ player)) {
				addMove(moves, square, square - 2, moveListLength);
				moveListLength ++;
			}
		}
		return moveListLength;
	}

	private static int getQueenMoves(Board board, int[] moves, long queens, int player, int moveListLength, long allOccupancy) {
		int playerBit = player << 3;
		while(queens != 0L) {
			int square = Long.numberOfTrailingZeros(queens);
			queens &= queens - 1;
			long result = Magic.queenMoves(square, allOccupancy) & ~board.bitboard(playerBit);
			while(result != 0L) {
				addMove(moves, square, Long.numberOfTrailingZeros(result), moveListLength);
				moveListLength ++;
				result &= result - 1;
			}
		}
		return moveListLength;
	}

	private static int getRookMoves(Board board, int[] moves, long rooks, int player, int moveListLength, long allOccupancy) {
		int playerBit = player << 3;
		while(rooks != 0L) {
			int square = Long.numberOfTrailingZeros(rooks);
			rooks &= rooks - 1;
			long result = Magic.rookMoves(square, allOccupancy) & ~board.bitboard(playerBit);
			while(result != 0L) {
				addMove(moves, square, Long.numberOfTrailingZeros(result), moveListLength);
				moveListLength ++;
				result &= result - 1;
			}
		}
		return moveListLength;
	}

	private static int getBishopMoves(Board board, int[] moves, long bishops, int player, int moveListLength, long allOccupancy) {
		int playerBit = player << 3;
		while(bishops != 0L) {
			int square = Long.numberOfTrailingZeros(bishops);
			bishops &= bishops - 1;
			long result = Magic.bishopMoves(square, allOccupancy) & ~board.bitboard(playerBit);
			while(result != 0L) {
				addMove(moves, square, Long.numberOfTrailingZeros(result), moveListLength);
				moveListLength ++;
				result &= result - 1;
			}
		}
		return moveListLength;
	}

	private static int getKnightMoves(Board board, int[] moves, long knights, int player, int moveListLength) {
		int playerBit = player << 3;
		while(knights != 0L) {
			int square = Long.numberOfTrailingZeros(knights);
			knights &= knights - 1;
			long result = B.LEAP_ATTACK[square] & ~board.bitboard(playerBit);
			while(result != 0L) {
				addMove(moves, square, Long.numberOfTrailingZeros(result), moveListLength);
				moveListLength ++;
				result &= result - 1;
			}
		}
		return moveListLength;
	}

	private static int getPawnMoves(Board board, int[] moves, long pawns, int player, int moveListLength, long allOccupancy) {
		int playerBit = player << 3;
		int eSquare = board.eSquare();
		while(pawns != 0L) {
			int square = Long.numberOfTrailingZeros(pawns);
			pawns &= pawns - 1;
			int pawnRank = square >>> 3;
			if((allOccupancy & B.PAWN_SINGLE_PUSH[player][square]) == 0L) {
				if(pawnRank == 6 - player * 5) {
					addPromoteMoves(moves, square, square + 8 - (player << 4), playerBit, moveListLength);
					moveListLength += 4;
				} else {
					addMove(moves, square, square + 8 - (player << 4), moveListLength);
					moveListLength ++;
					if(pawnRank == 1 + player * 5) {
						if((allOccupancy & B.PAWN_DOUBLE_PUSH[playerBit >>> 3][square]) == 0L) {
							addMove(moves, square, square + 16 - (player << 5), moveListLength);
							moveListLength ++;
						}
					}
				}
			}
			long result = B.PAWN_ATTACK[player][square] & (board.bitboard(8 ^ playerBit) | (board.eSquare() != -1 ? (1L << eSquare) : 0));
			while(result != 0L) {
				int targetSquare = Long.numberOfTrailingZeros(result);
				result &= result - 1;
				if(targetSquare == eSquare) {
					addMove(moves, square, targetSquare, moveListLength);
					moveListLength ++;
				} else {
					if(pawnRank == 6 - player * 5) {
						addPromoteMoves(moves, square, targetSquare, playerBit, moveListLength);
						moveListLength += 4;
					} else {
						addMove(moves, square, targetSquare, moveListLength);
						moveListLength ++;
					}
				}
			}
		}
		return moveListLength;
	}

	private static void addMove(int[] moves, int start, int target, int moveListLength) {
		moves[moveListLength] = moves[moveListLength] | start | (target << 6);
	}

	private static void addPromoteMoves(int[] moves, int start, int target, int playerBit, int moveListLength) {
		moves[moveListLength] = moves[moveListLength ++] | start | (target << 6) | ((2 | playerBit) << 12);
		moves[moveListLength] = moves[moveListLength ++] | start | (target << 6) | ((3 | playerBit) << 12);
		moves[moveListLength] = moves[moveListLength ++] | start | (target << 6) | ((4 | playerBit) << 12);
		moves[moveListLength] = moves[moveListLength ++] | start | (target << 6) | ((5 | playerBit) << 12);
	}

	private static int[] purgeIllegalMoves(Board board, int[] moves, int moveListLength) {
		int[] legalMoveList = new int[127];
		int legalMoveListLength = 0;
		int player = board.player();
		for(int i = 0; i < moveListLength; i ++) {
			Board tempBoard = Board.makeMove(board, moves[i]);
			if(!Board.isSquareAttacked(tempBoard, Long.numberOfTrailingZeros(tempBoard.bitboard(1 | (player << 3))), 1 ^ player)) {
				legalMoveList[legalMoveListLength ++] = moves[i];
			}
		}
		return Arrays.copyOf(legalMoveList, legalMoveListLength);
	}


}
