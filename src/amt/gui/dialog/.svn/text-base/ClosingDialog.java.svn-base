package amt.gui.dialog;

import amt.comm.CommException;
import amt.log.Logger;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Dialog box to tell user the program is closing
 *
 * @author Megan Kearl
 * @version 2010329 
 */
public class ClosingDialog extends JDialog {
  Logger logs = Logger.getLogger();
  Frame p;
  int count = 0;
  private static int failedAttempts = 0;

  /**
   * Creates new form ClosingDialog Form
   *
   * @param parent
   * @param modal
   */
  public ClosingDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setTitle("");
        initComponents();
        javax.swing.Timer timer = new javax.swing.Timer(1000, new UpdateActionListener());
        timer.setInitialDelay(0);
        timer.start();

        this.setLocationRelativeTo(parent);
        this.remove(jButton1);
        jButton1.setVisible(false);
        this.requestFocus(true);
        this.getContentPane();
        this.pack();
        this.setVisible(true);
        close(p);
    }

  public void close(java.awt.Frame p) {
    this.update(this.getGraphics());
    try {
    logs.close(p, this);
    } catch (CommException de) {
      System.out.println("logs do not close properly");
      System.exit(-1);
    }

  }

    public void updateCount(int c)
    {
        count = c;
        progressBar.setValue(count);
        this.update(this.getGraphics());
    }

    private class UpdateActionListener implements ActionListener
    {
    @Override
    public void actionPerformed(ActionEvent e) {
      boolean update = false;
      update = updateProgressBar();
      if (update) {
        System.exit(0);
      }
    }

    private boolean updateProgressBar() {
      boolean flag;
      if (count < 100) {
        progressBar.setValue(count);
        flag = false;
      } else{
        progressBar.setValue(count = 0);
        flag = true;
      }
      if (count == 0) {
        ClosingDialog.AddFailedAttempt();
      }
      return flag;
    }
  }

  public static int getFailedAttempts() {
    return failedAttempts;
  }

  public static void AddFailedAttempt() {
    failedAttempts++;
    if (failedAttempts >= 10) {
      System.exit(0);
    }
    else {
    System.out.println("Wrong Exit, FailedAttempts = " + failedAttempts);
    }
    
  }
    
  

  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Panel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        Panel.setBackground(new java.awt.Color(255, 255, 255));

        titleLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Please wait while your information is sending...");
        titleLabel.setDoubleBuffered(true);

        jButton1.setText("Send");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelLayout = new javax.swing.GroupLayout(Panel);
        Panel.setLayout(PanelLayout);
        PanelLayout.setHorizontalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(titleLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelLayout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(154, 154, 154))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelLayout.createSequentialGroup()
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(119, 119, 119))))
        );
        PanelLayout.setVerticalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(titleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

   private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
     this.update(this.getGraphics());
     try {
     logs.close(p, this);
     } catch (CommException de) {
       // do something
     }
   }//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Panel;
    private javax.swing.JButton jButton1;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
}
