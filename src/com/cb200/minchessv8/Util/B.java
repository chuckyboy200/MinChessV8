package cb200.minchessv8.Util;

public class B {
    
    private B() {}

	public final static long[] FILE = {
		0x0101010101010101L, 0x0202020202020202L, 0x0404040404040404L, 0x0808080808080808L,
		0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L, 0x8080808080808080L
	};
	public final static long[] RANK = {
		0x00000000000000ffL, 0x000000000000ff00L, 0x0000000000ff0000L, 0x00000000ff000000L,
		0x000000ff00000000L, 0x0000ff0000000000L, 0x00ff000000000000L, 0xff00000000000000L
	};

	public final static long[] DIAG = {
		// forward leaning diagonals
		0x8040201008040201L, 0x0080402010080402L, 0x0000804020100804L, 0x0000008040201008L,
		0x0000000080402010L, 0x0000000000804020L, 0x0000000000008040L, 0x0000000000000080L,
		0x4020100804020100L, 0x2010080402010000L, 0x1008040201000000L, 0x0804020100000000L,
		0x0402010000000000L, 0x0201000000000000L, 0x0100000000000000L,
		// backward leaning diagonals
		0x0000000000000001L, 0x0000000000000102L, 0x0000000000010204L, 0x0000000001020408L,
		0x0000000102040810L, 0x0000010204081020L, 0x0001020408102040L, 0x0102040810204080L,
		0x0204081020408000L, 0x0408102040800000L, 0x0810204080000000L, 0x1020408000000000L,
		0x2040800000000000L, 0x4080000000000000L, 0x8000000000000000L
	};

	public final static int[][] DIAG_INDEX = { {
		// index for forward leaning diagonals
		 0,  1,  2,  3,  4,  5,  6,  7,
		 8,  0,  1,  2,  3,  4,  5,  6,
		 9,  8,  0,  1,  2,  3,  4,  5,
		10,  9,  8,  0,  1,  2,  3,  4,
		11, 10,  9,  8,  0,  1,  2,  3,
		12, 11, 10,  9,  8,  0,  1,  2,
		13, 12, 11, 10,  9,  8,  0,  1,
		14, 13, 12, 11, 10,  9,  8,  0
	},{
		// index for backward leaning diagonals
		15, 16, 17, 18, 19, 20, 21, 22,
		16, 17, 18, 19, 20, 21, 22, 23,
		17, 18, 19, 20, 21, 22, 23, 24,
		18, 19, 20, 21, 22, 23, 24, 25,
		19, 20, 21, 22, 23, 24, 25, 26,
		20, 21, 22, 23, 24, 25, 26, 27,
		21, 22, 23, 24, 25, 26, 27, 28,
		22, 23, 24, 25, 26, 27, 28, 29
	} };

	public final static long[] LEAP_ATTACK = new long[64];
	public final static long[] KING_ATTACK = new long[64];
	public final static long[][] PAWN_ATTACK = new long[2][64];
	public final static long[][] PAWN_SINGLE_PUSH = new long[2][64];
	public final static long[][] PAWN_DOUBLE_PUSH = new long[2][64];
	public final static long[][] RANK_FILE_ATTACK = new long[64][64];
	public final static long[][] RANK_FILE_BETWEEN = new long[64][64];
	public final static long[][] DIAG_ATTACK = new long[64][64];
	public final static long[][] DIAG_BETWEEN = new long[64][64];

	static {
		int[][] leap = { { 17, 10, -6, -15, -17, -10, 6, 15 } , { 33, 18, -14, -31, -33, -18, 14, 31 } };
		int[][] slide = { { 8, 1, -8, -1, 9, -7, -9, 7 }, { 16, 1, -16, -1, 17, -15, -17, 15 } };
		int[][][] pawnCapture = { { { 7, 9 }, { 15, 17 } }, { { -7, -9 }, { -15, -17 } } };
		long mask;
		for(int square = 0; square < 64; square ++) {
			int square0x88 = ((square & 248) << 1) | (square & 7);
			mask = 0L;
			for(int direction = 0; direction < 8; direction ++) {
				if(((square0x88 + leap[1][direction]) & 0x88) == 0) {
					mask |= (1L << (square + leap[0][direction]));
				}
			}
			LEAP_ATTACK[square] = mask;
			mask = 0L;
			for(int direction = 0; direction < 8; direction ++) {
				if(((square0x88 + slide[1][direction]) & 0x88) == 0) {
					mask |= (1L << (square + slide[0][direction]));
				}
			}
			KING_ATTACK[square] = mask;
			for(int player = 0; player < 2; player ++) {
				mask = 0L;
				for(int direction = 0; direction < 2; direction ++) {
					if(((square0x88 + pawnCapture[player][1][direction]) & 0x88) == 0) {
						mask |= (1L << (square + pawnCapture[player][0][direction]));
					}
				}
				PAWN_ATTACK[player][square] = mask;
				if((square >>> 3) != (7 - player * 7)) {
					PAWN_SINGLE_PUSH[player][square] = 1L << (square + (8 - player * 16));
				}
				if((square >>> 3) == (1 + player * 5)) {
					PAWN_DOUBLE_PUSH[player][square] = 1L << (square + (16 - player * 32));
				}
			}
			for(int targetSquare = 0; targetSquare < 64; targetSquare ++) {
				if(square == targetSquare) {
					continue;
				}
				mask = 0L;
				int modulus = (square - targetSquare) % 9 == 0 ? 9 : (square - targetSquare) % 7 == 0 ? 7 : 0;
				int lower = square < targetSquare ? square : targetSquare;
				int higher = square > targetSquare ? square : targetSquare;
				int lowerRank = lower >>> 3;
				int higherRank = higher >>> 3;
				int lowerFile = lower & 7;
				int higherFile = higher & 7;
				int delta = lowerRank == higherRank ? 1 : lowerFile == higherFile ? 8 : lowerFile < higherFile && modulus == 9 ? 9 : lowerFile > higherFile && modulus == 7 ? 7 : 0;
				if(delta > 0) {
					for(int i = lower; i <= higher; i += delta) {
						mask |= (1L << i);
					}
					if(delta == 1 || delta == 8) {
						RANK_FILE_ATTACK[square][targetSquare] = mask;
						RANK_FILE_BETWEEN[square][targetSquare] = mask & ~(1L << square) & ~(1L << targetSquare);
					} else {
						DIAG_ATTACK[square][targetSquare] = mask;
						DIAG_BETWEEN[square][targetSquare] = mask & ~(1L << square) & ~(1L << targetSquare);
					}
				}
			}
		}
	}

	public final static long[][] CASTLE = {
			{
				0x0000000000000060L, 0x000000000000000eL
			},{
				0x6000000000000000L, 0x0e00000000000000L
			}
	};
}
