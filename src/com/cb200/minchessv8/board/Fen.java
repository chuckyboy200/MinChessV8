package com.cb200.minchessv8.board;

public class Fen {
    
    private final static int KING = 1;
	private final static int QUEEN = 2;
	private final static int ROOK = 3;
	private final static int BISHOP = 4;
	private final static int KNIGHT = 5;
	private final static int PAWN = 6;
	private final static int WHITE = 0;
	private final static int BLACK = 8;

	/**
	 *
	 * @author Charles Clark
	 *
	 * An enum used to keep track of the fen state while iterating through the characters of the fen string
	 *
	 */
	public enum FenState {
		BOARD, SIDE_TO_MOVE, CASTLING, ENPASSANT, HALF_MOVE_COUNT, MOVE_NUMBER
	};

	private Fen() {}

	/**
	 * Verifies that a fen string is valid
	 *
	 * @param fen The fen string
	 * @return True if the fen string is valid
	 */
	public static boolean verify(String fen) {
		FenState fenState = FenState.BOARD;
		int row = 0;
		int column = 0;
		boolean stateOn = false;
		for(int i = 0; i < fen.length(); i ++) {
			char c = fen.charAt(i);
			switch(fenState) {
				case BOARD: {
					switch(c) {
						case '/': {if(column != 8) return false; row ++; column = 0; break;}
						case 'k': {if(column > 7) return false; column ++; break;}
						case 'q': {if(column > 7) return false; column ++; break;}
						case 'r': {if(column > 7) return false; column ++; break;}
						case 'b': {if(column > 7) return false; column ++; break;}
						case 'n': {if(column > 7) return false; column ++; break;}
						case 'p': {if(column > 7) return false; column ++; break;}
						case 'K': {if(column > 7) return false; column ++; break;}
						case 'Q': {if(column > 7) return false; column ++; break;}
						case 'R': {if(column > 7) return false; column ++; break;}
						case 'B': {if(column > 7) return false; column ++; break;}
						case 'N': {if(column > 7) return false; column ++; break;}
						case 'P': {if(column > 7) return false; column ++; break;}
						case '1': {if(column > 7) return false; column ++; break;}
						case '2': {if(column > 6) return false; column += 2; break;}
						case '3': {if(column > 5) return false; column += 3; break;}
						case '4': {if(column > 4) return false; column += 4; break;}
						case '5': {if(column > 3) return false; column += 5; break;}
						case '6': {if(column > 2) return false; column += 6; break;}
						case '7': {if(column > 1) return false; column += 7; break;}
						case '8': {if(column > 0) return false; column += 8; break;}
						case ' ': {if(row != 7 && column != 8) return false; fenState = FenState.SIDE_TO_MOVE; break;}
						default: {System.out.println("Pieces"); return false;}
					}
					break;
				}
				case SIDE_TO_MOVE: {
					switch(c) {
						case 'w': {stateOn = true; break;}
						case 'b': {stateOn = true; break;}
						case '-': {stateOn = true; break;}
						case ' ': {if(!stateOn) return false; stateOn = false; fenState = FenState.CASTLING; break;}
						default: {System.out.println("Side To Move"); return false;}
					}
					break;
				}
				case CASTLING: {
					switch(Character.toLowerCase(c)) {
						case 'k': {stateOn = true; break;}
						case 'q': {stateOn = true; break;}
						case '-': {stateOn = true; break;}
						case ' ': {if(!stateOn) return false; stateOn = false; fenState = FenState.ENPASSANT; break;}
						default: {System.out.println("Castling"); return false;}
					}
					break;
				}
				case ENPASSANT: {
					if(c == '-') {stateOn = true; break;}
					if(c == ' ') {if(!stateOn) return false; stateOn = false; fenState = FenState.HALF_MOVE_COUNT; break;}
					String fileString = "abcdefgh";
					int eSquare = fileString.indexOf(c) + 8 * (Character.valueOf(fen.charAt(i + 1)) - 49);
					if(eSquare < 0 || eSquare > 63) {
						{System.out.println("EnPassant"); return false;}
					}
					i ++;
					stateOn = true;
					break;
				}
				case HALF_MOVE_COUNT: {

					break;
				}
				case MOVE_NUMBER: {

					break;
				}
			}
		}
		return true;
	}

	private static int skip(String fen, int num) {
		int index = 0;
		for(int i = 0; i < num; i ++) {
			index = fen.indexOf(32, index + 1);
		}
		return index + 1;
	}

	/**
	 * Get the piece positions from a fen string
	 *
	 * @param fen The fen string
	 * @return A 64 element array representing a chess board populated by pieces
	 */
	public static int[] getPieces(String fen) {
		int row = 7;
		int column = 0;
		int[] p = new int[64];
		for(int i = 0; i < fen.indexOf(' '); i ++) {
			char c = fen.charAt(i);
			int arrayPosition = (row << 3) | column;
			switch(c) {
				case '/': {row --; column = 0; break;}
				case 'k': {p[arrayPosition] = BLACK | KING; column ++; break;}
				case 'q': {p[arrayPosition] = BLACK | QUEEN; column ++; break;}
				case 'r': {p[arrayPosition] = BLACK | ROOK; column ++; break;}
				case 'b': {p[arrayPosition] = BLACK | BISHOP; column ++; break;}
				case 'n': {p[arrayPosition] = BLACK | KNIGHT; column ++; break;}
				case 'p': {p[arrayPosition] = BLACK | PAWN; column ++; break;}
				case 'K': {p[arrayPosition] = WHITE | KING; column ++; break;}
				case 'Q': {p[arrayPosition] = WHITE | QUEEN; column ++; break;}
				case 'R': {p[arrayPosition] = WHITE | ROOK; column ++; break;}
				case 'B': {p[arrayPosition] = WHITE | BISHOP; column ++; break;}
				case 'N': {p[arrayPosition] = WHITE | KNIGHT; column ++; break;}
				case 'P': {p[arrayPosition] = WHITE | PAWN; column ++; break;}
				case '1': {column ++; break;}
				case '2': {column += 2; break;}
				case '3': {column += 3; break;}
				case '4': {column += 4; break;}
				case '5': {column += 5; break;}
				case '6': {column += 6; break;}
				case '7': {column += 7; break;}
				case '8': {column += 8; break;}
			}
		}
		return p;
	}

	/**
	 * Get the player to move from a fen string
	 *
	 * @param fen The fen string
	 * @return True if white is moving, False if black is moving
	 */
	public static boolean getWhiteToMove(String fen) {
		return fen.charAt(skip(fen, 1)) == 'w';
	}

	/**
	 * Get the castling rights from a fen string
	 *
	 * @param fen The fen string
	 * @return An int whose 4 right-most bits contain the castling rights (1 = white kingside, 2 = white queenside, 4 = black kingside, 8 = black queenside)
	 */
	public static int getCastling(String fen) {
		int skip = skip(fen, 2);
		if(fen.charAt(skip) == '-') {
			return 0;
		}
		int c = 0;
		fen += "    ";
		for(int i = 0; i < 4; i ++){
            switch(fen.charAt(skip + i)) {
                case 'K': c |= 1; break;
                case 'Q': c |= 2; break;
                case 'k': c |= 4; break;
                case 'q': c |= 8; break;
            }
        }
		return c;
	}

	/**
	 * Get the EnPassant square if it exists
	 *
	 * @param fen The fen string
	 * @return The EnPassant square if it exists, otherise -1
	 */
	public static int getEnPassantSquare(String fen) {
		int skip = skip(fen, 3);
		if(fen.charAt(skip) == '-') {
			return -1;
		}
		String c = fen.substring(skip, skip + 2);
		String fileString = "abcdefgh";
		int file = fileString.indexOf(c.charAt(0));
		int rank = Character.valueOf(c.charAt(1)) - 49;
		return (rank << 3) | file;
	}

	/**
	 * Get the half move count for 50 move rule
	 *
	 * @param fen The fen string
	 * @return The half move count
	 */
	public static int getHalfMoveCount(String fen) {
		int skip = skip(fen, 4);
		return Integer.parseInt(fen.substring(skip, skip(fen, 5) - 1));
	}

	/**
	 *
	 * @param fen The fen string
	 * @return The full move count
	 */
	public static int getFullMoveCount(String fen) {
		int skip = skip(fen, 5);
		return Integer.parseInt(fen.substring(skip));
	}
}
