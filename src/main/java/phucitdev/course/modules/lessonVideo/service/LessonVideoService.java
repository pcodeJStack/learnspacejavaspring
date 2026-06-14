package phucitdev.course.modules.lessonVideo.service;

import jakarta.validation.Valid;
import phucitdev.course.modules.lessonVideo.dto.CreateLessonVideoRequest;
import phucitdev.course.modules.lessonVideo.dto.CreateLessonVideoResponse;
import phucitdev.course.modules.lessonVideo.dto.GetLessonVideoResponse;
import phucitdev.course.modules.lessonVideo.entity.VideoType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LessonVideoService {
    CreateLessonVideoResponse createLessonVideo(CreateLessonVideoRequest createLessonVideoRequest);
//    List<GetLessonVideoResponse> getVideos(UUID snapLessonId, VideoType videoType);
    List<Map<String, String>>  getVideosUrl(UUID snapLessonId, VideoType videoType);
}
