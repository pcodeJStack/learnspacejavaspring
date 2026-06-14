package phucitdev.course.modules.s3.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import phucitdev.course.modules.lessonVideo.entity.VideoType;
import phucitdev.course.modules.lessonVideo.repository.LessonVideoRepository;
import phucitdev.course.modules.lessonVideo.service.LessonVideoService;
import phucitdev.course.modules.s3.dto.PresignRequest;
import phucitdev.course.modules.s3.dto.PresignResponse;
import phucitdev.course.modules.s3.service.S3PresignService;
import phucitdev.course.modules.s3.service.S3Service;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoUploadController {
    @Autowired
    LessonVideoService lessonVideoService;
    @Autowired
    LessonVideoRepository lessonVideoRepository;
    private final S3Service s3Service;
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String videoUrl = s3Service.uploadVideo(file);
        return ResponseEntity.ok(videoUrl);
    }
    private final S3PresignService s3PresignService;
    @PostMapping("/presign")
    public PresignResponse presign(@RequestBody PresignRequest req) {
        return s3PresignService.generatePresignedUrl(req);
    }
    @GetMapping("/play/{snapLessonId}")
    public ResponseEntity<List<Map<String, String>>> getVideos(@PathVariable UUID snapLessonId, @RequestParam(required = false) VideoType type) {
         List<Map<String, String>> videos =
                lessonVideoService.getVideosUrl(
                        snapLessonId,
                        type
                );

        return ResponseEntity.ok(videos);
    }
}