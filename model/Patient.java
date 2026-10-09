package model;

import enums.Gender;

public final class Patient extends Person {
    private String disease;

    public Patient(int id, String name, int age, Gender gender, String disease) {
        super(id, name, age, gender);
        this.disease = disease;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    @Override
    public void displayDetails() {
        System.out.println("Patient Id: " + getId());
        System.out.println("Patient Name: " + getName());
        System.out.println("Patient Age: " + getAge());
        System.out.println("Patient Gender: " + getGender());
        System.out.println("Patient Disease: " + disease);
    }
}
