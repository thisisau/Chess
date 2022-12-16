/* Chess
 * Micah Friedman
 * November 8, 2022
 */

package chess;

import java.util.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.Color;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.awt.image.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;


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

    public static ImageIcon getSprite(int x, int y, int subX, int subY) {
        final int X = (96*x) + (24*subX);
        final int Y = 96*y + (24*subY);
        final int SUB_WIDTH = 24;
        final int SUB_LENGTH = 24;

        BufferedImage sprite = spritesheetImage.getSubimage(X+subX, Y+subY, SUB_WIDTH, SUB_LENGTH);

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
        drawCaptures(frame, board);
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

    public static void drawSubPieceAtIndefiniteSquare(JFrame frame, int x, int y, int subX, int subY, int rank, char file, int subRank, int subFile) {
        Container c = frame.getContentPane();
        JLabel square = new JLabel();
        int width = 96, height = 96;
        int subWidth = 24, subHeight = 24;
        rank = 9 - rank;
        
        square.setBounds((width*(Piece.squareFile(file)-1) + (subFile * subWidth)), (height*(rank-1) + (subRank * subHeight)), subWidth, subHeight);
        square.setIcon(getSprite(x, y, subX, subY));
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
        drawCaptures(frame, board);
    }

    public static void drawCaptures(JFrame frame, Board board) {
        char pointerFile = 'h';
        int pointerPos = 3;


        // draw black's captures
        for (int i=5; i>=0; i--) {
            int x = 0, y = 0;

            switch (i) {
                case 0: x = 1; y = 1; break;
                case 1: x = 1; y = 0; break;
                case 2: x = 2; y = 0; break;
                case 3: x = 0; y = 0; break;
                case 4: x = 3; y = 0; break;
                case 5: x = 0; y = 1; break;
            }

            for (int difference = board.pieceDifference[i]; difference < 0; difference++) { // black has piece advantage
                drawSubPieceAtIndefiniteSquare(frame, 0, 5, x, y, 0, pointerFile, 0, pointerPos);
                if (pointerPos > 0) pointerPos--;
                else {pointerPos = 3; pointerFile--;};
            }
        }

        
        pointerFile = 'a';
        pointerPos = 0;

        // draw white's captures
        for (int i=5; i>=0; i--) {
            int x = 0, y = 0;

            switch (i) {
                case 0: x = 3; y = 2; break;
                case 1: x = 3; y = 1; break;
                case 2: x = 0; y = 2; break;
                case 3: x = 2; y = 1; break;
                case 4: x = 1; y = 2; break;
                case 5: x = 2; y = 2; break;
            }

            for (int difference = board.pieceDifference[i]; difference > 0; difference--) { // black has piece advantage
                drawSubPieceAtIndefiniteSquare(frame, 0, 5, x, y, 0, pointerFile, 0, pointerPos);
                if (pointerPos < 3) pointerPos++;
                else {pointerPos = 0; pointerFile++;};
            }
        }
    }

    static JMenuBar menubar;
    static JRadioButtonMenuItem greenColor, purpleColor;
    static JMenuItem flipItem, resetBoard, importFEN, chess960, viewGame, copyPGN, undoMove;
    static JCheckBoxMenuItem autoFlipItem;
    static boolean autoFlipBoard = true;
    static boolean boardIsFlipped = false;
    static JMenu newGameMenu;
    public static void createMenuBar(JFrame frame, boolean green) {
        // "Board" menu
        menubar = new JMenuBar();
        JMenu menu = new JMenu("Board");
        JMenu boardStyle = new JMenu("Color");
        greenColor = new JRadioButtonMenuItem("Green", green);
        purpleColor = new JRadioButtonMenuItem("Purple", !green);
        flipItem = new JMenuItem("Flip Board");
        autoFlipItem = new JCheckBoxMenuItem("Flip After Turn", true);
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
        resetBoard = new JMenuItem("Default");
        importFEN = new JMenuItem("From FEN");
        chess960 = new JMenuItem("Chess960");
        newGameMenu = new JMenu("New Game");
        viewGame = new JMenuItem("View on Lichess");

        // "Moves" menu
        JMenu moveMenu = new JMenu("Move");
        copyPGN = new JMenuItem("Copy PGN to Clipboard");
        undoMove = new JMenuItem("Undo Move");

        newGameMenu.add(resetBoard);
        newGameMenu.add(importFEN);
        newGameMenu.add(chess960);
        gameMenu.add(newGameMenu);
        gameMenu.add(viewGame);
        gameMenu.add(copyPGN);
        moveMenu.add(undoMove);
        menubar.add(gameMenu);
        menubar.add(moveMenu);


        frame.setJMenuBar(menubar);
    }

    public static void resetBoard(Board board) {
        board = new Board();
        board.setLayout(1);
    }


    public static void main(String[] args) {
        // init window
        JFrame frame = new JFrame("Chess - White to move");
        frame.setSize(96*8+16, 96*8/*8 ranks of 96 pixels*/+62/*arbitrary offset thing*/+24/*bottom height*/+frame.getInsets().top+frame.getInsets().bottom);
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

        frame.getContentPane().setBackground(new Color(101, 140, 77)); 

        

        Board board = new Board();
        board.setLayout(1);
        drawPieces(frame, board);
        board.startingFen = board.toFen();

        drawEmptyBoard(frame, board.color);

        frame.setVisible(true);


        frame.addMouseListener(new MouseListener() {

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {

                if (board.gameStatus != 0) return;

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
                    try {
                        JOptionPane.showMessageDialog(frame, String.format("Debug Info:\nSquare Clicked: %s%s\nBlack's Turn: %s\nPiece type: %s | black: %s\nLocation of piece's king: %s\nBlack is in check: %s\nWhite is in check: %s\nEn Passant: (-1=nobody, 0=white, 1=black) %s on %s file\nCastling: wk: %s | wq: %s | bk: %s | bq: %s\nRook Start Files: %s, %s\nKing Start File: %s\nCurrent FEN: %s\nInitial FEN: %s", fileClicked, rankClicked, board.blacksTurn, pieceClicked.type, pieceClicked.black, board.getKingLocation(pieceClicked.black), board.blackKingInCheck, board.whiteKingInCheck, board.canEnPassant, board.enPassantFile, board.whiteO_O, board.whiteO_O_O, board.blackO_O, board.blackO_O_O, board.kingRookFile, board.queenRookFile, board.kingFile, board.toFen(), board.startingFen));
                    }
                    catch (Exception ee) {
                        JOptionPane.showMessageDialog(frame, String.format("Debug Info:\nCurrent PGN:\n%s", board.generatePgn()));
                    }
                    return;
                }

                if ((fileClicked > 'h') || (fileClicked < 'a') || (rankClicked > 8) || (rankClicked < 1)) return;
                ArrayList<move> allMoves = board.allMovesFor(rankClicked, fileClicked);

                Piece lastPieceClicked = board.pieceAt(board.lastRankClicked, board.lastFileClicked);

                // START IF
                if (
                    
                    (
                        board.showingLegalMoves 
                        && 
                        (
                            (
                                pieceClicked.type == 0
                            ) 
                            || 
                            (
                                (pieceClicked.type != 0) 
                                && 
                                (pieceClicked.black != board.blacksTurn)
                            )
                        )
                    ) 

                    ||

                    (
                        (
                            board.pieceCanMoveTo(board.lastRankClicked, board.lastFileClicked, rankClicked, fileClicked, lastPieceClicked) == 5
                                ||
                            board.pieceCanMoveTo(board.lastRankClicked, board.lastFileClicked, rankClicked, fileClicked, lastPieceClicked) == 6
                        )
                        &&
                            board.showingLegalMoves
                    )
                
                
                ) { // If the board is showing legal moves and a non-team piece is clicked
                    ArrayList<move> allLegalMoves = board.allMovesFor(board.lastRankClicked, board.lastFileClicked);
                    for (move m : allLegalMoves) {
                        if ((m.endFile == fileClicked) && (m.endRank == rankClicked)) {
                            board.movePiece(m, true);
                            board.blacksTurn = !board.blacksTurn;
                            clear(frame);

                            board.lastClicked = null;
                            board.showingLegalMoves = false;
                            if (board.blacksTurn) frame.setTitle("Chess - Black to move");
                            else frame.setTitle("Chess - White to move");
                            if (autoFlipBoard && (boardIsFlipped != board.blacksTurn)) boardIsFlipped = !boardIsFlipped;
                            drawPieces(frame, board);
                            drawEmptyBoard(frame, board.color);
                            drawCaptures(frame, board);
                            switch (board.gameStatus) {
                                case 0:
                                    break;
                                case 1:
                                    frame.setTitle("Chess - White is victorious");
                                    JOptionPane.showMessageDialog(frame, "White wins by checkmate", "Game Over", JOptionPane.PLAIN_MESSAGE);
                                    break;
                                case 2:
                                    frame.setTitle("Chess - Black is victorious");
                                    JOptionPane.showMessageDialog(frame, "Black wins by checkmate", "Game Over", JOptionPane.PLAIN_MESSAGE);
                                    break;
                                case 3:
                                    frame.setTitle("Chess - Game drawn");
                                    JOptionPane.showMessageDialog(frame, "Draw by stalemate", "Game Over", JOptionPane.PLAIN_MESSAGE);
                                    break;
                                case 4:
                                    frame.setTitle("Chess - Game drawn");
                                    JOptionPane.showMessageDialog(frame, "Draw by insufficient material", "Game Over", JOptionPane.PLAIN_MESSAGE);
                                    break;
                                default:
                                    break;
                            }
                            return;
                        }
                    }
                    clear(frame);
                    board.lastClicked = null;
                    board.showingLegalMoves = false;
                }


                else if ((pieceClicked.black == board.blacksTurn) && (pieceClicked.type != 0)) { // if a team piece is clicked
                        clear(frame);
                        
                        if ((!board.showingLegalMoves) || (pieceClicked != board.lastClicked)) {
                            for (move move : allMoves) {
                                if (move.moveType != 2 && move.moveType != 5 && move.moveType != 6) drawPiece(frame, 1, 4, move.endRank, move.endFile);
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
                drawCaptures(frame, board);
                
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
                frame.getContentPane().setBackground(new Color(101, 140, 77)); 
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
                frame.getContentPane().setBackground(new Color(115, 66, 173));
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
                board.reset();
                board.setLayout(1);
                board.startingFen = board.toFen();

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
                        board.importFEN(fen);
                        break;
                    } catch (Exception ee) {
                        JOptionPane.showMessageDialog(frame, "Invalid FEN", "Error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                }
                
                if (autoFlipBoard) boardIsFlipped = board.blacksTurn;
                if (board.blacksTurn) frame.setTitle("Chess - Black to move");
                else frame.setTitle("Chess - White to move");
                
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

        chess960.addMouseListener(new MouseListener() {

            @Override
            public void mousePressed(MouseEvent e) {
                board.reset();

                int knightA, knightB, bishopA, bishopB, queen, rookA, rookB, king;

                int[] backRank = new int[8];
                ArrayList<Integer> available = new ArrayList<Integer>();
                for (int i=0; i<8; i++) available.add(i);

                // bishop placement
                bishopA = ThreadLocalRandom.current().nextInt(0, 3 + 1)*2;
                bishopB = ThreadLocalRandom.current().nextInt(0, 3 + 1)*2 + 1;

                available.remove((Object) bishopA);
                available.remove((Object) bishopB);

                // knight placement

                knightA = available.get(ThreadLocalRandom.current().nextInt(0, 5+1));
                available.remove((Object) knightA);

                knightB = available.get(ThreadLocalRandom.current().nextInt(0, 4+1));
                available.remove((Object) knightB);

                // queen placement

                queen = available.get(ThreadLocalRandom.current().nextInt(0, 3+1));
                available.remove((Object) queen);

                // rook and king placement

                rookA = available.get(0);
                rookB = available.get(2);
                king = available.get(1);

                backRank[bishopA] = 3;
                backRank[bishopB] = 3;
                backRank[knightA] = 2;
                backRank[knightB] = 2;
                backRank[queen] = 5;
                backRank[rookA] = 4;
                backRank[rookB] = 4;
                backRank[king] = 6;

                board.reset();

                for (char file = 'a'; file <= 'h'; file++) {
                    int fileAsInt = Piece.squareFile(file) - 1;

                    board.setPiece(1, file, backRank[fileAsInt], false);
                    board.setPiece(8, file, backRank[fileAsInt], true);

                    board.setPiece(2, file, 1, false);
                    board.setPiece(7, file, 1, true);
                }

                board.kingFile = (char) (king + 97);
                board.queenRookFile = (char) (rookA + 97);
                board.kingRookFile = (char) (rookB + 97);
                board.startingFen = board.toFen();
                resetBoardVisuals(frame, board);
                
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                //  Auto-generated method stub
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //  Auto-generated method stub
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //  Auto-generated method stub
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //  Auto-generated method stub
                
            }

        });

        viewGame.addMouseListener(new MouseListener() {

            @Override
            public void mousePressed(MouseEvent e) {
                // stolen from https://stackoverflow.com/a/35013372
                try {

                    // create url
                    URL url;
                    url = new URL("https://lichess.org/api/import");

                    // connect to url
                    URLConnection con;
                    con = url.openConnection();
                    HttpURLConnection http = (HttpURLConnection) con;
                    http.setRequestMethod("POST");
                    http.setDoOutput(true);

                    // post stuff
                    Map<String,String> arguments = new HashMap<>();
                    arguments.put("pgn", board.generatePgn());

                    // idk
                    StringJoiner sj = new StringJoiner("&");
                    for(Map.Entry<String,String> entry : arguments.entrySet()) sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
                    byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
                    int length = out.length;
                    http.setFixedLengthStreamingMode(length);
                    http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                    try {
                        http.connect();
                    } catch (Exception ee) {
                        JOptionPane.showMessageDialog(frame, "A connection error occured.", "Connection Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    OutputStream os = http.getOutputStream();
                    os.write(out);

                    // no longer stolen

                    InputStream s = http.getInputStream();
                    String result;

                    try (Scanner scanner = new Scanner(s)) {
                        result = scanner.hasNextLine() ? scanner.nextLine() : "";
                    }

                    String[] gameUrl;
                    gameUrl = result.split("\"");

                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(new URI(gameUrl[7]));
                    }

                } catch (Exception ee) {
                    JOptionPane.showMessageDialog(frame, "An error occured.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                
            }
            
        });

        copyPGN.addMouseListener(new MouseListener() {

            @Override
            public void mousePressed(MouseEvent e) {
                String pgn = board.generatePgn();
                StringSelection stringSelection = new StringSelection(pgn);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                
            }
            
        });

        undoMove.addMouseListener(new MouseListener() {

            @Override
            public void mousePressed(MouseEvent e) {
                ArrayList<move> movesToMake = new ArrayList<move>(board.movesList);
                boolean blacksTurnAfter = (movesToMake.size() % 2 == 0) ? true : false;
                try {
                    movesToMake.remove(movesToMake.size()-1);
                } catch (java.lang.IndexOutOfBoundsException ee) {
                    return;
                }

                // reset stuff
                board.blacksTurn = false;
                board.lastClicked = null;
                board.showingLegalMoves = false;
                board.lastFileClicked = '\u0000';
                board.lastRankClicked = 0;
                board.canEnPassant = -1;
                board.enPassantFile = '\u0000';
                board.blackO_O = 2;
                board.blackO_O_O = 2;
                board.whiteO_O = 2;
                board.whiteO_O_O = 2;
                board.lastMove = new move(0, 'a', 0, 'a', 0);
                board.pieceDifference = new int[6];
                board.movesListStr = new ArrayList<String>();
                board.movesList = new ArrayList<move>();
                board.subMove = 1;
                board.gameStatus = 0;
                board.importFEN(board.startingFen);

                // do the moves
                for (move m : movesToMake) {
                    board.movePiece(m, true);
                }

                board.blacksTurn = blacksTurnAfter;
                if (autoFlipBoard) boardIsFlipped = board.blacksTurn;
                
                if (board.blacksTurn) frame.setTitle("Chess - Black to move");
                else frame.setTitle("Chess - White to move");
                
                resetBoardVisuals(frame, board);


                
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                //  Auto-generated method stub
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //  Auto-generated method stub
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //  Auto-generated method stub
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //  Auto-generated method stub
                
            }

        });
    }
}