import java.io.*;
import java.util.*;

public class ResultBankManagementSystem {
    private static final String FILE_NAME = "results.txt";
    private static final Scanner sc = new Scanner(System.in);
    private static final ArrayList<Student> students = new ArrayList<>();

    static class Student {
        int rollNumber;
        String name;
        int marks;

        Student(int rollNumber, String name, int marks) {
            this.rollNumber = rollNumber;
            this.name = name;
            this.marks = marks;
        }

        double percentage() {
            return marks; // One subject out of 100 in this beginner version
        }

        String status() {
            return marks >= 40 ? "PASS" : "FAIL";
        }

        public String toString() {
            return rollNumber + " | " + name + " | Marks: " + marks
                    + " | Percentage: " + String.format(Locale.US, "%.2f", percentage())
                    + "% | " + status();
        }
    }

    public static void main(String[] args) {
        loadFromFile();
        sortByRollNumber();

        while (true) {
            System.out.println("\\n===== RESULT BANK MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Student Result");
            System.out.println("2. Display All Results");
            System.out.println("3. Search by Roll Number (Binary Search)");
            System.out.println("4. Search by Name (Linear Search)");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            int choice = readInt();
            switch (choice) {
                case 1: addStudent(); break;
                case 2: displayAll(); break;
                case 3: searchByRollNumber(); break;
                case 4: searchByName(); break;
                case 5:
                    saveToFile();
                    System.out.println("Records saved. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Enter 1 to 5.");
            }
        }
    }

    private static int readInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            sc.next();
        }
        int value = sc.nextInt();
        sc.nextLine();
        return value;
    }

    private static void addStudent() {
        System.out.print("Enter roll number: ");
        int roll = readInt();
        if (roll <= 0) {
            System.out.println("Roll number must be positive.");
            return;
        }
        if (binarySearchIndex(roll) >= 0) {
            System.out.println("This roll number already exists.");
            return;
        }

        System.out.print("Enter student name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty() || name.contains("|")) {
            System.out.println("Name cannot be blank or contain '|'.");
            return;
        }

        System.out.print("Enter marks (0-100): ");
        int marks = readInt();
        if (marks < 0 || marks > 100) {
            System.out.println("Marks must be between 0 and 100.");
            return;
        }

        students.add(new Student(roll, name, marks));
        sortByRollNumber();
        saveToFile();
        System.out.println("Student result added successfully.");
    }

    private static void sortByRollNumber() {
        students.sort(Comparator.comparingInt(s -> s.rollNumber));
    }

    private static void displayAll() {
        if (students.isEmpty()) {
            System.out.println("No student records available.");
            return;
        }
        System.out.println("\\n--- STUDENT RESULTS ---");
        for (Student s : students) System.out.println(s);
    }

    private static int binarySearchIndex(int roll) {
        int low = 0, high = students.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int current = students.get(mid).rollNumber;
            if (current == roll) return mid;
            if (current < roll) low = mid + 1;
            else high = mid - 1;
        }
        return -1;
    }

    private static void searchByRollNumber() {
        System.out.print("Enter roll number to search: ");
        int roll = readInt();
        int index = binarySearchIndex(roll);
        if (index >= 0) System.out.println("Found: " + students.get(index));
        else System.out.println("Student with roll number " + roll + " not found.");
    }

    private static void searchByName() {
        System.out.print("Enter name (or part of name): ");
        String query = sc.nextLine().trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty()) {
            System.out.println("Search name cannot be blank.");
            return;
        }

        boolean found = false;
        for (Student s : students) {
            if (s.name.toLowerCase(Locale.ROOT).contains(query)) {
                System.out.println(s);
                found = true;
            }
        }
        if (!found) System.out.println("No matching student found.");
    }

    private static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Student s : students) {
                writer.write(s.rollNumber + "|" + s.name + "|" + s.marks);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Could not save records: " + e.getMessage());
        }
    }

    private static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\\\|", -1);
                if (parts.length != 3) continue;
                try {
                    int roll = Integer.parseInt(parts[0]);
                    String name = parts[1].trim();
                    int marks = Integer.parseInt(parts[2]);
                    if (roll > 0 && !name.isEmpty() && marks >= 0 && marks <= 100
                            && !containsRoll(roll)) {
                        students.add(new Student(roll, name, marks));
                    }
                } catch (NumberFormatException ignored) {
                    // Skip a malformed line and continue loading.
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load records: " + e.getMessage());
        }
    }

    private static boolean containsRoll(int roll) {
        for (Student s : students) if (s.rollNumber == roll) return true;
        return false;
    }
}
