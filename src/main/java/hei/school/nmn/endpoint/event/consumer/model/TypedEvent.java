package hei.school.nmn.endpoint.event.consumer.model;

import hei.school.nmn.PojaGenerated;
import hei.school.nmn.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
