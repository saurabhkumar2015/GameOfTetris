import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Draw the interface of the game of Tetris and simulate the pause and quit functionality.
 * Implements mouse motion listener to implement pause, change shape and quit functionality.
 * Implements mouse released listener to implement the left and right shift of blocks.
 * Implements mouse wheel listener to rotate the shapes left or right
 * The field mainArea represents the current state of the game with the colors of each square in the game area.
 * levelCount
 */
public class CvTetris extends Canvas {

    private boolean gameOver = false;
    int numShapes;
    int rowsBound = 30;     //Height or depth of the game area.
    int colsBound =20;              //Breadth or width of the game area.
    int sizeFactor = 5;
    int levelCount = 1;
    boolean change = true;
    private int linesCount = 0;
    private int scoreCount = 0;
    int M =1;
    int N =1;
    private float pixelSize;
    float rWidth = 750F, rHeight = 750F;
    boolean isPause= false;
    int resetCount = 0;
    private int centerX, centerY, left,right,top,bottom,size;
    private int startBoxNextX,startBoxNextY;


    private Color[][] mainArea = new Color[colsBound][rowsBound];          // The 2D array storing the current state of the game of tetris.
    private Random r = new Random();
    ReentrantLock lock = new ReentrantLock();

    private Position nextPosition;     // stores all the attributes related to the next block in the game
    private Position previousPosition; // stores all the attributes related to the previous block in the game
    Position currentPosition;          // stores all the attributes related to the current block in the game


    CvTetris() {
        this.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int motion = e.getWheelRotation();
                if (!isPause) {
                    if (motion < 0) {
                        lock.lock();
                        int newOr = (currentPosition.blockY + 1) % 4;    // new orientation of current block
                        checkAndRotate(newOr);                           // draw the current block
                        lock.unlock();
                    } else {
                        lock.lock();
                        int newOr = (currentPosition.blockY - 1) % 4;
                        if (newOr == -1) newOr = 3;
                        checkAndRotate(newOr);
                        lock.unlock();
                    }

                }
            }
        });

        // Motion Listener to detect the mouse movement and pause the game when required
        this.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent evt) {
                int x = evt.getX();
                int y = evt.getY();
                Graphics g = getGraphics();
                if(x >=left && x <=right && y >=top && y <=bottom) {    // Check if the mouse is now inside the main area.
                    g.setColor(Color.BLUE);
                    isPause = true;
                    if(change && checkIfInsideCurrentBlock(x, y)) { //Replace current Block with next Block
                       lock.lock();
                       Position tmp = currentPosition;
                       removeBlock(nextPosition, startBoxNextX, startBoxNextY);
                       currentPosition = nextPosition;

                       currentPosition.pX = tmp.pX;
                       currentPosition.pY = tmp.pY;
                       scoreCount -= levelCount*M;
                       repaint();
                       newNextBlock();
                       drawCurrentBlock(false);
                       lock.unlock();
                       change = false;
                    }
                    g.setFont(new Font(null,Font.BOLD,size));
                    g.drawRect(left +size *colsBound/4,top + size*(rowsBound/3),(colsBound/2+1)*size,2*size);
                    g.drawString(PAUSE, left + size*colsBound/4 + size * colsBound/10, top + size* (rowsBound/3+2) -2);
                }
                else {
                    if(isPause){
                        g.clearRect(left + size* colsBound/4 ,top + size*(rowsBound/3) ,(colsBound/2+1)*size +1,2*size+1);
                        drawMainArea(g);
                    }
                    isPause = false;
                }
            }
        });
        //Mouse listener to detect if a quit is pressed by the user
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent evt) {
                int x0 = evt.getX();
                int y0 = evt.getY();

                int x1 = iX(rWidth/3)+ size;
                int y1 = iY(-rHeight/sizeFactor) -sizeFactor*size/2;
                if(x0 > x1 && x0 < x1 +4*size && y0 > y1 && y0 < y1 + sizeFactor*size/2) //Check if the click is inside the quit rectangle
                    System.exit(0);
                if(evt.getButton() == MouseEvent.BUTTON1) {
                    if(!isPause){
                        lock.lock();
                        int newX =currentPosition.pX-1;
                        if(newX < 0) {
                            lock.unlock();
                            return;
                        }

                        for(Point p:currentPosition.block){
                            if(mainArea[newX+p.x][currentPosition.pY+p.y] !=null) {
                                boolean flag =false;
                                for(Point p1: currentPosition.block){
                                    if(p1.x == p.x-1 && p1.y ==p.y )flag = true;
                                }
                                if(!flag) {
                                    lock.unlock();
                                    return;
                                }
                            }
                        }
                        Position tmp = new Position(newX, currentPosition.pY, currentPosition.c,
                                currentPosition.block, currentPosition.blockX, currentPosition.blockY);

                        previousPosition = currentPosition;
                        currentPosition = tmp;
                        drawCurrentBlock(false);
                        lock.unlock();
                    }
                }
                if(evt.getButton() == MouseEvent.BUTTON3) {
                    if(!isPause){
                        lock.lock();
                        int newX = currentPosition.pX+1;
                        for(Point p :currentPosition.block){
                            if(newX+p.x >= colsBound ) {
                                lock.unlock();
                                return;
                            }
                        }
                        for(Point p:currentPosition.block){
                            if(mainArea[newX+p.x][currentPosition.pY+p.y] !=null) {
                                boolean flag =false;
                                for(Point p1: currentPosition.block){
                                    if(p1.x == 1+p.x && p1.y ==p.y )flag = true;
                                }
                                if(!flag) {
                                    lock.unlock();
                                    return;
                                }
                            }
                        }
                        Position tmp = new Position(newX, currentPosition.pY, currentPosition.c,
                                currentPosition.block, currentPosition.blockX, currentPosition.blockY);
                        previousPosition = currentPosition;
                        currentPosition = tmp;
                        drawCurrentBlock(false);
                        lock.unlock();
                    }
                }
            }
        });
    }

    private boolean checkIfInsideCurrentBlock(int x, int y) {

        Position po =  this.currentPosition;

         int px = left + po.pX*size;
         int py = top + po.pY*size;

        for(Point p : po.block) {
            int xx = px+ size*p.x;
            int yy = py + size*p.y;

            if(x >=xx && x <=xx+size && y >= yy && y <= yy+size) {
                return true;
            }
        }
        return false;
    }

    private void checkAndRotate(int newOr) {
        Point[] newBlock = GameOfTetris.gameBlocks[currentPosition.blockX][newOr];
        for(Point p:newBlock){
            int newX = currentPosition.pX +p.x;
            int newY = currentPosition.pY +p.y;
            if( newX < 0 || newX > colsBound-1 || newY <0 || currentPosition.pY+p.y > rowsBound-1)   {
                return;
            }
            if(mainArea[currentPosition.pX+ p.x][currentPosition.pY+p.y] !=null) {
                boolean flag =false;
                for(Point p1: currentPosition.block){
                    if(p1.x == p.x && p1.y ==p.y )flag = true;
                }
                if(!flag) {
                    return;
                }
            }
        }
        Position tmp = new Position(currentPosition.pX, currentPosition.pY, currentPosition.c,
                newBlock, currentPosition.blockX, newOr);

        previousPosition = currentPosition;
        currentPosition = tmp;
        drawCurrentBlock(false);
    }

    /**
     * Method which calculates and updates the pixelSize
     */
    private void setupPizelSize() {
        Dimension d = getSize();
        int maxX = d.width - 1, maxY = d.height - 1;
        pixelSize = Math.max(rWidth / maxX, rHeight / maxY);
        centerX = maxX / 2;
        centerY = maxY / 2;
    }

    private int iX(float x) {
        return Math.round(centerX + x / pixelSize);
    }

    private int iY(float y) {
        return Math.round(centerY - y / pixelSize);
    }
    /**
     * Paint method to paint the interface of the game of tetris.
     * @param g Graphics
     */
    public void paint(Graphics g) {
        setupPizelSize();
        size = (int)(rWidth/sizeFactor/10/pixelSize);

        left = iX(-rWidth / 2.5f);    //Decide on the dimensions of the main area of the game
        top = iY(rHeight /(sizeFactor-(float)rowsBound/30));
        bottom = top + rowsBound*size;
        right = left + colsBound*size;

        drawMainArea(g);

        // next shape box
        drawNextBlock();

        // paint the text
        printStats(g);

        //        quit button
        g.drawRect(iX(rWidth/3)+ size,iY(-rHeight/sizeFactor) -sizeFactor*size/2 ,size*4,size*sizeFactor/2);
        g.setFont(new Font(null,Font.BOLD,size*5/6));
        g.drawString(QUIT, iX(rWidth/3) + 2*size, iY(-rHeight/sizeFactor) - size/2);

    }

    private void printStats(Graphics g) {
        g.setFont(new Font(null,Font.BOLD,size));
        g.drawString(level+ levelCount, iX(rWidth/3), iY(-rHeight/sizeFactor) -10*size);
        g.drawString(lines+ linesCount, iX(rWidth/3), iY(-rHeight/sizeFactor) - 8*size);
        g.drawString(score+scoreCount, iX(rWidth/3), iY(-rHeight/sizeFactor) - 6*size);
    }

    private void drawNextBlock() {
        Graphics g = getGraphics();
        startBoxNextX = iX(rWidth / 3);
        startBoxNextY = iY(rHeight / sizeFactor);
        g.drawRect(startBoxNextX,startBoxNextY + size/2 ,6*size,5*size);
        drawBlock(nextPosition, startBoxNextX, startBoxNextY, false);
    }

    private void drawMainArea(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawRect(left, top, size*colsBound, size*rowsBound);     // Draw the main area of the game

        for(int i = 0; i < mainArea.length; i++) {
            for(int j = 0; j < mainArea[i].length; j++) {
                Color color = mainArea[i][j];
                if(color != null) {
                    g.setColor(color);
                    g.fillRect(left + i * size, top + j * size, size, size);
                    g.setColor(Color.black);
                    g.drawRect(left + i * size, top + j * size, size, size);
                }
            }
        }
    }

    public void drawCurrentBlock(boolean flag) {
        if(!checkForCollision()) {
            if (previousPosition != null) {
                removeBlock(previousPosition, left,top);
            }
            drawBlock(currentPosition, left, top, true);
            previousPosition = copyToPreviousPosition();
            this.resetCount++;
        }
        else {
            change = true;
            if(flag){
                if(this.currentPosition.pY !=0) {
                    removeBlock(nextPosition, startBoxNextX, startBoxNextY);
                    nextPosition.pX = colsBound/2-1;
                    nextPosition.pY = 0;
                    currentPosition = nextPosition;
                    this.nextPosition = null;
                    this.previousPosition = null;
                    resetCount =0;
                    checkForRowFills();
                    if(linesCount >= N) {
                        resetMainAreaForNewLevel();
                        linesCount = 0;
                    }
                    repaint();
                    newNextBlock();
                }
                else {                 // Game Over
                   if(!gameOver) {
                       JFrame frame = new JFrame();
                       frame.setSize(100, 150);
                       Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                       frame.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
                       JLabel l = new JLabel("Game Over", SwingConstants.CENTER);
                       l.setFont(new Font("Arial", 20,20));
                       frame.add(l);

                       frame.setVisible(true);
                       gameOver = true;
                   }
                }
            }
        }
    }

    private void resetMainAreaForNewLevel() {

        levelCount++;
        for (int i = rowsBound - 1; i > -1; i--) {
            for (int j = 0; j < colsBound; j++) {
                mainArea[j][i] = null;
            }
        }
    }

    private void checkForRowFills() {

        for(int i =rowsBound-1 ; i >-1 ; i-- ) {
            boolean isFilled = true;
            for(int j = 0 ; j < colsBound;j++){
                if(mainArea[j][i] == null) isFilled = false;
            }
            if(isFilled){
                linesCount +=1;
                scoreCount += levelCount* M;
                for(int n = 0; n < colsBound; n++){
                    mainArea[n][i] = null;
                    int emptySpot = i;
                    for (int m = i+1; m < rowsBound ; m++) {
                        if(mainArea[n][m] != null) break;
                        else emptySpot++;
                    }
                    for(int k =i-1 ; k >-1; k--) {
                        if(mainArea[n][k] != null){
                            mainArea[n][emptySpot] = mainArea[n][k];
                            mainArea[n][k] = null;
                            emptySpot--;
                        }
                    }
                }
                i = Math.min(rowsBound+2, rowsBound);
            }
        }
    }

    private Position copyToPreviousPosition() {
        return new Position(currentPosition.pX, currentPosition.pY, currentPosition.c, currentPosition.block, currentPosition.blockX, currentPosition.blockY);
    }

    private boolean checkForCollision() {
        Point[] blocks = this.currentPosition.block;
        int x = this.currentPosition.pX;
        int y = this.currentPosition.pY;

        Point[] previous = null;
        int prevX = 0;
        int prevY = 0;
        if(this.previousPosition != null) {
            previous = this.previousPosition.block;
            prevX = this.previousPosition.pX;
            prevY = this.previousPosition.pY;
        }

        for(Point p : blocks) {
            if (y + p.y > rowsBound - 1 ) return true;
            if(mainArea[x+p.x][y+p.y]!= null){
                if(previous!= null) {
                    boolean skip = false;
                    for (Point pp : previous) {
                        if (prevX + pp.x == x + p.x && prevY + pp.y == y + p.y) {
                            skip = true;
                            break;
                        }
                    }
                    if (!skip) return true;
                }
                else return true;
            }
        }
        return false;
    }

    private void drawBlock(Position po, int x1, int y1, boolean flag) {

        Graphics g = getGraphics();
        for(Point p : po.block) {
            int x = po.pX + p.x;
            int y = po.pY + p.y;
            g.setColor(po.c);
            g.fillRect(x1 + x*size, y1 + y*size, size,size);
            if(flag) mainArea[x][y] = po.c;
            g.setColor(Color.black);
            g.drawRect(x1+x*size, y1+y*size, size,size);
        }
    }

    private void removeBlock(Position position, int x1, int y1) {
        Graphics g = getGraphics();
        for(Point p : position.block) {
            int x = position.pX + p.x;
            int y = position.pY + p.y;
            mainArea[x][y] = null;
            g.clearRect(x1+x*size, y1 + (y * size), size+1,size+1);
            if(x-1 >=0 && mainArea[x-1][y] !=null) g.drawLine(x1+x*size, y1+y*size,x1+x*size, y1+(y+1)*size);
            if(x+1 < colsBound && mainArea[x+1][y] !=null) g.drawLine(x1+(x+1)*size, y1+y*size,x1+(x+1)*size, y1+(y+1)*size);
        }
        getGraphics().drawRect(left, top, size*colsBound, size*rowsBound);
    }

    void newNextBlock() {
        Position nPosition = new Position();
        nPosition.blockX =r.nextInt(GameOfTetris.gameBlocks.length);;
        nPosition.blockY =r.nextInt(3);
        nPosition.block = GameOfTetris.gameBlocks[nPosition.blockX][nPosition.blockY];
        nPosition.c = CvTetris.blockColors[r.nextInt(8)];
        nPosition.pX = 1;
        nPosition.pY = 1;
        nextPosition = nPosition;
        drawNextBlock();
    }


    //Each Tetris block is represented as a list of rectangles of different sizes
    static final Color[] blockColors = {Color.red, Color.green,Color.blue,Color.orange, Color.YELLOW, Color.cyan, Color.ORANGE,Color.PINK,Color.GRAY};
    static final Point[][][] tetrisBlocklines = {
            //S block
            {
                    { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) }, //clockwise arrangements
                    { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
                    { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) }
            },
            //Z block
            {
                    { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
                    { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) },
                    { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
                    { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) }
            },
            // T Block
            {
                    { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) },
                    { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) }
            },
            // O Block
            {
                    { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) }
            },
            // J Block
            {
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0) },
                    { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 2) },
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2) },
                    { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0) }
            },
            // L Block
            {
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2) },
                    { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2) },
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0) },
                    { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(1, 0) }
            },
            //I Block
            {
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
                    { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) },
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(0, 3) }
            },
            //Single square Block
            {
                    { new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0) },
                    { new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0) },
                    { new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0) },
                    { new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0) }
            },
            // Two Square Simple Block
            {
                    { new Point(0, 0), new Point(1, 0), new Point(0, 0),new Point(0, 0)},
                    { new Point(0, 0), new Point(0, 1), new Point(0, 0), new Point(0, 0) },
                    { new Point(0, 0), new Point(1, 0), new Point(0, 0), new Point(0, 0) },
                    { new Point(0, 0), new Point(0, 1), new Point(0, 0), new Point(0, 0) }
            },
            // Three Square Simple Block
            {
                    { new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(2, 0) },
                    { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(0, 2) },
                    { new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(2, 0) },
                    { new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(0, 2) }
            },
            // Three Square Ladder Block
            {
                    { new Point(0, 0), new Point(1, 1), new Point(2, 2), new Point(2, 2) },
                    { new Point(0, 2), new Point(1, 1), new Point(2, 0), new Point(2, 0) },
                    { new Point(0, 0), new Point(1, 1), new Point(2, 2), new Point(2, 2) },
                    { new Point(0, 2), new Point(1, 1), new Point(2, 0), new Point(2, 0) }
            },
            // Three Square Edge Block
            {
                    { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(0, 1) },
                    { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(1, 1) },
                    { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(1, 1) }
            },
            // Three Square Arrow Block
            {
                    { new Point(0, 1), new Point(1, 0), new Point(2, 1), new Point(2, 1) },
                    { new Point(0, 0), new Point(1, 1), new Point(0, 2), new Point(0, 2) },
                    { new Point(0, 0), new Point(1, 1), new Point(2, 0), new Point(2, 0) },
                    { new Point(0, 1), new Point(1, 0), new Point(1, 2), new Point(1, 2) }
            },
            // Three Square  Two Step Ladder Block
            {
                    { new Point(0, 0), new Point(1, 0), new Point(2, 1), new Point(2, 1) },
                    { new Point(1, 0), new Point(1, 1), new Point(0, 2), new Point(1, 0) },
                    { new Point(0, 1), new Point(1, 0), new Point(2, 0), new Point(2, 0) },
                    { new Point(0, 0), new Point(1, 1), new Point(1, 2), new Point(1, 2) }
            },
            // Three Square  Two Step Ladder Block
            {
                    { new Point(0, 0), new Point(1, 0), new Point(2, 1), new Point(2, 1) },
                    { new Point(1, 0), new Point(1, 1), new Point(0, 2), new Point(1, 0) },
                    { new Point(0, 1), new Point(1, 0), new Point(2, 0), new Point(2, 0) },
                    { new Point(0, 0), new Point(1, 1), new Point(1, 2), new Point(1, 2) }
            },
            // Two Square Two Step Ladder
            {
                    { new Point(0, 0), new Point(1, 1),new Point(0, 0),new Point(0, 0)},
                    { new Point(1, 0), new Point(0, 1),new Point(1, 0),new Point(1, 0)},
                    { new Point(0, 0), new Point(1, 1),new Point(0, 0),new Point(0, 0)},
                    { new Point(1, 0), new Point(0, 1),new Point(1, 0),new Point(1, 0)}
            },
    };

    private static final String level = "Level:   ";
    private static final String lines = "Lines:   ";

    private static final String score = "Score:   ";
    private static final String PAUSE = "PAUSE";
    private static final String QUIT = "QUIT";

}

class Position {
    Position(){}
    Position(int x, int y, Color c, Point[] block, int blockX, int blockY) {
        this.pX = x;
        this.pY =y;
        this.c = c;
        this.block = block;
        this.blockX = blockX;
        this.blockY = blockY;
    }

    public int blockX;
    public int blockY;
    public int pX;
    public int pY;
    public Color c;
    public Point[] block;
}
