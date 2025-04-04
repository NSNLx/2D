import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Bezier2D extends JFrame {
    private JPanel inputPanel;
    private JPanel bezierPanel;
    private JButton addCurveButton, drawButton;
    private JSlider colorSlider;
    private ArrayList<JPanel> curvePanels;
    private ArrayList<JTextField[]> curves;
    private Color bezierColor = Color.BLUE;

    public Bezier2D() {
        JOptionPane.showMessageDialog(null, "P1 oraz P4 są punktami współrzędnych, P2 oraz P3 są punktami modyfikującymi.");
        setTitle("Krzywe Beziera");
        setSize(1400, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        curves = new ArrayList<>();
        curvePanels = new ArrayList<>();

        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.setPreferredSize(new Dimension(400, 400));

        addCurveButton = new JButton("Dodaj krzywą");
        drawButton = new JButton("Rysuj");

        addCurveButton.addActionListener(e -> addCurve("Nowa krzywa"));
        drawButton.addActionListener(e -> bezierPanel.repaint());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addCurveButton);
        buttonPanel.add(drawButton);
        //suwak, który odpowiada za zmianę koloru
        colorSlider = new JSlider(0, 255, 127);
        colorSlider.setMajorTickSpacing(50);
        colorSlider.setPaintTicks(true);
        colorSlider.setPaintLabels(true);
        colorSlider.addChangeListener(e -> {
            int value = colorSlider.getValue();
            bezierColor = new Color(value, 0, 255 - value);
            bezierPanel.repaint();
        });
        buttonPanel.add(new JLabel("Kolor"));
        buttonPanel.add(colorSlider);

        bezierPanel = new BezierPanel();

        add(scrollPane, BorderLayout.WEST);
        add(bezierPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addDefaultCurves();
    }
    //metoda dodająca krzywą
    private void addCurve(String label) {
        JPanel curvePanel = new JPanel(new GridLayout(3, 4, 5, 5));
        curvePanel.setBorder(BorderFactory.createTitledBorder(label));

        JTextField[] fields = new JTextField[8];
        String[] labels = {"P1 X:", "P1 Y:", "P2 X:", "P2 Y:", "P3 X:", "P3 Y:", "P4 X:", "P4 Y:"};

        for (int i = 0; i < 8; i++) {
            curvePanel.add(new JLabel(labels[i]));
            fields[i] = new JTextField("0", 4);
            curvePanel.add(fields[i]);
        }

        curves.add(fields);
        curvePanels.add(curvePanel);
        inputPanel.add(curvePanel);
        inputPanel.revalidate();
        inputPanel.repaint();
    }

    private void addDefaultCurves() {
        int[][] defaultCurves = {
                {400, 400, 300, 100, 200, 100, 100, 400},
                {100, 320, 250, 320, 250, 320, 400, 320},
                {500, 200, 550, 500, 600, 400, 650, 200},
                {650, 200, 700, 500, 750, 400, 800, 200}
        };

        String[] labels = { "A - góra", "A - środek", "W - lewa", "W - prawa"};

        for (int i = 0; i < defaultCurves.length; i++) {
            JPanel curvePanel = new JPanel(new GridLayout(3, 4, 5, 5));
            curvePanel.setBorder(BorderFactory.createTitledBorder(labels[i]));

            JTextField[] fields = new JTextField[8];
            String[] pointLabels = {"P1 X:", "P1 Y:", "P2 X:", "P2 Y:", "P3 X:", "P3 Y:", "P4 X:", "P4 Y:"};

            for (int j = 0; j < 8; j++) {
                curvePanel.add(new JLabel(pointLabels[j]));
                fields[j] = new JTextField(String.valueOf(defaultCurves[i][j]), 4);
                curvePanel.add(fields[j]);
            }

            curves.add(fields);
            curvePanels.add(curvePanel);
            inputPanel.add(curvePanel);
        }
    }

    class BezierPanel extends JPanel {
        //innowacja: kolorowanie krzywych
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bezierColor);

            for (JTextField[] fields : curves) {
                try {
                    int x1 = Integer.parseInt(fields[0].getText());
                    int y1 = Integer.parseInt(fields[1].getText());
                    int x2 = Integer.parseInt(fields[2].getText());
                    int y2 = Integer.parseInt(fields[3].getText());
                    int x3 = Integer.parseInt(fields[4].getText());
                    int y3 = Integer.parseInt(fields[5].getText());
                    int x4 = Integer.parseInt(fields[6].getText());
                    int y4 = Integer.parseInt(fields[7].getText());

                    drawBezierCurve(g2, x1, y1, x2, y2, x3, y3, x4, y4);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        //ustalanie współrzędnych oraz punktów kontrolnych
        private void drawBezierCurve(Graphics2D g2, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
            int prevX = x1, prevY = y1;
            for (double t = 0; t <= 1; t += 0.01) {
                int xt = (int) ((1 - t) * (1 - t) * (1 - t) * x1 + 3 * (1 - t) * (1 - t) * t * x2 + 3 * (1 - t) * t * t * x3 + t * t * t * x4);
                int yt = (int) ((1 - t) * (1 - t) * (1 - t) * y1 + 3 * (1 - t) * (1 - t) * t * y2 + 3 * (1 - t) * t * t * y3 + t * t * t * y4);
                //rysowanie linii na podstawie współrzędnych wyżej ^
                g2.drawLine(prevX, prevY, xt, yt);
                prevX = xt;
                prevY = yt;
            }
        }
    }

    public static void main(String[] args) {
        Bezier2D b = new Bezier2D();
        b.setVisible(true);
    }
}
