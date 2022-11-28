package com.cb200.minchessv8.board;

import java.util.Arrays;

import com.cb200.minchessv8.Util.B;
import com.cb200.minchessv8.gen.Gen;
import com.cb200.minchessv8.gen.Magic;
import com.cb200.minchessv8.piece.Piece.LongType;
import com.cb200.minchessv8.zobrist.Zobrist;

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
        this.bitboard = new long[16];
        this.player = 0;
        this.castling = 0;
        this.eSquare = -1;
        this.halfMoveCount = 0;
        this.fullMoveCount = 1;
        this.key = 0L;
        this.move= new int[0];
        this.hasGenerated = false;
    }

    private Board(long[] bitboards, int _player, int _castling, int _eSquare, int _halfMoveCount, int _fullMoveCount, long _key) {
        this.bitboard = Arrays.copyOf(bitboards, bitboards.length);
        this.player = _player;
        this.castling = _castling;
        this.eSquare = _eSquare;
        this.halfMoveCount = _halfMoveCount;
        this.fullMoveCount = _fullMoveCount;
        this.key = _key;
        this.move = new int[0];
        this.hasGenerated = false;
    }

    private Board(Board board, int[] moves) {
        this.bitboard = Arrays.copyOf(board.bitboard, board.bitboard.length);
        this.player = board.player;
        this.castling = board.castling;
        this.eSquare = board.eSquare;
        this.halfMoveCount = board.halfMoveCount;
        this.fullMoveCount = board.fullMoveCount;
        this.key = board.key;
        this.move = Arrays.copyOf(moves, moves.length);
        this.hasGenerated = true;
    }

    public static Board fromFen(String fen) {
		int[] pieces = Fen.getPieces(fen);
		long[] _bitboards = new long[16];
		for(int i = 0; i < 64; i ++) {
			int piece = pieces[i];
			if(piece != 0) {
				_bitboards[piece] |= (1L << i);
				_bitboards[piece & 8] |= (1L << i);
			}
		}
		long key = Zobrist.getKey(pieces, Fen.getWhiteToMove(fen), (Fen.getCastling(fen) & 1) > 0, (Fen.getCastling(fen) & 2) > 0, (Fen.getCastling(fen) & 4) > 0, (Fen.getCastling(fen) & 8) > 0,
			Fen.getEnPassantSquare(fen));
		return new Board(_bitboards, Fen.getWhiteToMove(fen) ? 0 : 1, Fen.getCastling(fen), Fen.getEnPassantSquare(fen), Fen.getHalfMoveCount(fen), Fen.getFullMoveCount(fen), key);
	}

    public static Board gen(Board board, boolean legal) {
		return new Board(board, Gen.moves(board, legal));
	}

    public static int getPiece(Board board, int square) {
		long squareBit = 1L << square;
		return (board.bitboard[0] & squareBit) != 0L ? // is the white bitboard square occupied?
				(
				// is the white pawn bitboard square occupied? If so, return white pawn value
				(board.bitboard[6] & squareBit) != 0L ? 6 :
				// is the white knight bitboard square occupied? If so, return white knight value
				(board.bitboard[5] & squareBit) != 0L ? 5 :
				// white bishop
				(board.bitboard[4] & squareBit) != 0L ? 4 :
				// white rook
				(board.bitboard[3] & squareBit) != 0L ? 3 :
				// white queen
				(board.bitboard[2] & squareBit) != 0L ? 2 :
				// only option left is white king
						1) :
				(board.bitboard[8] & squareBit) != 0L ? // is the black bitboard square occupied?
				(
				// same as above but for black pieces
				(board.bitboard[14] & squareBit) != 0L ? 14 :
				(board.bitboard[13] & squareBit) != 0L ? 13 :
				(board.bitboard[12] & squareBit) != 0L ? 12 :
				(board.bitboard[11] & squareBit) != 0L ? 11 :
				(board.bitboard[10] & squareBit) != 0L ? 10 :
						9) : 0
		;
	}

    public static boolean isSquareAttacked(Board board, int attackedSquare, int player) {
		int playerBit = player << 3;
		if((B.LEAP_ATTACK[attackedSquare] & board.bitboard(5 | playerBit)) != 0L) {
			return true;
		}
		if((B.PAWN_ATTACK[1 ^ player][attackedSquare] & board.bitboard(6 | playerBit)) != 0L) {
			return true;
		}
		if((B.KING_ATTACK[attackedSquare] & board.bitboard(1 | playerBit)) != 0L) {
			return true;
		}
		long allOccupancy = board.bitboard(0) | board.bitboard(8);
		long result = Magic.queenMoves(attackedSquare, allOccupancy) & board.bitboard(2 | playerBit);
		if(Long.bitCount(result) > 0) {
			return true;
		}
		result = Magic.rookMoves(attackedSquare, allOccupancy) & board.bitboard(3 | playerBit);
		if(Long.bitCount(result) > 0) {
			return true;
		}
		result = Magic.bishopMoves(attackedSquare, allOccupancy) & board.bitboard(4 | playerBit);
		if(Long.bitCount(result) > 0) {
			return true;
		}
		return false;
	}

    public long bitboard(int index) {
        return this.bitboard[index];
    }

    public int player() {
        return this.player;
    }

    public int castling() {
        return this.castling;
    }

    public boolean kingSide(int player) {
        return (this.castling & (player == 0 ? 1 : 4)) != 0;
    }

    public boolean queenSide(int player) {
        return (this.castling & (player == 0 ? 2 : 8)) != 0;
    }

    public int eSquare() {
        return this.eSquare;
    }

    public int halfMoveCount() {
        return this.halfMoveCount;
    }

    public int fullMoveCount() {
        return this.fullMoveCount;
    }

    public long key() {
        return this.key;
    }

    public int moveSize() {
        return this.move.length;
    }

    public int move(int index) {
        return this.move[index];
    }

    public boolean hasGenerated() {
        return this.hasGenerated;
    }

    public String moveAlgebraic(int index) {
        return FILE.charAt(this.move[index] & 7) + Integer.toString(((this.move[index] & 0x3f) >>> 3) + 1) + FILE.charAt((this.move[index] >>> 6) & 7) + Integer.toString(((this.move[index] >>> 6 & 0x3f) >>> 3) + 1);
    }

    public String moveNotation(int index) {
        int startSquare = this.move[index] & 0x3f;
        int targetSquare = (this.move[index] >>> 6) & 0x3f;
		int startType = getPiece(this, startSquare) & 7;
        int playerBit = this.player << 3;
        String notation = "";
		switch(startType) {
			case 1: {
                boolean isCastling = Math.abs(startSquare - targetSquare) == 2;
				if(isCastling) {
					if((targetSquare & 7) == 6) {
						return "O-O";
					} else {
						return "O-O-O";
					}
				}
				notation = "K";
				break;
			}
			case 2: {
				notation = "Q";
                long queens = this.bitboard[2 | playerBit] ^ (1L << startSquare);
                long allOccupancy = this.bitboard[0] | this.bitboard[8];
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
			case 3: {
				notation = "R";
				long rooks = this.bitboard[3 | playerBit] ^ (1L << startSquare);
                long allOccupancy = this.bitboard[0] | this.bitboard[8];
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
			case 4: {
				notation = "B";
				long bishops = this.bitboard[4 | playerBit] ^ (1L << startSquare);
                long allOccupancy = this.bitboard[0] | this.bitboard[8];
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
			case 5: {
				notation = "N";
				if(Long.bitCount(B.LEAP_ATTACK[targetSquare] & this.bitboard[5 | playerBit]) > 1) {
					if(Long.bitCount(B.FILE[startSquare & 7] & this.bitboard[5 | playerBit]) > 1) {
						notation += Integer.toString((startSquare >>> 3) + 1);
					} else {
						notation += FILE.charAt(startSquare & 7);
					}
				}
				break;
			}
			case 6: {
				notation = "";
                break;
			}
            default: break;
		}
		if(getPiece(this, targetSquare) != 0 || targetSquare == this.eSquare) {
			if(startType == 6) {
				notation += FILE.charAt(startSquare & 7);
			}
			notation += "x";
		}
		notation += FILE.charAt(targetSquare & 7) + Integer.toString((targetSquare >>> 3) + 1);
		if((this.move[index] >>> 12) != 0) {
			notation += "=";
			switch((this.move[index] >>> 12) & 7) {
				case 2: notation += "Q"; break;
				case 3: notation += "R"; break;
				case 4: notation += "B"; break;
				case 5: notation += "N"; break;
                default: break;
			}
		}
		Board tempBoard = Board.makeMove(this, this.move[index]);
		if(Board.isSquareAttacked(tempBoard, Long.numberOfTrailingZeros(tempBoard.bitboard[1 | (8 ^ playerBit)]), this.player)) {
			tempBoard = Board.gen(tempBoard, true);
			if(tempBoard.moveSize() == 0) {
				notation += "#";
			} else {
				notation += "+";
			}
		}
		return notation;
    }

    public static Board makeMove(Board board, int move) {
        long[] bitboards = Arrays.copyOf(board.bitboard, board.bitboard.length);
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
        bitboards[startPiece] &= ~startSquareBit;
		bitboards[playerBit] &= ~startSquareBit;
        key ^= Zobrist.PIECE[startPieceType][player][startSquare];
        if(targetPieceType != 0) {
            halfMoveCount = 0;
            bitboards[targetPiece] &= ~targetSquareBit;
            bitboards[otherBit] &= ~targetSquareBit;
            key ^= Zobrist.PIECE[targetPieceType][other][targetSquare];
            if(targetPieceType == 3) {
                if(targetSquare == (player == 0 ? 63 : 7) && (castling & (player == 0 ? 4 : 1)) != 0) {
                    castling ^= (player == 0 ? 4 : 1);
                    key ^= Zobrist.KING_SIDE[other];
                } else {
                    if(targetSquare == (player == 0 ? 56 : 0) && (castling & (player == 0 ? 8 : 2)) != 0) {
                        castling ^= (player == 0 ? 8 : 2);
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
                castling = castling & (player == 0 ? ~3 : ~12);
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
                if(startSquare == (player == 0 ? 7 : 63) && (castling & (player == 0 ? 1 : 4)) != 0) {
                    castling ^= (player == 0 ? 1 : 4);
                    key ^= Zobrist.KING_SIDE[player];
                } else {
                    if(startSquare == (player == 0 ? 0 : 56) && (castling & (player == 0 ? 2 : 8)) != 0) {
                        castling ^= (player == 0 ? 2 : 8);
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

    public static void draw(Board board) {
		System.out.println(boardString(board));
	}

    public static void moveList(Board board) {
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
