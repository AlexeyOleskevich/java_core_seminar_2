import java.util.Random;
import java.util.Scanner;

public class Program {
    private static char[][] field;

    private static int fieldSizeX;
    private static int fieldSizeY;

    private static final char DOT_HUMAN = 'X';
    private static final char DOT_AI = '0';
    private static final char DOT_EMPTY = '*';

    private static final int WIN_COUNT = 4;

    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();

    public static void main(String[] args) {
        while (true) {
            initializeField();
            printField();
            while (true) {
                humanTurn();
                printField();
                if (checkGameState(DOT_HUMAN, "Вы победили")) {
                    break;
                }
                aiTurn();
                printField();
                if (checkGameState(DOT_AI, "Победил компьютер")) {
                    break;
                }
            }
            System.out.print("Желаете сыграть еще раз? (Y - да): ");
            if (!scanner.next().equalsIgnoreCase("Y")) {
                break;
            }
        }

    }

    private static void initializeField() {
        fieldSizeX = 5;
        fieldSizeY = 5;
        field = new char[fieldSizeX][fieldSizeY];

        for (int x = 0; x < fieldSizeX; x++) {
            for (int y = 0; y < fieldSizeY; y++) {
                field[x][y] = DOT_EMPTY;
            }
        }
    }

    private static void printField() {
        System.out.print("+");
        for (int i = 0; i < fieldSizeX; i++) {
            System.out.print("-" + (i + 1));
        }
        System.out.println("-");
        for (int x = 0; x < fieldSizeX; x++) {
            System.out.print(x + 1 + "|");
            for (int y = 0; y < fieldSizeY; y++) {
                System.out.print(field[x][y] + "|");
            }
            System.out.println();
        }

        for (int i = 0; i < fieldSizeX * 2 + 2; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

    static void humanTurn() {
        int x;
        int y;

        do {
            System.out.printf("Введите координаты хода X и Y (от %d до %d) через пробел: \n", 1, fieldSizeY);
            x = scanner.nextInt() - 1;
            y = scanner.nextInt() - 1;
        } while (!isCellValid(x, y) || !isCellEmpty(x, y));

        field[x][y] = DOT_HUMAN;
    }

    static void aiTurn() {
        int[] optimalPoint = getOptimalPointForAiTurn();
        int x = optimalPoint[0];
        int y = optimalPoint[1];

        field[x][y] = DOT_AI;
    }

    private static int[] getOptimalPointForAiTurn() {
        int maxAllowableHumanConnectedDots = WIN_COUNT - 1;

        // ищем оптимальный ход для бота
        for (int i = 0; i < fieldSizeX; i++) {
            for (int j = 0; j < fieldSizeY; j++) {
                if (isCellEmpty(i, j)) {
                    if (checkWin(DOT_HUMAN, maxAllowableHumanConnectedDots)) {
                        field[i][j] = DOT_HUMAN;
                        if (checkWin(DOT_HUMAN, WIN_COUNT)) {
                            field[i][j] = DOT_EMPTY;
                            return new int[]{i, j};
                        }
                        field[i][j] = DOT_EMPTY;
                        continue;
                    }

                    field[i][j] = DOT_HUMAN;
                    if (checkWin(DOT_HUMAN, maxAllowableHumanConnectedDots) || checkWin(DOT_HUMAN, WIN_COUNT)) {
                        field[i][j] = DOT_EMPTY;
                        return new int[]{i, j};
                    }
                    field[i][j] = DOT_EMPTY;
                }
            }
        }

        /*
        в случае, если не было найдено позиции на поле, которую необходимо заблокировать (чтобы человек не победил),
        генерируем случайных ход компьютера
         */
        int[] point = getRandomPoint();
        int x = point[0];
        int y = point[1];
        return new int[]{x, y};
    }

    private static int[] getRandomPoint() {
        int x = random.nextInt(fieldSizeX);
        int y = random.nextInt(fieldSizeY);
        while (!isCellEmpty(x, y)) {
            x = random.nextInt(fieldSizeX);
            y = random.nextInt(fieldSizeY);
        }
        return new int[]{x, y};
    }

    private static boolean isCellEmpty(int x, int y) {
        return field[x][y] == DOT_EMPTY;
    }

    private static boolean isCellValid(int x, int y) {
        return x >= 0 && x < fieldSizeX && y >= 0 && y < fieldSizeY;
    }

    private static boolean checkGameState(char dot, String s) {
        if (checkWin(dot, WIN_COUNT)) {
            System.out.println(s);
            return true;
        }
        if (checkDraw()) {
            System.out.println("Ничья!");
            return true;
        }
        return false;
    }

    private static boolean checkDraw() {
        for (int x = 0; x < fieldSizeX; x++) {
            for (int y = 0; y < fieldSizeY; y++) {
                if (isCellEmpty(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkWin(char dot, int winCount) {
        for (int i = 0; i < fieldSizeX; i++) {
            for (int j = 0; j < fieldSizeY; j++) {
                if (isHorizontalWin(i, j, dot, winCount) || isVerticalWin(i, j, dot, winCount)
                        || isUpDiagonalWin(i, j, dot, winCount) || isDownDiagonalWin(i, j, dot, winCount)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isHorizontalWin(int x, int y, char dot, int winCount) {
        int connectedDots = 0;

        for (int i = y; i < fieldSizeY; i++) {
            if (field[x][i] != dot) {
                return false;
            } else {
                connectedDots++;
                if (connectedDots == winCount) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isVerticalWin(int x, int y, char dot, int winCount) {
        int connectedDots = 0;

        for (int i = x; i < fieldSizeX; i++) {
            if (field[i][y] != dot) {
                return false;
            } else {
                connectedDots++;
                if (connectedDots == winCount) {
                    return true;
                }
            }

        }
        return false;
    }

    private static boolean isDownDiagonalWin(int x, int y, char dot, int winCount) {
        int connectedDots = 0;

        for (int i = x, j = y; i < fieldSizeX && j < fieldSizeY; i++, j++) {
            if (field[i][j] != dot) {
                return false;
            } else {
                connectedDots++;
                if (connectedDots == winCount) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isUpDiagonalWin(int x, int y, char dot, int winCount) {
        int connectedDots = 0;

        for (int i = x, j = y; i >= 0 && j < fieldSizeY; i--, j++) {
            if (field[i][j] != dot) {
                return false;
            } else {
                connectedDots++;
                if (connectedDots == winCount) {
                    return true;
                }
            }
        }
        return false;
    }

}
