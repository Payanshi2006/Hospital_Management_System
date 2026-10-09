package model;

import enums.Gender;

public final class Doctor extends Person {
    private String specialty;

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Doctor(int id, String name, int age, Gender gender, String specialty) {
        super(id, name, age, gender);
        this.specialty = specialty;
    }

    @Override
    public void displayDetails() {
        System.out.println("Doctor Id: " + getId());
        System.out.println("Doctor Name: " + getName());
        System.out.println("Doctor Age: " + getAge());
        System.out.println("Doctor Gender: " + getGender());
        System.out.println("Doctor Disease: " + specialty);
    }
}
