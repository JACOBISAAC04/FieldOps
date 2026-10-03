package com.fieldops.repository;

import com.fieldops.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByEquipmentIdOrderByUploadedAtDesc(Long equipmentId);

    List<Document> findByWorkOrderIdOrderByUploadedAtDesc(Long workOrderId);
}