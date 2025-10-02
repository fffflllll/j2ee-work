package my_project.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UploadController {
    @Value("${upload.dir:../uploads}") // 默认存放在项目上级j2ee-work/uploads
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件为空");
        }
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + (ext != null ? "." + ext : "");
        // 获取j2ee-work目录作为根目录
        String projectDir = System.getProperty("user.dir");
        File j2eeWorkDir = Paths.get(projectDir).getParent().toFile();
        File dir = new File(j2eeWorkDir, "uploads");
        if (!dir.exists()) dir.mkdirs();
        File dest = new File(dir, filename);
        file.transferTo(dest);
        String url = "/uploads/" + filename;
        return ResponseEntity.ok(url);
    }
}
