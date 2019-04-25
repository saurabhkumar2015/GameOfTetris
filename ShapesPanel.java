import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

public class ShapesPanel extends JPanel {

    Semaphore s;
    public JCheckBox [] shapesCheckboxes = new JCheckBox[9];

    public ShapesPanel(Semaphore s) {

        setLayout(new GridBagLayout());
        setPreferredSize( new Dimension( 1000    , 750 ) );
        this.s =s;

        JLabel shapesLabel = new JLabel("Select",SwingConstants.RIGHT);
        shapesLabel.setFont(new Font("Arial", 20,20));

        JLabel shapesLabel2 = new JLabel(" the shapes you want to add in this page.",SwingConstants.LEFT);
        shapesLabel2.setFont(new Font("Arial", 20,20));

        JLabel shapesLabel3 = new JLabel("",SwingConstants.RIGHT);
        shapesLabel3.setFont(new Font("Arial", 20,20));

        JLabel shapesLabel4 = new JLabel("Please tick on the small shapes",SwingConstants.RIGHT);
        shapesLabel4.setFont(new Font("Arial", 16,16));

        JLabel shapesLabel5 = new JLabel(" to be added in the game",SwingConstants.LEFT);
        shapesLabel5.setFont(new Font("Arial", 16,16));


        //SettingsPanel component adding
        add(shapesLabel, getGridBagConstraints(0,0));
        add(shapesLabel2, getGridBagConstraints(1,0));
        add(shapesLabel3, getGridBagConstraints(2,0));
        add(shapesLabel4, getGridBagConstraints(0,1));
        add(shapesLabel5, getGridBagConstraints(1,1));

        JCheckBox j = new JCheckBox();
        shapesCheckboxes[0] = j;
        j.setHorizontalTextPosition(SwingConstants.RIGHT);
        this.add(j, getGridBagConstraints(0,2));
        JLabel jj = new JLabel(new ShapesIcon(new int[][]{{1,0,0},{0,0,0},{0,0,0}}, Color.PINK), SwingConstants.LEFT);
        this.add(jj, getGridBagConstraints(1, 2));

        JCheckBox j1 = new JCheckBox();
        shapesCheckboxes[1] = j1;
        j1.setHorizontalTextPosition(SwingConstants.RIGHT);
        this.add(j1, getGridBagConstraints(2,2));
        JLabel jj1 = new JLabel(new ShapesIcon(new int[][]{{1,1,0},{0,0,0},{0,0,0}}, Color.PINK), SwingConstants.LEFT);
        this.add(jj1, getGridBagConstraints(3, 2));

        JCheckBox j2 = new JCheckBox();
        shapesCheckboxes[2] = j2;
        j2.setHorizontalTextPosition(SwingConstants.RIGHT);
        this.add(j2, getGridBagConstraints(4,2));
        JLabel jj2 = new JLabel(new ShapesIcon(new int[][]{{1,1,1},{0,0,0},{0,0,0}}, Color.ORANGE), SwingConstants.LEFT);
        this.add(jj2, getGridBagConstraints(5, 2));

        JCheckBox j3 = new JCheckBox();
        shapesCheckboxes[3] = j3;
        j3.setHorizontalTextPosition(SwingConstants.RIGHT);
        this.add(j3, getGridBagConstraints(0,3));
        JLabel jj3 = new JLabel(new ShapesIcon(new int[][]{{1,0,0},{0,1,0},{0,0,1}}, Color.BLUE), SwingConstants.LEFT);
        this.add(jj3, getGridBagConstraints(1, 3));

        JCheckBox j4 = new JCheckBox();
        shapesCheckboxes[4] = j4;
        j4.setHorizontalTextPosition(SwingConstants.RIGHT);
        this.add(j4, getGridBagConstraints(2,3));
        JLabel jj4 = new JLabel(new ShapesIcon(new int[][]{{1,1,0},{0,1,0},{0,0,0}}, Color.GREEN), SwingConstants.LEFT);
        this.add(jj4, getGridBagConstraints(3, 3));

        JCheckBox j5 = new JCheckBox();
        shapesCheckboxes[5] = j5;
        j5.setHorizontalTextPosition(SwingConstants.RIGHT);
        this.add(j5, getGridBagConstraints(4,3));
        JLabel jj5 = new JLabel(new ShapesIcon(new int[][]{{1,0,0},{0,1,0},{1,0,0}}, Color.GREEN), SwingConstants.LEFT);
        this.add(jj5, getGridBagConstraints(5, 3));

        JCheckBox j6 = new JCheckBox();
        shapesCheckboxes[6] = j6;
        j6.setHorizontalTextPosition(SwingConstants.RIGHT);
        this.add(j6, getGridBagConstraints(0,4));
        JLabel jj6 = new JLabel(new ShapesIcon(new int[][]{{1,0,0},{0,1,0},{0,1,0}}, Color.cyan), SwingConstants.LEFT);
        this.add(jj6, getGridBagConstraints(1, 4));

        JCheckBox j7 = new JCheckBox();
        shapesCheckboxes[7] = j7;
        j7.setHorizontalTextPosition(SwingConstants.RIGHT);
        this.add(j7, getGridBagConstraints(2,4));
        JLabel jj7 = new JLabel(new ShapesIcon(new int[][]{{1,0,0},{0,1,1},{0,0,0}}, Color.magenta), SwingConstants.LEFT);
        this.add(jj7, getGridBagConstraints(3, 4));

        JCheckBox j8 = new JCheckBox();
        shapesCheckboxes[8] = j8;
        j8.setHorizontalTextPosition(SwingConstants.RIGHT);
        this.add(j8, getGridBagConstraints(4,4));
        JLabel jj8 = new JLabel(new ShapesIcon(new int[][]{{0,0,1},{0,1,0},{0,0,0}}, Color.GREEN), SwingConstants.LEFT);
        this.add(jj8, getGridBagConstraints(5, 4));

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(new StartButtonListener(s));

        JButton selectAllButton = new JButton("Select All");
        selectAllButton.addActionListener(new SelectAllButtonListener(shapesCheckboxes));
        add(selectAllButton, getGridBagConstraints(1,5));
        add(startButton,getGridBagConstraints(2,5));



    }

    private GridBagConstraints getGridBagConstraints(int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = x;
        c.insets = new Insets(5,0,5,0);
        c.gridy = y;
        return c;
    }

}

class ShapesIcon implements Icon{

    private final int[][] shape;
    private final Color c;

    ShapesIcon(int[][] shape , Color c) {
        this.shape = shape;
        this.c = c;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int i, int i1) {

        for(int m =0 ; m <3 ; m++) {
            for(int n =0; n < 3 ;n++) {
                if(shape[m][n] ==1) {
                    graphics.setColor(c);
                    graphics.fillRect(10 * n, 10 * m, 10, 10);
                    graphics.setColor(Color.BLACK);
                    graphics.drawRect(10 * n, 10 * m, 10, 10);
                }
            }
        }
    }

    @Override
    public int getIconWidth() {
        return 100;
    }

    @Override
    public int getIconHeight() {
        return 100;
    }
}

class StartButtonListener implements ActionListener {

    private final Semaphore s;

    StartButtonListener(Semaphore s ) {
        this.s = s;
    }
    public void actionPerformed(ActionEvent event){
        //Increment count and update countLabel
        s.release();
    }
}

class SelectAllButtonListener implements ActionListener {

    private final JCheckBox[] s;

    SelectAllButtonListener(JCheckBox[] s ) {
        this.s = s;
    }
    public void actionPerformed(ActionEvent event){
        //Increment count and update countLabel
        for(JCheckBox j : s){
            j.setSelected(true);
        }
    }
}
