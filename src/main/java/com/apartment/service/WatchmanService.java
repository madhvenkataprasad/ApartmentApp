package com.apartment.service;

import com.apartment.entity.Announcement;
import com.apartment.entity.Notification;
import com.apartment.entity.User;
import com.apartment.repository.AnnouncementRepository;
import com.apartment.repository.NotificationRepository;
import com.apartment.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WatchmanService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public WatchmanService(AnnouncementRepository announcementRepository, UserRepository userRepository, 
                          NotificationRepository notificationRepository) {
        this.announcementRepository = announcementRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<Announcement> getWatchmanAnnouncements(Long watchmanId) {
        return announcementRepository.findByWatchmanIdOrderByCreatedAtDesc(watchmanId);
    }

    @Transactional
    public Announcement createAnnouncement(Long watchmanId, Announcement announcement) {
        User watchman = userRepository.findById(watchmanId)
                .orElseThrow(() -> new RuntimeException("Watchman not found"));

        announcement.setWatchman(watchman);
        announcement.setApartment(watchman.getApartment());
        announcement.setPublished(true);

        Announcement savedAnnouncement = announcementRepository.save(announcement);

        // Notify all residents
        List<User> residents = userRepository.findResidentsByApartmentId(watchman.getApartment().getId());
        for (User resident : residents) {
            Notification notification = Notification.builder()
                    .user(resident)
                    .title("New Announcement: " + announcement.getTitle())
                    .message(announcement.getContent())
                    .type(Notification.NotificationType.ANNOUNCEMENT)
                    .build();
            notificationRepository.save(notification);
        }

        return savedAnnouncement;
    }

    @Transactional
    public Announcement updateAnnouncement(Long announcementId, Announcement announcement) {
        Announcement existing = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        existing.setTitle(announcement.getTitle());
        existing.setContent(announcement.getContent());
        existing.setType(announcement.getType());

        return announcementRepository.save(existing);
    }

    @Transactional
    public void deleteAnnouncement(Long announcementId) {
        announcementRepository.deleteById(announcementId);
    }
}
