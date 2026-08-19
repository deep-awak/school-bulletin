package api.poja.app.service;

import api.poja.app.file.bucket.BucketComponent;
import java.io.File;
import java.net.URL;
import java.time.Duration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/** Thin wrapper around the existing BucketComponent, dedicated to transcript/excel storage. */
@Service
@AllArgsConstructor
public class StorageService {
  private static final Duration DEFAULT_LINK_DURATION = Duration.ofDays(7);

  private final BucketComponent bucketComponent;

  public String upload(File file, String bucketKey) {
    bucketComponent.upload(file, bucketKey);
    return presignedUrl(bucketKey);
  }

  public String presignedUrl(String bucketKey) {
    URL url = bucketComponent.presign(bucketKey, DEFAULT_LINK_DURATION);
    return url.toString();
  }
}
