/* Chess
 * Micah Friedman
 * November 8, 2022
 */

import java.util.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.*;

import java.util.concurrent.ThreadLocalRandom;


public class window {

    

    public static BufferedImage getImage(String path) {
        try {
            return ImageIO.read(new File(path));
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(null, e, "Error", 0);
            return new BufferedImage(0, 0, 0);
        }
    }

    static BufferedImage spritesheetImage = getImage("textures/spritesheet.png");

    public static ImageIcon getSprite(int x, int y) {
        final int X = 96*x;
        final int Y = 96*y;
        final int WIDTH = 96;
        final int LENGTH = 96;

        BufferedImage sprite = spritesheetImage.getSubimage(X, Y, WIDTH, LENGTH);

        return new ImageIcon(sprite);

    }

    public static void clear(JFrame frame) {
        frame.getContentPane().removeAll();
        frame.repaint();
    }

    public static void drawEmptyBoard(JFrame frame, int color) {
        // add light square textures
        Container c = frame.getContentPane(); 
        for (int row = 1; row<=8; row++) {
            for (int col = 1; col<=8; col++) {
                JLabel square = new JLabel();
                int width = 96, height = 96;
                square.setBounds((width*(col-1)), (height*(row-1)), width, height);
                int boardX = 0, boardY = 0;
                switch (color) {
                    case 0:
                        boardX = 0;
                        boardY = 0;
                        break;
                    case 1:
                        boardX = 2;
                        boardY = 0;
                        break;
                }
                if ((row % 2) == (col % 2)) {
                    square.setIcon(getSprite(boardX, boardY));
                }
                else {
                    square.setIcon(getSprite(boardX+1, boardY));
                }
                c.add(square);
            }
        }
    }

    public static void resetBoardVisuals(JFrame frame, Board board) {
        clear(frame);
        drawPieces(frame, board);
        board.lastClicked = null;
        board.showingLegalMoves = false;
        drawEmptyBoard(frame, board.color);
    }

    public static void drawPiece(JFrame frame, Piece piece, int rank, char file) {
        Container c = frame.getContentPane();
        JLabel square = new JLabel();
        int width = 96, height = 96;
        rank = 9 - rank;
        if (boardIsFlipped) {
            rank = Piece.flip(rank);
            file = Piece.flip(file);
        }
        square.setBounds((width*(Piece.squareFile(file)-1)), (height*(rank-1)), width, height);
        if (!piece.black) {
            switch (piece.type) {
                case 0:
                    break;
                case 1:
                    square.setIcon(getSprite(1,2));
                    break;
                case 2:
                    square.setIcon(getSprite(1,1));
                    break;
                case 3:
                    square.setIcon(getSprite(2,1));
                    break;
                case 4:
                    square.setIcon(getSprite(0,1));
                    break;
                case 5:
                    square.setIcon(getSprite(3,1));
                    break;
                case 6:
                    square.setIcon(getSprite(0,2));
                    break;
            }
        } else {
            switch (piece.type) {
                case 0:
                    break;
                case 1:
                    square.setIcon(getSprite(3,3));
                    break;
                case 2:
                    square.setIcon(getSprite(3,2));
                    break;
                case 3:
                    square.setIcon(getSprite(0,3));
                    break;
                case 4:
                    square.setIcon(getSprite(2,2));
                    break;
                case 5:
                    square.setIcon(getSprite(1,3));
                    break;
                case 6:
                    square.setIcon(getSprite(2,3));
                    break;
            }  
        }
        c.add(square);
    }

    public static void drawPiece(JFrame frame, int x, int y, int rank, char file) {
        Container c = frame.getContentPane();
        JLabel square = new JLabel();
        int width = 96, height = 96;
        rank = 9 - rank;
        if (boardIsFlipped) {
            rank = Piece.flip(rank);
            file = Piece.flip(file);
        }
        square.setBounds((width*(Piece.squareFile(file)-1)), (height*(rank-1)), width, height);
        square.setIcon(getSprite(x, y));
        c.add(square);

    }

    public static void drawPieces(JFrame frame, Board board) {
        for (int i = 1; i<=8; i++) {
            for (char j = 'a'; j<='h'; j++) {
                Piece pieceToDraw = board.pieceAt(i, j);
                drawPiece(frame, pieceToDraw, i, j);
                if (board.whiteKingInCheck && board.pieceAt(i, j).type == 6 && !board.pieceAt(i, j).black) drawPiece(frame, 0, 4, i, j);
                else if (board.blackKingInCheck && board.pieceAt(i, j).type == 6 && board.pieceAt(i, j).black) drawPiece(frame, 0, 4, i, j);
                if (!board.lastMove.equals(new move(0, 'a', 0, 'a', 0))) {
                    if (board.lastMove.startRank == i && board.lastMove.startFile == j) drawPiece(frame, 3, 4, i, j);
                    else if (board.lastMove.endRank == i && board.lastMove.endFile == j) drawPiece(frame, 3, 4, i, j);
                }

            }
        }
    }

    static JMenuBar menubar;
    static JRadioButtonMenuItem greenColor, purpleColor;
    static JMenuItem flipItem, resetBoard, importFEN;
    static JCheckBoxMenuItem autoFlipItem;
    static boolean autoFlipBoard;
    static boolean boardIsFlipped = false;
    public static void createMenuBar(JFrame frame, boolean green) {
        // "Board" menu
        menubar = new JMenuBar();
        JMenu menu = new JMenu("Board");
        JMenu boardStyle = new JMenu("Color");
        greenColor = new JRadioButtonMenuItem("Green", green);
        purpleColor = new JRadioButtonMenuItem("Purple", !green);
        flipItem = new JMenuItem("Flip Board");
        autoFlipItem = new JCheckBoxMenuItem("Flip After Turn");
        ButtonGroup colorButtons = new ButtonGroup(); 
        colorButtons.add(greenColor);
        colorButtons.add(purpleColor);
        boardStyle.add(greenColor);
        boardStyle.add(purpleColor);
        menu.add(boardStyle);
        menu.add(flipItem);
        menu.add(autoFlipItem);
        menubar.add(menu);

        // "Game" menu
        JMenu gameMenu = new JMenu("Game");
        resetBoard = new JMenuItem("New Game");
        importFEN = new JMenuItem("Import FEN");

        gameMenu.add(resetBoard);
        gameMenu.add(importFEN);
        menubar.add(gameMenu);

        
        frame.setJMenuBar(menubar);
    }

    public static void resetBoard(Board board) {
        board = new Board();
        board.setLayout(1);
    }

    public static void main(String[] args) {
        // init window
        JFrame frame = new JFrame("Chess - White to move");
        frame.setSize(96*8+16, 96*8+62);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        {
            int x = ThreadLocalRandom.current().nextInt(0, 11 + 1);
            int y = 1;
            while (x >= 4) {
                y++;
                x-=4;
            }
            frame.setIconImage(getSprite(x,y).getImage());
        }
        createMenuBar(frame, true);

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        Board board = new Board();
        board.setLayout(1);
        drawPieces(frame, board);

        drawEmptyBoard(frame, board.color);

        frame.setVisible(true);


        // board.print();


        frame.addMouseListener(new MouseListener() {

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {


                int x=e.getX();
                int y=e.getY();

                x -= 10;
                y -= 59;

                char fileClicked = (char) (((x / 96) + 96) + 1);
                int rankClicked = 8 - (y / 96);
                if (boardIsFlipped) {
                    rankClicked = Piece.flip(rankClicked);
                    fileClicked = Piece.flip(fileClicked);
                }
                Piece pieceClicked = board.pieceAt(rankClicked, fileClicked);

                

                if (e.getButton() == 3) {
                    JOptionPane.showMessageDialog(frame, String.format("Debug Info:\nSquare Clicked: %s%s\nBlack's Turn: %s\nPiece is black: %s\nLocation of piece's king: %s\nBlack is in check: %s\nWhite is in check: %s\nEn Passant: (-1=nobody, 0=white, 1=black) %s on %s file", fileClicked, rankClicked, board.blacksTurn, pieceClicked.black, board.getKingLocation(pieceClicked.black), board.blackKingInCheck, board.whiteKingInCheck, board.canEnPassant, board.enPassantFile));
                    return;
                }

                if ((fileClicked > 'h') || (fileClicked < 'a') || (rankClicked > 8) || (rankClicked < 1)) return;
                ArrayList<move> allMoves = board.allMovesFor(rankClicked, fileClicked);

                // START IF
                if (board.showingLegalMoves && ((pieceClicked.type == 0) || ((pieceClicked.type != 0) && (pieceClicked.black != board.blacksTurn)))) { // If the board is showing legal moves and a non-team piece is clicked
                    ArrayList<move> allLegalMoves = board.allMovesFor(board.lastRankClicked, board.lastFileClicked);
                    for (move m : allLegalMoves) {
                        if ((m.endFile == fileClicked) && (m.endRank == rankClicked)) {
                            board.movePiece(m, true);
                            board.blacksTurn = !board.blacksTurn;
                            // System.out.println(String.format("%s%s moved to %s%s", board.lastFileClicked, board.lastRankClicked, fileClicked, rankClicked));
                            clear(frame);

                            // gets the location of the enemy king
                            String kingLocation = board.getKingLocation(!board.blacksTurn);
                            char kingFile = Piece.squareFile(kingLocation);
                            int kingRank = Piece.squareRank(kingLocation);

                            // checks to see if enemy king is attacked
                            if (board.pieceIsAttacked(kingRank, kingFile)) {
                                if (board.blacksTurn) board.whiteKingInCheck = true;
                                else board.blackKingInCheck = true;
                            }



                            board.lastClicked = null;
                            board.showingLegalMoves = false;
                            if (board.blacksTurn) frame.setTitle("Chess - Black to move");
                            else frame.setTitle("Chess - White to move");
                            if (autoFlipBoard && (boardIsFlipped != board.blacksTurn)) boardIsFlipped = !boardIsFlipped;
                            drawPieces(frame, board);
                            drawEmptyBoard(frame, board.color);
                            // Piece pieceLastClicked = board.pieceAt(board.lastRankClicked, board.lastFileClicked);
                            // boolean isCapture;
                            // if (pieceLastClicked.type == 0) isCapture = false;
                            // // if (board.pieceIsAttacked(kingRank, kingFile)) ;

                            // // board.movesList.add(new move(board.pieceAt(board.lastRankClicked, board.lastFileClicked), board.lastRankClicked, board.lastFileClicked, rankClicked, fileClicked, isCapture, )));
                            return;
                        }
                    }
                    clear(frame);
                    board.lastClicked = null;
                    board.showingLegalMoves = false;
                }


                else if ((pieceClicked.black == board.blacksTurn) && (pieceClicked.type != 0)) {
                    clear(frame);
                    
                    if ((!board.showingLegalMoves) || (pieceClicked != board.lastClicked)) {
                        for (move move : allMoves) {
                            if (move.moveType != 2) drawPiece(frame, 1, 4, move.endRank, move.endFile);
                            else drawPiece(frame, 2, 4, move.endRank, move.endFile);
                        }
                        drawPiece(frame, 3, 4, rankClicked, fileClicked);
                        board.lastClicked = pieceClicked;
                        board.lastFileClicked = fileClicked;
                        board.lastRankClicked = rankClicked;
                        board.showingLegalMoves = true;
                    }
                    else {
                        board.lastClicked = null;
                        board.showingLegalMoves = false;
                    }

                }


                else {
                    clear(frame);
                    board.lastClicked = null;
                    board.showingLegalMoves = false;
                }
                drawPieces(frame, board);
                drawEmptyBoard(frame, board.color);
            }
            // END IF

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                // do nothing
                
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // do nothing
                
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                // do nothing
                
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // do nothing
                
            }
        });

        greenColor.addMouseListener(new MouseListener() {

            @Override
            public void mousePressed(MouseEvent e) {
                board.color = 0;
                resetBoardVisuals(frame, board);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // ignore this
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // ignore this
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // ignore this
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // ignore this
                
            }

            
            
        });
        purpleColor.addMouseListener(new MouseListener() {


            @Override
            public void mousePressed(MouseEvent e) {
                board.color = 1;
                resetBoardVisuals(frame, board);
                
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // ignore this
                
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                // ignore this
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // ignore this
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // ignore this
                
            }

        });
        flipItem.addMouseListener(new MouseListener() {

            @Override
            public void mousePressed(MouseEvent e) {
                boardIsFlipped = !boardIsFlipped;
                resetBoardVisuals(frame, board);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // 
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // 
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // 
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // 
                
            }

        });
        autoFlipItem.addMouseListener(new MouseListener() {

            @Override
            public void mousePressed(MouseEvent e) {
                autoFlipBoard = !autoFlipBoard;
                
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // d
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // c
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // b
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // a
                
            }
            
        });

        resetBoard.addMouseListener(new MouseListener() {

            @Override
            public void mousePressed(MouseEvent e) {
                board.setLayout(1);
                board.blacksTurn = false;
                boardIsFlipped = false;
                board.lastMove = new move(0, 'a', 0, 'a', 0);
                board.blackKingInCheck = false;
                board.whiteKingInCheck = false;
                board.whiteO_O = 2;
                board.blackO_O = 2;
                board.whiteO_O_O = 2;
                board.whiteO_O_O = 2;

                resetBoardVisuals(frame, board);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // t
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // t
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // t
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // t
                
            }

        });

        importFEN.addMouseListener(new MouseListener() {

            @Override
            public void mousePressed(MouseEvent e) {
                while (true) {
                try {
                    String fen = JOptionPane.showInputDialog(frame, "Enter a FEN, or type -1 to exit.", "Import Game", JOptionPane.PLAIN_MESSAGE);
                    if (fen.equals("-1")) break;
                    String[] fenInfo = fen.split(" ");


                    String[] boardPieces = fenInfo[0].split("\\/");

                    // set pieces
                    for (int rank = 8; rank>=1; rank--) {
                        String currentRow = boardPieces[8-rank];

                        currentRow = currentRow.replace("1", "e");
                        currentRow = currentRow.replace("2", "ee");
                        currentRow = currentRow.replace("3", "eee");
                        currentRow = currentRow.replace("4", "eeee");
                        currentRow = currentRow.replace("5", "eeeee");
                        currentRow = currentRow.replace("6", "eeeeee");
                        currentRow = currentRow.replace("7", "eeeeeee");
                        currentRow = currentRow.replace("8", "eeeeeeee");


                        for (char file = 'a'; file<='h'; file++) {

                            char currentItem = currentRow.charAt(file-97);

                            switch (currentItem) {
                                case 'P': board.setPiece(rank, file, 1, false); break;
                                case 'N': board.setPiece(rank, file, 2, false); break;
                                case 'B': board.setPiece(rank, file, 3, false); break;
                                case 'R': board.setPiece(rank, file, 4, false); break;
                                case 'Q': board.setPiece(rank, file, 5, false); break;
                                case 'K': board.setPiece(rank, file, 6, false); break;
                                case 'p': board.setPiece(rank, file, 1, true); break;
                                case 'n': board.setPiece(rank, file, 2, true); break;
                                case 'b': board.setPiece(rank, file, 3, true); break;
                                case 'r': board.setPiece(rank, file, 4, true); break;
                                case 'q': board.setPiece(rank, file, 5, true); break;
                                case 'k': board.setPiece(rank, file, 6, true); break;
                                case 'e': board.setPiece(rank, file, 0, false); break;
                            }
                        }
                    }

                    // set turn
                    switch (fenInfo[1]) {
                        case "w": board.blacksTurn = false; break;
                        case "b": board.blacksTurn = true; break;
                    }

                    if (board.blacksTurn) frame.setTitle("Chess - Black to move");
                    else frame.setTitle("Chess - White to move");

                    if (autoFlipBoard) boardIsFlipped = board.blacksTurn;

                    // set castle ability
                    String castleAbility = fenInfo[2];

                    if (castleAbility.contains("K")) board.whiteO_O = 2; else board.whiteO_O = 0;
                    if (castleAbility.contains("Q")) board.whiteO_O_O = 2; else board.whiteO_O_O = 0;
                    if (castleAbility.contains("k")) board.blackO_O = 2; else board.blackO_O = 0;
                    if (castleAbility.contains("q")) board.blackO_O_O = 2; else board.blackO_O_O = 0;

                    // set en passant stuff
                    String enPassantAbility = fenInfo[3];
                    if (!enPassantAbility.equals("-")) {
                        if (enPassantAbility.charAt(1) == '3') board.canEnPassant = 1;
                        else if (enPassantAbility.charAt(1) == '6') board.canEnPassant = 0;

                        board.enPassantFile = enPassantAbility.charAt(0);
                    }

                    resetBoardVisuals(frame, board);

                    break;
                } catch (Exception ee) {
                    JOptionPane.showMessageDialog(frame, "Invalid FEN", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // t
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // t
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // t
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // t
                
            }

        });
    }
}