
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main extends JFrame {

    private final JButton[] cards = new JButton[16];

    private final String[] symbols = {
            "🍎", "🍎",
            "🚗", "🚗",
            "🐱", "🐱",
            "⚽", "⚽",
            "🌟", "🌟",
            "🎮", "🎮",
            "🍕", "🍕",
            "🚀", "🚀"
    };

    private final boolean[] matched = new boolean[16];

    private int firstCard = -1;
    private int secondCard = -1;
    private int moves = 0;
    private int pairsFound = 0;

    private boolean checking = false;

    private JLabel movesLabel;
    private JLabel pairsLabel;
    private JLabel timerLabel;
    private JLabel statusLabel;

    private Timer gameTimer;
    private Timer flipTimer;

    private int seconds = 0;

    private final Color backgroundColor = new Color(18, 22, 38);
    private final Color cardColor = new Color(43, 51, 78);
    private final Color accentColor = new Color(0, 220, 180);
    private final Color textColor = new Color(240, 243, 255);

    public Main() {

        setTitle("Memory Match - Java Game");
        setSize(650, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        createGame();

        setVisible(true);
    }

    private void createGame() {

        getContentPane().setBackground(backgroundColor);
        setLayout(new BorderLayout(15, 15));

        // HEADER
        JPanel header = new JPanel();
        header.setBackground(backgroundColor);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel title = new JLabel("MEMORY MATCH");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(accentColor);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Find all 8 matching pairs!");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(textColor);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);
        header.add(Box.createVerticalStrut(20));

        // STATISTICS
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statsPanel.setBackground(backgroundColor);

        movesLabel = createStatLabel("MOVES", "0");
        pairsLabel = createStatLabel("PAIRS", "0 / 8");
        timerLabel = createStatLabel("TIME", "00:00");

        statsPanel.add(movesLabel);
        statsPanel.add(pairsLabel);
        statsPanel.add(timerLabel);

        header.add(statsPanel);

        add(header, BorderLayout.NORTH);

        // GAME BOARD
        JPanel board = new JPanel(new GridLayout(4, 4, 10, 10));
        board.setBackground(backgroundColor);
        board.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        List<String> shuffledSymbols = new ArrayList<>();

        Collections.addAll(shuffledSymbols, symbols);
        Collections.shuffle(shuffledSymbols);

        for (int i = 0; i < 16; i++) {

            final int index = i;

            JButton button = new JButton("?");

            button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 34));
            button.setForeground(textColor);
            button.setBackground(cardColor);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createLineBorder(
                    new Color(70, 82, 120), 2
            ));

            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            cards[i] = button;

            String symbol = shuffledSymbols.get(i);

            button.putClientProperty("symbol", symbol);

            button.addActionListener(e -> flipCard(index));

            board.add(button);
        }

        add(board, BorderLayout.CENTER);

        // FOOTER
        JPanel footer = new JPanel();
        footer.setBackground(backgroundColor);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(BorderFactory.createEmptyBorder(5, 20, 20, 20));

        statusLabel = new JLabel("Ready? Find your first pair!");
        statusLabel.setForeground(textColor);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton restartButton = new JButton("↻  RESTART GAME");

        restartButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        restartButton.setForeground(Color.BLACK);
        restartButton.setBackground(accentColor);
        restartButton.setFocusPainted(false);
        restartButton.setBorderPainted(false);
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        restartButton.setMaximumSize(new Dimension(230, 45));
        restartButton.setPreferredSize(new Dimension(230, 45));
        restartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        restartButton.addActionListener(e -> restartGame());

        footer.add(statusLabel);
        footer.add(Box.createVerticalStrut(15));
        footer.add(restartButton);

        add(footer, BorderLayout.SOUTH);

        // GAME TIMER
        gameTimer = new Timer(1000, e -> {

            seconds++;

            int minutes = seconds / 60;
            int remainingSeconds = seconds % 60;

            timerLabel.setText(
                    "TIME\n" + String.format("%02d:%02d", minutes, remainingSeconds)
            );
        });

        gameTimer.start();
    }

    private JLabel createStatLabel(String title, String value) {

        JLabel label = new JLabel(
                "<html><center>" + title + "<br><br>" + value + "</center></html>",
                SwingConstants.CENTER
        );

        label.setOpaque(true);
        label.setBackground(cardColor);
        label.setForeground(textColor);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setBorder(BorderFactory.createEmptyBorder(12, 5, 12, 5));

        return label;
    }

    private void flipCard(int index) {

        if (checking || matched[index]) {
            return;
        }

        if (index == firstCard) {
            return;
        }

        JButton button = cards[index];

        button.setText((String) button.getClientProperty("symbol"));
        button.setBackground(new Color(64, 78, 112));

        if (firstCard == -1) {

            firstCard = index;

            statusLabel.setText("Find the matching card!");

        } else {

            secondCard = index;
            moves++;

            updateStatistics();

            String firstSymbol = (String)
                    cards[firstCard].getClientProperty("symbol");

            String secondSymbol = (String)
                    cards[secondCard].getClientProperty("symbol");

            if (firstSymbol.equals(secondSymbol)) {

                matched[firstCard] = true;
                matched[secondCard] = true;

                cards[firstCard].setBackground(new Color(20, 125, 105));
                cards[secondCard].setBackground(new Color(20, 125, 105));

                pairsFound++;

                pairsLabel.setText("PAIRS\n" + pairsFound + " / 8");

                statusLabel.setText("Nice! You found a matching pair!");

                firstCard = -1;
                secondCard = -1;

                if (pairsFound == 8) {

                    gameTimer.stop();

                    statusLabel.setText(
                            "🏆 YOU WIN! Completed in " + moves + " moves!"
                    );

                    JOptionPane.showMessageDialog(
                            this,
                            "Congratulations! You matched all 8 pairs!\n\n"
                                    + "Moves: " + moves + "\n"
                                    + "Time: " + timerLabel.getText()
                                    .replaceAll("<[^>]*>", ""),
                            "YOU WIN!",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }

            } else {

                checking = true;

                statusLabel.setText("Not a match! Try again.");

                flipTimer = new Timer(800, e -> {

                    cards[firstCard].setText("?");
                    cards[secondCard].setText("?");

                    cards[firstCard].setBackground(cardColor);
                    cards[secondCard].setBackground(cardColor);

                    firstCard = -1;
                    secondCard = -1;

                    checking = false;

                    statusLabel.setText("Keep going! Find another pair!");

                    flipTimer.stop();
                });

                flipTimer.setRepeats(false);
                flipTimer.start();
            }
        }
    }

    private void updateStatistics() {

        movesLabel.setText("MOVES\n" + moves);
    }

    private void restartGame() {

        if (gameTimer != null) {
            gameTimer.stop();
        }

        if (flipTimer != null) {
            flipTimer.stop();
        }

        firstCard = -1;
        secondCard = -1;
        moves = 0;
        pairsFound = 0;
        seconds = 0;
        checking = false;

        for (int i = 0; i < cards.length; i++) {
            matched[i] = false;
        }

        getContentPane().removeAll();

        createGame();

        movesLabel.setText("MOVES\n0");
        pairsLabel.setText("PAIRS\n0 / 8");
        timerLabel.setText("TIME\n00:00");

        revalidate();
        repaint();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(Main::new);
    }
}

