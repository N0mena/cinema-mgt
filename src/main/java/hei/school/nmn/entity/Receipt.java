package hei.school.nmn.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record Receipt(UUID id, Reservation reservation, LocalDateTime createdAt, String filePath) {}
