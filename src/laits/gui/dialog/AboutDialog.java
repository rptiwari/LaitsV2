package laits.gui.dialog;

import laits.common.EditorConstants;

public class AboutDialog extends javax.swing.JDialog {

  /**
   * Constructor
   *
   * @param parent
   * @param modal
   */
  public AboutDialog(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    this.setTitle("About LAITS...");
    initComponents();
    this.versionLabel.setText(EditorConstants.VERSION);
    this.setLocationRelativeTo(parent);
    this.pack();
  }

  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Panel = new javax.swing.JPanel();
        amtLabel = new javax.swing.JLabel();
        asuLogoLabel = new javax.swing.JLabel();
        author1Label = new javax.swing.JLabel();
        author2Label = new javax.swing.JLabel();
        versionLabel = new javax.swing.JLabel();
        copyRightLabel = new javax.swing.JLabel();
        amtLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About Us");
        setResizable(false);

        Panel.setBackground(new java.awt.Color(255, 255, 255));
        Panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PanelMouseClicked(evt);
            }
        });

        amtLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        amtLabel.setText("Learning by Authoring an");

        asuLogoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/amt/gui/asu.jpeg"))); // NOI18N
        asuLogoLabel.setToolTipText("ASU");

        author1Label.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        author1Label.setText("Dr. Kurt VanLehn");

        author2Label.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        author2Label.setText("Ramayan Tiwari");

        versionLabel.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        versionLabel.setText("Version:");

        copyRightLabel.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        copyRightLabel.setText("Copyright (c) 2011");

        amtLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        amtLabel2.setText("Intelligent Tutoring System (LAITS)");

        javax.swing.GroupLayout PanelLayout = new javax.swing.GroupLayout(Panel);
        Panel.setLayout(PanelLayout);
        PanelLayout.setHorizontalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelLayout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelLayout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(copyRightLabel))
                            .addComponent(author1Label)
                            .addComponent(author2Label))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(PanelLayout.createSequentialGroup()
                        .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(amtLabel)
                            .addComponent(amtLabel2)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelLayout.createSequentialGroup()
                                .addGap(57, 57, 57)
                                .addComponent(asuLogoLabel))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelLayout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(versionLabel)))
                        .addGap(0, 18, Short.MAX_VALUE)))
                .addContainerGap())
        );
        PanelLayout.setVerticalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(asuLogoLabel)
                .addGap(18, 18, 18)
                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelLayout.createSequentialGroup()
                        .addComponent(amtLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 55, 55))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelLayout.createSequentialGroup()
                        .addComponent(amtLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)))
                .addComponent(versionLabel)
                .addGap(7, 7, 7)
                .addComponent(copyRightLabel)
                .addGap(34, 34, 34)
                .addComponent(author1Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(author2Label)
                .addContainerGap(235, Short.MAX_VALUE))
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

  private void PanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelMouseClicked
    // TODO add your handling code here:
    this.dispose();
  }//GEN-LAST:event_PanelMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Panel;
    private javax.swing.JLabel amtLabel;
    private javax.swing.JLabel amtLabel2;
    private javax.swing.JLabel asuLogoLabel;
    private javax.swing.JLabel author1Label;
    private javax.swing.JLabel author2Label;
    private javax.swing.JLabel copyRightLabel;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables

}
