package phucitdev.course.modules.lessonVideo.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import phucitdev.course.commo.exception.classroom.NotFoundException;
import phucitdev.course.modules.lessonVideo.dto.CreateLessonVideoRequest;
import phucitdev.course.modules.lessonVideo.dto.CreateLessonVideoResponse;
import phucitdev.course.modules.lessonVideo.dto.GetLessonVideoResponse;
import phucitdev.course.modules.lessonVideo.entity.LessonVideo;
import phucitdev.course.modules.lessonVideo.entity.VideoType;
import phucitdev.course.modules.lessonVideo.repository.LessonVideoRepository;
import phucitdev.course.modules.lessonVideo.service.LessonVideoService;
import phucitdev.course.modules.s3.service.S3PresignService;
import phucitdev.course.modules.s3.service.S3Service;
import phucitdev.course.modules.snap_lesson.entity.SnapLesson;
import phucitdev.course.modules.snap_lesson.repository.SnapLessonRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class LessonVideoServiceImpl implements LessonVideoService {
    @Autowired
    SnapLessonRepository snapLessonRepository;
    @Autowired
    LessonVideoRepository lessonVideoRepository;
    @Autowired
    S3PresignService s3PresignService;
    @Override
    public CreateLessonVideoResponse createLessonVideo(CreateLessonVideoRequest createLessonVideoRequest) {
        SnapLesson snapLesson = snapLessonRepository.findById(createLessonVideoRequest.getSnapLessonId()).orElseThrow(() ->
                new NotFoundException("Lesson không tìm thấy"));
        LessonVideo lessonVideo = new LessonVideo();
        lessonVideo.setSnapLesson(snapLesson);
        lessonVideo.setTitle(createLessonVideoRequest.getTitle());
        lessonVideo.setVideoType(createLessonVideoRequest.getVideoType());
        lessonVideo.setVideoUrl("videoUrl");
        lessonVideo.setFileKey(createLessonVideoRequest.getFileKey());
        lessonVideoRepository.save(lessonVideo);
        return new CreateLessonVideoResponse("LessonVideo đã tạo thành công!");
    }
//    @Override
//    public List<GetLessonVideoResponse> getVideos(UUID snapLessonId, VideoType videoType) {
//        snapLessonRepository.findById(snapLessonId)
//                .orElseThrow(() ->
//                        new NotFoundException("SnapLesson không tồn tại"));
//        if (videoType != null) {
//            return lessonVideoRepository
//                    .findBySnapLessonIdAndVideoType(
//                            snapLessonId,
//                            videoType
//                    );
//        }
//        return lessonVideoRepository.findBySnapLessonId(snapLessonId);
//    }

    @Override
    public List<Map<String, String>> getVideosUrl(UUID snapLessonId, VideoType videoType) {
        List<LessonVideo> lessonVideos;
        if (videoType != null) {
            lessonVideos = lessonVideoRepository
                    .findBySnapLessonIdAndVideoType(
                            snapLessonId,
                            videoType
                    );
        } else {
            lessonVideos = lessonVideoRepository
                    .findBySnapLessonId(
                            snapLessonId
                    );
        }

        return lessonVideos.stream()
                .map(video -> Map.of(
                        "id", video.getId().toString(),
                        "title", video.getTitle(),
                        "type", video.getVideoType().name(),
                        "url",
                        s3PresignService
                                .generatePresignedGetUrl(
                                        video.getFileKey()
                                )
                ))
                .toList();
    }
}
