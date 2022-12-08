package com.cb200.minchessv8.eval;

import com.cb200.minchessv8.Util.Value;
import com.cb200.minchessv8.piece.Piece;

public class Bonus {
    
    public final static int[][][][] BONUS;

    static {
        BONUS = new int[7][2][64][2];
        int[][][] KingBonus = {
            { {271,  1}, {327, 45}, {271, 85}, {198, 76} },
            { {278, 53}, {303,100}, {234,133}, {179,135} },
            { {195, 88}, {258,130}, {169,169}, {120,175} },
            { {164,103}, {190,156}, {138,172}, { 98,172} },
            { {154, 96}, {179,166}, {105,199}, { 70,199} },
            { {123, 92}, {145,172}, { 81,184}, { 31,191} },
            { { 88, 47}, {120,121}, { 65,116}, { 33,131} },
            { { 59, 11}, { 89, 59}, { 45, 73}, { -1, 78} }
        };
        int[][][] QueenBonus = {
            { { 3,-69}, {-5,-57}, {-5,-47}, { 4,-26} },
            { {-3,-54}, { 5,-31}, { 8,-22}, {12, -4} },
            { {-3,-39}, { 6,-18}, {13, -9}, { 7,  3} },
            { { 4,-23}, { 5, -3}, { 9, 13}, { 8, 24} },
            { { 0,-29}, {14, -6}, {12,  9}, { 5, 21} },
            { {-4,-38}, {10,-18}, { 6,-11}, { 8,  1} },
            { {-5,-50}, { 6,-27}, {10,-24}, { 8, -8} },
            { {-2,-74}, {-2,-52}, { 1,-43}, {-2,-34} }
        };
        int[][][] RookBonus = {
            { {-31, -9}, {-20,-13}, {-14,-10}, {-5, -9} },
            { {-21,-12}, {-13, -9}, { -8, -1}, { 6, -2} },
            { {-25,  6}, {-11, -8}, { -1, -2}, { 3, -6} },
            { {-13, -6}, { -5,  1}, { -4, -9}, {-6,  7} },
            { {-27, -5}, {-15,  8}, { -4,  7}, { 3, -6} },
            { {-22,  6}, { -2,  1}, {  6, -7}, {12, 10} },
            { { -2,  4}, { 12,  5}, { 16, 20}, {18, -5} },
            { {-17, 18}, {-19,  0}, { -1, 19}, { 9, 13} }
        };
        int[][][] BishopBonus = {
            { {-37,-40}, {-4 ,-21}, { -6,-26}, {-16, -8} },
            { {-11,-26}, {  6, -9}, { 13,-12}, {  3,  1} },
            { {-5 ,-11}, { 15, -1}, { -4, -1}, { 12,  7} },
            { {-4 ,-14}, {  8, -4}, { 18,  0}, { 27, 12} },
            { {-8 ,-12}, { 20, -1}, { 15,-10}, { 22, 11} },
            { {-11,-21}, {  4,  4}, {  1,  3}, {  8,  4} },
            { {-12,-22}, {-10,-14}, {  4, -1}, {  0,  1} },
            { {-34,-32}, {  1,-29}, {-10,-26}, {-16,-17} }
        };
        int[][][] KnightBonus = {
            { {-175, -96}, {-92,-65}, {-74,-49}, {-73,-21} },
            { { -77, -67}, {-41,-54}, {-27,-18}, {-15,  8} },
            { { -61, -40}, {-17,-27}, {  6, -8}, { 12, 29} },
            { { -35, -35}, {  8, -2}, { 40, 13}, { 49, 28} },
            { { -34, -45}, { 13,-16}, { 44,  9}, { 51, 39} },
            { {  -9, -51}, { 22,-44}, { 58,-16}, { 53, 17} },
            { { -67, -69}, {-27,-50}, {  4,-51}, { 37, 12} },
            { {-201,-100}, {-83,-88}, {-56,-56}, {-26,-17} }
        };
        int[][][] PawnBonus = {
            { {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0} },
            { {  2, -8}, {  4, -6}, { 11,  9}, { 18,  5}, { 16, 16}, { 21,  6}, {  9, -6}, { -3,-18} },
            { { -9, -9}, {-15, -7}, { 11,-10}, { 15,  5}, { 31,  2}, { 23,  3}, {  6, -8}, {-20, -5} },
            { { -3,  7}, {-20,  1}, {  8, -8}, { 19, -2}, { 39,-14}, { 17,-13}, {  2,-11}, { -5, -6} },
            { { 11, 12}, { -4,  6}, {-11,  2}, {  2, -6}, { 11, -5}, {  0, -4}, {-12, 14}, {  5,  9} },
            { {  3, 27}, {-11, 18}, { -6, 19}, { 22, 29}, { -8, 30}, { -5,  9}, {-14,  8}, {-11, 14} },
            { { -7, -1}, {  6,-14}, { -2, 13}, {-11, 22}, {  4, 24}, {-14, 17}, { 10,  7}, { -9,  7} },
            { {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0} }
        };
        for(int rank = 0; rank < 8; rank ++) {
            for(int file = 0; file < 4; file ++) {
                for(int phase = 0; phase < 2; phase ++) {
                    BONUS[Piece.KING][Value.WHITE][(rank << 3) | file][phase] = KingBonus[rank][file][phase];
                    BONUS[Piece.KING][Value.WHITE][(rank << 3) | (7 - file)][phase] = KingBonus[rank][file][phase];
                    BONUS[Piece.KING][Value.BLACK][(rank << 3) | file][phase] = KingBonus[7 - rank][file][phase];
                    BONUS[Piece.KING][Value.BLACK][(rank << 3) | (7 - file)][phase] = KingBonus[7 - rank][file][phase];
                    BONUS[Piece.QUEEN][Value.WHITE][(rank << 3) | file][phase] = QueenBonus[rank][file][phase];
                    BONUS[Piece.QUEEN][Value.WHITE][(rank << 3) | (7 - file)][phase] = QueenBonus[rank][file][phase];
                    BONUS[Piece.QUEEN][Value.BLACK][(rank << 3) | file][phase] = QueenBonus[7 - rank][file][phase];
                    BONUS[Piece.QUEEN][Value.BLACK][(rank << 3) | (7 - file)][phase] = QueenBonus[7 - rank][file][phase];
                    BONUS[Piece.ROOK][Value.WHITE][(rank << 3) | file][phase] = RookBonus[rank][file][phase];
                    BONUS[Piece.ROOK][Value.WHITE][(rank << 3) | (7 - file)][phase] = RookBonus[rank][file][phase];
                    BONUS[Piece.ROOK][Value.BLACK][(rank << 3) | file][phase] = RookBonus[7 - rank][file][phase];
                    BONUS[Piece.ROOK][Value.BLACK][(rank << 3) | (7 - file)][phase] = RookBonus[7 - rank][file][phase];
                    BONUS[Piece.BISHOP][Value.WHITE][(rank << 3) | file][phase] = BishopBonus[rank][file][phase];
                    BONUS[Piece.BISHOP][Value.WHITE][(rank << 3) | (7 - file)][phase] = BishopBonus[rank][file][phase];
                    BONUS[Piece.BISHOP][Value.BLACK][(rank << 3) | file][phase] = BishopBonus[7 - rank][file][phase];
                    BONUS[Piece.BISHOP][Value.BLACK][(rank << 3) | (7 - file)][phase] = BishopBonus[7 - rank][file][phase];
                    BONUS[Piece.KNIGHT][Value.WHITE][(rank << 3) | file][phase] = KnightBonus[rank][file][phase];
                    BONUS[Piece.KNIGHT][Value.WHITE][(rank << 3) | (7 - file)][phase] = KnightBonus[rank][file][phase];
                    BONUS[Piece.KNIGHT][Value.BLACK][(rank << 3) | file][phase] = KnightBonus[7 - rank][file][phase];
                    BONUS[Piece.KNIGHT][Value.BLACK][(rank << 3) | (7 - file)][phase] = KnightBonus[7 - rank][file][phase];
                    BONUS[Piece.PAWN][Value.WHITE][(rank << 3) | file][phase] = PawnBonus[rank][file][phase];
                    BONUS[Piece.PAWN][Value.WHITE][(rank << 3) | file + 4][phase] = PawnBonus[rank][file + 4][phase];
                    BONUS[Piece.PAWN][Value.BLACK][(rank << 3) | file][phase] = PawnBonus[7 - rank][file][phase];
                    BONUS[Piece.PAWN][Value.BLACK][(rank << 3) | file + 4][phase] = PawnBonus[7 - rank][file + 4][phase];
                }
            }
        }
    }

    private Bonus() {}

}
