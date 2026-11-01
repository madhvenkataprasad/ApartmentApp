package com.apartment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_reports", schema = "apartment")
public class MonthlyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = false)
    @JsonIgnore
    private Apartment apartment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User treasurer;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Double openingBalance;

    @Column(nullable = false)
    private Double totalIncome;

    @Column(nullable = false)
    private Double totalExpenses;

    @Column(nullable = false)
    private Double closingBalance;

    @Column
    private Integer totalResidents;

    @Column
    private Integer residentsPaid;

    @Column
    private Integer residentsPending;

    @Column
    private String notes;

    @Column(nullable = false)
    private Boolean published = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public MonthlyReport() {
    }

    public MonthlyReport(Apartment apartment, User treasurer, Integer month, Integer year, 
                        Double openingBalance, Double totalIncome, Double totalExpenses, 
                        Double closingBalance, Integer totalResidents, Integer residentsPaid, 
                        Integer residentsPending, String notes, Boolean published) {
        this.apartment = apartment;
        this.treasurer = treasurer;
        this.month = month;
        this.year = year;
        this.openingBalance = openingBalance;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.closingBalance = closingBalance;
        this.totalResidents = totalResidents;
        this.residentsPaid = residentsPaid;
        this.residentsPending = residentsPending;
        this.notes = notes;
        this.published = published;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public User getTreasurer() {
        return treasurer;
    }

    public void setTreasurer(User treasurer) {
        this.treasurer = treasurer;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(Double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public Double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public Double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(Double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public Double getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(Double closingBalance) {
        this.closingBalance = closingBalance;
    }

    public Integer getTotalResidents() {
        return totalResidents;
    }

    public void setTotalResidents(Integer totalResidents) {
        this.totalResidents = totalResidents;
    }

    public Integer getResidentsPaid() {
        return residentsPaid;
    }

    public void setResidentsPaid(Integer residentsPaid) {
        this.residentsPaid = residentsPaid;
    }

    public Integer getResidentsPending() {
        return residentsPending;
    }

    public void setResidentsPending(Integer residentsPending) {
        this.residentsPending = residentsPending;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class Builder {
        private Apartment apartment;
        private User treasurer;
        private Integer month;
        private Integer year;
        private Double openingBalance;
        private Double totalIncome;
        private Double totalExpenses;
        private Double closingBalance;
        private Integer totalResidents;
        private Integer residentsPaid;
        private Integer residentsPending;
        private String notes;
        private Boolean published = false;

        public Builder apartment(Apartment apartment) {
            this.apartment = apartment;
            return this;
        }

        public Builder treasurer(User treasurer) {
            this.treasurer = treasurer;
            return this;
        }

        public Builder month(Integer month) {
            this.month = month;
            return this;
        }

        public Builder year(Integer year) {
            this.year = year;
            return this;
        }

        public Builder openingBalance(Double openingBalance) {
            this.openingBalance = openingBalance;
            return this;
        }

        public Builder totalIncome(Double totalIncome) {
            this.totalIncome = totalIncome;
            return this;
        }

        public Builder totalExpenses(Double totalExpenses) {
            this.totalExpenses = totalExpenses;
            return this;
        }

        public Builder closingBalance(Double closingBalance) {
            this.closingBalance = closingBalance;
            return this;
        }

        public Builder totalResidents(Integer totalResidents) {
            this.totalResidents = totalResidents;
            return this;
        }

        public Builder residentsPaid(Integer residentsPaid) {
            this.residentsPaid = residentsPaid;
            return this;
        }

        public Builder residentsPending(Integer residentsPending) {
            this.residentsPending = residentsPending;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder published(Boolean published) {
            this.published = published;
            return this;
        }

        public MonthlyReport build() {
            return new MonthlyReport(apartment, treasurer, month, year, openingBalance, 
                                    totalIncome, totalExpenses, closingBalance, totalResidents, 
                                    residentsPaid, residentsPending, notes, published);
        }
    }
}
