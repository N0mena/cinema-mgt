package hei.school.nmn.entity;

import hei.school.nmn.entity.enums.Genre;
import hei.school.nmn.entity.model.JProjection;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public record Movie(UUID id, String title, Genre genre, String description, Duration duration, List<Projection> projection) {
}
