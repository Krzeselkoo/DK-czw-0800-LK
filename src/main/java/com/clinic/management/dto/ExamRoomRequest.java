package com.clinic.management.dto;

import com.clinic.management.model.util.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExamRoomRequest {

    @NotBlank(message = "Room code cannot be blank")
    @Pattern(regexp = "^[A-Z]-\\d{3}$", message = "Wrong room code format.")
    private String roomCode;

    @NotNull(message = "Room type cannot be null")
    private RoomType roomType;
}
