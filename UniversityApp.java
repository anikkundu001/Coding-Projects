import java.util.*;

// ---------------------------- Student ----------------------------
class Student {
    private int sId;
    private String sName;
    private double sCGPA;
    private ArrayList<Course> courseList = new ArrayList<>();

    public int getsId() { return sId; }
    public String getsName() { return sName; }
    public double getsCGPA() { return sCGPA; }
    public void setsId(int sId) { this.sId = sId; }
    public void setsName(String sName) { this.sName = sName; }
    public void setsCGPA(double sCGPA) { this.sCGPA = sCGPA; }
    public List<Course> getCourseList() { return courseList; }

    Student() {}
    Student(int sId, String sName, double sCGPA) {
        this.sId = sId;
        this.sName = sName;
        this.sCGPA = sCGPA;
    }

    public void addCourse(Course course) {
        if (!courseList.contains(course)) courseList.add(course);
    }
    public void dropCourse(Course course) { courseList.remove(course); }
    public void display() {
        System.out.println("Student ID: " + sId + " | Name: " + sName + " | CGPA: " + sCGPA);
    }
    public void printCourseList() {
        System.out.println("Courses of " + sName + ":");
        for (Course c : courseList) System.out.println("- " + c.getCTitle());
    }
}

// ---------------------------- Course ----------------------------
class Course {
    private String cId;
    private String cTitle;
    private double credit;
    private ArrayList<Student> studentList = new ArrayList<>();
    private Faculty faculty;

    public String getCId() { return cId; }
    public String getCTitle() { return cTitle; }
    public double getCredit() { return credit; }
    public Faculty getFaculty() { return faculty; }
    public List<Student> getStudentList() { return studentList; }

    public void setCId(String cId) { this.cId = cId; }
    public void setCTitle(String cTitle) { this.cTitle = cTitle; }
    public void setCredit(double credit) { this.credit = credit; }
    public void assignFaculty(Faculty faculty) { this.faculty = faculty; }

    Course() {}
    Course(String cId, String cTitle, double credit) {
        this.cId = cId;
        this.cTitle = cTitle;
        this.credit = credit;
        this.faculty = null;
    }

    public void addStudent(Student s) { if (!studentList.contains(s)) studentList.add(s); }
    public void dropStudent(Student s) { studentList.remove(s); }
    public void display() {
        System.out.println("Course ID: " + cId + " | Title: " + cTitle + " | Credit: " + credit);
        if (faculty != null) System.out.println("  Faculty: " + faculty.getFacultyName());
        else System.out.println("  No faculty assigned.");
    }
    public void printStudentList() {
        System.out.println("Students enrolled in " + cTitle + ":");
        for (Student s : studentList) System.out.println("- " + s.getsName());
    }
}

// ---------------------------- Faculty ----------------------------
class Faculty {
    private int facultyId;
    private String facultyName;
    private String facultyPosition;

    public int getFacultyId() { return facultyId; }
    public String getFacultyName() { return facultyName; }
    public String getFacultyPosition() { return facultyPosition; }
    public void setFacultyId(int id) { this.facultyId = id; }
    public void setFacultyName(String name) { this.facultyName = name; }
    public void setFacultyPosition(String pos) { this.facultyPosition = pos; }

    Faculty() {}
    Faculty(int id, String name, String pos) {
        this.facultyId = id;
        this.facultyName = name;
        this.facultyPosition = pos;
    }
    public void display() {
        System.out.println("Faculty ID: " + facultyId + " | Name: " + facultyName + " | Position: " + facultyPosition);
    }
}

// ---------------------------- Main App ----------------------------
public class UniversityApp {
    static ArrayList<Student> students = new ArrayList<>();
    static ArrayList<Course> courses = new ArrayList<>();
    static ArrayList<Faculty> faculties = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== UNIVERSITY MANAGEMENT ===");
            System.out.println("1. Add  2. Delete  3. Update  4. Print  5. Search  6. Exit");
            System.out.print("Choice: ");
            int ch = sc.nextInt(); sc.nextLine();
            switch (ch) {
                case 1: addMenu(); break;
                case 2: deleteMenu(); break;
                case 3: updateMenu(); break;
                case 4: printMenu(); break;
                case 5: searchMenu(); break;
                case 6: System.out.println("Exiting..."); return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    static void addMenu() {
        System.out.println("\n1.Student 2.Course 3.Faculty");
        int ch = sc.nextInt(); sc.nextLine();
        if (ch == 1) {
            System.out.print("ID: "); int id = sc.nextInt(); sc.nextLine();
            if (findStudent(id) != null) { System.out.println("Student ID exists."); return; }
            System.out.print("Name: "); String name = sc.nextLine();
            System.out.print("CGPA: "); double cgpa = sc.nextDouble();
            students.add(new Student(id, name, cgpa));
            System.out.println("Student added.");
        } else if (ch == 2) {
            System.out.print("Course ID: "); String cid = sc.nextLine();
            if (findCourse(cid) != null) { System.out.println("Course ID exists."); return; }
            System.out.print("Title: "); String title = sc.nextLine();
            System.out.print("Credit: "); double cred = sc.nextDouble(); sc.nextLine();
            Course c = new Course(cid, title, cred);
            courses.add(c);
            // Enroll student
            System.out.print("Enroll a student now? (yes/no): ");
            if (sc.nextLine().equalsIgnoreCase("yes")) {
                System.out.print("Student ID: "); int sid = sc.nextInt(); sc.nextLine();
                Student s = findStudent(sid);
                if (s != null) { s.addCourse(c); c.addStudent(s); System.out.println("Enrolled."); }
                else System.out.println("Student not found.");
            }
            // Assign faculty
            System.out.print("Assign a faculty now? (yes/no): ");
            if (sc.nextLine().equalsIgnoreCase("yes")) {
                System.out.print("Faculty ID: "); int fid = sc.nextInt(); sc.nextLine();
                Faculty f = findFaculty(fid);
                if (f != null) { c.assignFaculty(f); System.out.println("Faculty assigned."); }
                else System.out.println("Faculty not found.");
            }
        } else if (ch == 3) {
            System.out.print("Faculty ID: "); int id = sc.nextInt(); sc.nextLine();
            if (findFaculty(id) != null) { System.out.println("Faculty ID exists."); return; }
            System.out.print("Name: "); String name = sc.nextLine();
            System.out.print("Position: "); String pos = sc.nextLine();
            faculties.add(new Faculty(id, name, pos));
            System.out.println("Faculty added.");
        } else System.out.println("Invalid.");
    }

    static void deleteMenu() {
        System.out.println("\n1.Student 2.Course 3.Faculty");
        int ch = sc.nextInt(); sc.nextLine();
        if (ch == 1) {
            System.out.print("Student ID: "); int id = sc.nextInt();
            Student s = findStudent(id);
            if (s == null) { System.out.println("Not found."); return; }
            for (Course c : new ArrayList<>(s.getCourseList())) { c.dropStudent(s); s.dropCourse(c); }
            students.remove(s);
            System.out.println("Student deleted.");
        } else if (ch == 2) {
            System.out.print("Course ID: "); String cid = sc.nextLine();
            Course c = findCourse(cid);
            if (c == null) { System.out.println("Not found."); return; }
            for (Student s : new ArrayList<>(c.getStudentList())) { s.dropCourse(c); c.dropStudent(s); }
            courses.remove(c);
            System.out.println("Course deleted.");
        } else if (ch == 3) {
            System.out.print("Faculty ID: "); int id = sc.nextInt();
            Faculty f = findFaculty(id);
            if (f == null) { System.out.println("Not found."); return; }
            for (Course c : courses) if (c.getFaculty() == f) c.assignFaculty(null);
            faculties.remove(f);
            System.out.println("Faculty deleted.");
        } else System.out.println("Invalid.");
    }

    static void updateMenu() {
        System.out.println("\n1.Student 2.Course 3.Faculty");
        int ch = sc.nextInt(); sc.nextLine();
        if (ch == 1) {
            System.out.print("Student ID: "); int id = sc.nextInt(); sc.nextLine();
            Student s = findStudent(id);
            if (s == null) { System.out.println("Not found."); return; }
            System.out.print("New Name: "); s.setsName(sc.nextLine());
            System.out.print("New CGPA: "); s.setsCGPA(sc.nextDouble());
            System.out.println("Updated.");
        } else if (ch == 2) {
            System.out.print("Course ID: "); String cid = sc.nextLine();
            Course c = findCourse(cid);
            if (c == null) { System.out.println("Not found."); return; }
            System.out.print("New Title: "); c.setCTitle(sc.nextLine());
            System.out.print("New Credit: "); c.setCredit(sc.nextDouble());
            System.out.println("Updated.");
        } else if (ch == 3) {
            System.out.print("Faculty ID: "); int id = sc.nextInt(); sc.nextLine();
            Faculty f = findFaculty(id);
            if (f == null) { System.out.println("Not found."); return; }
            System.out.print("New Name: "); f.setFacultyName(sc.nextLine());
            System.out.print("New Position: "); f.setFacultyPosition(sc.nextLine());
            System.out.println("Updated.");
        }
    }

    static void printMenu() {
        System.out.println("\n1.All Students 2.All Courses 3.All Faculties 4.Student details 5.Course details 6.Faculty details");
        System.out.println("7.Student list & faculty of a course 8.Courses of a student");
        int ch = sc.nextInt(); sc.nextLine();
        if (ch == 1) for (Student s : students) s.display();
        else if (ch == 2) for (Course c : courses) c.display();
        else if (ch == 3) for (Faculty f : faculties) f.display();
        else if (ch == 4) { System.out.print("Student ID: "); Student s = findStudent(sc.nextInt()); if(s!=null) s.display(); else System.out.println("Not found."); }
        else if (ch == 5) { System.out.print("Course ID: "); Course c = findCourse(sc.nextLine()); if(c!=null) c.display(); else System.out.println("Not found."); }
        else if (ch == 6) { System.out.print("Faculty ID: "); Faculty f = findFaculty(sc.nextInt()); if(f!=null) f.display(); else System.out.println("Not found."); }
        else if (ch == 7) {
            System.out.print("Course ID: "); Course c = findCourse(sc.nextLine());
            if (c == null) System.out.println("Not found.");
            else { System.out.println("Faculty:"); if(c.getFaculty()!=null) c.getFaculty().display(); else System.out.println("None");
                    System.out.println("Students:"); for(Student s : c.getStudentList()) s.display(); }
        } else if (ch == 8) {
            System.out.print("Student ID: "); Student s = findStudent(sc.nextInt());
            if(s!=null) s.printCourseList(); else System.out.println("Not found.");
        } else System.out.println("Invalid.");
    }

    static void searchMenu() {
        System.out.println("\n1.Student 2.Course 3.Faculty 4.Student takes course? 5.Faculty teaches course? 6.Courses of student 7.Courses of faculty");
        int ch = sc.nextInt(); sc.nextLine();
        if (ch == 1) { System.out.print("ID: "); Student s = findStudent(sc.nextInt()); if(s!=null) s.display(); else System.out.println("Not found."); }
        else if (ch == 2) { System.out.print("ID: "); Course c = findCourse(sc.nextLine()); if(c!=null) c.display(); else System.out.println("Not found."); }
        else if (ch == 3) { System.out.print("ID: "); Faculty f = findFaculty(sc.nextInt()); if(f!=null) f.display(); else System.out.println("Not found."); }
        else if (ch == 4) {
            System.out.print("Student ID: "); int sid = sc.nextInt(); sc.nextLine();
            System.out.print("Course ID: "); String cid = sc.nextLine();
            Student s = findStudent(sid); Course c = findCourse(cid);
            if (s!=null && c!=null && s.getCourseList().contains(c)) System.out.println("Yes, student takes this course.");
            else System.out.println("No.");
        } else if (ch == 5) {
            System.out.print("Faculty ID: "); int fid = sc.nextInt(); sc.nextLine();
            System.out.print("Course ID: "); String cid = sc.nextLine();
            Faculty f = findFaculty(fid); Course c = findCourse(cid);
            if (c!=null && c.getFaculty()==f) System.out.println("Yes, faculty teaches this course.");
            else System.out.println("No.");
        } else if (ch == 6) {
            System.out.print("Student ID: "); Student s = findStudent(sc.nextInt());
            if(s!=null) s.printCourseList(); else System.out.println("Not found.");
        } else if (ch == 7) {
            System.out.print("Faculty ID: "); Faculty f = findFaculty(sc.nextInt());
            if(f==null) System.out.println("Not found.");
            else { System.out.println("Courses taught by "+f.getFacultyName()+":"); for(Course c: courses) if(c.getFaculty()==f) System.out.println("- "+c.getCTitle()); }
        } else System.out.println("Invalid.");
    }

    static Student findStudent(int id) { for (Student s : students) if (s.getsId() == id) return s; return null; }
    static Course findCourse(String id) { for (Course c : courses) if (c.getCId().equals(id)) return c; return null; }
    static Faculty findFaculty(int id) { for (Faculty f : faculties) if (f.getFacultyId() == id) return f; return null; }
}