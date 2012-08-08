package laits.gui;

import laits.graph.Graph;
import laits.graph.GraphCanvas;
import javax.swing.JLabel;

/**
 * Dialog box About
 *
 * @author Javier Gonzalez Sanchez
 * @author Maria Elena Chavez Echeagaray
 * @version 20090817
 */
public class MessageDialog extends javax.swing.JDialog {
  Graph g;
  static GraphCanvas gc;

  boolean yesNo = false;
  /**
   * Constructor
   *
   * @param parent
   * @param modal
   */
  public MessageDialog(java.awt.Frame parent, boolean modal, String message, boolean yesNo, Graph g) {
    super(parent, modal);
    this.setTitle("");
    this.yesNo = yesNo;
    this.g = g;
    initComponents();
    if(yesNo)
    {
        yesButton.setVisible(true);
        noButton.setVisible(true);
        okButton.setVisible(false);
    }
    else
    {
        //jPanel1.remove(yesButton);
        yesButton.setVisible(false);
        noButton.setVisible(false);
        okButton.setVisible(true);
        //noButton.setText("OK");
    }
    //messageLabel.
    message = "<html><center>"+message+"</center></html>";
    messageLabel.setText(message);
    messageLabel.setVerticalTextPosition(JLabel.CENTER);
    messageLabel.setHorizontalTextPosition(JLabel.CENTER);
    this.setLocationRelativeTo(parent);
    this.requestFocus();
    this.pack();
  }

  public static void showYesNoDialog(java.awt.Frame parent, boolean modal, String message, Graph g)
  {
    for (int i = 0; i < GraphCanvas.getOpenTabs().size(); i++) {
      GraphCanvas.getOpenTabs().get(i).setAlwaysOnTop(false);
    }

    MessageDialog md = new MessageDialog(parent, modal, message, true, g);
    md.setAlwaysOnTop(true);
    md.toFront();
    md.setFocusable(true);
    md.requestFocus();
    md.setVisible(true);
  }

  public static void showMessageDialog(java.awt.Frame parent, boolean modal, String message, Graph g)
  {
    for (int i = 0; i < GraphCanvas.getOpenTabs().size(); i++) {
      GraphCanvas.getOpenTabs().get(i).setAlwaysOnTop(false);
    }

    MessageDialog md = new MessageDialog(parent, modal, message, false, g);
    md.setAlwaysOnTop(true);
    md.toFront();
    md.setFocusable(true);
    md.requestFocus();
    md.setVisible(true);
  }

  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel1 = new javax.swing.JLabel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        Panel = new javax.swing.JPanel();
        messageLabel = new javax.swing.JLabel();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        yesButton = new javax.swing.JButton();
        noButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        titleLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        titleLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel1.setText("Are you sure you would like to exit?");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        Panel.setBackground(new java.awt.Color(255, 255, 255));

        messageLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        messageLabel.setText("Are you sure you would like to exit?");

        yesButton.setText("Yes");
        yesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yesButtonActionPerformed(evt);
            }
        });
        yesButton.setBounds(10, 0, 75, 29);
        jLayeredPane2.add(yesButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        noButton.setText("No");
        noButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noButtonActionPerformed(evt);
            }
        });
        noButton.setBounds(80, -1, 75, 30);
        jLayeredPane2.add(noButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        okButton.setText("OK");
        okButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                okButtonMouseClicked(evt);
            }
        });
        okButton.setBounds(40, 0, 75, 29);
        jLayeredPane2.add(okButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout PanelLayout = new javax.swing.GroupLayout(Panel);
        Panel.setLayout(PanelLayout);
        PanelLayout.setHorizontalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(messageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelLayout.setVerticalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelLayout.createSequentialGroup()
                .addComponent(messageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void yesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yesButtonActionPerformed
    if(yesNo) {
        g.setN(0);
    } else {
      g.setN(-1);
    }

    for (int i = 0; i < GraphCanvas.getOpenTabs().size(); i++) {
      GraphCanvas.getOpenTabs().get(i).setAlwaysOnTop(true);
    }

    this.dispose();
  }//GEN-LAST:event_yesButtonActionPerformed

  private void noButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noButtonActionPerformed
    if(yesNo) {
        g.setN(1);
    } else {
      g.setN(-1);
    }

    for (int i = 0; i < GraphCanvas.getOpenTabs().size(); i++) {
      GraphCanvas.getOpenTabs().get(i).setAlwaysOnTop(true);
    }

    this.dispose();
  }//GEN-LAST:event_noButtonActionPerformed

  private void okButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_okButtonMouseClicked
    // TODO add your handling code here:
    for (int i = 0; i < GraphCanvas.getOpenTabs().size(); i++) {
      GraphCanvas.getOpenTabs().get(i).setAlwaysOnTop(true);
    }
    
    g.setN(-1);
    this.dispose();
  }//GEN-LAST:event_okButtonMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Panel;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JButton noButton;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel titleLabel1;
    private javax.swing.JButton yesButton;
    // End of variables declaration//GEN-END:variables

}
