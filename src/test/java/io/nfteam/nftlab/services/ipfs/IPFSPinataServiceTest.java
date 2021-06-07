package io.nfteam.nftlab.services.ipfs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IPFSPinataServiceTest {
  @Mock
  private RestTemplate restTemplate;

  @Mock
  private ByteArrayResource file;

  @Mock
  private ResponseEntity<PinataUploadImage> pinataUploadImageResponseEntity;

  @Test
  public void defaultConstructor() {
    String baseUrl = "localhost:3000";
    String apiKey = "";
    String secretKey = "";

    new IPFSPinataService(baseUrl, apiKey, secretKey, restTemplate);
  }

  @Test
  public void uploadImage_ValidImage_UploadedImage() throws IOException {
    String baseUrl = "localhost:3000";
    String apiKey = "";
    String secretKey = "";

    String hash = "QmeK3GCfbMzRp3FW3tWZCg5WVZKM52XZrk6WCTLXWwALbq";
    int pinSize = 12;
    String timestamp = "2021";

    PinataUploadImage expectedUploadedImage = new PinataUploadImage();

    expectedUploadedImage.IpfsHash = hash;
    expectedUploadedImage.PinSize = pinSize;
    expectedUploadedImage.Timestamp = timestamp;

    doReturn(when(pinataUploadImageResponseEntity.getBody()).thenReturn(expectedUploadedImage).getMock())
      .when(restTemplate)
      .postForEntity(any(String.class), ArgumentMatchers.<HttpEntity<MultiValueMap<String, Object>>>any(), any(Class.class));

    IPFSPinataService ipfsPinataService = new IPFSPinataService(baseUrl, apiKey, secretKey, restTemplate);

    IPFSResponses.UploadImage actualUploadedImage = ipfsPinataService.uploadImage(file);

    assertEquals(expectedUploadedImage.getHash(), actualUploadedImage.getHash());
    assertEquals(expectedUploadedImage.getSize(), actualUploadedImage.getSize());
    assertEquals(expectedUploadedImage.getTimestamp(), actualUploadedImage.getTimestamp());
  }
}
