package services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import exceptions.InvalidAppointmentException;
import enums.Status;
import model.Appointment;
import model.Doctor;
import model.Patient;

public class AppointmentService {
    DoctorService doctorService = new DoctorService();
    Random random = new Random();

    private HashMap<Integer, Appointment> map = new HashMap<>();
    private HashMap<Integer, List<Appointment>> patientAppointments = new HashMap<>();

    public void createAppointment(Patient patient, LocalDateTime time) throws InvalidAppointmentException {
        if (patient == null) {
            throw new InvalidAppointmentException("Patient is required to create an appointment.");
        }
        if (time == null || time.isBefore(LocalDateTime.now())) {
            throw new InvalidAppointmentException("Appointment date and time must be in the future.");
        }

        String disease = patient.getDisease();
        if (disease == null || disease.isBlank()) {
            throw new InvalidAppointmentException("Patient disease is required to find a doctor.");
        }

        String specialty = null;
        switch (disease) {
            case "Joint Pain", "Fracture":
                specialty = "Orthopedic";
                break;
            case "Acne", "Skin Allergy":
                specialty = "Dermatologist";
                break;
            case "Heart":
                specialty = "Cardiologist";
                break;
            case "Migraine":
                specialty = "Neurologist";
                break;
            case "Inflamation":
                specialty = "Rheumatologist";
                break;
            default:
                break;
        }

        List<Doctor> doctors = doctorService.searchDoctor(specialty);
        if (doctors.isEmpty()) {
            throw new InvalidAppointmentException("No doctor is available for the patient's disease: " + disease);
        }

        Doctor doctor = doctors.get(0);

        int id;
        do {
            id = random.nextInt();
        } while (map.containsKey(id));

        Appointment appointment = new Appointment(id, patient, doctor, time, Status.SCHEDULED);
        map.put(id, appointment);

        if (patientAppointments.containsKey(patient.getId())) {
            patientAppointments.get(patient.getId()).add(appointment);
        } else {
            List<Appointment> list = new ArrayList<>();
            list.add(appointment);
            patientAppointments.put(patient.getId(), list);
        }
    }

    public void updateStatus(int id, Status status) throws InvalidAppointmentException {
        if (!map.containsKey(id)) {
            throw new InvalidAppointmentException("No appointment present with ID: " + id);
        }
        if (status == null) {
            throw new InvalidAppointmentException("Appointment status is required.");
        }

        Appointment appointment = map.get(id);
        appointment.setStatus(status);
        map.put(id, appointment);

        int patientId = appointment.getPatient().getId();
        List<Appointment> appointments = patientAppointments.get(patientId);

        for (Appointment appointment2 : appointments) {
            if (appointment2.getId() == id) {
                appointment2.setStatus(status);
            }
        }
    }

    public Appointment getAppointmentById(int id) {
        return map.get(id);
    }

    public List<Appointment> getPatientAppointments(int patientId) {
        return patientAppointments.getOrDefault(patientId, new ArrayList<>());
    }
}
