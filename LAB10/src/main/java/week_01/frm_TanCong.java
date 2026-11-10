package week_01;

import javax.swing.JOptionPane;

public class frm_TanCong extends javax.swing.JFrame {

    public frm_TanCong() {
        initComponents();
        setLocationRelativeTo(null);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_cipher = new javax.swing.JTextArea();
        btn_bruteforce = new javax.swing.JButton();
        btn_frequency = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txt_known_plain = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txt_known_cipher = new javax.swing.JTextField();
        btn_known = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txt_result = new javax.swing.JTextArea();
        lbl_status = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Phân tích mật mã cổ điển");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("PHÂN TÍCH (TẤN CÔNG) MẬT MÃ CỔ ĐIỂN");

        jLabel2.setText("Ciphertext:");

        txt_cipher.setColumns(20);
        txt_cipher.setLineWrap(true);
        txt_cipher.setRows(2);
        jScrollPane1.setViewportView(txt_cipher);

        btn_bruteforce.setText("Brute Force Caesar");
        btn_bruteforce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_bruteforceActionPerformed(evt);
            }
        });

        btn_frequency.setText("Frequency Analysis");
        btn_frequency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_frequencyActionPerformed(evt);
            }
        });

        jLabel6.setText("Bản rõ đã biết:");

        txt_known_plain.setText("HELLO");

        jLabel8.setText("Bản mã:");

        txt_known_cipher.setText("RIJVS");

        btn_known.setText("Known Plaintext (Vigenere)");
        btn_known.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_knownActionPerformed(evt);
            }
        });

        jLabel11.setText("Kết quả:");

        txt_result.setEditable(false);
        txt_result.setColumns(20);
        txt_result.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txt_result.setRows(8);
        jScrollPane2.setViewportView(txt_result);

        lbl_status.setText("Sẵn sàng.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addComponent(jScrollPane2)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btn_bruteforce, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btn_frequency, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txt_known_plain, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(txt_known_cipher, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btn_known, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 20, Short.MAX_VALUE)))))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_bruteforce, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_frequency, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txt_known_plain, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txt_known_cipher, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_known, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(lbl_status, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>                        

    private void btn_bruteforceActionPerformed(java.awt.event.ActionEvent evt) {                                               
        String cipher = txt_cipher.getText().trim();
        if (cipher.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hãy nhập Ciphertext.");
            return;
        }
        txt_result.setText(AttackDemos.caesarBruteForce(cipher));
        txt_result.setCaretPosition(0);
        lbl_status.setText("Đã thử đủ 26 khóa Caesar.");
    }                                              

    private void btn_frequencyActionPerformed(java.awt.event.ActionEvent evt) {                                              
        String cipher = txt_cipher.getText().trim();
        if (cipher.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hãy nhập Ciphertext.");
            return;
        }
        txt_result.setText(AttackDemos.frequency(cipher));
        txt_result.setCaretPosition(0);
        lbl_status.setText("Đã thống kê tần suất chữ cái.");
    }                                             

    private void btn_knownActionPerformed(java.awt.event.ActionEvent evt) {                                          
        try {
            String key = AttackDemos.knownPlaintext(txt_known_plain.getText(), txt_known_cipher.getText());
            txt_result.setText("Khóa Vigenère suy ra từng vị trí: " + key
                    + "\nNếu khóa ngắn hơn bản rõ, khóa sẽ lặp lại (KEY KEY ...).");
            lbl_status.setText("Khóa suy ra: " + key);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dữ liệu không hợp lệ", JOptionPane.ERROR_MESSAGE);
        }
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

        java.awt.EventQueue.invokeLater(() -> new frm_TanCong().setVisible(true));
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton btn_bruteforce;
    private javax.swing.JButton btn_frequency;
    private javax.swing.JButton btn_known;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbl_status;
    private javax.swing.JTextArea txt_cipher;
    private javax.swing.JTextField txt_known_cipher;
    private javax.swing.JTextField txt_known_plain;
    private javax.swing.JTextArea txt_result;
    // End of variables declaration                   
}
