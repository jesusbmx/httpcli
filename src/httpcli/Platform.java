package httpcli;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import javax.swing.SwingUtilities;

public class Platform implements Executor {
  private static final Platform PLATFORM = findPlatform();

  static Platform get() {
    return PLATFORM;
  }

  private static Platform findPlatform() {
    try {
      Class.forName("android.os.Build");
      if (Build.VERSION.SDK_INT != 0) {
        return new Android();
      }
    } catch (ClassNotFoundException ignored) {
    }
    try {
      Class.forName("javax.swing.SwingUtilities");
      return new JavaSwing();
    } catch (ClassNotFoundException ignored) {
    }
    return new Platform();
  }
  @Override public void execute(Runnable command) {
    command.run();
  }
  
  public static class Android extends Platform {
    final Handler mHandler = new Handler(Looper.getMainLooper());
    @Override public void execute(Runnable command) {
      mHandler.post(command);
    }
  }
  
  public static class JavaSwing extends Platform {
    @Override public void execute(Runnable command) {
      SwingUtilities.invokeLater(command);
    }
  }
}
