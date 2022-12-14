package chess;

public class Piece {

    int type; boolean black; String name;
    boolean showingLegalMoves = false;

    public Piece(int pieceType, boolean isBlack) {
        /* 0 empty
         * 1 pawn
         * 2 knight
         * 3 bishop
         * 4 rook
         * 5 queen
         * 6 king
         */
        if (pieceType == 0) {
            type = 0;
            black = false;
            return;
        }

        type = pieceType;
        black = isBlack;

    }

    public static char squareFile(String x) {
        x = x.toLowerCase();
        return x.charAt(0);
    }

    public static int squareFile(char x) {
        return (int) x - 96; 
    }

    public static int squareRank(String x) {
        char y = x.charAt(1);
        return (int) y - 48;
    }

    public static char flip(char f) {
        return (char) ((9 - ((int) f - 96)) + 96);
    }
    public static int flip(int r) {
        return 9 - r;
    }

    public Piece clone() {
        return new Piece(this.type, this.black);
    }
    public static boolean squareIsDark(int rank, char file) {
        int fileInt = squareFile(file);
        return !(fileInt % 2 == rank % 2);
    }
    public static boolean squareIsDark(String square) {
        return squareIsDark(squareRank(square), squareFile(square));
    }

    

}
