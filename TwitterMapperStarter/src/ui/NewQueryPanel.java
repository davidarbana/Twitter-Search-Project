package ui;

import query.Query;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

/**
 * A UI panel for entering new queries.
 */
public class NewQueryPanel extends JPanel {
    private final JTextField newQuery = new JTextField(10);
    private final JLabel queryLabel = new JLabel("Enter Search: ");
    private final JPanel colorSetter;
    private final Application app;
    private Random random;

    public NewQueryPanel(Application app) {
        this.app = app;
        this.colorSetter = new JPanel();

        random = new Random();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        queryLabel.setLabelFor(newQuery);
        GridBagConstraints gbc = new GridBagConstraints();

        configSearchQuery(gbc);

        add(Box.createRigidArea(new Dimension(5, 5)));

        configSelectColor(gbc);

        add(Box.createRigidArea(new Dimension(5, 5)));

        JButton addQueryButton = configAddButton(gbc);

        // This makes the "Enter" key submit the query.
        app.getRootPane().setDefaultButton(addQueryButton);

        chooseColor(app);
    }

    private void chooseColor(Application app) {
        colorSetter.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Color newColor = JColorChooser.showDialog(
                            app,
                            "Choose Background Color",
                            colorSetter.getBackground());
                    // newColor is "null" if user clicks "Cancel" button on JColorChooser popup.
                    if (newColor != null) {
                        colorSetter.setBackground(newColor);
                    }
                }
            }
        });
    }

    private JButton configAddButton(GridBagConstraints gbc) {
        JButton addQueryButton = new JButton("Add New Search");
        gbc.gridx = GridBagConstraints.RELATIVE;       //aligned with button 2
        gbc.gridwidth = 2;   //2 columns wide
        gbc.gridy = GridBagConstraints.RELATIVE;       //third row
        add(addQueryButton, gbc);

        setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("New Search"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));

        addQueryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!newQuery.getText().equals("")) {
                    addQuery(newQuery.getText().toLowerCase());
                    newQuery.setText("");
                }
            }
        });
        return addQueryButton;
    }

    private void configSelectColor(GridBagConstraints gbc) {
        JLabel colorLabel = new JLabel("Select Color: ");
        colorSetter.setBackground(getRandomColor());

        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 1;
        gbc.gridx = 0;
        add(colorLabel, gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        colorSetter.setMaximumSize(new Dimension(200, 20));
        add(colorSetter, gbc);
    }

    private void configSearchQuery(GridBagConstraints gbc) {
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 0;
        gbc.gridx = 0;
        add(queryLabel, gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        newQuery.setMaximumSize(new Dimension(200, 20));
        gbc.gridx = 1;
        add(newQuery, gbc);
    }

    private void addQuery(String newQuery) {
        Query query = new Query(newQuery, colorSetter.getBackground(), app.map());
        app.getQueryConfiguration().addQuery(query);
        colorSetter.setBackground(getRandomColor());
    }

    public Color getRandomColor() {
        // Pleasant colors: https://stackoverflow.com/questions/4246351/creating-random-colour-in-java#4246418
        final float hue = random.nextFloat();
        final float saturation = (random.nextInt(2000) + 1000) / 10000f;
        final float luminance = 0.9f;
        return Color.getHSBColor(hue, saturation, luminance);
    }
}
