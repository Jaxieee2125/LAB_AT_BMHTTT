package week_01;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CipherAlgorithmsTest {

    @Test
    @DisplayName("1.1 Caesar Cipher: HELLO with key 3 -> KHOOR")
    void testCaesar() {
        String plain = "HELLO";
        int key = 3;
        String cipher = CaesarCipher.encrypt(plain, key);
        assertEquals("KHOOR", cipher);
        assertEquals(plain, CaesarCipher.decrypt(cipher, key));

        // Preserves non-alphabetic characters and casing
        assertEquals("Khoor, Zruog! 123", CaesarCipher.encrypt("Hello, World! 123", 3));
        assertEquals("Hello, World! 123", CaesarCipher.decrypt("Khoor, Zruog! 123", 3));
    }

    @Test
    @DisplayName("1.2 Vigenere Cipher: HELLO with key KEY -> RIJVS")
    void testVigenere() {
        String plain = "HELLO";
        String key = "KEY";
        String cipher = VigenereCipher.encrypt(plain, key);
        assertEquals("RIJVS", cipher);
        assertEquals(plain, VigenereCipher.decrypt(cipher, key));

        // Mixed case and spaces (spaces do not advance key)
        assertEquals("Rijvs Uyvjn!", VigenereCipher.encrypt("Hello World!", "KEY"));
        assertEquals("Hello World!", VigenereCipher.decrypt("Rijvs Uyvjn!", "KEY"));

        // Invalid key
        assertThrows(IllegalArgumentException.class, () -> VigenereCipher.encrypt("HELLO", "K3Y"));
    }

    @Test
    @DisplayName("1.3 Rail Fence Cipher: HELLO WORLD with key 3 -> HOREL OLLWD")
    void testRailFence() {
        String plain = "HELLO WORLD";
        int rails = 3;
        String cipher = RailFenceCipher.encrypt(plain, rails);
        assertEquals("HOREL OLLWD", cipher);
        assertEquals(plain, RailFenceCipher.decrypt(cipher, rails));

        // Invalid rail count
        assertThrows(IllegalArgumentException.class, () -> RailFenceCipher.encrypt("HELLO", 1));
        assertThrows(IllegalArgumentException.class, () -> RailFenceCipher.encrypt("HELLO", 1001));
    }

    @Test
    @DisplayName("1.4 Playfair Cipher: HELLOWORLD with key KEYWORD -> GYIZSCOKCFBU")
    void testPlayfair() {
        String plain = "HELLOWORLD";
        String key = "KEYWORD";
        String cipher = PlayfairCipher.encrypt(plain, key);
        assertEquals("GYIZSCOKCFBU", cipher);
        // Decrypted has padding 'X'
        assertEquals("HELXLOWORLDX", PlayfairCipher.decrypt(cipher, key));

        // Key matrix generation
        String matrix = PlayfairCipher.getKeyMatrix(key);
        assertNotNull(matrix);
        assertTrue(matrix.contains("K E Y W O"));
    }

    @Test
    @DisplayName("1.5 Transposition Cipher: BAOMAT with key 2,3,1 -> AAOTBM")
    void testTransposition() {
        String plain = "BAOMAT";
        int[] key = TranspositionCipher.parseKey("2,3,1");
        assertArrayEquals(new int[]{2, 3, 1}, key);

        String cipher = TranspositionCipher.encrypt(plain, key);
        assertEquals("AAOTBM", cipher);
        assertEquals(plain, TranspositionCipher.decrypt(cipher, key));

        // Invalid keys
        assertThrows(IllegalArgumentException.class, () -> TranspositionCipher.parseKey("1,1,2"));
        assertThrows(IllegalArgumentException.class, () -> TranspositionCipher.parseKey("1,4"));
    }

    @Test
    @DisplayName("1.6 Autokey Cipher: HELLO with key KEY -> RIJSS")
    void testAutokey() {
        String plain = "HELLO";
        String key = "KEY";
        String cipher = AutokeyCipher.encrypt(plain, key);
        assertEquals("RIJSS", cipher);
        assertEquals(plain, AutokeyCipher.decrypt(cipher, key));

        // Invalid key
        assertThrows(IllegalArgumentException.class, () -> AutokeyCipher.encrypt("HELLO", "123"));
    }

    @Test
    @DisplayName("3.1 CipherTongHop: Dispatches all 6 ciphers correctly")
    void testCipherTongHop() {
        assertEquals("KHOOR", CipherTongHop.encrypt("Caesar", "HELLO", "3"));
        assertEquals("HELLO", CipherTongHop.decrypt("Caesar", "KHOOR", "3"));

        assertEquals("RIJVS", CipherTongHop.encrypt("Vigenere", "HELLO", "KEY"));
        assertEquals("HELLO", CipherTongHop.decrypt("Vigenere", "RIJVS", "KEY"));

        assertEquals("HOREL OLLWD", CipherTongHop.encrypt("Rail Fence", "HELLO WORLD", "3"));
        assertEquals("HELLO WORLD", CipherTongHop.decrypt("Rail Fence", "HOREL OLLWD", "3"));

        assertEquals("GYIZSCOKCFBU", CipherTongHop.encrypt("Playfair", "HELLOWORLD", "KEYWORD"));
        assertEquals("HELXLOWORLDX", CipherTongHop.decrypt("Playfair", "GYIZSCOKCFBU", "KEYWORD"));

        assertEquals("AAOTBM", CipherTongHop.encrypt("Transposition", "BAOMAT", "2,3,1"));
        assertEquals("BAOMAT", CipherTongHop.decrypt("Transposition", "AAOTBM", "2,3,1"));

        assertEquals("RIJSS", CipherTongHop.encrypt("Autokey", "HELLO", "KEY"));
        assertEquals("HELLO", CipherTongHop.decrypt("Autokey", "RIJSS", "KEY"));

        // Sample keys
        assertEquals("3", CipherTongHop.sampleKey("Caesar"));
        assertEquals("3", CipherTongHop.sampleKey("Rail Fence"));
        assertEquals("KEYWORD", CipherTongHop.sampleKey("Playfair"));
        assertEquals("2,3,1", CipherTongHop.sampleKey("Transposition"));
        assertEquals("KEY", CipherTongHop.sampleKey("Vigenere"));
        assertEquals("KEY", CipherTongHop.sampleKey("Autokey"));
    }

    @Test
    @DisplayName("4.1 Attack Demos: Caesar Brute Force, Frequency, Known Plaintext")
    void testAttackDemos() {
        // Caesar Brute Force
        String bruteForce = AttackDemos.caesarBruteForce("KHOOR");
        assertTrue(bruteForce.contains("3 | HELLO <== có nghĩa nhất"));
        assertTrue(bruteForce.contains("Kết luận: khóa k = 3, bản rõ = HELLO"));

        // Frequency Analysis
        String freq = AttackDemos.frequency("PHHW PH DIWHU WKH WRJD SDUWB");
        assertTrue(freq.contains("H |  5 |  21.7 %"));
        assertTrue(freq.contains("W |  5 |  21.7 %"));
        assertTrue(freq.contains("Tổng số chữ: 23"));

        // Known Plaintext (Vigenere)
        String known = AttackDemos.knownPlaintext("HELLO", "RIJVS");
        assertEquals("KEYKE", known);
    }

    @Test
    @DisplayName("Database fallback when SQL server is offline")
    void testDbOffline() {
        DbConfig.clear();
        assertFalse(DbConfig.isReady());
        String msg = HistoryDao.tryInsert("Caesar", "ENCRYPT", "HELLO", "KHOOR");
        assertEquals("Chưa kết nối SQL Server nên chưa lưu lịch sử.", msg);
        assertNotNull(HistoryDao.emptyModel());
    }
}
