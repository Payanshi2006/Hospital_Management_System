package services;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import exceptions.PaymentException;
import model.Bill;
import repository.InMemoryRepository;

public class BillingService extends InMemoryRepository<Integer, Bill> {
    private static final long QUERY_TIMEOUT_SECONDS = 4;

    public void generateBill(Bill bill) throws PaymentException {
        if (bill == null || bill.getPatient() == null || bill.getDoctor() == null) {
            throw new PaymentException("A bill, patient, and doctor are required.");
        }
        if (bill.getBillId() < 0 || !Double.isFinite(bill.getConsultationFee())
                || bill.getConsultationFee() < 0) {
            throw new PaymentException("Bill ID and consultation fee must be non-negative.");
        }
        if (!add(bill.getBillId(), bill)) {
            throw new PaymentException("Bill ID already exists: " + bill.getBillId());
        }
        System.out.println("Bill generated successfully");
    }

    public void showAllBills() {
        findAll().forEach(Bill::displayBill);
    }

    public Bill getBillById(int billId) {
        return findByKey(billId);
    }

    public synchronized void payBill(Bill bill) throws PaymentException {
        if (bill == null) {
            throw new PaymentException("Bill is required to make a payment.");
        }

        Bill storedBill = getBillById(bill.getBillId());
        if (storedBill == null || storedBill != bill) {
            throw new PaymentException("Bill not found: " + bill.getBillId());
        }
        if (storedBill.isPaid()) {
            throw new PaymentException("Bill is already paid");
        }

        storedBill.makePayment();
        System.out.println("Payment successful");
    }

    public List<Bill> getBillsByPatient(int patientId) throws PaymentException {
        try {
            return CompletableFuture
                    .supplyAsync(() -> findAll().stream()
                            .filter(bill -> bill.getPatient().getId() == patientId)
                            .toList())
                    .get(QUERY_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PaymentException("Patient bill lookup was interrupted.");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            String detail = cause == null || cause.getMessage() == null
                    ? "Unknown task failure."
                    : cause.getMessage();
            throw new PaymentException("Unable to retrieve patient bills: " + detail);
        } catch (TimeoutException e) {
            throw new PaymentException("Patient bill lookup exceeded "
                    + QUERY_TIMEOUT_SECONDS + " seconds.");
        }
    }

    public void showBillsByPatient(int patientId) throws PaymentException {
        List<Bill> patientBills = getBillsByPatient(patientId);
        if (patientBills.isEmpty()) {
            System.out.println("No bills found for this patient.");
            return;
        }
        patientBills.forEach(Bill::displayBill);
    }
}
