package app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import model.Bill;
import services.BillingService;
import exceptions.PaymentException;
import exceptions.HospitalException;
import exceptions.InvalidAppointmentException;
import enums.Gender;
import enums.Status;
import model.Appointment;
import model.Doctor;
import model.Patient;
import services.AppointmentService;
import services.DoctorService;
import services.PatientService;

public class Hospital {

    private static final Scanner sc = new Scanner(System.in);

    private static final DoctorService doctorService = new DoctorService();
    private static final PatientService patientService = new PatientService();
    private static final AppointmentService appointmentService = new AppointmentService();
    private static final BillingService billingService = new BillingService();
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {

        try {
            loadSampleData();
        } catch (IllegalStateException e) {
            System.out.println("Unable to load sample hospital data: " + e.getMessage());
            return;
        }

        try {
            int choice;

            do {
                System.out.println("\n===== HOSPITAL MANAGEMENT SYSTEM =====");
                System.out.println("1. Book Appointment");
                System.out.println("2. Patient Management");
                System.out.println("3. Doctor Management");
                System.out.println("4. Billing Management");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");

                choice = readInt();

                switch (choice) {
                    case 1:
                        bookAppointment();
                        break;
                    case 2:
                        patientMenu();
                        break;
                    case 3:
                        doctorMenu();
                        break;
                    case 4:
                        billingMenu();
                        break;
                    case 5:
                        System.out.println("Exiting Hospital Management System.");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                        break;
                }

            } while (choice != 5);
        } catch (NoSuchElementException e) {
            System.out.println("\nInput ended. Exiting Hospital Management System.");
        }
    }

    private static void bookAppointment() {

        int choice;

        do {
            System.out.println("\n===== APPOINTMENT MENU =====");
            System.out.println("1. Create Appointment");
            System.out.println("2. Update Appointment Status");
            System.out.println("3. Search Appointment");
            System.out.println("4. View Patient Appointment History");
            System.out.println("5. Back");
            System.out.print("Enter your choice: ");

            choice = readInt();

            switch (choice) {
                case 1:
                    createAppointment();
                    break;
                case 2:
                    updateAppointmentStatus();
                    break;
                case 3:
                    searchAppointment();
                    break;
                case 4:
                    viewPatientAppointmentHistory();
                    break;
                case 5:
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }

        } while (choice != 5);
    }

    private static void billingMenu() {

        int choice;

        do {
            System.out.println("\n===== BILLING MANAGEMENT =====");
            System.out.println("1. Generate Bill");
            System.out.println("2. Pay Bill");
            System.out.println("3. View All Bills");
            System.out.println("4. View Patient Bills");
            System.out.println("5. Back");
            System.out.print("Enter your choice: ");

            choice = readInt();

            switch (choice) {
                case 1:
                    generateBill();
                    break;
                case 2:
                    payBill();
                    break;
                case 3:
                    billingService.showAllBills();
                    break;
                case 4:
                    showPatientBills();
                    break;
                case 5:
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }

        } while (choice != 5);
    }

    private static void showPatientBills() {
        System.out.println("\n===== PATIENT BILLS =====");
        System.out.print("Enter Patient ID: ");
        int patientId = readInt();

        try {
            billingService.showBillsByPatient(patientId);
        } catch (PaymentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void generateBill() {
        System.out.println("\n===== GENERATE BILL =====");
        System.out.print("Enter Appointment ID: ");
        int appointmentId = readInt();

        Appointment appointment = appointmentService.getAppointmentById(appointmentId);

        if (appointment == null) {
            System.out.println("Appointment not found.");
            return;
        }

        if (appointment.getStatus() != Status.COMPLETED) {
            System.out.println("Bill can only be generated for a completed appointment.");
            return;
        }

        System.out.print("Enter consultation fee: ");
        double fee = readDouble();

        int billId = (int) (System.currentTimeMillis() % 100000);

        Bill bill = new Bill(
                billId,
                appointment.getPatient(),
                appointment.getDoctor(),
                fee
        );

        try {
            billingService.generateBill(bill);
        } catch (PaymentException e) {
            System.out.println(e.getMessage());
            return;
        }

        bill.displayBill();
    }

    private static void payBill() {
        System.out.println("\n===== PAY BILL =====");
        System.out.print("Enter Bill ID: ");
        int billId = readInt();

        Bill bill = billingService.getBillById(billId);

        if (bill == null) {
            System.out.println("Bill not found.");
            return;
        }

        try {
            billingService.payBill(bill);
        } catch (PaymentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createAppointment() {
        System.out.println("\n===== CREATE APPOINTMENT =====");
        System.out.print("Enter Patient ID: ");
        int patientId = readInt();

        Patient patient = patientService.searchPatient(patientId);

        if (patient == null) {
            System.out.println("Patient not found. Please register the patient first.");
            return;
        }

        System.out.print("Enter appointment date and time (yyyy-MM-dd HH:mm): ");
        String input = sc.nextLine();

        LocalDateTime appointmentTime;
        try {
            appointmentTime = LocalDateTime.parse(input, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format.");
            return;
        }

        try {
            appointmentService.createAppointment(patient, appointmentTime);
        } catch (InvalidAppointmentException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Appointment created successfully.");
        System.out.println("Patient: " + patient.getName());
        System.out.println("Date and Time: " + appointmentTime.format(formatter));
    }

    private static void updateAppointmentStatus() {
        System.out.println("\n===== UPDATE APPOINTMENT STATUS =====");
        System.out.print("Enter Appointment ID: ");
        int appointmentId = readInt();

        Appointment appointment = appointmentService.getAppointmentById(appointmentId);

        if (appointment == null) {
            System.out.println("Appointment not found.");
            return;
        }

        System.out.println("1. Scheduled");
        System.out.println("2. Cancelled");
        System.out.println("3. Completed");
        System.out.print("Select new status: ");

        int choice = readInt();
        Status status;

        switch (choice) {
            case 1:
                status = Status.SCHEDULED;
                break;
            case 2:
                status = Status.CANCELLED;
                break;
            case 3:
                status = Status.COMPLETED;
                break;
            default:
                System.out.println("Invalid status choice.");
                return;
        }

        try {
            appointmentService.updateStatus(appointmentId, status);
        } catch (InvalidAppointmentException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Appointment status updated successfully.");
    }

    private static void searchAppointment() {
        System.out.println("\n===== SEARCH APPOINTMENT =====");
        System.out.print("Enter Appointment ID: ");
        int appointmentId = readInt();

        Appointment appointment = appointmentService.getAppointmentById(appointmentId);

        if (appointment == null) {
            System.out.println("Appointment not found.");
            return;
        }

        displayAppointment(appointment);
    }

    private static void viewPatientAppointmentHistory() {
        System.out.println("\n===== PATIENT APPOINTMENT HISTORY =====");
        System.out.print("Enter Patient ID: ");
        int patientId = readInt();

        Patient patient = patientService.searchPatient(patientId);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }

        List<Appointment> appointments = appointmentService.getPatientAppointments(patientId);
        if (appointments.isEmpty()) {
            System.out.println("No appointments found for this patient.");
            return;
        }

        System.out.println("\nPatient Name: " + patient.getName());
        System.out.println("Patient ID: " + patient.getId());
        System.out.println("\n===== APPOINTMENT HISTORY =====");

        for (Appointment appointment : appointments) {
            displayAppointment(appointment);
            System.out.println("------------------------------");
        }
    }

    private static void displayAppointment(Appointment appointment) {
        System.out.println("Appointment ID: " + appointment.getId());
        System.out.println("Patient: " + appointment.getPatient().getName());
        System.out.println("Doctor: " + appointment.getDoctor().getName());
        System.out.println("Specialty: " + appointment.getDoctor().getSpecialty());
        System.out.println("Date and Time: " + appointment.getTime().format(formatter));
        System.out.println("Status: " + appointment.getStatus());
    }

    private static void patientMenu() {
        int choice;

        do {
            System.out.println("\n===== PATIENT MANAGEMENT =====");
            System.out.println("1. Search Patient by ID");
            System.out.println("2. Search Patient by Name");
            System.out.println("3. Register Patient");
            System.out.println("4. Delete Patient");
            System.out.println("5. View Patient Appointment History");
            System.out.println("6. Back");
            System.out.print("Enter your choice: ");

            choice = readInt();

            switch (choice) {
                case 1:
                    searchPatientById();
                    break;
                case 2:
                    searchPatientByName();
                    break;
                case 3:
                    registerPatient();
                    break;
                case 4:
                    deletePatient();
                    break;
                case 5:
                    viewPatientAppointmentHistory();
                    break;
                case 6:
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }

        } while (choice != 6);
    }

    private static void loadSampleData() {
        Patient[] patients = {
                new Patient(201, "Aarav Mehta", 34, Gender.MALE, "Joint Pain"),
                new Patient(202, "Priya Sharma", 28, Gender.FEMALE, "Acne"),
                new Patient(203, "Kabir Verma", 52, Gender.MALE, "Heart"),
                new Patient(204, "Ananya Singh", 41, Gender.FEMALE, "Migraine"),
                new Patient(205, "Rohan Gupta", 47, Gender.MALE, "Inflamation")
        };
        double[] consultationFees = {800.0, 650.0, 1200.0, 900.0, 750.0};

        try {
            for (int i = 0; i < patients.length; i++) {
                Patient patient = patients[i];
                patientService.addPatient(patient);

                LocalDateTime appointmentTime = LocalDateTime.now()
                        .plusDays(i + 1)
                        .withHour(9 + i)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);
                appointmentService.createAppointment(patient, appointmentTime);

                Appointment appointment = appointmentService
                        .getPatientAppointments(patient.getId()).get(0);
                appointment.setTime(LocalDateTime.now()
                        .minusDays(i + 1)
                        .withHour(9 + i)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0));
                appointmentService.updateStatus(appointment.getId(), Status.COMPLETED);

                Bill bill = new Bill(
                        5001 + i,
                        patient,
                        appointment.getDoctor(),
                        consultationFees[i]
                );
                if (i == 0) {
                    bill.makePayment();
                }
                billingService.generateBill(bill);
            }
        } catch (HospitalException e) {
            throw new IllegalStateException("Unable to load sample hospital data.", e);
        }

        System.out.println("Sample data loaded: 5 patients, 5 appointments, and 5 bills.");
    }

    private static void registerPatient() {
        System.out.println("\n===== REGISTER PATIENT =====");
        System.out.print("Enter Patient ID: ");
        int id = readInt();

        if (patientService.searchPatient(id) != null) {
            System.out.println("Patient ID already exists.");
            return;
        }

        System.out.print("Enter Patient Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Patient Age: ");
        int age = readInt();

        Gender gender = selectGender();
        if (gender == null) {
            return;
        }

        String disease = selectDisease();
        if (disease == null) {
            return;
        }

        Patient patient = new Patient(id, name, age, gender, disease);

        try {
            patientService.addPatient(patient);
        } catch (HospitalException e) {
            System.out.println(e.getMessage());
        }
    }

    private static Gender selectGender() {
        System.out.println("\n===== SELECT GENDER =====");
        System.out.println("1. Male");
        System.out.println("2. Female");
        System.out.print("Enter your choice: ");

        int choice = readInt();

        switch (choice) {
            case 1:
                return Gender.MALE;
            case 2:
                return Gender.FEMALE;
            default:
                System.out.println("Invalid gender choice.");
                return null;
        }
    }

    private static String selectDisease() {
        System.out.println("\n===== SELECT DISEASE =====");
        System.out.println("1. Joint Pain");
        System.out.println("2. Fracture");
        System.out.println("3. Acne");
        System.out.println("4. Skin Allergy");
        System.out.println("5. Heart");
        System.out.println("6. Migraine");
        System.out.println("7. Inflamation");
        System.out.print("Enter your choice: ");

        int choice = readInt();

        switch (choice) {
            case 1:
                return "Joint Pain";
            case 2:
                return "Fracture";
            case 3:
                return "Acne";
            case 4:
                return "Skin Allergy";
            case 5:
                return "Heart";
            case 6:
                return "Migraine";
            case 7:
                return "Inflamation";
            default:
                System.out.println("Invalid disease choice.");
                return null;
        }
    }

    private static void searchPatientById() {
        System.out.println("\n===== SEARCH PATIENT BY ID =====");
        System.out.print("Enter Patient ID: ");
        int id = readInt();

        Patient patient = patientService.searchPatient(id);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }

        patient.displayDetails();
    }

    private static void searchPatientByName() {
        System.out.println("\n===== SEARCH PATIENT BY NAME =====");
        System.out.print("Enter Patient Name: ");
        String name = sc.nextLine();

        List<Patient> patients = patientService.searchPatient(name);
        if (patients.isEmpty()) {
            System.out.println("No patient found with this name.");
            return;
        }

        for (Patient patient : patients) {
            patient.displayDetails();
            System.out.println("------------------------------");
        }
    }

    private static void deletePatient() {
        System.out.println("\n===== DELETE PATIENT =====");
        System.out.print("Enter Patient ID: ");
        int id = readInt();

        Patient patient = patientService.searchPatient(id);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }

        try {
            patientService.deletePatient(id);
        } catch (HospitalException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void doctorMenu() {
        int choice;

        do {
            System.out.println("\n===== DOCTOR MANAGEMENT =====");
            System.out.println("1. Search Doctor by ID");
            System.out.println("2. Search Doctor by Specialty");
            System.out.println("3. View All Doctors");
            System.out.println("4. Back");
            System.out.print("Enter your choice: ");

            choice = readInt();

            switch (choice) {
                case 1:
                    searchDoctorById();
                    break;
                case 2:
                    searchDoctorBySpecialty();
                    break;
                case 3:
                    viewAllDoctors();
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }

        } while (choice != 4);
    }

    private static void searchDoctorById() {
        System.out.println("\n===== SEARCH DOCTOR BY ID =====");
        System.out.print("Enter Doctor ID: ");
        int id = readInt();

        Doctor doctor = doctorService.getById(id);
        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }

        doctor.displayDetails();
    }

    private static void searchDoctorBySpecialty() {
        System.out.println("\n===== SEARCH DOCTOR BY SPECIALTY =====");
        System.out.println("1. Orthopedic");
        System.out.println("2. Dermatologist");
        System.out.println("3. Cardiologist");
        System.out.println("4. Neurologist");
        System.out.println("5. Rheumatologist");
        System.out.print("Select specialty: ");

        int choice = readInt();
        String specialty;

        switch (choice) {
            case 1:
                specialty = "Orthopedic";
                break;
            case 2:
                specialty = "Dermatologist";
                break;
            case 3:
                specialty = "Cardiologist";
                break;
            case 4:
                specialty = "Neurologist";
                break;
            case 5:
                specialty = "Rheumatologist";
                break;
            default:
                System.out.println("Invalid specialty choice.");
                return;
        }

        List<Doctor> doctors = doctorService.searchDoctor(specialty);
        if (doctors.isEmpty()) {
            System.out.println("No doctor found for this specialty.");
            return;
        }

        for (Doctor doctor : doctors) {
            doctor.displayDetails();
            System.out.println("------------------------------");
        }
    }

    private static void viewAllDoctors() {
        System.out.println("\n===== ALL DOCTORS =====");

        for (int id = 101; id <= 105; id++) {
            Doctor doctor = doctorService.getById(id);
            if (doctor != null) {
                doctor.displayDetails();
                System.out.println("------------------------------");
            }
        }
    }

    private static int readInt() {
        while (true) {
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid whole number: ");
            }
        }
    }

    private static double readDouble() {
        while (true) {
            String input = sc.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (Double.isFinite(value) && value >= 0) {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid non-negative amount: ");
                continue;
            }
            System.out.print("Please enter a valid non-negative amount: ");
        }
    }
}
