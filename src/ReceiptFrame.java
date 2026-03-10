import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;

public class ReceiptFrame extends JFrame {
    public ReceiptFrame(String receiptText) {
        setTitle("Receipt");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTextArea area = new JTextArea(receiptText);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        add(new JScrollPane(area), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton saveBtn = new JButton("Save Receipt");
        JButton closeBtn = new JButton("Close");
        bottom.add(saveBtn);
        bottom.add(closeBtn);
        add(bottom, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("receipt.txt"));
            int res = chooser.showSaveDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                try {
                    Files.write(f.toPath(), receiptText.getBytes());
                    JOptionPane.showMessageDialog(this, "Saved to: " + f.getAbsolutePath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error saving receipt: " + ex.getMessage());
                }
            }
        });

        closeBtn.addActionListener(e -> this.dispose());
    }
}
