package com.apartment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "expenses", schema = "apartment")
public class Expense {

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
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column
    private String receiptUrl;

    @Column
    private String billNumber;

    @Column
    private String vendorName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Expense() {
    }

    public Expense(Apartment apartment, User treasurer, String title, String description, 
                  Double amount, Integer month, Integer year, String receiptUrl, 
                  String billNumber, String vendorName, ExpenseCategory category) {
        this.apartment = apartment;
        this.treasurer = treasurer;
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.month = month;
        this.year = year;
        this.receiptUrl = receiptUrl;
        this.billNumber = billNumber;
        this.vendorName = vendorName;
        this.category = category;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
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

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
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
        private String title;
        private String description;
        private Double amount;
        private Integer month;
        private Integer year;
        private String receiptUrl;
        private String billNumber;
        private String vendorName;
        private ExpenseCategory category;

        public Builder apartment(Apartment apartment) {
            this.apartment = apartment;
            return this;
        }

        public Builder treasurer(User treasurer) {
            this.treasurer = treasurer;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder amount(Double amount) {
            this.amount = amount;
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

        public Builder receiptUrl(String receiptUrl) {
            this.receiptUrl = receiptUrl;
            return this;
        }

        public Builder billNumber(String billNumber) {
            this.billNumber = billNumber;
            return this;
        }

        public Builder vendorName(String vendorName) {
            this.vendorName = vendorName;
            return this;
        }

        public Builder category(ExpenseCategory category) {
            this.category = category;
            return this;
        }

        public Expense build() {
            return new Expense(apartment, treasurer, title, description, amount, month, year, 
                              receiptUrl, billNumber, vendorName, category);
        }
    }

    public enum ExpenseCategory {
        CLEANING,
        PLUMBING,
        ELECTRICITY,
        SECURITY,
        GARDENING,
        ELEVATOR,
        WATER,
        COMMON_AREA,
        REPAIRS,
        ADMINISTRATIVE,
        OTHER
    }
}
