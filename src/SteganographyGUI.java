import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SteganographyGUI extends JFrame {
    private JTextField inputImageField;
    private JTextField outputImageField;
    private JTextField dataField;
    private JButton encodeButton;
    private JButton decodeButton;

    public SteganographyGUI() {
        setTitle("Steganography");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        inputImageField = new JTextField(20);
        outputImageField = new JTextField(20);
        dataField = new JTextField(20);
        encodeButton = new JButton("Encode");
        decodeButton = new JButton("Decode");

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Input image:"), gbc);
        gbc.gridx = 1;
        add(inputImageField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Output image:"), gbc);
        gbc.gridx = 1;
        add(outputImageField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Data:"), gbc);
        gbc.gridx = 1;
        add(dataField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(encodeButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(decodeButton, gbc);

        encodeButton.addActionListener(new EncodeButtonListener());
        decodeButton.addActionListener(new DecodeButtonListener());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class EncodeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String inputImage = inputImageField.getText();
            String outputImage = outputImageField.getText();
            String data = dataField.getText();

            if (inputImage.isEmpty() || outputImage.isEmpty() || data.isEmpty()) {
                JOptionPane.showMessageDialog(SteganographyGUI.this, "All fields must be filled in.");
                return;
            }

            try {
                Steganography.encode(inputImage, outputImage, data);
                JOptionPane.showMessageDialog(SteganographyGUI.this, "Data encoded successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(SteganographyGUI.this, "An error occurred: " + ex.getMessage());
            }
        }
    }

    private class DecodeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String inputImage = inputImageField.getText();

            if (inputImage.isEmpty()) {
                JOptionPane.showMessageDialog(SteganographyGUI.this, "Input image field must be filled in.");
                return;
            }

            try {
                String decodedData = Steganography.decode(inputImage);
                dataField.setText(decodedData);
                JOptionPane.showMessageDialog(SteganographyGUI.this, "Data decoded successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(SteganographyGUI.this, "An error occurred: " + ex.getMessage());
            }
        }
    }
}
