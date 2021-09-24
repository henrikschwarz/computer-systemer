import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

/*
 * A simple GUI simulator for the machine
 * in Appendix C of Computer Science: An Overview.
 *
 * Written by Mike Slattery - Nov. 2001
 * Modified by Glenn Brookshear - Mar. 2002
 */

public class Simulator
{
    public static void main (String[] args)
    {
        MachDisplay md = new MachDisplay();
        md.addWindowListener(new DisplayWindowListener());
        md.show();
    }
}

class DisplayWindowListener extends WindowAdapter
{

    public void windowClosing(WindowEvent e)
    {
        System.exit(0);
    }
}

class MachDisplay extends JFrame
{
    JTextArea inArea;
    // extend edge of grid to make checking neighbors easier
    JLabel[][] mem = new JLabel[17][17];
    JLabel[][] regs = new JLabel[16][2];
        JLabel[][] spRegs = new JLabel[2][2];
    JButton clearb, loadb, runb, stepb, haltb, helpb;
    boolean running;

    static String toHex(int val, int width)
    {
        //Return a string of at least width chars holding
        //the hex representation of the non-negative val.

        String s = Integer.toHexString(val).toUpperCase();
        while (s.length() < width)
            s = "0"+s;
        return s;
    }

    static int toInt(String hex)
    {
        int r = 0;

        //Return the value represented by the string hex
        try
        {
            r = Integer.parseInt(hex, 16);
        }
        catch (Exception e)
        {
            //System.out.println("Trouble with :"+hex+":");
        };
        return r;
    }

    // First design the visual layout.
    MachDisplay()
    {
        super("Simple Computer");
        setSize(850,700);
        JPanel contPanel = new JPanel(new BorderLayout());
        JPanel memPanel = new JPanel(new BorderLayout());

        memPanel.add(new JLabel("Main Memory", SwingConstants.CENTER), BorderLayout.NORTH);
        JPanel memContPanel = new JPanel(new GridLayout(17,17,5,5));
        memContPanel.setBackground(Color.white);
        Border b = BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(5,5,5,5),
        BorderFactory.createLineBorder(Color.black,2));
        memContPanel.setBorder(b);
        memPanel.add(memContPanel, BorderLayout.CENTER);
           //memPanel.setBackground(Color.white);
        JPanel cpuPanel = new JPanel(new BorderLayout());
            //cpuPanel.setBackground(Color.white);
        cpuPanel.add(new JLabel("CPU", SwingConstants.CENTER), BorderLayout.NORTH);
        JPanel gpRegPanel = new JPanel(new GridLayout(16,2,5,5));
        gpRegPanel.setBackground(Color.white);
        JPanel spRegPanel = new JPanel(new GridLayout(2,2,5,5));
        spRegPanel.setBackground(Color.white);
        JPanel regPanel = new JPanel(new FlowLayout(0,15,0));
        regPanel.setBackground(Color.white);
        regPanel.setBorder(b);

        regPanel.add(gpRegPanel, BorderLayout.EAST);
        regPanel.add(spRegPanel, BorderLayout.WEST);
        cpuPanel.add(regPanel, BorderLayout.CENTER);
        // Create space for regs and memory in
        // internal arrays
        for(int i = 0; i < 16; i++)
        {
            regs[i][0] = new JLabel("R"+toHex(i,1));
            regs[i][1] = new JLabel("00");
        }

        spRegs[0][0] = new JLabel("PC");
        spRegs[0][1] = new JLabel("00");
        spRegs[1][0] = new JLabel("IR");
        spRegs[1][1] = new JLabel("0000");
        for(int i = 0; i < 17; i++)
        {
            mem[i][0] = new JLabel(toHex(i-1,1), SwingConstants.CENTER);
            for(int j = 1; j < 17; j++)
            if (i == 0)
                //mem[i][j] = new JLabel(toHex(j-1,1), SwingConstants.CENTER);
                mem[i][j] = new JLabel(" " + toHex(j-1,1));
            else
                mem[i][j] = new JLabel("00");
        }
        // Reset a special case
        mem[0][0].setText("");
        // Then display the arrays on screen
        for(int i = 0; i < 16; i++)
        {
            gpRegPanel.add(regs[i][0]);
            gpRegPanel.add(regs[i][1]);
        }
        for(int i = 0; i < 2; i++)
        {
            spRegPanel.add(spRegs[i][0]);
            spRegPanel.add(spRegs[i][1]);
        }

        for(int i = 0; i < 17; i++)
        {
            for(int j = 0; j < 17; j++)
                memContPanel.add(mem[i][j]);
        }
        contPanel.add(cpuPanel, BorderLayout.WEST);
        contPanel.add(memPanel, BorderLayout.CENTER);
        JPanel inPanel = new JPanel(new BorderLayout());
        inPanel.add(new JLabel("Data Input Window",SwingConstants.CENTER), BorderLayout.NORTH);
        inArea = new JTextArea(10,10);
        inArea.setBorder(b);
        inPanel.add(inArea, BorderLayout.CENTER);
        contPanel.add(inPanel, BorderLayout.NORTH);
        JPanel controls = new JPanel();
        BListen ops = new BListen();
        clearb = new JButton("Clear Memory");
        clearb.addActionListener(ops);
        controls.add(clearb);
        loadb = new JButton("Load Data");
        loadb.addActionListener(ops);
        controls.add(loadb);
        runb = new JButton("Run");
        runb.addActionListener(ops);
        controls.add(runb);
        stepb = new JButton("Single Step");
        stepb.addActionListener(ops);
        controls.add(stepb);
        haltb = new JButton("Halt");
        haltb.addActionListener(ops);
        controls.add(haltb);
        helpb = new JButton("Help");
        helpb.addActionListener(ops);
        controls.add(helpb);
        contPanel.add(controls, BorderLayout.SOUTH);
        setContentPane(contPanel);
    }

    // Next, define the Listener classes that make things work
    class BListen implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            Object button = e.getSource();

            if (button == clearb)
                clearMem();
            else if (button == loadb)
                initMem();
            else if (button == runb)
                doRun();
            else if (button == stepb)
                doStep();
            else if (button == haltb)
                running = false;
            else if (button == helpb)
                getHelp();
        }
    }

    class Runner extends Thread
    {
        public void run()
        {
            running = true;
            while (running)
                doStep();
            System.out.println("Simulated execution halted.");
        }
    }

    void clearMem()
    {
        for (int i = 1; i < 17; i++)
            for (int j = 1; j < 17; j++)
                mem[i][j].setText("00");
    }

    void getHelp()
    {
        String helpString = "The machine is programmed by means of the data input window.\n\n" +
                             "The syntax follows these examples:\n" +
                             "  [PC] 80            Sets the program counter to 80 (hex).\n" +
                             "  [R7] 23            Sets the contents of register 7 to 23 (hex).\n" +
                             "  [30] 40 56 C0 00   Sets the contents of memory starting\n" +
                             "                     at 30 (hex) to the values 40, 56, C0, 00.\n\n" +
                             "The syntax is free format. For example, \n" +
                             "         [PC] \n" +
                             "         00 [00] 20 FF\n" +
                             "         40 02 C0 00 \n" +
                             "sets the program counter and memory cells. \n\n" +
                             "Once changes have been entered into the data input window\n" +
                             "   they can be transferred into the machine by clicking the\n" +
                             "   Load Data button.\n\n" +
                             "Programs can be placed in the data input window or saved from \n" +
                             "   the data input window by copying (highlight text and then \n" +
                             "   type Ctrl-C) and pasting (Ctrl-V) from or to a text file.\n";
        JDialog helpBox = new JDialog();
        helpBox.setSize(new Dimension(500, 300));
        JTextArea helpArea = new JTextArea(23, 70);
        helpArea.append(helpString);
        JScrollPane helpScrollPane = new JScrollPane(helpArea);
        helpBox.getContentPane().add(helpScrollPane);
        helpBox.show();
    }

    // Finally, the methods that actually run the machine
    void initMem()
    {
        // Process program input.
        // Values in square brackets set mem address
        // for loading of following values.
        // Value in asterisks sets initial PC.

        // First, clear various regs, etc.
        for (int i = 0; i < 16; i++)
            regs[i][1].setText("00");
        spRegs[0][1].setText("00");
        spRegs[1][1].setText("0000");
        for (int i = 1; i < 17; i++)
            for (int j = 1; j < 17; j++)
                mem[i][j].setText("00");

        String input = inArea.getText();

        // The following code should parse valid input, but will
        // be unpredictable on invalid input.
        int address = 0;
        int rNum = 0;
        int ptr = 0;
        boolean chgReg = false;
        boolean chgMem = false;
        boolean chgPC = false;
        while (ptr < input.length())
        {
            char c = input.charAt(ptr);
            if (c <= ' ')
            {
                //System.out.println("skip");
                ptr++;
                continue;
            }
            else if (c == '[')
            {
                ptr++;
                c = input.charAt(ptr);
                if (c == 'R' || c == 'r')
                {
                    rNum = toInt(input.substring(ptr+1, ptr+2));
                    chgReg = true;
                    chgMem = false;
                    chgPC = false;
                 }
                 else if (c == 'P' || c =='p')
                 {
                     chgPC = true;
                     chgMem = false;
                     chgReg = false;
                 }
                 else
                 {
                     address = toInt(input.substring(ptr,ptr+2));
                     chgMem = true;
                     chgPC = false;
                     chgReg = false;
                 }
                 ptr += 3;
            }
            else // should be hex digit
            {
                //System.out.println("Bottom:"+c+":");
                int val = toInt(input.substring(ptr,ptr+2));
                //System.out.println("Read:"+val);
                ptr += 2;
                if (chgPC) spRegs[0][1].setText(toHex(val,2));
                if (chgReg) regs[rNum][1].setText(toHex(val,2));
                if (chgMem)
                {
                    mem[(address/16)+1][(address%16)+1].setText(toHex(val,2));
                    address++;
                }
            }
        }
    }

    void doStep()
    {
        //Execute one step of the machine

        //Get current program counter
        int loc = toInt(spRegs[0][1].getText());
        //I think this next case will never happen, but...
        if ((loc < 0) || (loc > 0xFE))
        {
            System.out.println("Illegal instruction address");
            running = false;
            return;
        }
        //Fetch instruction
        String byte1 = mem[(loc/16)+1][(loc%16)+1].getText();
        loc++;
        loc &= 0xFF;
        String byte2 = mem[(loc/16)+1][(loc%16)+1].getText();
        loc++;
        loc &= 0xFF;
        spRegs[1][1].setText(byte1 + byte2);
        //Reset PC
        spRegs[0][1].setText(toHex(loc,2));

        int opcode = toInt(byte1.substring(0,1));
        if (opcode == 1) //Load
        {
            int reg = toInt(byte1.substring(1,2));
            int address = toInt(byte2);
            regs[reg][1].setText(mem[(address/16)+1][(address%16)+1].getText());
        }
        else if (opcode == 2) //Load Immediate
        {
            int reg = toInt(byte1.substring(1,2));
            regs[reg][1].setText(byte2);
        }
        else if (opcode == 3) //Store
        {
            int reg = toInt(byte1.substring(1,2));
            int address = toInt(byte2);
            mem[(address/16)+1][(address%16)+1].setText(regs[reg][1].getText());
        }
        else if (opcode == 4) //Reg move
        {
            regs[toInt(byte2.substring(1,2))][1].setText(regs[toInt(byte2.substring(0,1))][1].getText());
        }
        else if (opcode == 5) //Add 2's comp
        {
            int reg1 = toInt(byte1.substring(1,2));
            int reg2 = toInt(byte2.substring(0,1));
            int reg3 = toInt(byte2.substring(1,2));
            int val2 = toInt(regs[reg2][1].getText());
            int val3 = toInt(regs[reg3][1].getText());
            int sum = (val2 + val3) & 0xFF;
            regs[reg1][1].setText(toHex(sum,2));
        }
        else if (opcode == 6) //Add float
        {
            int reg1 = toInt(byte1.substring(1,2));
            int reg2 = toInt(byte2.substring(0,1));
            int reg3 = toInt(byte2.substring(1,2));
            int mant2 = (toInt(regs[reg2][1].getText())) % 16;
            int mant3 = (toInt(regs[reg3][1].getText())) % 16;
            int exp2 = ((toInt(regs[reg2][1].getText())) & 0x70) / 16;
            int exp3 = ((toInt(regs[reg3][1].getText())) & 0x70) / 16;
            int sign[] = new int[2];
            sign[0] = 1;
            sign[1] = -1;
            int sign1 = 0;
            int sign2 = (toInt(regs[reg2][1].getText())) / 128;
            int sign3 = (toInt(regs[reg3][1].getText())) / 128;
            mant2 = (mant2 << exp2) * sign[sign2];
            mant3 = (mant3 << exp3) * sign[sign3];
            int mant1 = mant2 + mant3;
            if (mant1 < 0)
            {
                sign1 = 0x8;
                mant1 = -mant1;
            }
            int exp1 = 0;
            while (mant1 > 15)
            {
                mant1 = mant1 / 2;
                exp1++;
            }
            regs[reg1][1].setText(toHex(((sign1 | exp1) * 16) + mant1, 2));
        }
        else if (opcode == 7) //Or
        {
            int reg1 = toInt(byte1.substring(1,2));
            int reg2 = toInt(byte2.substring(0,1));
            int reg3 = toInt(byte2.substring(1,2));
            int val2 = toInt(regs[reg2][1].getText());
            int val3 = toInt(regs[reg3][1].getText());
            int result = val2 | val3;
            regs[reg1][1].setText(toHex(result,2));
        }
        else if (opcode == 8) //And
        {
            int reg1 = toInt(byte1.substring(1,2));
            int reg2 = toInt(byte2.substring(0,1));
            int reg3 = toInt(byte2.substring(1,2));
            int val2 = toInt(regs[reg2][1].getText());
            int val3 = toInt(regs[reg3][1].getText());
            int result = val2 & val3;
            regs[reg1][1].setText(toHex(result,2));
        }
        else if (opcode == 9) //Xor
        {
            int reg1 = toInt(byte1.substring(1,2));
            int reg2 = toInt(byte2.substring(0,1));
            int reg3 = toInt(byte2.substring(1,2));
            int val2 = toInt(regs[reg2][1].getText());
            int val3 = toInt(regs[reg3][1].getText());
            int result = val2 ^ val3;
            regs[reg1][1].setText(toHex(result,2));
        }
        else if (opcode == 0xA) //Rotate
        {
            int reg = toInt(byte1.substring(1,2));
            int val = (toInt(byte2.substring(1,2))) % 8;
            int bits = toInt(regs[reg][1].getText());
            bits = bits << (8 - val);
            int hbits = (0xFF00 & bits) >> 8;
            int lbits = (0x00FF & bits);
            regs[reg][1].setText(toHex((hbits | lbits), 2));
        }
        else if (opcode == 0xB) //Branch
        {
            int reg = toInt(byte1.substring(1,2));
            String val1 = regs[0][1].getText();
            String val2 = regs[reg][1].getText();
            if (val1.equals(val2))
                spRegs[0][1].setText(byte2);
        }
        else if (opcode == 0xC) //Halt
        {
            running = false;
        }
        else if (opcode == 0xD) //Load indirect
        {
            int reg1 = toInt(byte1.substring(1,2));
            int reg2 = toInt(byte2.substring(1,2));
            int addr = toInt(regs[reg2][1].getText());
            regs[reg1][1].setText(mem[(addr / 16) +1][(addr % 16) + 1].getText());
        }
        else if (opcode == 0xE) //Store indirect
        {
            int reg1 = toInt(byte1.substring(1,2));
            int reg2 = toInt(byte2.substring(1,2));
            int addr = toInt(regs[reg2][1].getText());
            mem[(addr / 16) + 1][(addr % 16) + 1].setText(regs[reg1][1].getText());
        }
        else
        {
            System.out.println("Unexpected opcode="+opcode);
            running = false;
        }
    }

    void doRun()
    {
        // Use a separate Thread to run the program.  This way
        // we can stop an infinite loop (with the Halt button).
        Runner r = new Runner();
        r.start();
    }
}



