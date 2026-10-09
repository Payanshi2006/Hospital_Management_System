package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import exceptions.DuplicateRecordException;
import exceptions.HospitalException;
import model.Patient;

public class PatientService {
    private HashMap<Integer, Patient> patients = new HashMap<>();

    public void addPatient(Patient patient) throws HospitalException {
        if (patient == null) {
            throw new HospitalException("Patient cannot be null.");
        }
        if (patients.containsKey(patient.getId())) {
            throw new DuplicateRecordException("Patient ID already exists.");
        }
        patients.put(patient.getId(), patient);
        System.out.println("Patient added successfully");
    }

    public List<Patient> searchPatient(String name) {
        List<Patient> list = new ArrayList<>();

        for (Patient patient : patients.values()) {
            if (patient.getName().equalsIgnoreCase(name)) {
                list.add(patient);
            }
        }

        return list;
    }

    public Patient searchPatient(int id) {
        return patients.get(id);
    }

    public void displayDetails(int id) throws HospitalException {
        Patient patient = patients.get(id);
        if (patient == null) {
            throw new HospitalException("Patient not found with ID: " + id);
        }
        patient.displayDetails();
    }

    public void deletePatient(int id) throws HospitalException {
        if (patients.remove(id) == null) {
            throw new HospitalException("Patient not found with ID: " + id);
        }
        System.out.println("Patient Deleted successfully");
    }
}
