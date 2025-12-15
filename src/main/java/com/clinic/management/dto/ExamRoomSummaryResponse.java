package com.clinic.management.dto;

import com.clinic.management.model.util.RoomType;

public record ExamRoomSummaryResponse(
        String roomCode,
        RoomType roomType
) {}

