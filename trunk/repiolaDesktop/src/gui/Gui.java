/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Gui.java
 *
 * Created on 17/05/2009, 06:27:07
 */

package gui;

import repiola.Interpreter;
import repiola.Machine;

/**
 *
 * @author mariano
 */
public class Gui extends javax.swing.JFrame {
    private SwingCanvas swingCanvas;
    private Machine machine;

    /** Creates new form Gui */
    public Gui() {
        initComponents();
        //canvas.setBackground(java.awt.Color.GREEN);
        swingCanvas = new SwingCanvas();
        machine = new Machine(swingCanvas);
        swingCanvas.setSize(200, 200);
        this.add(swingCanvas);
        swingCanvas.setBounds(285, 15, 200, 200);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollText = new javax.swing.JScrollPane();
        code = new javax.swing.JTextArea();
        run = new javax.swing.JButton();
        scrollOutput = new javax.swing.JScrollPane();
        output = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("repiola!");
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        code.setColumns(20);
        code.setRows(5);
        code.setText(": loop\n\nset r3 r0\nmul r3 r3\n\nset r4 r1\nmul r4 r4\n\nset r2 r3\nadd r2 r4\n\nge r0 199 ay\nadd r0 1\nput r2\njmp loop\n\n: ay\nge r1 199 end\nset r0 0\nadd r1 1\njmp loop\n\n: end\n");
        scrollText.setViewportView(code);

        run.setText("run");
        run.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runActionPerformed(evt);
            }
        });

        output.setColumns(20);
        output.setRows(5);
        scrollOutput.setViewportView(output);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollText, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scrollOutput, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                    .addComponent(run, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollText, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(run, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained


    }//GEN-LAST:event_formFocusGained

    private void runActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runActionPerformed
        Interpreter interpreter;
        String line = null;
        int opcode;
        clearOutput();
        swingCanvas.clear();

        try
        {
            interpreter = new Interpreter(code.getText(), machine);
            line = interpreter.step();
        }
        catch(Exception ex)
        {
            appendOutput("\nError0 line: " + line + "\n\t" + ex.getMessage());
            return;
        }

        machine.clear();

        while(line != null)
        {
            opcode = interpreter.getCurrentOpcode();
            
            try
            {
                if(opcode == -1) {
                    opcode = interpreter.parseLine(line);
                    interpreter.setCurrentOpcode(opcode);
                }
            }
            catch(Exception ex)
            {
                appendOutput("\nError1 line: " + line + "\n\t" + ex.getMessage());
                break;
            }

            try
            {
                machine.execute(opcode);
            }
            catch(Exception ex)
            {
                appendOutput("\nError executing code: " + line + "\n\t" + ex.getMessage());
                break;
            }
            
            try
            {
                line = interpreter.step();
            }
            catch(Exception ex)
            {
                appendOutput("\nError2 line: " + line + "\n\t" + ex.getMessage());
                break;
            }
        }
}//GEN-LAST:event_runActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Gui().setVisible(true);
            }
        });
    }

    private void appendOutput(String str)
    {
        output.append(str);
    }

    private void clearOutput()
    {
        output.setText("");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea code;
    private javax.swing.JTextArea output;
    private javax.swing.JButton run;
    private javax.swing.JScrollPane scrollOutput;
    private javax.swing.JScrollPane scrollText;
    // End of variables declaration//GEN-END:variables

}
