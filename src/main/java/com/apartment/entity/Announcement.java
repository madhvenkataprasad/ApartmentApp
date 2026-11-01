package com.apartment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "announcements", schema = "apartment")
public class Announcement {

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
    private User watchman;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnouncementType type;

    @Column(nullable = false)
    private Boolean published = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Announcement() {
    }

    public Announcement(Apartment apartment, User watchman, String title, String content, 
                       AnnouncementType type, Boolean published) {
        this.apartment = apartment;
        this.watchman = watchman;
        this.title = title;
        this.content = content;
        this.type = type;
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

    public User getWatchman() {
        return watchman;
    }

    public void setWatchman(User watchman) {
        this.watchman = watchman;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AnnouncementType getType() {
        return type;
    }

    public void setType(AnnouncementType type) {
        this.type = type;
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
        private User watchman;
        private String title;
        private String content;
        private AnnouncementType type;
        private Boolean published = true;

        public Builder apartment(Apartment apartment) {
            this.apartment = apartment;
            return this;
        }

        public Builder watchman(User watchman) {
            this.watchman = watchman;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder type(AnnouncementType type) {
            this.type = type;
            return this;
        }

        public Builder published(Boolean published) {
            this.published = published;
            return this;
        }

        public Announcement build() {
            return new Announcement(apartment, watchman, title, content, type, published);
        }
    }

    public enum AnnouncementType {
        WATER_SUPPLY,
        ELECTRICITY,
        CLEANING,
        SECURITY,
        PARKING,
        GENERAL,
        MAINTENANCE_REMINDER
    }
}
