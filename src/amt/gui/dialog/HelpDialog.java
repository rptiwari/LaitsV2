package amt.gui.dialog;

/**
* Dialog box to send a Ticket
 *
 * @author javiergs y helenchavez
 * @version 20090817
 *
 */

public class HelpDialog extends javax.swing.JDialog {

  /** Creates new form AboutAMTDialog Form */
  public HelpDialog(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    this.setTitle("");
    initComponents();
    this.setLocationRelativeTo(parent);
    this.pack();
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Panel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        lineOneLabel = new javax.swing.JLabel();
        lineTwoLabel = new javax.swing.JLabel();
        lineThreeLabel = new javax.swing.JLabel();
        lineFourLabel = new javax.swing.JLabel();
        titleLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        Panel.setBackground(new java.awt.Color(255, 255, 255));

        titleLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        titleLabel.setText("Help Content");

        lineOneLabel.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        lineOneLabel.setText("Constants. Out: Auxiliaries. In: Nothing.      ");

        lineTwoLabel.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        lineTwoLabel.setText("Auxiliaries. Out: Flow or Auxiliaries. In: Constsants, Auxiliaies, Stock or Flow.     ");

        lineThreeLabel.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        lineThreeLabel.setText("Stocks. Out: Flow or Auxiliar. In: Stock or Flow.     ");

        lineFourLabel.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        lineFourLabel.setText("Flows. Out: Stocks or Auxiliary. In: Stock, Auxiliaries or Constants. ");

        titleLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        titleLabel1.setText("1. Connection Rules");

        javax.swing.GroupLayout PanelLayout = new javax.swing.GroupLayout(Panel);
        Panel.setLayout(PanelLayout);
        PanelLayout.setHorizontalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelLayout.createSequentialGroup()
                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(titleLabel)
                            .addComponent(titleLabel1)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lineTwoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lineThreeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                            .addComponent(lineFourLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                            .addComponent(lineOneLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE))))
                .addContainerGap())
        );
        PanelLayout.setVerticalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(titleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(titleLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lineOneLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lineTwoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lineThreeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lineFourLabel)
                .addContainerGap(18, Short.MAX_VALUE))
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Panel;
    private javax.swing.JLabel lineFourLabel;
    private javax.swing.JLabel lineOneLabel;
    private javax.swing.JLabel lineThreeLabel;
    private javax.swing.JLabel lineTwoLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel titleLabel1;
    // End of variables declaration//GEN-END:variables

}