package org.example.book.service;

import org.example.book.entity.Announcement;
import java.util.List;

public interface AnnouncementService {
    List<Announcement> findAll();
    Announcement findById(Long id);
    Announcement save(Announcement announcement);
    Announcement update(Long id, Announcement announcement);
    void delete(Long id);
    List<Announcement> getActiveAnnouncements();
}