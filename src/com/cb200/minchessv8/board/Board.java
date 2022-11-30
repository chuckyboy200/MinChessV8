package com.cb200.minchessv8.board;

import java.util.Arrays;

import com.cb200.minchessv8.Util.B;
import com.cb200.minchessv8.Util.Value;
import com.cb200.minchessv8.gen.Gen;
import com.cb200.minchessv8.gen.Magic;
import com.cb200.minchessv8.piece.Piece;
import com.cb200.minchessv8.piece.Piece.LongType;
import com.cb200.minchessv8.zobrist.Zobrist;

/**
 * An immutable instance of a board representation consisting of:
 * 
 * <p>A bitboard (long) for each piece (WHITE KING, BLACK KING, etc.) and an occupancy (ALL WHITE, ALL BLACK);
 * <p>Fields for Side to Move, Castling Rights, EnPassant Square, Half Move Count, Full Move Count, a 64-bit hash key (for transposition tables), and an array of moves.
 * 
 * <p>To create a new instance, use either of the two static creating methods (Board.fromFen(String fenString) or Board.gen(Board board, boolean Legal)) or the makeMove method (Board.makeMove(Board board, int move)). Any of these methods will automatically return an immutable instance relating to the method used.
 * 
 * @author Charles Clark
 * 
 */
public class Board {

    private final static String FILE = "abcdefgh";
    
    private final long[] bitboard;
    private final int player;
    private final int castling;
    private final int eSquare;
    private final int halfMoveCount;
    private final int fullMoveCount;
    private final long key;
    private final int[] move;
    private final boolean hasGenerated;

    private Board() {
        this.bitboard = new long[15];
        this.player = Value.WHITE;
        this.castling = Value.NONE;
        this.eSquare = Value.INVALID;
        this.halfMoveCount = 0;
        this.fullMoveCount = 1;
        this.key = 0L;
        this.move= new int[Value.EMPTY];
        this.hasGenerated = false;
    }

    private Board(long[] bitboards, int _player, int _castling, int _eSquare, int _halfMoveCount, int _fullMoveCount, long _key) {
        this.bitboard = Arrays.copyOf(bitboards, 15);
        this.player = _player;
        this.castling = _castling;
        this.eSquare = _eSquare;
        this.halfMoveCount = _halfMoveCount;
        this.fullMoveCount = _fullMoveCount;
        this.key = _key;
        this.move = new int[Value.EMPTY];
        this.hasGenerated = false;
    }

    private Board(Board board, int[] moves) {
        this.bitboard = Arrays.copyOf(board.bitboard, 15);
        this.player = board.player;
        this.castling = board.castling;
        this.eSquare = board.eSquare;
        this.halfMoveCount = board.halfMoveCount;
        this.fullMoveCount = board.fullMoveCount;
        this.key = board.key;
        this.move = Arrays.copyOf(moves, moves.length);
        this.hasGenerated = true;
    }
    /**
     * Create a new immutable Board object from a valid fen string
     * 
     * @param fen A valid fen string
     * @return a new immutable Board object
     */
    public static Board fromFen(String fen) {
		int[] pieces = Fen.getPieces(fen);
		long[] _bitboards = new long[15];
		for(int i = 0; i < 64; i ++) {
			int piece = pieces[i];
			if(piece != 0) {
				_bitboards[piece] |= (1L << i);
				_bitboards[piece & 8] |= (1L << i);
			}
		}
		long key = Zobrist.getKey(pieces, Fen.getWhiteToMove(fen), (Fen.getCastling(fen) & Value.KINGSIDE_BIT[Value.WHITE]) > 0, (Fen.getCastling(fen) & Value.QUEENSIDE_BIT[Value.WHITE]) > 0, (Fen.getCastling(fen) & Value.KINGSIDE_BIT[Value.BLACK]) > 0, (Fen.getCastling(fen) & Value.QUEENSIDE_BIT[Value.BLACK]) > 0,
			Fen.getEnPassantSquare(fen));
		return new Board(_bitboards, Fen.getWhiteToMove(fen) ? Value.WHITE : Value.BLACK, Fen.getCastling(fen), Fen.getEnPassantSquare(fen), Fen.getHalfMoveCount(fen), Fen.getFullMoveCount(fen), key);
	}

    /**
     * Create a copy of a Board object but also generate it's legal or pseudo-legal moves
     * 
     * @param board A Board object
     * @param legal true if only legal moves are generated, false if all pseudo-legal moves are generated 
     * @return A copy of the passed Board object with a list of generated moves, or if a list of moves has already been generated, return the passed Board object
     */
    public static Board gen(Board board, boolean legal) {
		return board.hasGenerated ? board : new Board(board, Gen.moves(board, legal));
	}

    /**
     * Get the int value of a piece from a square. If there is no piece on that square, return 0
     * 
     * @param board A Board object
     * @param square The square number (A1 = 0, H8 = 63)
     * @return The int value of the piece on the square, or -1 if there is none
     */
    public static int getPiece(Board board, int square) {
		long squareBit = 1L << square;
		return (board.bitboard[Value.WHITE_BIT] & squareBit) != 0L ? // is the white bitboard square occupied?
				(
				// is the white pawn bitboard square occupied? If so, return white pawn value
				(board.bitboard[Piece.WHITE_PAWN] & squareBit) != 0L ? Piece.WHITE_PAWN :
				// is the white knight bitboard square occupied? If so, return white knight value
				(board.bitboard[Piece.WHITE_KNIGHT] & squareBit) != 0L ? Piece.WHITE_KNIGHT :
				// white bishop
				(board.bitboard[Piece.WHITE_BISHOP] & squareBit) != 0L ? Piece.WHITE_BISHOP :
				// white rook
				(board.bitboard[Piece.WHITE_ROOK] & squareBit) != 0L ? Piece.WHITE_ROOK :
				// white queen
				(board.bitboard[Piece.WHITE_QUEEN] & squareBit) != 0L ? Piece.WHITE_QUEEN :
				// only option left is white king
						Piece.WHITE_KING) :
				(board.bitboard[Value.BLACK_BIT] & squareBit) != 0L ? // is the black bitboard square occupied?
				(
				// same as above but for black pieces
				(board.bitboard[Piece.BLACK_PAWN] & squareBit) != 0L ? Piece.BLACK_PAWN :
				(board.bitboard[Piece.BLACK_KNIGHT] & squareBit) != 0L ? Piece.BLACK_KNIGHT :
				(board.bitboard[Piece.BLACK_BISHOP] & squareBit) != 0L ? Piece.BLACK_BISHOP :
				(board.bitboard[Piece.BLACK_ROOK] & squareBit) != 0L ? Piece.BLACK_ROOK :
				(board.bitboard[Piece.BLACK_QUEEN] & squareBit) != 0L ? Piece.BLACK_QUEEN :
                Piece.BLACK_KING) : Value.NONE
		;
	}

    /**
     * Return true if the attackedSquare is attacked by a player
     * 
     * @param board A Board object
     * @param attackedSquare The square under attack (A1 = 0, H8 = 63)
     * @param player The int value of the player (WHITE = 0, BLACK = 1)
     * @return true if the square is attacked, false if the square is not attacked
     */
    public static boolean isSquareAttacked(Board board, int attackedSquare, int player) {
		int playerBit = player << 3;
		if((B.LEAP_ATTACK[attackedSquare] & board.bitboard[Piece.KNIGHT | playerBit]) != 0L) {
			return true;
		}
		if((B.PAWN_ATTACK[1 ^ player][attackedSquare] & board.bitboard[Piece.PAWN | playerBit]) != 0L) {
			return true;
		}
		if((B.KING_ATTACK[attackedSquare] & board.bitboard[Piece.KING | playerBit]) != 0L) {
			return true;
		}
		long allOccupancy = board.bitboard[Value.WHITE_BIT] | board.bitboard[Value.BLACK_BIT];
		long result = Magic.queenMoves(attackedSquare, allOccupancy) & board.bitboard[Piece.QUEEN | playerBit];
		if(Long.bitCount(result) > 0) {
			return true;
		}
		result = Magic.rookMoves(attackedSquare, allOccupancy) & board.bitboard[Piece.ROOK | playerBit];
		if(Long.bitCount(result) > 0) {
			return true;
		}
		result = Magic.bishopMoves(attackedSquare, allOccupancy) & board.bitboard[Piece.BISHOP | playerBit];
		if(Long.bitCount(result) > 0) {
			return true;
		}
		return false;
	}

    /**
     * Return a bitboard for a piece or occupancy
     * 
     * @param index index into the bitboard array
     * @return a bitboard for a piece or occupancy
     */
    public long bitboard(int index) {
        return this.bitboard[index];
    }

    /**
     * Return the side to move player
     * 
     * @return The int value of the player (WHITE = 0, BLACK = 1)
     */
    public int player() {
        return this.player;
    }

    /**
     * Return the int representation of the overall castling rights. A bit which is set to 1 represents castling is possible
     * 
     * <p> Bit 0 set = White KingSide
     * <p> Bit 1 set = White QueenSide
     * <p> Bit 2 set = Black KingSide
     * <p> Bit 3 set = Black QueenSide
     * 
     * @return the int representation of the overall castling rights
     */
    public int castling() {
        return this.castling;
    }

    /**
     * Return true if kingside castling for a player is possible
     * 
     * @param player The int value of the player (WHITE = 0, BLACK = 1)
     * @return true if kingside castling is possible, false if not
     */
    public boolean kingSide(int player) {
        return (this.castling & Value.KINGSIDE_BIT[player]) != 0;
    }

    /**
     * Return true if queenside castling for a player is possible
     * 
     * @param player The int value of the player (WHITE = 0, BLACK = 1)
     * @return true if queenside castling is possible, false if not
     */
    public boolean queenSide(int player) {
        return (this.castling & Value.QUEENSIDE_BIT[player]) != 0;
    }

    /**
     * Return the current enpassant square if there is one. Otherwise, return -1
     * 
     * @return the current enpassant square (A1 = 0, H8 = 63) or -1 if there isn't one
     */
    public int eSquare() {
        return this.eSquare;
    }

    /**
     * The half move count for the purposes of the 50 move draw rule. For each move made, it is incremented unless that move is a capture or pawn move, in which case it is set to 0
     * 
     * @return the current half move count
     */
    public int halfMoveCount() {
        return this.halfMoveCount;
    }

    /**
     * The full move count. This starts at 1 at the beginning of a game and is incremented after each black move
     * 
     * @return the current full move count
     */
    public int fullMoveCount() {
        return this.fullMoveCount;
    }

    /**
     * The 64 bit zobrist key for the position
     * 
     * @return the 64 bit zobrist key
     */
    public long key() {
        return this.key;
    }

    /**
     * The number of generated moves for this Board object or -1 if there are no generated moves
     * 
     * @return the number of generated moves, or -1 if there are none
     */
    public int moveSize() {
        return this.hasGenerated ? this.move.length : Value.INVALID;
    }

    /**
     * Return a move from the array of generated moves or -1 if there are no generated moves
     * 
     * @param index The index into the move array
     * @return a move from the array of generated moves, or -1 if there are none
     */
    public int move(int index) {
        return this.hasGenerated ? this.move[index] : Value.INVALID;
    }

    /**
     * Returns whether this Board object has generated moves.
     * 
     * @return true if this Board object has generated moves, false if not
     */
    public boolean hasGenerated() {
        return this.hasGenerated;
    }

    /**
     * Return a string representing the algebraic format for a move (e.g. e1g1)
     * 
     * @param index The index into the move array
     * @return a string representing the algebraic format for a move
     */
    public String moveAlgebraic(int index) {
        return FILE.charAt(this.move[index] & 7) + Integer.toString(((this.move[index] & 0x3f) >>> 3) + 1) + FILE.charAt((this.move[index] >>> 6) & 7) + Integer.toString(((this.move[index] >>> 6 & 0x3f) >>> 3) + 1);
    }

    /**
     * Return a string representing the notation format for a move (e.g. O-O, Nb3)
     * 
     * @param index The index into the move array
     * @return a string representing the notation format for a move
     */
    public String moveNotation(int index) {
        int startSquare = this.move[index] & 0x3f;
        int targetSquare = (this.move[index] >>> 6) & 0x3f;
		int startType = getPiece(this, startSquare) & Piece.TYPE;
        int playerBit = this.player << 3;
        String notation = "";
		switch(startType) {
			case Piece.KING: {
				if(Math.abs(startSquare - targetSquare) == 2) {
					if((targetSquare & 7) == 6) {
						return "O-O";
					} else {
						return "O-O-O";
					}
				}
				notation = "K";
				break;
			}
			case Piece.QUEEN: {
				notation = "Q";
                long queens = this.bitboard[Piece.QUEEN | playerBit] ^ (1L << startSquare);
                long allOccupancy = this.bitboard[Value.WHITE_BIT] | this.bitboard[Value.BLACK_BIT];
                while(queens != 0L) {
                    int square = Long.numberOfTrailingZeros(queens);
                    queens &= queens - 1;
                    if(((allOccupancy & B.RANK_FILE_ATTACK[square][targetSquare] ^ B.RANK_FILE_BETWEEN[square][targetSquare]) == B.RANK_FILE_ATTACK[square][targetSquare] && B.RANK_FILE_ATTACK[square][targetSquare] != 0L) ||
                    ((allOccupancy & B.DIAG_ATTACK[square][targetSquare] ^ B.DIAG_BETWEEN[square][targetSquare]) == B.DIAG_ATTACK[square][targetSquare] && B.DIAG_ATTACK[square][targetSquare] != 0L)) {
                        if((square & 7) == (startSquare & 7)) {
                            notation += Integer.toString((startSquare >>> 3) + 1);
                        } else {
                            notation += FILE.charAt(startSquare & 7);
                        }
                        break;
                    }
                }
				break;
			}
			case Piece.ROOK: {
				notation = "R";
				long rooks = this.bitboard[Piece.ROOK | playerBit] ^ (1L << startSquare);
                long allOccupancy = this.bitboard[Value.WHITE_BIT] | this.bitboard[Value.BLACK_BIT];
                while(rooks != 0L) {
                    int square = Long.numberOfTrailingZeros(rooks);
                    rooks &= rooks - 1;
                    if((allOccupancy & B.RANK_FILE_ATTACK[square][targetSquare] ^ B.RANK_FILE_BETWEEN[square][targetSquare]) == B.RANK_FILE_ATTACK[square][targetSquare] && B.RANK_FILE_ATTACK[square][targetSquare] != 0L) {
                        if((square & 7) == (startSquare & 7)) {
                            notation += Integer.toString((startSquare >>> 3) + 1);
                        } else {
                            notation += FILE.charAt(startSquare & 7);
                        }
                        break;
                    }
                }
				break;
			}
			case Piece.BISHOP: {
				notation = "B";
				long bishops = this.bitboard[Piece.BISHOP | playerBit] ^ (1L << startSquare);
                long allOccupancy = this.bitboard[Value.WHITE_BIT] | this.bitboard[Value.BLACK_BIT];
                while(bishops != 0L) {
                    int square = Long.numberOfTrailingZeros(bishops);
                    bishops &= bishops - 1;
                    if((allOccupancy & B.DIAG_ATTACK[square][targetSquare] ^ B.DIAG_BETWEEN[square][targetSquare]) == B.DIAG_ATTACK[square][targetSquare] && B.DIAG_ATTACK[square][targetSquare] != 0L) {
                        if((square & 7) == (startSquare & 7)) {
                            notation += Integer.toString((startSquare >>> 3) + 1);
                        } else {
                            notation += FILE.charAt(startSquare & 7);
                        }
                        break;
                    }
                }
				break;
			}
			case Piece.KNIGHT: {
				notation = "N";
				if(Long.bitCount(B.LEAP_ATTACK[targetSquare] & this.bitboard[Piece.KNIGHT | playerBit]) > 1) {
					if(Long.bitCount(B.FILE[startSquare & 7] & this.bitboard[Piece.KNIGHT | playerBit]) > 1) {
						notation += Integer.toString((startSquare >>> 3) + 1);
					} else {
						notation += FILE.charAt(startSquare & 7);
					}
				}
				break;
			}
			case Piece.PAWN: {
				notation = "";
                break;
			}
            default: break;
		}
		if(getPiece(this, targetSquare) != 0 || targetSquare == this.eSquare) {
			if(startType == Piece.PAWN) {
				notation += FILE.charAt(startSquare & 7);
			}
			notation += "x";
		}
		notation += FILE.charAt(targetSquare & 7) + Integer.toString((targetSquare >>> 3) + 1);
		if((this.move[index] >>> 12) != Value.NONE) {
			notation += "=";
			switch((this.move[index] >>> 12) & Piece.TYPE) {
				case Piece.QUEEN: notation += "Q"; break;
				case Piece.ROOK: notation += "R"; break;
				case Piece.BISHOP: notation += "B"; break;
				case Piece.KNIGHT: notation += "N"; break;
                default: break;
			}
		}
		Board tempBoard = Board.makeMove(this, this.move[index]);
		if(Board.isSquareAttacked(tempBoard, Long.numberOfTrailingZeros(tempBoard.bitboard[Piece.KING | (8 ^ playerBit)]), this.player)) {
			tempBoard = Board.gen(tempBoard, true);
			if(tempBoard.moveSize() == 0) {
				notation += "#";
			} else {
				notation += "+";
			}
		}
		return notation;
    }
    /**
     * 
     * 
     * @param board
     * @param move
     * @return
     */
    public static Board makeMove(Board board, int move) {
        long[] bitboards = Arrays.copyOf(board.bitboard, 15);
        long key = board.key;
        int startSquare = move & 0x3f;
        int startPiece = getPiece(board, startSquare);
        int startPieceType = startPiece & Piece.TYPE;
        int targetSquare = (move >>> 6) & 0x3f;
        int targetPiece = getPiece(board, targetSquare);
        int halfMoveCount = board.halfMoveCount + 1;
        int castling = board.castling;
        int eSquare = board.eSquare;
        if(eSquare != Value.INVALID) {
            key ^= Zobrist.ENPASSANT_FILE[eSquare & 7];
            eSquare = Value.INVALID;
        } 
        switch(startPieceType) {
            case Piece.QUEEN:
            case Piece.BISHOP:
            case Piece.KNIGHT: {
                long startSquareBit = 1L << startSquare;
                long targetSquareBit = 1L << targetSquare;
                int player = board.player;
                bitboards[startPiece] ^= startSquareBit | targetSquareBit;
                bitboards[player << 3] ^= startSquareBit | targetSquareBit;
                key ^= Zobrist.PIECE[startPieceType][player][startSquare] | Zobrist.PIECE[startPieceType][player][targetSquare];
                if(targetPiece != Value.NONE) {
                    halfMoveCount = 0;
                    int other = 1 ^ player;
                    bitboards[targetPiece] ^= targetSquareBit;
                    bitboards[other << 3] ^= targetSquareBit;
                    key ^= Zobrist.PIECE[targetPiece & Piece.TYPE][other][targetSquare];
                }
                break;
            }
            case Piece.KING: {
                int player = board.player;
                int playerBit = player << 3;
                int playerKingSideBit = Value.KINGSIDE_BIT[player];
                int playerQueenSideBit = Value.QUEENSIDE_BIT[player];
                boolean playerKingSideCastling = (castling & playerKingSideBit) != Value.NONE;
                boolean playerQueenSideCastling = (castling & playerQueenSideBit) != Value.NONE;
                long startSquareBit = 1L << startSquare;
                long targetSquareBit = 1L << targetSquare;
                bitboards[startPiece] ^= startSquareBit | targetSquareBit;
                bitboards[playerBit] ^= startSquareBit | targetSquareBit;
                key ^= Zobrist.PIECE[startPieceType][player][startSquare] | Zobrist.PIECE[startPieceType][player][targetSquare];
                if(playerKingSideCastling || playerQueenSideCastling) {
                    key ^= (playerKingSideCastling ? Zobrist.KING_SIDE[player] : 0) ^ (playerQueenSideCastling ? Zobrist.QUEEN_SIDE[player] : 0);
                    castling &= (player == Value.WHITE ? ~3 : ~12);
                }
                if(Math.abs(startSquare - targetSquare) == 2) {
                    int rookRank = (player << 6) - (player << 3);
                    if((targetSquare & 7) == 6) {
                        bitboards[Piece.ROOK | playerBit] = bitboards[Piece.ROOK | playerBit] & ~(1L << (rookRank | 7)) | (1L << (rookRank | 5));
                        bitboards[playerBit] = bitboards[playerBit] & ~(1L << (rookRank | 7)) | (1L << (rookRank | 5));
                        key ^= Zobrist.PIECE[Piece.ROOK][player][rookRank | 7] ^ Zobrist.PIECE[Piece.ROOK][player][rookRank | 5];
                    } else {
                        bitboards[Piece.ROOK | playerBit] = bitboards[Piece.ROOK | playerBit] & ~(1L << rookRank) | (1L << (rookRank | 3));
                        bitboards[playerBit] = bitboards[playerBit] & ~(1L << rookRank) | (1L << (rookRank | 3));
                        key ^= Zobrist.PIECE[Piece.ROOK][player][rookRank] ^ Zobrist.PIECE[Piece.ROOK][player][rookRank | 3];
                    }
                }
                if(targetPiece != Value.NONE) {
                    halfMoveCount = 0;
                    int other = 1 ^ player;
                    bitboards[targetPiece] ^= targetSquareBit;
                    bitboards[other << 3] ^= targetSquareBit;
                    key ^= Zobrist.PIECE[targetPiece & Piece.TYPE][other][targetSquare];
                }
                break;
            }
            case Piece.ROOK: {
                long startSquareBit = 1L << startSquare;
                long targetSquareBit = 1L << targetSquare;
                int player = board.player;
                bitboards[startPiece] ^= startSquareBit | targetSquareBit;
                bitboards[player << 3] ^= startSquareBit | targetSquareBit;
                key ^= Zobrist.PIECE[startPieceType][player][startSquare] | Zobrist.PIECE[startPieceType][player][targetSquare];
                int playerKingSideBit = Value.KINGSIDE_BIT[player];
                int playerQueenSideBit = Value.QUEENSIDE_BIT[player];
                boolean playerKingSideCastling = (castling & playerKingSideBit) != Value.NONE;
                boolean playerQueenSideCastling = (castling & playerQueenSideBit) != Value.NONE;
                if(startSquare == (player == Value.WHITE ? 7 : 63) && playerKingSideCastling) {
                    castling ^= playerKingSideBit;
                    key ^= Zobrist.KING_SIDE[player];
                } else {
                    if(startSquare == (player == Value.WHITE ? 0 : 56) && playerQueenSideCastling) {
                        castling ^= playerQueenSideBit;
                        key ^= Zobrist.QUEEN_SIDE[player];
                    }
                }
                if(targetPiece != Value.NONE) {
                    int other = 1 ^ player;
                    halfMoveCount = 0;
                    bitboards[targetPiece] ^= targetSquareBit;
                    bitboards[other << 3] ^= targetSquareBit;
                    key ^= Zobrist.PIECE[targetPiece & Piece.TYPE][other][targetSquare];
                }
                break;
            }
            case Piece.PAWN: {
                int promotePiece = (move >>> 12) & 0xf;
                long targetSquareBit = 1L << targetSquare;
                int player = board.player;
                if(promotePiece == Value.NONE) {
                    long startSquareBit = 1L << startSquare;
                    bitboards[startPiece] ^= startSquareBit | targetSquareBit;
                    bitboards[player << 3] ^= startSquareBit | targetSquareBit;
                    key ^= key ^= Zobrist.PIECE[startPieceType][player][startSquare] | Zobrist.PIECE[startPieceType][player][targetSquare];
                } else {
                    long startSquareBit = 1L << startSquare;
                    bitboards[startPiece] ^= startSquareBit;
                    bitboards[promotePiece] ^= targetSquareBit;
                    bitboards[player << 3] ^= startSquareBit | targetSquareBit;
                    key ^= key ^= Zobrist.PIECE[startPieceType][player][startSquare] | Zobrist.PIECE[promotePiece & Piece.TYPE][player][targetSquare];
                }
                if(targetPiece != Value.NONE) {
                    int other = 1 ^ player;
                    halfMoveCount = 0;
                    bitboards[targetPiece] ^= targetSquareBit;
                    bitboards[other << 3] ^= targetSquareBit;
                    key ^= Zobrist.PIECE[targetPiece & Piece.TYPE][other][targetSquare];
                }
                if(targetSquare == board.eSquare) {
                    int other = 1 ^ player;
                    int otherBit = other << 3;
                    int captureSquare = targetSquare + (player == Value.WHITE ? -8 : 8);
                    long captureSquareBit = 1L << captureSquare;
                    halfMoveCount = 0;
                    bitboards[6 | otherBit] ^= captureSquareBit;
                    bitboards[otherBit] ^= captureSquareBit;
                    key ^= Zobrist.PIECE[Piece.PAWN][other][captureSquare];
                }
                if(Math.abs(startSquare - targetSquare) == 16) {
                    eSquare = startSquare + (player == Value.WHITE ? 8 : -8);
                    key ^= Zobrist.ENPASSANT_FILE[eSquare & 7];
                }
            }
        }
        if((targetPiece & Piece.TYPE) == Piece.ROOK) {
            int other = 1 ^ board.player;
            int otherKingSideBit = Value.KINGSIDE_BIT[other];
            int otherQueenSideBit = Value.QUEENSIDE_BIT[other];
            boolean otherKingSideCastling = (castling & otherKingSideBit) != Value.NONE;
            boolean otherQueenSideCastling = (castling & otherQueenSideBit) != Value.NONE;
            if(targetSquare == (other == Value.WHITE ? 7 : 63) && otherKingSideCastling) {
                castling ^= otherKingSideBit;
                key ^= Zobrist.KING_SIDE[other];
            } else {
                if(targetSquare == (other == Value.WHITE ? 0 : 56) && otherQueenSideCastling) {
                    castling ^= otherQueenSideBit;
                    key ^= Zobrist.QUEEN_SIDE[other];
                }
            }
        }
        return new Board(bitboards, 1 ^ board.player, castling, eSquare, halfMoveCount, board.fullMoveCount + board.player, key);
    }


    /*
    public static Board makeMove(Board board, int move) {
        long[] bitboards = Arrays.copyOf(board.bitboard, 15);
        long key = board.key;
        int player = board.player;
        int playerBit = player << 3;
        int other = 1 ^ player;
        int otherBit = other << 3;
        int startSquare = move & 0x3f;
        long startSquareBit = 1L << startSquare;
        int startFile = startSquare & 7;
        int targetSquare = (move >>> 6) & 0x3f;
        long targetSquareBit = 1L << targetSquare;
        int targetFile = targetSquare & 7;
        int startPiece = Board.getPiece(board, startSquare);
        int startPieceType = startPiece & 7;
        int targetPiece = Board.getPiece(board, targetSquare);
        int targetPieceType = targetPiece & 7;
        int promotePiece = (move >>> 12) & 0xf;
        int halfMoveCount = board.halfMoveCount + 1;
        int eSquare = board.eSquare;
        int castling = board.castling;
        int playerKingSideBit = player == 0 ? 1 : 4;
        int playerQueenSideBit = player == 0 ? 2 : 8;
        int otherKingSideBit = player == 0 ? 4 : 1;
        int otherQueenSideBit = player == 0 ? 8 : 2;
        boolean playerKingSideCastling = (castling & playerKingSideBit) != 0;
        boolean playerQueenSideCastling = (castling & playerQueenSideBit) != 0;
        boolean otherKingSideCastling = (castling & otherKingSideBit) != 0;
        boolean otherQueenSideCastling = (castling & otherQueenSideBit) != 0;
        bitboards[startPiece] &= ~startSquareBit;
		bitboards[playerBit] &= ~startSquareBit;
        key ^= Zobrist.PIECE[startPieceType][player][startSquare];
        if(targetPieceType != 0) {
            halfMoveCount = 0;
            bitboards[targetPiece] &= ~targetSquareBit;
            bitboards[otherBit] &= ~targetSquareBit;
            key ^= Zobrist.PIECE[targetPieceType][other][targetSquare];
            if(targetPieceType == 3) {
                if(targetSquare == (player == 0 ? 63 : 7) && otherKingSideCastling) {
                    castling ^= otherKingSideBit;
                    key ^= Zobrist.KING_SIDE[other];
                } else {
                    if(targetSquare == (player == 0 ? 56 : 0) && otherQueenSideCastling) {
                        castling ^= otherQueenSideBit;
                        key ^= Zobrist.QUEEN_SIDE[other];
                    }
                }
            }
        }
        if(startPieceType == 6 && targetSquare == eSquare) {
            halfMoveCount = 0;
            int eCaptureSquare = (eSquare + (player == 0 ? -8 : 8));
            long eCaptureBit = 1L << eCaptureSquare;
            bitboards[6 | otherBit] &= ~eCaptureBit;
            bitboards[otherBit] &= ~eCaptureBit;
            key ^= Zobrist.PIECE[6][other][eCaptureSquare];
        }
        if(eSquare != -1) {
            key ^= Zobrist.ENPASSANT_FILE[eSquare & 7];
            eSquare = -1;
        }
        bitboards[playerBit] |= targetSquareBit;
        if(promotePiece == 0) {
            bitboards[startPiece] |= targetSquareBit;
            key ^= Zobrist.PIECE[startPieceType][player][targetSquare];
        } else {
            bitboards[promotePiece] |= targetSquareBit;
            key ^= Zobrist.PIECE[promotePiece & 7][player][targetSquare];
        }
        switch(startPieceType) {
            case 1: {
                if(playerKingSideCastling || playerQueenSideCastling) {
                    key ^= (playerKingSideCastling ? Zobrist.KING_SIDE[player] : 0) ^ (playerQueenSideCastling ? Zobrist.QUEEN_SIDE[player] : 0);
                    castling = castling & (player == 0 ? ~3 : ~12);
                }
                if(Math.abs(startSquare - targetSquare) == 2) {
                    int rookRank = player * 56;
                    if(targetFile == 6) {
                        bitboards[3 | playerBit] = bitboards[3 | playerBit] & ~(1L << (rookRank | 7)) | (1L << (rookRank | 5));
                        bitboards[playerBit] = bitboards[playerBit] & ~(1L << (rookRank | 7)) | (1L << (rookRank | 5));
                        key ^= Zobrist.PIECE[3][player][rookRank | 7] ^ Zobrist.PIECE[3][player][rookRank | 5];
                    } else {
                        bitboards[3 | playerBit] = bitboards[3 | playerBit] & ~(1L << rookRank) | (1L << (rookRank | 3));
                        bitboards[playerBit] = bitboards[playerBit] & ~(1L << rookRank) | (1L << (rookRank | 3));
                        key ^= Zobrist.PIECE[3][player][rookRank] ^ Zobrist.PIECE[3][player][rookRank | 3];
                    }
                }
                break;
            }
            case 3: {
                if(startSquare == (player == 0 ? 7 : 63) && playerKingSideCastling) {
                    castling ^= playerKingSideBit;
                    key ^= Zobrist.KING_SIDE[player];
                } else {
                    if(startSquare == (player == 0 ? 0 : 56) && playerQueenSideCastling) {
                        castling ^= playerQueenSideBit;
                        key ^= Zobrist.QUEEN_SIDE[player];
                    }
                }
                break;
            }
            case 6: {
                halfMoveCount = 0;
                if(Math.abs(startSquare - targetSquare) == 16) {
                    eSquare = startSquare + (player == 0 ? 8 : -8);
                    key ^= Zobrist.ENPASSANT_FILE[startFile];
                }
                break;
            }
            default: break;
        }
        return new Board(bitboards, other, castling, eSquare, halfMoveCount, board.fullMoveCount + player, key ^ Zobrist.WHITE_MOVE);
    }
*/

    public static void draw(Board board) {
		System.out.println(boardString(board));
	}

    public static void drawMoveList(Board board) {
        System.out.println(moveListString(board));
    }

    private static String boardString(Board board) {
        String boardString = "";
        for(int rank = 7; rank >= 0; rank --) {
			for(int file = 0; file < 8; file ++) {
				int square = rank << 3 | file;
				int piece = getPiece(board, square);
				if(piece != 0) {
                    boardString += LongType.values()[piece].shortString() + " ";
				} else {
                    boardString += ". ";
				}
			}
			boardString += "\n";
		}
        return boardString;
    }

    private static String moveListString(Board board) {
        if(board.hasGenerated) {
            if(board.move.length == 0) {
                return "No Legal Moves Available\n";
            }
            String moveListString = "";
            for(int i = 0; i < board.move.length; i ++) {
                moveListString += (i + 1) + " " + board.moveAlgebraic(i) + "\n";
            }
            return moveListString;
        }
        return "No Moves Generated\n";
    }

    private static String fieldString(Board board) {
        String fieldString = board.player == 0 ? "White to Move\n" : "Black to Move\n";
        fieldString += "castling: " + (board.castling == 0 ? "-" : board.kingSide(0) ? "K" : "") + (board.queenSide(0) ? "Q" : "") + (board.kingSide(1) ? "k" : "") + (board.queenSide(1) ? "q" : "") + "\n";
        fieldString += "eSquare: " + (board.eSquare == -1 ? "-" : FILE.charAt(board.eSquare & 7) + Integer.toString(((board.eSquare & 0x3f) >>> 3) + 1)) + "\n";
        fieldString += "halfMoveCount: " + board.halfMoveCount + "\n";
        fieldString += "fullMoveCount: " + board.fullMoveCount + "\n";
        fieldString += "key: " + Long.toHexString(board.key) + "\n";
        return fieldString;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(!(o instanceof Board)) {
            return false;
        }
        Board board = (Board) o;
        if(board.key != this.key) {
            return false;
        }
        if(board.move.length != this.move.length) {
            return false;
        }
        if(board.player != this.player || board.castling != this.castling || board.eSquare != this.eSquare || board.halfMoveCount != this.halfMoveCount || board.fullMoveCount != this.fullMoveCount || board.hasGenerated != this.hasGenerated) {
            return false;
        }
        for(int i = 0; i < 16; i ++) {
            if(board.bitboard[i] != this.bitboard[i]) {
                return false;
            }
        }
        if(board.move.length != 0) {
            for(int i = 0; i < board.move.length; i ++) {
                if(board.move[i] != this.move[i]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return (int) this.key;
    }

    @Override
    public String toString() {
        return boardString(this) + moveListString(this) + fieldString(this);
    }

}
