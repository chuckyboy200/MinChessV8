package com.cb200.minchessv8.piece;

public class Piece {
    
	public final static int KING = 1;
	public final static int WHITE_KING = 1;
	public final static int QUEEN = 2;
	public final static int WHITE_QUEEN = 2;
	public final static int ROOK = 3;
	public final static int WHITE_ROOK = 3;
	public final static int BISHOP = 4;
	public final static int WHITE_BISHOP = 4;
	public final static int KNIGHT = 5;
	public final static int WHITE_KNIGHT = 5;
	public final static int PAWN = 6;
	public final static int WHITE_PAWN = 6;
	public final static int TYPE = 7;
	public final static int BLACK_KING = 9;
	public final static int BLACK_QUEEN = 10;
	public final static int BLACK_ROOK = 11;
	public final static int BLACK_BISHOP = 12;
	public final static int BLACK_KNIGHT = 13;
	public final static int BLACK_PAWN = 14;

	public final static int[] VALUE = { 0, 0, 2538, 1276, 825, 781, 126};

    public enum Type {
		EMPTY, KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN, PASSED_PAWN;
	};

	public enum LongType {
		EMPTY(".", "."),
		WHITE_KING("K", "White King"),
		WHITE_QUEEN("Q", "White Queen"),
		WHITE_ROOK("R", "White Rook"),
		WHITE_BISHOP("B", "White Bishop"),
		WHITE_KNIGHT("N", "White Knight"),
		WHITE_PAWN("P", "White Pawn"),
		WHITE_PASSED_PAWN("P", "White Pawn"),
		EMPTY2(".", "."),
		BLACK_KING("k", "Black King"),
		BLACK_QUEEN("q", "Black Queen"),
		BLACK_ROOK("r", "Black Rook"),
		BLACK_BISHOP("b", "Black Bishop"),
		BLACK_KNIGHT("n", "Black Knight"),
		BLACK_PAWN("p", "Black Pawn"),
		BLACK_PASSED_PAWN("p", "Black Pawn");

		private String shortString;
		private String longString;

		private LongType(String _shortString, String _longString) {
			this.shortString = _shortString;
			this.longString = _longString;
		}

		public String shortString() {
			return this.shortString;
		}

		public String longString() {
			return this.longString;
		}

	};

	public enum Slide {
		N(8, 16, 0L),
		E(1, 1, 0L),
		S(-8, -16, 0L),
		W(-1, -1, 0L),
		NE(9, 17, 0L),
		SE(-7, -15, 0L),
		SW(-9, -17, 0L),
		NW(7, 15, 0L);

		private int delta;
		private int delta0x88;
		private long deltaBit;

		private Slide(int _delta, int _delta0x88, long _deltaBit) {
			this.delta = _delta;
			this.delta0x88 = _delta0x88;
			this.deltaBit = _deltaBit;
		}

		public int delta() {
			return this.delta;
		}

		public int delta0x88() {
			return this.delta0x88;
		}

		public long deltaBit() {
			return this.deltaBit;
		}
	}

	public enum Leap {
		NE(17, 33, 0L),
		EN(10, 18, 0L),
		ES(-6, -14, 0L),
		SE(-15, -31, 0L),
		SW(-17, -33, 0L),
		WS(-10, -18, 0L),
		WN(6, 14, 0L),
		NW(15, 31, 0L);

		private int delta;
		private int delta0x88;
		private long deltaBit;

		private Leap(int _delta, int _delta0x88, long _deltaBit) {
			this.delta = _delta;
			this.delta0x88 = _delta0x88;
			this.deltaBit = _deltaBit;
		}

		public int delta() {
			return this.delta;
		}

		public int delta0x88() {
			return this.delta0x88;
		}

		public long deltaBit() {
			return this.deltaBit;
		}

	}

	public enum PawnAdvance {
		ONE(8, -8), TWO(16, -16);

		private int white;
		private int black;

		private PawnAdvance(int _white, int _black) {
			this.white = _white;
			this.black = _black;
		}

		public int relative(int player) {
			return player > 0 ? this.black : this.white;
		}

	}

}
