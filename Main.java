import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Main {

    private static class Result {
        public int indexN;
        public int indexM;
        public double similarity;

        public Result(int indexN, int indexM, double similarity) {
            this.indexN = indexN;
            this.indexM = indexM;
            this.similarity = similarity;
        }

        public static int compareSimilarity(Result r1, Result r2) {
           if (r1.similarity > r2.similarity)
               return 1;
           return -1;
        }

        @Override
        public String toString() {
            return "(" + indexN + "," + indexM + ")" + " - " + similarity;
        }
    }

    private static int n;
    private static int m;
    private static List<String> nList = new ArrayList<>();
    private static List<String> mList = new ArrayList<>();
    private static final String INPUT_FILE = "input.txt";
    private static final String OUTPUT_FILE = "output.txt";
    private static List<Result> resultList = new LinkedList<>(); // helper
    private static List<String> outputList = new LinkedList<>();

    public static int getLevenshteinDistance(String X, String Y) {
        int m = X.length();
        int n = Y.length();

        int[][] T = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            T[i][0] = i;
        }
        for (int j = 1; j <= n; j++) {
            T[0][j] = j;
        }

        int cost;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                cost = X.charAt(i - 1) == Y.charAt(j - 1) ? 0: 1;
                T[i][j] = Integer.min(Integer.min(T[i - 1][j] + 1, T[i][j - 1] + 1),
                        T[i - 1][j - 1] + cost);
            }
        }

        return T[m][n];
    }

    public static double findSimilarity(String x, String y) {
        if (x == null || y == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        double maxLength = Double.max(x.length(), y.length());
        if (maxLength > 0) {
            // Опционально игнорировать регистр, если это необходимо
            return (maxLength - getLevenshteinDistance(x, y)) / maxLength;
        }
        return 1.0;
    }

    public static void readInput() {
        try (FileReader fileReader = new FileReader(INPUT_FILE);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            // Считываем число n
            n = Integer.parseInt(bufferedReader.readLine());

            // Считываем первые n строк в массив
            for (int i = 0; i < n; i++) {
                nList.add(bufferedReader.readLine());
            }

            // Считываем число m
            m = Integer.parseInt(bufferedReader.readLine());

            // Считываем вторую часть файла - m следующих строк
            for (int i = 0; i < m; i++) {
                mList.add(bufferedReader.readLine());
            }

        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void addEntry() {
        if (nList.isEmpty() || mList.isEmpty()) return;
        // Для каждого элемента найдём численное значение сопоставления
        for (int i = 0; i < nList.size(); i++) {
            for (int j = 0; j < mList.size(); j++) {
                resultList.add(new Result(i, j, findSimilarity(nList.get(i), mList.get(j))));
            }
        }

        // найдём лучшее сопоставление
        Result max = resultList.stream().max(Result::compareSimilarity).get();

        // Теперь необходимо это лучшее сопоставление добавить в результирующий лист, а из вспомогательных листов удалить его
        outputList.add(nList.remove(max.indexN) + " : " + mList.remove(max.indexM));
        resultList.clear();
        addEntry();
    }

    public static void mapUnmapped() {
        if (!nList.isEmpty()) {
            for (int i = 0; i < nList.size(); i++) {
                outputList.add(nList.get(i) + " : ?");
            }
        }
        if (!mList.isEmpty()) {
            for (int i = 0; i < mList.size(); i++) {
                outputList.add("? : " + mList.get(i));
            }
        }
    }

    public static void writeOutput() {
        try (FileWriter fileWriter = new FileWriter(OUTPUT_FILE, false);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            for (String string : outputList) {
                bufferedWriter.write(string + "\n");
            }

        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Считаем input.txt
        readInput();
        // Рекурсивно будем добавлять по одному самому удачному
        addEntry();
        // После того как сопоставления найдены, нужно разобраться с элементами для которых сопоставления не нашлось
        mapUnmapped();
        // Записываем результат работы программы в выходной файл
        writeOutput();
    }
}