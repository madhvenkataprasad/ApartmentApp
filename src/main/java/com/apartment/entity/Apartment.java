package com.apartment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "apartments", schema = "apartment")
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String secretCode;

    @Column
    private String address;

    @Column
    private String city;

    @Column
    private String state;

    @Column
    private Integer totalFlats;

    @Column
    private Double monthlyMaintenanceAmount;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Announcement> announcements = new ArrayList<>();

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<MonthlyReport> monthlyReports = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Apartment() {}

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSecretCode() { return secretCode; }
    public void setSecretCode(String secretCode) { this.secretCode = secretCode; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Integer getTotalFlats() { return totalFlats; }
    public void setTotalFlats(Integer totalFlats) { this.totalFlats = totalFlats; }

    public Double getMonthlyMaintenanceAmount() { return monthlyMaintenanceAmount; }
    public void setMonthlyMaintenanceAmount(Double monthlyMaintenanceAmount) { this.monthlyMaintenanceAmount = monthlyMaintenanceAmount; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public List<Announcement> getAnnouncements() { return announcements; }
    public void setAnnouncements(List<Announcement> announcements) { this.announcements = announcements; }

    public List<Expense> getExpenses() { return expenses; }
    public void setExpenses(List<Expense> expenses) { this.expenses = expenses; }

    public List<MonthlyReport> getMonthlyReports() { return monthlyReports; }
    public void setMonthlyReports(List<MonthlyReport> monthlyReports) { this.monthlyReports = monthlyReports; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class Builder {
        private String name;
        private String secretCode;
        private String address;
        private String city;
        private String state;
        private Integer totalFlats;
        private Double monthlyMaintenanceAmount;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder secretCode(String secretCode) {
            this.secretCode = secretCode;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder totalFlats(Integer totalFlats) {
            this.totalFlats = totalFlats;
            return this;
        }

        public Builder monthlyMaintenanceAmount(Double monthlyMaintenanceAmount) {
            this.monthlyMaintenanceAmount = monthlyMaintenanceAmount;
            return this;
        }

        public Apartment build() {
            Apartment apartment = new Apartment();
            apartment.setName(name);
            apartment.setSecretCode(secretCode);
            apartment.setAddress(address);
            apartment.setCity(city);
            apartment.setState(state);
            apartment.setTotalFlats(totalFlats);
            apartment.setMonthlyMaintenanceAmount(monthlyMaintenanceAmount);
            return apartment;
        }
    }
}
