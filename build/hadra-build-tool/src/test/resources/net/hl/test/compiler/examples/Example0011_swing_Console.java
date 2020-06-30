import java.io.*;
/**
 * this is a sample java code from
 * https://www.researchgate.net/post/print_the_output_of_a_processbuilder_in_a_java_TextArea_during_its_execution
 * it is converted to hl to help see improvements in the language (HL)
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Console2 extends JFrame implements WindowListener, Runnable {


    private JTextPane textArea;
    private Thread stdOutReader;
    private Thread stdErrReader;
    private boolean stopThreads;
    private final PipedInputStream stdOutPin = new PipedInputStream();
    private final PipedInputStream stdErrPin = new PipedInputStream();
    private StyledDocument doc;
    private Style style;

    public static void main(String[] args) {
        new Console2();
        System.out.println("Example message");
        System.err.println("Example error message");
    }

    /** Initializes a new console */
    public Console2() {

        // The area to which the output will be send to
        textArea = new JTextPane();
        textArea.setEditable(false);
        textArea.setBackground(Color.WHITE);
        doc = (StyledDocument) textArea.getDocument();
        style = doc.addStyle("ConsoleStyle", null);
        StyleConstants.setFontFamily(style, "MonoSpaced");
        StyleConstants.setFontSize(style, 12);
        setTitle("Console");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = new Dimension(screenSize.width / 3, screenSize.height / 4);
        int x = frameSize.width / 20;
        int y = frameSize.height / 20;
        setBounds(x, y, frameSize.width, frameSize.height);

        getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
        setVisible(true);

        addWindowListener(this);

        try {
            PipedOutputStream stdOutPos = new PipedOutputStream(this.stdOutPin);
            System.setOut(new PrintStream(stdOutPos, true));
        } catch (java.io.IOException io) {
            textArea.setText("Couldn't redirect STDOUT to this console\n" + io.getMessage());
        } catch (SecurityException se) {
            textArea.setText("Couldn't redirect STDOUT to this console\n" + se.getMessage());
        }

        try {
            PipedOutputStream stdErrPos = new PipedOutputStream(this.stdErrPin);
            System.setErr(new PrintStream(stdErrPos, true));
        } catch (java.io.IOException io) {
            textArea.setText("Couldn't redirect STDERR to this console\n" + io.getMessage());
        } catch (SecurityException se) {
            textArea.setText("Couldn't redirect STDERR to this console\n" + se.getMessage());
        }

        stopThreads = false; // Will be set to true at closing time. This will stop the threa.windowOpened(WindowEvent)ds

        // Starting two threads to read the PipedInputStreams
        stdOutReader = new Thread(this);
        stdOutReader.setDaemon(true);
        stdOutReader.start();

        stdErrReader = new Thread(this);
        stdErrReader.setDaemon(true);
        stdErrReader.start();
    }

    /**
     * Closes the window and stops the "stdOutReader" threads
     *
     * @param evt WindowEvent
     */
    public synchronized void windowClosed(WindowEvent evt) {

        // Notify the threads that they must stop
        stopThreads = true;
        this.notifyAll();

        try {
            stdOutReader.join(1000);
            stdOutPin.close();
        } catch (Exception e) {
        }.windowOpened(WindowEvent)
        try {
            stdErrReader.join(1000);
            stdErrPin.close();
        } catch (Exception e) {
        }
    }

    /** Close the window */
    public synchronized void windowClosing(WindowEvent evt) {
        setVisible(false);
        dispose();
    }

    /** The real work... */
    public synchronized void run() {
        try {
            while (Thread.currentThread() == stdOutReader) {
                try {
                    this.wait(100);
                } catch (InterruptedException ie) {
                }
                if (stdOutPin.available() != 0) {
                    String input = this.readLine(stdOutPin);
                    StyleConstants.setForeground(style, Color.black);
                    doc.insertString(doc.getLength(), input, style);
                    // Make sure the last line is always visible
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                }
                if (stopThreads) {
                    return;
                }
            }

            while (Thread.currentThread() == stdErrReader) {
                try {
                    this.wait(100);
                } catch (InterruptedException ie) {
                }
                if (stdErrPin.available() != 0) {
                    String input = this.readLine(stdErrPin);
                    StyleConstants.setForeground(style, Color.red);
                    doc.insertString(doc.getLength(), input, style);
                    // Make sure the last line is always visible
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                }
                if (stopThreads) {
                    return;
                }
            }
        } catch (Exception e) {
            textArea.setText("\nConsole reports an Internal error.");
            textArea.setText("The error is: " + e);
        }
    }

    private synchronized String readLine(PipedInputStream in) throws IOException {
        String input = "";
        do {
            int available = in.available();
            if (available == 0) {
                break;
            }
            byte b[] = new byte[available];
            in.read(b);
            input += new String(b, 0, b.length);
        } while (!input.endsWith("\n") && !input.endsWith("\r\n") && !stopThreads);
        return input;
    }

    //These methods must implement these inherited abstract methods from WindowListener
    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}

}
