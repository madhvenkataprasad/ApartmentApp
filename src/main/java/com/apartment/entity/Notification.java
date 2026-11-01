package com.apartment.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", schema = "apartment")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private Boolean read = false;

    @Column
    private String link;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Notification() {
    }

    public Notification(User user, String title, String message, NotificationType type, Boolean read, String link) {
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
        this.read = read;
        this.link = link;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class Builder {
        private User user;
        private String title;
        private String message;
        private NotificationType type;
        private Boolean read = false;
        private String link;

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder type(NotificationType type) {
            this.type = type;
            return this;
        }

        public Builder read(Boolean read) {
            this.read = read;
            return this;
        }

        public Builder link(String link) {
            this.link = link;
            return this;
        }

        public Notification build() {
            return new Notification(user, title, message, type, read, link);
        }
    }

    public enum NotificationType {
        ANNOUNCEMENT,
        PAYMENT,
        EXPENSE,
        REPORT,
        SYSTEM
    }
}
