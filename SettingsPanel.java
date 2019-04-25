import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.*;
import java.util.Hashtable;
import java.util.concurrent.Semaphore;
import javax.swing.*;
import javax.swing.border.LineBorder;

/**
 * <b>SettingsPanel Class</b>
 * <p>
 * Represents the primary content panel for configurations of Tetris Game.
 * <p>
 */

public class SettingsPanel extends JPanel {

    private JLabel countLabel;
    JSlider scoringFactorSlider;
    JSlider rowsPerLevelSlider;
    JSlider speedFactorSlider;
    JSlider mainAreaWidthSlider;
    JSlider mainAreaHeightSlider;
    JSlider squareSizeSlider;

    
    public SettingsPanel(Semaphore s){

        setLayout(new GridBagLayout());
        setPreferredSize( new Dimension( 1000    , 750 ) );

        //Create Components

        scoringFactorSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        scoringFactorSlider.setMinorTickSpacing(1);
        scoringFactorSlider.setMajorTickSpacing(1);
        scoringFactorSlider.setPaintTicks(true);
        scoringFactorSlider.setPaintLabels(true);
        JLabel scoringLabel = new JLabel("Scoring Factor:    ",JLabel.RIGHT);
        // We'll just use the standard numeric labels for now...
        scoringFactorSlider.setLabelTable(scoringFactorSlider.createStandardLabels(1));

        rowsPerLevelSlider = new JSlider(JSlider.HORIZONTAL, 20, 50,20);
        rowsPerLevelSlider.setMinorTickSpacing(2);
        rowsPerLevelSlider.setMajorTickSpacing(6);
        rowsPerLevelSlider.setPaintTicks(true);
        rowsPerLevelSlider.setPaintLabels(true);
        JLabel rowsPerLevelLabel = new JLabel("Rows Per Level:    ",JLabel.RIGHT);
        rowsPerLevelSlider.setLabelTable(rowsPerLevelSlider.createStandardLabels(2));


        speedFactorSlider = new JSlider(JSlider.HORIZONTAL, 1, 10,1);
        speedFactorSlider.setMinorTickSpacing(1);
        speedFactorSlider.setMajorTickSpacing(1);
        speedFactorSlider.setPaintTicks(true);
        speedFactorSlider.setPaintLabels(true);
        JLabel speedFactorLabel = new JLabel("Speed Factor:    ",JLabel.RIGHT);
        speedFactorSlider.setLabelTable(speedFactorSlider.createStandardLabels(1));

        mainAreaWidthSlider = new JSlider(JSlider.HORIZONTAL, 10, 20,10);
        mainAreaWidthSlider.setMinorTickSpacing(1);
        mainAreaWidthSlider.setMajorTickSpacing(1);
        mainAreaWidthSlider.setPaintTicks(true);
        mainAreaWidthSlider.setPaintLabels(true);
        JLabel   mainAreaWidthLabel = new JLabel("Play Area Width:   ",JLabel.RIGHT);
        mainAreaWidthSlider.setLabelTable(  mainAreaWidthSlider.createStandardLabels(1));

        mainAreaHeightSlider = new JSlider(JSlider.HORIZONTAL, 20, 30,20);
        mainAreaHeightSlider.setMinorTickSpacing(1);
        mainAreaHeightSlider.setMajorTickSpacing(1);
        mainAreaHeightSlider.setPaintTicks(true);
        mainAreaHeightSlider.setPaintLabels(true);
        JLabel   mainAreaHeightLabel = new JLabel("Play Area Height:    ",JLabel.RIGHT);
        mainAreaHeightSlider.setLabelTable(  mainAreaHeightSlider.createStandardLabels(1));

        squareSizeSlider = new JSlider(JSlider.HORIZONTAL, 3, 5,3);
        squareSizeSlider.setMinorTickSpacing(1);
        squareSizeSlider.setMajorTickSpacing(1);
        squareSizeSlider.setPaintTicks(true);
        squareSizeSlider.setPaintLabels(true);
        JLabel   squareSizeLabel = new JLabel("Set Square Size:    ",JLabel.RIGHT);
        Hashtable labels = new Hashtable();
        labels.put(3, new JLabel("Large"));
        labels.put(4, new JLabel("Medium"));
        labels.put(5, new JLabel("Small"));
        squareSizeSlider.setValue(3);
        squareSizeSlider.setLabelTable(  labels);




        //mainPanel component adding
        JLabel emptyLabel = new JLabel(" ",SwingConstants.CENTER);
        JLabel shapesLabel = new JLabel("Next Page: Select and add more shapes.",SwingConstants.CENTER);
        shapesLabel.setFont(new Font("Arial", 20,20));
        shapesLabel.setBorder(LineBorder.createGrayLineBorder());

        //SettingsPanel component adding
        add(scoringLabel, getGridBagConstraints(0,0));
        add(scoringFactorSlider, getGridBagConstraints(1,0));
        add(emptyLabel, getGridBagConstraints(2,0));

        add(rowsPerLevelLabel, getGridBagConstraints(0,1));
        add(rowsPerLevelSlider, getGridBagConstraints(1,1));
        add(emptyLabel, getGridBagConstraints(2,1));

        add(speedFactorLabel, getGridBagConstraints(0,2));
        add(speedFactorSlider, getGridBagConstraints(1,2));
        add(emptyLabel, getGridBagConstraints(2,2));

        add(mainAreaWidthLabel, getGridBagConstraints(0,3));
        add(mainAreaWidthSlider, getGridBagConstraints(1,3));
        add(emptyLabel, getGridBagConstraints(2,3));

        add(mainAreaHeightLabel, getGridBagConstraints(0,4));
        add(mainAreaHeightSlider, getGridBagConstraints(1,4));
        add(emptyLabel, getGridBagConstraints(2,4));

        add(squareSizeLabel, getGridBagConstraints(0,5));
        add(squareSizeSlider, getGridBagConstraints(1,5));
        add(emptyLabel, getGridBagConstraints(2,5));

        add(emptyLabel, getGridBagConstraints(0,6));
        
        add(emptyLabel, getGridBagConstraints(0,7));
        add(shapesLabel,getGridBagConstraints(1,7));
        add(emptyLabel, getGridBagConstraints(2,7));

        JButton startButton = new JButton("Continue");
        startButton.addActionListener(new ContinueButtonListener(s));
        add(startButton,getGridBagConstraints(1,16));

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

    private class ContinueButtonListener implements ActionListener {

        private final Semaphore s;

        ContinueButtonListener(Semaphore s ) {
              this.s = s;
        }
        public void actionPerformed(ActionEvent event){
            //Increment count and update countLabel
            s.release();
        }
    }

}


