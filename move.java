public class move {
    int moveNum;
    boolean subMove;
    int piece;
    int startRank;
    int endRank;
    char startFile;
    char endFile;
    int moveType; // 0=illegal 1=move 2=capture 3=ep 
    boolean isCapture, isCheck, isMate, isEnPassant;
    public move(int piece, int startRank, int endRank, char startFile, char endFile, boolean isCapture, boolean isCheck, boolean isMate, boolean isEnPassant) {
        this.piece = piece; this.startRank = startRank; this.endRank = endRank; this.startFile = startFile; this.endFile = endFile; this.isCapture = isCapture; this.isCheck = isCheck; this.isMate = isMate; this.isEnPassant = isEnPassant;

    }

    public move(int startRank, int endRank, char startFile, char endFile, int moveType) {
        this.startRank = startRank; this.endRank = endRank; this.startFile = startFile; this.endFile = endFile; this.moveType = moveType;
    }

    /*public move(int startRank, int endRank, char startFile, char endFile) {
        this.startRank = startRank; this.endRank = endRank; this.startFile = startFile; this.endFile = endFile;
    }

    public move(int startRank, char startFile, int endRank, char endFile) {
        this.startRank = startRank; this.endRank = endRank; this.startFile = startFile; this.endFile = endFile;
    }*/

    public move(int startRank, char startFile, int endRank, char endFile, int moveType) {
        this.startRank = startRank; this.endRank = endRank; this.startFile = startFile; this.endFile = endFile; this.moveType = moveType;
    }



    /*public static boolean kingIsCheckedAfter(Board initialBoard, move move, boolean kingIsBlack) {
        // initialBoard.board;
        System.out.println("Checking move: "+move.startFile+move.startRank+" "+move.startFile+move.startRank);
        Piece[][] checkBoard;
        Board newBoard;

        checkBoard = (Piece[][]) initialBoard.getBoard().clone();
        newBoard = new Board(checkBoard);
        newBoard.print();
        initialBoard.print();
        System.out.println(newBoard.getBoard() == initialBoard.getBoard());
        System.out.println(newBoard.getBoard().equals(initialBoard.getBoard()));

        newBoard.movePiece(move.startRank, move.startFile, move.endRank, move.endFile);
        String kingLocation = initialBoard.getKingLocation(kingIsBlack);
        int kingRank = Piece.squareRank(kingLocation);
        char kingFile = Piece.squareFile(kingLocation);
        if (newBoard.pieceIsAttacked(kingRank, kingFile)) return true;
        else return false;
        // else return false;
    }*/
}