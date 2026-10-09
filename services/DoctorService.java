package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import enums.Gender;
import model.Doctor;

public class DoctorService {
    private HashMap<Integer, Doctor> doctors = new HashMap<>();

    public DoctorService() {
        Doctor doctor1 = new Doctor(101, "Mittal", 45, Gender.MALE, "Dermatologist");
        Doctor doctor2 = new Doctor(102, "Rahul Jain", 56, Gender.MALE, "Orthopedic");
        Doctor doctor3 = new Doctor(103, "Yugal Sharma", 65, Gender.MALE, "Cardiologist");
        Doctor doctor4 = new Doctor(104, "Khetan", 34, Gender.FEMALE, "Neurologist");
        Doctor doctor5 = new Doctor(105, "Manipal", 29, Gender.FEMALE, "Rheumatologist");

        doctors.put(doctor1.getId(), doctor1);
        doctors.put(doctor2.getId(), doctor2);
        doctors.put(doctor3.getId(), doctor3);
        doctors.put(doctor4.getId(), doctor4);
        doctors.put(doctor5.getId(), doctor5);
    }

    public List<Doctor> searchDoctor(String specialty) {
        List<Doctor> list = new ArrayList<>();
        for (Doctor doctor : doctors.values()) {
            if (doctor.getSpecialty().equals(specialty)) {
                list.add(doctor);
            }
        }
        return list;
    }

    public Doctor getById(int id) {
        return doctors.get(id);
    }
}
