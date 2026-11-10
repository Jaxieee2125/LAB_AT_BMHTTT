package week_01;

public class frm_Menu extends javax.swing.JFrame {

    public frm_Menu() {
        initComponents();
        setLocationRelativeTo(null);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btn_caesar = new javax.swing.JButton();
        btn_vigenere = new javax.swing.JButton();
        btn_railfence = new javax.swing.JButton();
        btn_playfair = new javax.swing.JButton();
        btn_transposition = new javax.swing.JButton();
        btn_autokey = new javax.swing.JButton();
        btn_tonghop = new javax.swing.JButton();
        btn_tancong = new javax.swing.JButton();
        btn_sql = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LAB 1 - Mã hóa đối xứng cổ điển");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel1.setText("LAB 1 - MÃ HÓA ĐỐI XỨNG CỔ ĐIỂN");

        jLabel2.setText("Chọn một bài để mở form tương ứng:");

        btn_caesar.setText("1.1 Caesar");
        btn_caesar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_caesarActionPerformed(evt);
            }
        });

        btn_vigenere.setText("1.2 Vigenère");
        btn_vigenere.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_vigenereActionPerformed(evt);
            }
        });

        btn_railfence.setText("1.3 Rail Fence");
        btn_railfence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_railfenceActionPerformed(evt);
            }
        });

        btn_playfair.setText("1.4 Playfair");
        btn_playfair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_playfairActionPerformed(evt);
            }
        });

        btn_transposition.setText("1.5 Transposition");
        btn_transposition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_transpositionActionPerformed(evt);
            }
        });

        btn_autokey.setText("1.6 Autokey");
        btn_autokey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_autokeyActionPerformed(evt);
            }
        });

        btn_tonghop.setText("Nâng cao 01 - Tổng hợp");
        btn_tonghop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tonghopActionPerformed(evt);
            }
        });

        btn_tancong.setText("Nâng cao 02 - Tấn công");
        btn_tancong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tancongActionPerformed(evt);
            }
        });

        btn_sql.setText("Kết nối SQL Server");
        btn_sql.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_sqlActionPerformed(evt);
            }
        });

        jLabel3.setText("Mỗi form mở ra là một cửa sổ riêng; đóng form con không làm tắt chương trình.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btn_caesar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_playfair, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_tonghop, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btn_vigenere, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_transposition, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_tancong, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btn_railfence, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_autokey, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_sql, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(25, 25, 25))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_caesar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_vigenere, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_railfence, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_playfair, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_transposition, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_autokey, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_tonghop, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_tancong, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_sql, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    private void btn_caesarActionPerformed(java.awt.event.ActionEvent evt) {                                           
        new frm_Caesar().setVisible(true);
    }                                          

    private void btn_vigenereActionPerformed(java.awt.event.ActionEvent evt) {                                             
        new frm_Vigenere().setVisible(true);
    }                                            

    private void btn_railfenceActionPerformed(java.awt.event.ActionEvent evt) {                                              
        new frm_RailFence().setVisible(true);
    }                                             

    private void btn_playfairActionPerformed(java.awt.event.ActionEvent evt) {                                             
        new frm_Playfair().setVisible(true);
    }                                            

    private void btn_transpositionActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        new frm_Transposition().setVisible(true);
    }                                                 

    private void btn_autokeyActionPerformed(java.awt.event.ActionEvent evt) {                                            
        new frm_Autokey().setVisible(true);
    }                                           

    private void btn_tonghopActionPerformed(java.awt.event.ActionEvent evt) {                                            
        new frm_TongHop().setVisible(true);
    }                                           

    private void btn_tancongActionPerformed(java.awt.event.ActionEvent evt) {                                            
        new frm_TanCong().setVisible(true);
    }                                           

    private void btn_sqlActionPerformed(java.awt.event.ActionEvent evt) {                                        
        new frm_KetNoiSQL().setVisible(true);
    }                                       

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        java.awt.EventQueue.invokeLater(() -> new frm_Menu().setVisible(true));
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton btn_autokey;
    private javax.swing.JButton btn_caesar;
    private javax.swing.JButton btn_playfair;
    private javax.swing.JButton btn_railfence;
    private javax.swing.JButton btn_sql;
    private javax.swing.JButton btn_tancong;
    private javax.swing.JButton btn_tonghop;
    private javax.swing.JButton btn_transposition;
    private javax.swing.JButton btn_vigenere;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration                   
}
