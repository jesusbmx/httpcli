package httpcli.adapter;

import httpcli.ResponseBody;
import httpcli.io.IOUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RespBodyFile implements RespBodyAdapter<File> {

  private final String localPath;

  public RespBodyFile(String localPath) {
    this.localPath = localPath;
  }
  
  public RespBodyFile() {
    this(null);
  }

  public String localPath() {
    return localPath;
  }
  
  public File localFile() throws IOException {
    String path = localPath();
    if (path == null || path.isEmpty()) {
      return File.createTempFile("download", ".temp");
    }
    return new File(path);
  }
  
  @Override
  public File parse(ResponseBody respBody) throws Exception {
    BufferedOutputStream bos = null;
    try {
      File file = localFile();
      bos = new BufferedOutputStream(new FileOutputStream(file));
      respBody.writeTo(bos);
      return file;
    } finally {
      IOUtils.closeQuietly(bos);
      respBody.close();
    }
  }
}
