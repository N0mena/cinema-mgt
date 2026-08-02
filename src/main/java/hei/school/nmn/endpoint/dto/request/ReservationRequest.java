package hei.school.nmn.endpoint.dto.request;

import java.util.List;
import java.util.UUID;

public record ReservationRequest(UUID projectionId, List<UUID> seatIds) {}
