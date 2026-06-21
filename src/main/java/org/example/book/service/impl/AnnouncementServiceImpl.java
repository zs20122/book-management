package org.example.book.service.impl;

import org.example.book.entity.Announcement;
import org.example.book.exception.BusinessException;
import org.example.book.repository.AnnouncementRepository;
import org.example.book.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    @Override
    public List<Announcement> findAll() {
        return announcementRepository.findAll();
    }

    @Override
    public Announcement findById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new BusinessException("公告不存在"));
    }

    @Override
    public Announcement save(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    @Override
    public Announcement update(Long id, Announcement announcement) {
        Announcement existing = findById(id);
        existing.setTitle(announcement.getTitle());
        existing.setContent(announcement.getContent());
        existing.setIsActive(announcement.getIsActive());
        existing.setExpireTime(announcement.getExpireTime());
        return announcementRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        announcementRepository.deleteById(id);
    }

    @Override
    public List<Announcement> getActiveAnnouncements() {
        return announcementRepository.findByIsActiveTrueOrderByCreateTimeDesc();
    }
}