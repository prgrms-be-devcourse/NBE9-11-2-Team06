package com.back.nbe9112team06.domain.timeblock.controller;

import com.back.nbe9112team06.domain.timeblock.dto.TimeBlockDeleteRequest;
import com.back.nbe9112team06.domain.timeblock.dto.TimeBlockRequest;
import com.back.nbe9112team06.domain.timeblock.service.TimeBlockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TimeBlockController {
    private final TimeBlockService timeBlockService;

    @PostMapping("/meetings/{meetingId}/time-blocks")
    public ResponseEntity<Void> addTimeBlock(@PathVariable Integer meetingId, @RequestBody @Valid TimeBlockRequest timeBlockRequest){
        timeBlockService.registerTimeBlock(meetingId, timeBlockRequest);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/meetings/{meetingId}/time-blocks")
    public ResponseEntity<Void> deleteTimeBlock(@PathVariable Integer meetingId, @RequestBody @Valid TimeBlockDeleteRequest timeBlockDeleteRequest){
        timeBlockService.deleteTImeBlock(meetingId, timeBlockDeleteRequest);
        return ResponseEntity.status(204).build();
    }
}
