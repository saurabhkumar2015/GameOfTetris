import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.concurrent.Semaphore;

/** Theme: Configure and play the game of Tetris.
 *  Author: Saurabh Kumar
 *  The Game starts with a setting screen where the user can select the size of a block, gameSpeed, rows, columns and scoring Fator.
 *  The next screen is to allow the user to add more shapes to the testris game i.e. more shapes on top of the standard 4 block shapes.
 *
 *  @Parameter: gameSpeed is the starting speed of the game.
 *
 *  The game starts a thread with a while loop to sleep after one fall and then resume.
 *  The gameSpeed decides the sleeptime and the speed of the fall of shapes in the game of tetris
 *
 */

public class GameOfTetris extends Frame {

    private static SettingsPanel settingsPanel;
    private long gameSpeed = 800L;
    private double speedFactor;
    static Point[][][]  gameBlocks ;  // The final array deciding the shapes in play in the TETRIS game.

    private GameOfTetris() {

        super("Game Of Tetris!!!. Hover your mouse to pause the game!! Use mouse clicks and scroll to play the game");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        setSize(900,750);

        CvTetris canvas = new CvTetris();

        /**
         * Extract the value of each slider and configure the parameters
         */
        speedFactor = settingsPanel.speedFactorSlider.getValue();
        canvas.M = settingsPanel.scoringFactorSlider.getValue();
        canvas.N = settingsPanel.rowsPerLevelSlider.getValue();
        canvas.colsBound = settingsPanel.mainAreaWidthSlider.getValue();
        canvas.rowsBound = settingsPanel.mainAreaHeightSlider.getValue();
        canvas.sizeFactor = settingsPanel.squareSizeSlider.getValue();

        canvas.rWidth = 800F ;
        canvas.rHeight = 800F ;
        add("Center", canvas);    // the program to form the interface of the game of tetris
        setCursor(Cursor.getPredefinedCursor(Cursor.
                DEFAULT_CURSOR));
        setVisible(true);

        Random r = new Random();
        int figure = r.nextInt(gameBlocks.length);  //select a random shape block
        int orientation = r.nextInt(3);  // select a random orientation of the selected shape block

        Position cPosition = new Position();
        cPosition.block = GameOfTetris.gameBlocks[figure][orientation];
        cPosition.c = CvTetris.blockColors[r.nextInt(8)];
        cPosition.pX = (canvas.colsBound/2)-1;
        cPosition.pY = 0;
        cPosition.blockX = figure;
        cPosition.blockY = orientation;

        canvas.currentPosition = cPosition;
        canvas.newNextBlock();                // generate a new next block shape
        canvas.repaint();
        //Engine Thread of the game.
        new Thread(() -> {
            System.out.println("MAIN thread::"+Thread.currentThread().getName());
            while (true) {
                try {
                    if(!canvas.isPause) {
                        canvas.lock.lock();
                        if(canvas.resetCount != 0){
                            canvas.currentPosition.pY +=1;
                            canvas.drawCurrentBlock(true);
                        }
                        else {
                            canvas.currentPosition.pY =0;
                            canvas.drawCurrentBlock(true);
                        }
                        canvas.lock.unlock();
                    }
                    Thread.sleep((long)(gameSpeed/(1+(canvas.levelCount-1)*speedFactor)));            // Controlled by the game speed. Game speed is the speed at which a block falls.
                } catch ( InterruptedException e ) {
                    System.out.println("Game Interrupted::" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public static void main(String args[]) throws InterruptedException {
        JFrame settingsFrame = new JFrame("Settings for the Game OF Tetris");

        //Create counting functionality SettingsPanel and add to display
        Semaphore s  = new Semaphore(0);
        settingsPanel = new SettingsPanel(s);
        settingsFrame.getContentPane().add(settingsPanel);

        //Set close operation response to normal window close
        settingsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Size counterFrame and contents according to set preferred sizes
        settingsFrame.setResizable(false);
        settingsFrame.pack();

        //Render the display visible
        settingsFrame.setVisible(true);
        s.acquire();
        settingsFrame.setVisible(false);

        JFrame shapesFrame = new JFrame("Select the shapes you want in the Game Of Tetris");
        ShapesPanel shapesPanel = new ShapesPanel(s);
        shapesFrame.getContentPane().add(shapesPanel);

        //Set close operation response to normal window close
        shapesFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Size counterFrame and contents according to set preferred sizes
        shapesFrame.setResizable(false);
        shapesFrame.pack();

        //Render the display visible
        shapesFrame.setVisible(true);
        s.acquire(); // Waits till the user has confirmed the settings for the game
        shapesFrame.setVisible(false);
        int size =0;
        for(int k=0; k<9; k++){
           if(shapesPanel.shapesCheckboxes[k].isSelected() ) {
               size++;
           }
        }
        gameBlocks =  new Point[7+size][4][4];
        for(int k =0; k <7 ;k++) {
            for (int i =0; i < 4 ;i++){
                System.arraycopy(CvTetris.tetrisBlocklines[k][i], 0, gameBlocks[k][i], 0, 4);
            }

        }
        int index = 7;
        for(int k = 7; k < 16; k++){
            if(shapesPanel.shapesCheckboxes[k-7].isSelected())
            {
                for (int i =0; i < 4 ;i++) {
                    System.arraycopy(CvTetris.tetrisBlocklines[k][i], 0, gameBlocks[index][i], 0, 4);
                }
                index++;
            }
        }
        new GameOfTetris();
    }
}
