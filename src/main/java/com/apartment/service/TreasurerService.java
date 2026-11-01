package com.apartment.service;

import com.apartment.entity.*;
import com.apartment.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TreasurerService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;
    private final MonthlyReportRepository monthlyReportRepository;
    private final NotificationRepository notificationRepository;

    public TreasurerService(UserRepository userRepository, PaymentRepository paymentRepository, 
                           ExpenseRepository expenseRepository, MonthlyReportRepository monthlyReportRepository, 
                           NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.expenseRepository = expenseRepository;
        this.monthlyReportRepository = monthlyReportRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<Payment> getAllPayments(Long apartmentId) {
        return paymentRepository.findByApartmentId(apartmentId);
    }

    public List<Payment> getPaymentsByMonthYear(Long apartmentId, Integer month, Integer year) {
        return paymentRepository.findByApartmentIdAndMonthAndYear(apartmentId, month, year);
    }

    public List<Expense> getAllExpenses(Long apartmentId) {
        return expenseRepository.findByApartmentIdOrderByCreatedAtDesc(apartmentId);
    }

    public List<Expense> getExpensesByMonthYear(Long apartmentId, Integer month, Integer year) {
        return expenseRepository.findByApartmentIdAndMonthAndYearOrderByCreatedAtDesc(apartmentId, month, year);
    }

    @Transactional
    public Expense createExpense(Long treasurerId, Expense expense) {
        User treasurer = userRepository.findById(treasurerId)
                .orElseThrow(() -> new RuntimeException("Treasurer not found"));

        expense.setTreasurer(treasurer);
        expense.setApartment(treasurer.getApartment());

        Expense savedExpense = expenseRepository.save(expense);

        // Notify all residents about the expense
        List<User> residents = userRepository.findResidentsByApartmentId(treasurer.getApartment().getId());
        for (User resident : residents) {
            Notification notification = Notification.builder()
                    .user(resident)
                    .title("New Expense: " + expense.getTitle())
                    .message("Amount: ₹" + expense.getAmount() + " - " + expense.getDescription())
                    .type(Notification.NotificationType.EXPENSE)
                    .build();
            notificationRepository.save(notification);
        }

        return savedExpense;
    }

    @Transactional
    public Expense updateExpense(Long expenseId, Expense expense) {
        Expense existing = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        existing.setTitle(expense.getTitle());
        existing.setDescription(expense.getDescription());
        existing.setAmount(expense.getAmount());
        existing.setCategory(expense.getCategory());
        existing.setReceiptUrl(expense.getReceiptUrl());
        existing.setBillNumber(expense.getBillNumber());
        existing.setVendorName(expense.getVendorName());

        return expenseRepository.save(existing);
    }

    @Transactional
    public void deleteExpense(Long expenseId) {
        expenseRepository.deleteById(expenseId);
    }

    @Transactional
    public MonthlyReport generateMonthlyReport(Long treasurerId, Integer month, Integer year) {
        User treasurer = userRepository.findById(treasurerId)
                .orElseThrow(() -> new RuntimeException("Treasurer not found"));

        Long apartmentId = treasurer.getApartment().getId();

        // Get opening balance from previous month's closing balance
        Optional<MonthlyReport> previousReport = monthlyReportRepository
                .findByApartmentIdAndMonthAndYear(apartmentId, month == 1 ? 12 : month - 1, month == 1 ? year - 1 : year);

        double openingBalance = previousReport.map(MonthlyReport::getClosingBalance).orElse(0.0);

        // Calculate total income (payments)
        Double totalIncome = paymentRepository.sumPaidAmountByApartmentAndMonthYear(apartmentId, month, year);
        if (totalIncome == null) totalIncome = 0.0;

        // Calculate total expenses
        Optional<Double> totalExpensesOpt = expenseRepository.sumExpensesByApartmentAndMonthYear(apartmentId, month, year);
        double totalExpenses = totalExpensesOpt.orElse(0.0);

        // Calculate closing balance
        double closingBalance = openingBalance + totalIncome - totalExpenses;

        // Get resident counts
        Long totalResidents = userRepository.countResidentsByApartmentId(apartmentId);
        Long residentsPaid = paymentRepository.countPaidPaymentsByApartmentAndMonthYear(apartmentId, month, year);
        Long residentsPending = totalResidents - residentsPaid;

        MonthlyReport report = MonthlyReport.builder()
                .apartment(treasurer.getApartment())
                .treasurer(treasurer)
                .month(month)
                .year(year)
                .openingBalance(openingBalance)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .closingBalance(closingBalance)
                .totalResidents(totalResidents != null ? totalResidents.intValue() : 0)
                .residentsPaid(residentsPaid != null ? residentsPaid.intValue() : 0)
                .residentsPending(residentsPending != null ? residentsPending.intValue() : 0)
                .published(false)
                .build();

        return monthlyReportRepository.save(report);
    }

    @Transactional
    public MonthlyReport publishReport(Long reportId) {
        MonthlyReport report = monthlyReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setPublished(true);

        // Notify all residents
        List<User> residents = userRepository.findResidentsByApartmentId(report.getApartment().getId());
        for (User resident : residents) {
            Notification notification = Notification.builder()
                    .user(resident)
                    .title("Monthly Financial Report Published")
                    .message("Financial report for " + report.getMonth() + "/" + report.getYear() + " is now available. Closing Balance: ₹" + report.getClosingBalance())
                    .type(Notification.NotificationType.REPORT)
                    .build();
            notificationRepository.save(notification);
        }

        return monthlyReportRepository.save(report);
    }

    public List<MonthlyReport> getAllReports(Long apartmentId) {
        return monthlyReportRepository.findByApartmentIdOrderByYearDescMonthDesc(apartmentId);
    }
}
