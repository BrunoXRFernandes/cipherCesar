/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sadproject1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author bruno
 */
public class BreakingEnigma {

    static HashMap<Character, Character> plugBoard = new HashMap<Character, Character>();
    static HashMap<Character, Integer> alphabet = new HashMap<Character, Integer>();
    static char[] salt = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M'};
    static String hash = "";
    static String file = "";
    static String plug = "";

    //variaveis finais
    static String palavra = "";
    static String rotorGerado = "";
    static String saltGerado = "";
    static String plugboard = "";
    static int numRotor = 0;
    static int numIncremento = 0;

    /**
     * Main do programa onde recebemos os inputs.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 3) {
            hash = verifyHash(args[0]);
            plug = verifyPlug(args[1]);
            file = verifyFile(args[2]);
            readFile();
        } else {
            System.out.println("Insira 3 valores");
            System.exit(0);
        }
    }

    /**
     * Verificação da hash inserida. Se esta contém apenas numeros e 128
     * caracteres.
     *
     * @param s
     * @return
     */
    public static String verifyHash(String s) {
        Pattern p = Pattern.compile("(([A-Z].*[0-9])|([0-9].*[A-Z]))");
        Matcher m = p.matcher(s);
        boolean b = m.find();
        try {
            if (b = true && s.length() == 128) {
                return s;
            } else {
                System.out.println("Verifique a hash inserida");
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println("Verifique a hash inserida");
            System.out.println(e);
            System.exit(0);
        }
        return null;
    }

    /**
     * Tratamento do plugboard inserido
     *
     * @param p
     * @return
     */
    public static String verifyPlug(String p) {
        try {
            String[] arrStr = p.split(",");
            for (String a : arrStr) {
                String splitKeyValue[] = a.split(":");
                String key = splitKeyValue[0];
                String value = splitKeyValue[1];
                String apostrofeKey = key.replace("'", "");
                String apostrofeValue = value.replace("'", "");
                String bracketKey = apostrofeKey.replace("{", "");
                String bracketValue = apostrofeValue.replace("}", "");
                for (int j = 0; j < bracketKey.length(); j++) {
                    for (int k = 0; k < bracketValue.length(); k++) {
                        plugBoard.put(bracketKey.charAt(j), bracketValue.charAt(k));
                    }
                }
            }
            return p;
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Verifique o plugboard");
            System.exit(0);
        }
        return null;
    }

    /**
     * Tratamento do ficheiro inserido. Se este existe e se não é pasta.
     *
     * @param f
     * @return
     */
    public static String verifyFile(String f) {
        File file = new File(f);
        try {
            if (file.exists() && !file.isDirectory()) {
                return f;
            }
        } catch (Exception e) {
            System.out.println("Não existe ficheiro");
            System.out.println(e);
            System.exit(0);
        }
        return null;
    }

    /**
     * Leitura do ficheiro inserido. Palavra enviada por parametro para fazer o
     * salt
     *
     * @return
     * @throws IOException
     */
    public static boolean readFile() throws IOException {
        try {
            BufferedReader r = new BufferedReader(new FileReader(file));
            String line;
            while ((line = r.readLine()) != null) {
                palavra = line;
                if (saltCompleteWord(palavra)) {
                    apresentationWord();
                    return true;
                }
            }
            r.close();
        } catch (Exception e) {
            System.out.println("Verifique o caminho do ficheiro");
            System.exit(0);
        }
        return false;
    }

    /**
     * Gerar o salto do alfabeto(sufixo e prefixo)
     *
     * @param word
     * @return
     * @throws IOException
     */
    public static boolean saltCompleteWord(String word) throws IOException {
        for (char first : salt) {
            for (char second : salt) {
                String suffix = String.valueOf(first) + String.valueOf(second) + word;
                if (plugboardSubs(suffix, false)) {
                    saltGerado = suffix;
                    return true;
                }
                String prefix = word + String.valueOf(first) + String.valueOf(second);
                if (plugboardSubs(prefix, false)) {
                    saltGerado = prefix;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Inserir a palavra que veio por parametro no hashmap e fazer a troca da
     * chave valor. Existe uma flag para sabermos se devemos executar o rotor ou
     * se já devemos ir diretos para a encriptação
     *
     * @param word
     * @param flag
     * @return
     * @throws IOException
     */
    public static boolean plugboardSubs(String word, boolean flag) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (char letra : word.toCharArray()) {
            if (plugBoard.containsKey(letra)) {
                sb.append(plugBoard.get(letra));
            } else {
                sb.append(letra);
            }
        }
        if (flag) {
            if (encryptCompare(String.valueOf(sb))) {
                plugboard = String.valueOf(sb);
                return true;
            }
        } else {
            if (rotor(String.valueOf(sb))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Rotor onde recebe a palavra do plugboard e calcula o incremento e o valor
     * da palavra final. Troca do valor pela chave num alfabeto previamente
     * criado
     *
     * @param word
     * @return
     * @throws IOException
     */
    public static boolean rotor(String word) throws IOException {
        hashmapAlpha();
        for (int r = 0; r < 26; r++) {
            for (int f = 0; f < 26; f++) {
                StringBuilder sb = new StringBuilder(); // criar sb nova a cada encremento F
                for (int l = 0; l < word.length(); l++) { //palavra, array char[] na pos 0
                    int inc = l * f;
                    char letra = word.charAt(l);
                    int posLetra = letra + r + inc; //hashmap  0
                    int posicion = posLetra % 26; //nunca retorna acima de 26 
                    for (Entry<Character, Integer> entry : alphabet.entrySet()) {
                        if (entry.getValue() == posicion) {
                            sb.append(entry.getKey());
                        }
                    }
                }
                if (plugboardSubs(String.valueOf(sb), true)) {
                    rotorGerado = String.valueOf(sb);
                    numRotor = r;
                    numIncremento = f;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * HashMap do alfabeto completo
     */
    public static void hashmapAlpha() {
        alphabet.put('A', 0);
        alphabet.put('B', 1);
        alphabet.put('C', 2);
        alphabet.put('D', 3);
        alphabet.put('E', 4);
        alphabet.put('F', 5);
        alphabet.put('G', 6);
        alphabet.put('H', 7);
        alphabet.put('I', 8);
        alphabet.put('J', 9);
        alphabet.put('K', 10);
        alphabet.put('L', 11);
        alphabet.put('M', 12);
        alphabet.put('N', 13);
        alphabet.put('O', 14);
        alphabet.put('P', 15);
        alphabet.put('Q', 16);
        alphabet.put('R', 17);
        alphabet.put('S', 18);
        alphabet.put('T', 19);
        alphabet.put('U', 20);
        alphabet.put('V', 21);
        alphabet.put('W', 22);
        alphabet.put('X', 23);
        alphabet.put('Y', 24);
        alphabet.put('Z', 25);
    }

    /**
     * Encriptação da palavra recebida do plugboard. Comparação do hash recebido
     * no input com o hash gerado neste método
     *
     * @param word
     * @return
     * @throws IOException
     */
    public static boolean encryptCompare(String word) throws IOException {
        String testEncrypt = hash; //"7d34099a6cd09e8692355bcfa922cb3418e83e1979ff5098ad0f6d6c1ab31d6b64587423b91d4b62e33e2b1b4ba6b32dc445eb4d0c60b503a1e414e84aad912d";
        String hashWord = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(word.getBytes("utf8"));
            hashWord = String.format("%0128x", new BigInteger(1, digest.digest()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (hashWord.equals(testEncrypt)) {

            return true;
        }
        return false;
    }

    /**
     * Apresentação das informações da palavra
     */
    public static void apresentationWord() {
        System.out.println("A PALAVRA FOI ENCONTRADA!");
        System.out.println("-------" + palavra + "---------");
        System.out.println("Rotor ->" + rotorGerado
                + "\nNumero rotor -> " + numRotor);
        System.out.println("Plugboard ->" + plugboard);
        System.out.println("Incremento ->" + numIncremento);
        System.out.println("Salt ->" + saltGerado);
    }
}
