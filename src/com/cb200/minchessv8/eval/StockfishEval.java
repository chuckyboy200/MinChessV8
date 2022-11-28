package com.cb200.minchessv8.eval;

public class StockfishEval {
    
    public final static int LAZY_THRESHOLD_1 = 3631;
    public final static int LAZY_THRESHOLD_2 = 2084;
    public final static int SPACE_THRESHOLD = 11551;

    public final static int[] KING_ATTACK_WEIGHTS = { 0, 0, 14, 45, 46, 76, 0 };

    public final static int[][] SAFE_CHECK = { {}, {}, {730, 1128}, {1071, 1886}, {650, 984}, {805, 1292} };

    public final static int[][][] MOBILITY_BONUS = {
        {}, {},
        {   // queen
            {-29,-49}, {-16,-29}, { -8, -8}, { -8, 17}, { 18, 39}, { 25, 54},
            { 23, 59}, { 37, 73}, { 41, 76}, { 54, 95}, { 65, 95}, { 68,101},
            { 69,124}, { 70,128}, { 70,132}, { 70,133}, { 71,136}, { 72,140},
            { 74,147}, { 76,149}, { 90,153}, {104,169}, {105,171}, {106,171},
            {112,178}, {114,185}, {114,187}, {119,221}  
        },
        {   // rook
            {-60,-82}, {-24,-15}, {  0, 17}, {  3, 43}, {  4, 72}, { 14,100},
            { 20,102}, { 30,122}, { 41,133}, {41 ,139}, { 41,153}, { 45,160},
            { 57,165}, { 58,170}, { 67,175}
        },
        {   // bishop
            {-47,-59}, {-20,-25}, { 14, -8}, { 29, 12}, { 39, 21}, { 53, 40},
            { 53, 56}, { 60, 58}, { 62, 65}, { 69, 72}, { 78, 78}, { 83, 87},
            { 91, 88}, { 96, 98}
        },
        {   // knight
            {-62,-79}, {-53,-57}, {-12,-31}, { -3,-17}, {  3,  7}, { 12, 13},
            { 21, 16}, { 28, 21}, { 37, 26}
        }
    };

    public final static int[][] BISHOP_PAWNS = { {3, 8}, {3, 9}, {2, 7}, {3, 7} };

    public final static int[][] KING_PROTECTOR = { {7, 9}, {9, 9} };

    public final static int[][] OUTPOST = { {31, 25}, {54, 34} };

    public final static int[][] PASSED_RANK = { {0, 0}, {2, 38}, {15, 36}, {22, 50}, {64, 81}, {166, 184}, {284, 269} };

    public final static int[] ROOK_ON_CLOSED_FILE = { 10, 5 };

    public final static int[][] ROOK_ON_OPEN_FILE = { {18, 8}, {49, 26} };

    public final static int[][] THREAT_BY_MINOR_PIECE = { {0, 0}, {0, 0}, {81, 163}, {103, 130}, {82, 57}, {64, 50}, {6, 37} };

    public final static int[][] THREAT_BY_ROOK = { {0, 0}, {0, 0}, {60, 39}, {0, 39}, {44, 59}, {36, 71}, {3, 44} };

    public final static int CORNERED_BISHOP = 50;

    public final static int[] UNCONTESTED_OUTPOST   = {  0, 10};
    public final static int[] BISHOP_ON_KING_RING   = { 24,  0};
    public final static int[] BISHOP_XRAY_PAWNS     = {  4,  5};
    public final static int[] FLANK_ATTACKS         = {  8,  0};
    public final static int[] HANGING               = { 72, 40};
    public final static int[] KNIGHT_ON_QUEEN       = { 16, 11};
    public final static int[] LONG_DIAGONAL_BISHOP  = { 45,  0};
    public final static int[] MINOR_BEHIND_PAWN     = { 18,  3};
    public final static int[] PASSED_FILE           = { 13,  8};
    public final static int[] PAWNLESS_FLANK        = { 19, 97};
    public final static int[] REACHABLE_OUTPOST     = { 33, 19};
    public final static int[] RESTRICTED_PIECE      = {  6,  7};
    public final static int[] ROOK_ON_KING_RING     = { 16,  0};
    public final static int[] SLIDER_ON_QUEEN       = { 62, 21};
    public final static int[] THREAT_BY_KING        = { 24, 87};
    public final static int[] THREAT_BY_PAWN_PUSH   = { 48, 39};
    public final static int[] THREAT_BY_SAFE_PAWN   = {167, 99};
    public final static int[] TRAPPED_ROOK          = { 55, 13};
    public final static int[] WEAK_QUEEN_PROTECTION = { 14,  0};
    public final static int[] WEAK_QUEEN            = { 57, 19};
    
    

}
