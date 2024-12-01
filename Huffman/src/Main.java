import java.io.*;
import java.util.*;

class HuffmanNode{
    char ch;
    int probabilityData;
    HuffmanNode left;
    HuffmanNode right;

    public HuffmanNode(char ch, int probabilityData, HuffmanNode left, HuffmanNode right){
        this.ch=ch;
        this.probabilityData=probabilityData;
        this.left=left;
        this.right=right;
    }
    @Override
    public String toString() {
        return "{char: " + ch + ", probability: " + probabilityData + "}";
    }
}

class Huffman{

    public static String[] compression(String input){
        Map<Character, Integer> map=new HashMap<>();

        for(int i=0;i<input.length();i++){
            char c=input.charAt(i);
            map.put(c, map.getOrDefault(c,0)+1);


        }
//        int n=6;
//        char[] charArray= { 'a', 'b', 'c', 'd', 'e', 'f' };
//        double[] probability={0.22 , 0.11 , 0.22 , 0.11 , 0.22 , 0.11};

        PriorityQueue<HuffmanNode> pq= new PriorityQueue<>(Comparator.comparingDouble(node ->node.probabilityData));

        int totalCharacters = input.length();
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            char char1= entry.getKey();
            int freq= entry.getValue();
            double probability1= (double)freq/totalCharacters;
            HuffmanNode huffmanNode=new HuffmanNode(char1, freq, null, null);

            pq.add(huffmanNode);
        }
//        for(int i=0;i<n;i++) {
//            HuffmanNode huffmanNode = new HuffmanNode(charArray[i], probability[i], null, null);
//            pq.add(huffmanNode);
//        }
//        System.out.println("PriorityQueue contents:");
//        for (HuffmanNode node : pq) {
//            while(!pq.isEmpty()){
//                System.out.println(pq.poll());
//            }
//        }

        HuffmanNode root=null;
        while(pq.size() > 1){
            HuffmanNode left=pq.poll();
            HuffmanNode right=pq.poll();

//            if(right==null){
//                newProbability= left.probabilityData;
//            }else{
//                newProbability= left.probabilityData+ right.probabilityData;
//            }
            int newProbability= left.probabilityData+ right.probabilityData;
            HuffmanNode huffmanNode=new HuffmanNode(' ',  newProbability, left,right);
            pq.add(huffmanNode);
        }

        if (!pq.isEmpty()) {
            root = pq.poll();
        }

        Map<Character, String> FinalcodeMap = new HashMap<>();
        generateHuffmanCodes(root, "", FinalcodeMap);

        System.out.println("Huffman generated codes:");
        FinalcodeMap.forEach((key, value) -> System.out.println(key + " : " + value));

        //write the table of huffman generated codes in a file (table) to be used in decompression
        String TableCodesfilePath = "E:\\Year 3\\Data Compression\\Huffman\\table.txt";
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(TableCodesfilePath))){
            for (Map.Entry<Character,String> entry : FinalcodeMap.entrySet()){
                writer.write(entry.getKey()+" : ");
                writer.write(entry.getValue());
                writer.newLine();
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }

        String encodedString = "";
        for(char c: input.toCharArray()){
            encodedString += FinalcodeMap.get(c);
        }
        System.out.println( "encodedString: " + encodedString);

        saveCompressedData(encodedString, "compressed.bin");
        String[] returnValues={encodedString, TableCodesfilePath};
        return returnValues;
    }

    public static void generateHuffmanCodes(HuffmanNode root, String s, Map<Character, String> FinalcodeMap) {
        if (root == null) {
            return;
        }
        if (root.left == null && root.right == null) {
            FinalcodeMap.put(root.ch, s);
            return;
        }
        generateHuffmanCodes(root.left, s + "0", FinalcodeMap);
        generateHuffmanCodes(root.right, s + "1", FinalcodeMap);
    }

    public static void saveCompressedData(String encodedString, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            int byteValue = 0;
            int bitCount = 0;
            for (char bit : encodedString.toCharArray()) {
                byteValue = (byteValue << 1) | (bit - '0');
                bitCount++;
                if (bitCount == 8) {
                    fos.write(byteValue);
                    byteValue = 0;//bnrga3o to original for next 8 bits
                    bitCount = 0;
                }
            }
            if (bitCount > 0) {
                byteValue <<= (8 - bitCount);
                fos.write(byteValue);
            }
            System.out.println("Compressed data is saved to binary file named: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String decompression(String encodedString, String tableFilePath) throws IOException {
         Map<Character, String> tablecodingmap=new HashMap<>();
        BufferedReader reader= new BufferedReader(new FileReader(tableFilePath));
        String tableCodes;
        while ((tableCodes=reader.readLine()) != null){
            if(tableCodes.charAt(0) !=' '){ //because if character is a space we don't want it to be trimmed
                tableCodes = tableCodes.trim();
            }

            if (!tableCodes.isEmpty()) {
                    String[] parts = tableCodes.split(" : ");
                    if (parts.length == 2) {
                        String key = parts[0];
                        char letter = key.charAt(0);
                        String generatedCode = parts[1].trim();
                        tablecodingmap.put(letter, generatedCode);
                    }
            }


        }
        reader.close();
//        tablecodingmap.forEach((key,value)-> System.out.println("key: "+key + " value: "+value));
        StringBuilder temporary= new StringBuilder();
        StringBuilder decodedString=new StringBuilder();

        for(char ch: encodedString.toCharArray()) {
            temporary.append(ch);
//            System.out.println("temporary " + temporary);
            if (tablecodingmap.containsValue(temporary.toString())) {
                for (Map.Entry<Character, String> entry : tablecodingmap.entrySet()) {
                    if (Objects.equals(entry.getValue(), temporary.toString())) {
                        decodedString.append(entry.getKey());
                        temporary.setLength(0);
                        break;
                    }
                }

            }

        }
        System.out.println("decodedString "+decodedString);

        String decompressedOutputPath= "E:\\Year 3\\Data Compression\\Huffman\\decompressedOutput.txt";
        String decompressedOutput=decodedString.toString();
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(decompressedOutputPath))){
            for (char c: decompressedOutput.toCharArray()){
                writer.write(c);

            }
            System.out.println("Decompressed output written to decompressedOutput file");
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
        return decodedString.toString();
    }



    public static void main(String[] args) throws IOException {
        String filePath ="E:\\Year 3\\Data Compression\\Huffman\\input.txt";
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return;
        }
        String input="";
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                input += scanner.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String[] returnOfCompression=compression(input);
        String output=decompression(returnOfCompression[0], returnOfCompression[1]);
        boolean flag=false;
        for(int i=0;i<input.length();i++){
            if(input.charAt(i)!=output.charAt(i)){
                System.out.println("input and output are not equal");
                flag=true;
                break;
            }
        }
        if(!flag){
            System.out.println("input and output are equal");
        }


//        String filePath2 = "E:\\Year 3\\Data Compression\\Huffman\\compressed.bin";
//
//        try (FileInputStream fis = new FileInputStream(filePath2)) {
//            int byteValue;
//            while ((byteValue = fis.read()) != -1) {
//                // Print byte value in binary format
//                System.out.print(String.format("%8s", Integer.toBinaryString(byteValue)).replace(' ', '0') + " ");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


}


//    public static void printHuffmanCodes(HuffmanNode root, String s){
//        if(root.left==null && root.right==null&& Character.isLetter(root.ch)){
//            System.out.println(root.ch + ":" + s);
//            return;
//        }
//        printHuffmanCodes(root.left, s+"1");
//        printHuffmanCodes(root.right, s+"0");
//    }



