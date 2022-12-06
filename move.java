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

    public move(int startRank, char startFile, int endRank, char endFile, int moveType) {
        this.startRank = startRank; this.endRank = endRank; this.startFile = startFile; this.endFile = endFile; this.moveType = moveType;
    }
}