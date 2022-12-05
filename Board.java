import java.util.*;

import javax.swing.*;
public class Board {
    int color = 0;
    Piece lastClicked;
    char lastFileClicked;
    int lastRankClicked;
    boolean showingLegalMoves;
    Piece[][] board = new Piece[8][8];
    boolean blacksTurn;
    int canEnPassant = -1; // -1 = nobody 0 = white 1 = black
    char enPassantFile;
    boolean blackKingInCheck = false;
    boolean whiteKingInCheck = false;
    int blackO_O = 2;
    int blackO_O_O = 2;
    int whiteO_O = 2;
    int whiteO_O_O = 2;
    move lastMove = new move(0, 'a', 0, 'a', 0);
    
    

    ArrayList<Piece> piecesCapturedW = new ArrayList<Piece>();
    ArrayList<Piece> piecesCapturedB = new ArrayList<Piece>();
    ArrayList<move> movesList = new ArrayList<move>();

    static String[] allSquares = {"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1",
                                  "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                                  "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                                  "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                                  "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                                  "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                                  "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                                  "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",};

    int rank = 0;
    char file;


    public void setPiece(int rank, char file, int type, boolean black) {
        if (((rank > 8) || (rank < 1)) || ((file > 'h') || (file < 'a'))) {
            JOptionPane.showMessageDialog(null, "ERROR: Out of Bounds");
        } else {
            this.board[rank-1][Piece.squareFile(file)-1] = new Piece(type, black);
        }
    }

    public void setLayout(int preset) {
        switch (preset) {
            case 0:
                for (int rank = 1; rank <= 8; rank++) {
                    for (char file = 'a'; file <= 'h'; file++) {
                        setPiece(rank, file, 0, false);
                    }
                }
                break;
            case 1:
                // place pawns

                for (String s : allSquares) this.setPiece(Piece.squareRank(s), Piece.squareFile(s), 0, false);

                for (char file = 'a'; file <= 'h'; file++) {
                    setPiece(2, file, 1, false);
                    setPiece(7, file, 1, true);
                }

                // place pieces
                for (int i = 0; i <= 1; i++) {
                    int rank; boolean black;
                    if (i == 0) {rank = 1; black = false;}
                    else {rank = 8; black = true;}
                    setPiece(rank, 'b', 2, black);
                    setPiece(rank, 'g', 2, black);
                    setPiece(rank, 'c', 3, black);
                    setPiece(rank, 'f', 3, black);
                    setPiece(rank, 'a', 4, black);
                    setPiece(rank, 'h', 4, black);
                    setPiece(rank, 'd', 5, black);
                    setPiece(rank, 'e', 6, black);
                }

                break;
        }
    }

    public Piece pieceAt(int rank, char file) {
        if ((file > 'h') || (file < 'a') || (rank > 8) || (rank < 1)) return null;
        else if (this.getBoard()[rank-1][Piece.squareFile(file)-1] != null) return this.getBoard()[rank-1][Piece.squareFile(file)-1];
        else return null;
    }

    public Piece pieceAt(String location) {
        char file = Piece.squareFile(location);
        int rank = Piece.squareRank(location);
        return this.pieceAt(rank, file);
    }


    public Board() {
        for (int rank = 1; rank <= 8; rank++) {
            for (char file = 'a'; file <= 'h'; file++) {
                this.setPiece(rank, file, 0, false);
            }
        }
        this.blackO_O = 2;
        this.blackO_O_O = 2;
        this.whiteO_O = 2;
        this.whiteO_O_O = 2;
    }

    public Board(Piece[][] premadeBoard) {
        this.board = premadeBoard;
    }

    public void print() {
        for (int i = 8; i >= 1; i--) {
            for (char j = 'a'; j <= 'h'; j++) {
                Piece piece = this.pieceAt(i, j);
                if (piece.type == 0) System.out.print("[]");
                else if (piece.black) System.out.print("B"+piece.type);
                else System.out.print("W" + piece.type);
            }
            System.out.println();
        }
    }

    public int pieceCanMoveTo(int initRank, char initFile, int endRank, char endFile, Piece piece) {
        if (piece == null) return 0;
        else if ((this.pieceAt(endRank, endFile).black == piece.black) && this.pieceAt(endRank, endFile).type != 0) return 0;
        int fileNum = Piece.squareFile(initFile);
        int endFileNum = Piece.squareFile(endFile);
        int pawnMove;
        int initialPawnRank;
        int pieceType = piece.type;
        boolean isBlack = piece.black;

        switch (pieceType) {
            case 0:
                return 0;
            // PAWN MOVE
            case 1:

                if (!isBlack) {pawnMove = 1; initialPawnRank = 2;}
                else {pawnMove = -1; initialPawnRank = 7;}

                Piece searchPiece = this.pieceAt(endRank, endFile);

                if (searchPiece == null) return 0;
                else if (searchPiece.type != 0 && endFile == initFile) return 0;

                if ((this.canEnPassant == 1) && piece.black && (initRank == 4) && ((initFile+1 == enPassantFile) || (initFile-1 == enPassantFile)) && (endFile == enPassantFile) && endRank == 3) return 4;
                else if ((this.canEnPassant == 0) && !piece.black && (initRank == 5) && ((initFile+1 == enPassantFile) || (initFile-1 == enPassantFile)) && (endFile == enPassantFile) && endRank == 6) return 4;

                else if ((this.pieceAt(endRank, endFile).type == 0) && ((initFile == endFile) && ((initRank + pawnMove) == endRank))) {
                    return 1;
                }
                else if ((initRank == initialPawnRank) && (endRank == initRank + (2 * pawnMove)) && (endFile == initFile) && (this.pieceAt(endRank-pawnMove, endFile).type == 0)) {
                    return 1;
                }
                else if ((this.pieceAt(endRank, endFile).type != 0) && (this.pieceAt(endRank, endFile).black != isBlack) && ((endFile == (initFile - 1)) || (endFile == (initFile + 1))) && (endRank == (initRank + pawnMove))) {
                    return 2;
                }
                // else if 
                return 0;
            // knight move
            case 2:
                int squaresMoved = Math.abs(endRank - initRank) + Math.abs(endFileNum - fileNum);
                if ((endRank != initRank) && (endFile != initFile) && (squaresMoved == 3)) {
                    Piece destPiece = this.pieceAt(endRank, endFile);
                    if (destPiece.type != 0) {
                        if (destPiece.black == blacksTurn) return 0;
                        else return 2;
                    }

                    else return 3;
                }
                return 0;
            // bishop move
            case 3:
                if (Math.abs(endRank - initRank) == Math.abs(endFileNum - fileNum)) {
                    int rc, fc;
                    char checkFile = initFile;
                    int checkRank = initRank;
                    if (endRank > initRank) rc = 1;
                    else rc = -1;
                    if (endFile > initFile) fc = 1;
                    else fc = -1;

                    checkFile += fc;
                    checkRank += rc;

                    
                    if (checkRank == endRank) {
                        if (this.pieceAt(checkRank, checkFile).type == 0) return 3;
                        else if (this.pieceAt(checkRank, checkFile).black != this.blacksTurn) return 2;
                        else return 0;
                    }

                    // while you haven't reached the right place yet
                    while (endRank != checkRank && endFile != checkFile) {
                        Piece checkPiece = this.pieceAt(checkRank, checkFile);

                        // if board is stupid then say no
                        if (checkPiece == null) return 0;

                        // if 
                        if (checkPiece.type == 0) {
                            checkFile += fc;
                            checkRank += rc;
                            continue;
                        }
                        // else if (checkPiece.black != blacksTurn) return 2;
                        else return 0;
                    }
                    if (this.pieceAt(checkRank, checkFile).type == 0) return 3;
                    else if (this.pieceAt(checkRank, checkFile).black != this.blacksTurn) return 2;
                    else return 0;
                }
                return 0;
            // Rook
            case 4:
                if ((endRank == initRank) ^ (endFile == initFile)) {
                    int rc = 0, fc = 0;
                    int checkRank = initRank;
                    char checkFile = initFile;
                    if (endRank == initRank) {
                        if (endFile > initFile) fc = 1;
                        else fc = -1;
                    } 
                    else {
                        if (endRank > initRank) rc = 1;
                        else rc = -1;
                    }

                    checkFile += fc; checkRank += rc;

                    if ((endRank == checkRank) && (endFile == checkFile)) {
                        if (this.pieceAt(checkRank, checkFile) == null) return 0;
                        if (this.pieceAt(checkRank, checkFile).type == 0) return 3;
                        else if (this.pieceAt(checkRank, checkFile).black != blacksTurn) return 2;
                        else return 0;
                    }
                    while ((endRank != checkRank) ^ (endFile != checkFile)) {
                        Piece checkPiece = this.pieceAt(checkRank, checkFile);
                        if (checkPiece == null) return 0;
                        else if (checkPiece.type == 0) {
                            checkFile += fc;
                            checkRank += rc;
                            continue;
                        }
                        else return 0;
                    }
                    if (this.pieceAt(checkRank, checkFile).type == 0) return 3;
                    else if (this.pieceAt(checkRank, checkFile).black != blacksTurn) return 2;
                    else return 0;

                }
                return 0;
            case 5: //queen - combo of rook and bishop
            if (Math.abs(endRank - initRank) == Math.abs(endFileNum - fileNum)) {
                int rc, fc;
                char checkFile = initFile;
                int checkRank = initRank;
                if (endRank > initRank) rc = 1;
                else rc = -1;
                if (endFile > initFile) fc = 1;
                else fc = -1;

                checkFile += fc;
                checkRank += rc;

                
                if (checkRank == endRank) {
                    if (this.pieceAt(checkRank, checkFile).type == 0) return 3;
                    else if (this.pieceAt(checkRank, checkFile).black != blacksTurn) return 2;
                    else return 0;
                }

                // while you haven't reached the right place yet
                while (endRank != checkRank) {
                    Piece checkPiece = this.pieceAt(checkRank, checkFile);

                    // if board is stupid then say no
                    if (checkPiece == null) return 0;

                    // if 
                    if (checkPiece.type == 0) {
                        checkFile += fc;
                        checkRank += rc;
                        continue;
                    }
                    // else if (checkPiece.black != blacksTurn) return 2;
                    else return 0;
                }
                if (this.pieceAt(checkRank, checkFile).type == 0) return 3;
                else if (this.pieceAt(checkRank, checkFile).black != blacksTurn) return 2;
                else return 0;
            }
            else if ((endRank == initRank) ^ (endFile == initFile)) {
                int rc = 0, fc = 0;
                int checkRank = initRank;
                char checkFile = initFile;
                if (endRank == initRank) {
                    if (endFile > initFile) fc = 1;
                    else fc = -1;
                } 
                else {
                    if (endRank > initRank) rc = 1;
                    else rc = -1;
                }

                checkFile += fc; checkRank += rc;

                if ((endRank == checkRank) && (endFile == checkFile)) {
                    if (this.pieceAt(checkRank, checkFile) == null) return 0;
                    if (this.pieceAt(checkRank, checkFile).type == 0) return 3;
                    else if (this.pieceAt(checkRank, checkFile).black != blacksTurn) return 2;
                    else return 0;
                }
                while ((endRank != checkRank) ^ (endFile != checkFile)) {
                    Piece checkPiece = this.pieceAt(checkRank, checkFile);
                    if (checkPiece == null) return 0;
                    else if (checkPiece.type == 0) {
                        checkFile += fc;
                        checkRank += rc;
                        continue;
                    }
                    else return 0;
                }
                if (this.pieceAt(checkRank, checkFile).type == 0) return 3;
                else if (this.pieceAt(checkRank, checkFile).black != blacksTurn) return 2;
                else return 0;

            }

                return 0;
            case 6:
            
                // black castle
                if (!blackKingInCheck) {
                    if (piece.black && this.blackO_O == 2 && (this.pieceAt(8, 'f').type == 0) && (this.pieceAt(8, 'g').type == 0) && (this.pieceAt(8, 'h').type == 4) && (this.pieceAt(8, 'h').black) && (endRank == 8) && (endFile == 'g') && (!pieceIsAttacked(8, 'e'))) return 5;
                    else if (piece.black && this.blackO_O_O == 2 && (this.pieceAt(8, 'd').type == 0) && (this.pieceAt(8, 'c').type == 0) && (this.pieceAt(8, 'b').type == 0) && (this.pieceAt(8, 'a').type == 4) && (this.pieceAt(8, 'a').black) && (endRank == 8) && (endFile == 'c')) return 6;
                }

                // white castle
                if (!whiteKingInCheck) {
                    if (!piece.black && this.whiteO_O == 2 && (this.pieceAt(1, 'f').type == 0) && (this.pieceAt(1, 'g').type == 0) && (this.pieceAt(1, 'h').type == 4) && (!this.pieceAt(1, 'h').black) && (endRank == 1) && (endFile == 'g') && (!pieceIsAttacked(1, 'e'))) return 5;
                    else if (!piece.black && this.whiteO_O_O == 2 && (this.pieceAt(1, 'd').type == 0) && (this.pieceAt(1, 'c').type == 0) && (this.pieceAt(1, 'b').type == 0) && (this.pieceAt(1, 'a').type == 4) && (!this.pieceAt(1, 'a').black) && (endRank == 1) && (endFile == 'c')) return 6;
                }

                int maxSquaresMoved = Math.max(Math.abs(endRank - initRank), Math.abs(endFileNum - fileNum));
                if (maxSquaresMoved == 1) {
                    Piece checkPiece = this.pieceAt(endRank, endFile);
                    if (checkPiece == null) return 0;
                    else if (checkPiece.type == 0) return 3;
                    else if (this.pieceAt(endRank, endFile).black != blacksTurn) return 2;
                    else return 0;
                }
                else return 0;
            }
        return 0;

    }

    public ArrayList<move> allMovesFor(int rank, char file) {
        ArrayList<move> allMoves = new ArrayList<move>();
        Piece searchPiece = this.pieceAt(rank, file);
        for (String square : allSquares) {
            int rankToGetTo = Piece.squareRank(square);
            char fileToGetTo = Piece.squareFile(square);
            if ((rankToGetTo == rank) && (fileToGetTo == file)) continue;
            int moveType = pieceCanMoveTo(rank, file, rankToGetTo, fileToGetTo, searchPiece);
            if (moveType != 0) {
                move c = new move(rank, rankToGetTo, file, fileToGetTo, moveType);
                if (this.kingIsCheckedAfter(c, searchPiece.black)) continue;
                else allMoves.add(c);
                
                
            }
        }

        return allMoves;
    }

    public ArrayList<move> everyMoveFor(int rank, char file) {
        ArrayList<move> allMoves = new ArrayList<move>();
        Piece searchPiece = this.pieceAt(rank, file);
        for (String square : allSquares) {
            int rankToGetTo = Piece.squareRank(square);
            char fileToGetTo = Piece.squareFile(square);
            if ((rankToGetTo == rank) && (fileToGetTo == file)) continue;
            int moveType = pieceCanMoveTo(rank, file, rankToGetTo, fileToGetTo, searchPiece);
            if (moveType != 0) {
                move c = new move(rank, rankToGetTo, file, fileToGetTo, moveType);
                allMoves.add(c);
                
                
            }
        }

        return allMoves;
    }

    public void movePiece(move m, boolean forreal) {
        char initFile = m.startFile;
        int initRank = m.startRank;
        char endFile = m.endFile;
        int endRank = m.endRank;

        Piece pieceToMove = this.pieceAt(initRank, initFile);
        Piece pieceToGo = this.pieceAt(endRank, endFile);

        if (pieceToMove.type == 4) {
            if (pieceToMove.black && (initFile == 'h')) this.blackO_O = 0;
            else if (pieceToMove.black && (initFile == 'a')) this.blackO_O_O = 0;
            else if (!pieceToMove.black && (initFile == 'h')) this.whiteO_O = 0;
            else if (!pieceToMove.black && (initFile == 'a')) this.whiteO_O_O = 0;
        }
        if (pieceToMove.type == 6) {
            if (pieceToMove.black) {
                this.blackO_O = 0;
                this.blackO_O_O = 0;
            }
            else {
                this.whiteO_O = 0;
                this.whiteO_O_O = 0;
            }
        }

        if (m.moveType == 4 && forreal) {
            if (pieceToMove.black) {
                this.piecesCapturedB.add(this.pieceAt(endRank+1, endFile));
                this.setPiece(endRank+1, endFile, 0, false);
            }
            else {
                this.piecesCapturedW.add(this.pieceAt(endRank-1, endFile));
                this.setPiece(endRank-1, endFile, 0, false);
            }
        }
        else if (m.moveType == 5) {
            if (pieceToMove.black) this.movePiece(new move(8, 'h', 8, 'f', 1), true);
            else this.movePiece(new move(1, 'h', 1, 'f', 1), true);
        }
        else if (m.moveType == 6) {
            if (pieceToMove.black) this.movePiece(new move(8, 'a', 8, 'd', 1), true);
            else this.movePiece(new move(1, 'a', 1, 'd', 1), true);
        }
    

        if (forreal) this.canEnPassant = -1;
        if (pieceToGo.type != 0) {
            if (pieceToMove.black) this.piecesCapturedB.add(pieceToGo);
            else this.piecesCapturedW.add(pieceToGo);
        }
        int initFileNum, endFileNum;
        initFileNum = Piece.squareFile(initFile);
        endFileNum = Piece.squareFile(endFile);


        this.board[endRank-1][endFileNum-1] = pieceToMove;
        this.board[initRank-1][initFileNum-1] = new Piece(0, false);
        
        if (forreal) {

            this.lastMove = m;
            

            if ((pieceToMove.type == 1) && (((initRank == 2) || (initRank == 7)) && ((endRank == 4) || (endRank == 5)))) {
                if (pieceToMove.black) this.canEnPassant = 0;
                else this.canEnPassant = 1;
                this.enPassantFile = endFile;
            }
            if (pieceAt(getKingLocation(!pieceToMove.black)).black && this.pieceIsAttacked(Piece.squareRank(this.getKingLocation(!pieceToMove.black)), Piece.squareFile(this.getKingLocation(!pieceToMove.black)))) blackKingInCheck = true;
            else blackKingInCheck = false;

            if (!pieceAt(getKingLocation(!pieceToMove.black)).black && this.pieceIsAttacked(Piece.squareRank(this.getKingLocation(!pieceToMove.black)), Piece.squareFile(this.getKingLocation(!pieceToMove.black)))) whiteKingInCheck = true;
            else whiteKingInCheck = false;

            
            if ((endRank == 8 || endRank == 1) && pieceToMove.type == 1) {
                String[] promotionOptions = {"Queen", "Rook", "Bishop", "Knight"};
                int pieceChoice = JOptionPane.showOptionDialog(null, "Which piece do you want to promote to?", "Pawn Promotion", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, promotionOptions, promotionOptions[0]);
                int pieceType = 5-pieceChoice;
                if (pieceChoice == -1) pieceType = 5;
                this.setPiece(endRank, endFile, pieceType, blacksTurn);
            }
        }

        

    }

    public boolean pieceIsAttacked(int rank, char file) {
        int attackerRank;
        char attackerFile;
        Piece attackerPiece;
        for (String square : allSquares) {
            attackerRank = Piece.squareRank(square);
            attackerFile = Piece.squareFile(square);
            attackerPiece = this.pieceAt(attackerRank, attackerFile);
            if (attackerPiece.type == 0) continue;
            if ((this.pieceCanMoveTo(attackerRank, attackerFile, rank, file, attackerPiece) == 2)) {
                return true;
            }
        }
        return false;
    }

    public String getKingLocation(boolean isBlack) {
        for (String square : allSquares) {
            Piece pieceAtSquare = this.pieceAt(Piece.squareRank(square), Piece.squareFile(square));
            if ((pieceAtSquare.type == 6) && (pieceAtSquare.black == isBlack)) return square;
        }
        return "a0";
    }
    public Piece[][] getBoard() {
        return this.board;
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public boolean kingIsCheckedAfter(move m, boolean kingIsBlack) {
        int a1 = this.blackO_O;
        int a2 = this.blackO_O_O;
        int a3 = this.whiteO_O;
        int a4 = this.whiteO_O_O;


        Piece beforePiece = this.pieceAt(m.endRank, m.endFile);

        this.movePiece(m, false);

        this.blacksTurn = !this.blacksTurn;

        
        String kingLocation = this.getKingLocation(kingIsBlack);
        char kingFile = Piece.squareFile(kingLocation);
        int kingRank = Piece.squareRank(kingLocation);
        boolean pia = this.pieceIsAttacked(kingRank, kingFile);

        this.movePiece(new move(m.endRank, m.endFile, m.startRank, m.startFile, 2), false);
        this.setPiece(m.endRank, m.endFile, beforePiece.type, beforePiece.black);

        if (m.moveType == 5) {
            if (kingIsBlack) {
                this.setPiece(8, 'h', 4, true);
                this.setPiece(8, 'f', 0, false);
            }
            else {
                this.setPiece(1, 'h', 4, false);
                this.setPiece(1, 'f', 0, false);
            }
        }
        else if (m.moveType == 6) {
            if (kingIsBlack) {
                this.setPiece(8, 'a', 4, true);
                this.setPiece(8, 'c', 0, false);
            }
            else {
                this.setPiece(1, 'a', 4, false);
                this.setPiece(8, 'c', 0, false);
            }
        }

        this.blacksTurn = !this.blacksTurn;

        this.blackO_O = a1;
        this.blackO_O_O = a2;
        this.whiteO_O = a3;
        this.whiteO_O_O = a4;


        return pia;

    }
}