package chess;

public class move {
    int startRank;
    int endRank;
    char startFile;
    char endFile;
    int moveType;
    int pawnPromotion = 0;

    public move(int startRank, char startFile, int endRank, char endFile, int moveType) {
        this.startRank = startRank; this.endRank = endRank; this.startFile = startFile; this.endFile = endFile; this.moveType = moveType;
    }

    public String toString() {
        return String.format("%s%s%s%s %s", startFile, startRank, endFile, endRank, moveType);
    }
}