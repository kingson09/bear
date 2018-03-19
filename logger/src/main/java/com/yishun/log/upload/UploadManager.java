package com.yishun.log.upload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.text.TextUtils;

import com.bear.core.Recorder;
import com.bear.core.RecorderManager;
import com.bear.core.command.CommandEvent;
import com.bear.core.command.CommandExecuteHandler;
import com.yishun.log.AndroidLog;
import com.yishun.log.util.snappy.SnappyInputStream2;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by bjliuzhanyong on 2017/9/8.
 */

public class UploadManager {
  private static final int MAX_SIZE_ONE_TIME = 20 * 1024 * 1024;//20M
  private static final String TYPE_STREAM = "text/plain; charset=utf-8";
  private static final String UPLOAD_URL = "http://applog.ttacp8.com/uploadLogs";
  //private static final String UPLOAD_URL = "http://applog.s.rrzcp8/uploadLogs";
  private static UploadManager instance = null;
  private static OkHttpClient client;
  private static Recorder recorder;
  private boolean isUploading;
  private ArrayList<File> required = new ArrayList<File>();
  private Semaphore uploadLock = new Semaphore(1);

  private UploadManager() {
    if (RecorderManager.getRecorderContext() != null) {
      recorder = RecorderManager.getRecorderContext().getRecorder("logger");
    }
  }

  public static synchronized UploadManager getInstance() {
    if (instance == null) {
      instance = new UploadManager();

    }
    return instance;
  }

  static {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS);
    SSLSocketFactory sslSocketFactory = null;
    TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
      @Override
      public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws
          CertificateException {
      }

      @Override
      public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws
          CertificateException {
      }

      @Override
      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[0];
      }
    } };
    try {
      final SSLContext sslContext = SSLContext.getInstance("SSL");
      sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
      sslSocketFactory = sslContext.getSocketFactory();
      builder.sslSocketFactory(sslSocketFactory);
    } catch (Exception ignored) {
    }
    builder.hostnameVerifier(new HostnameVerifier() {
      @Override
      public boolean verify(String hostname, SSLSession session) {
        return true;
      }
    });
    client = builder.build();
  }

  public void upload(final String unixtime) {
    if (recorder == null) {
      return;//说明项目中没有配置此logger
    }
    final long[] time = parseTime(unixtime);
    if (time == null) {
      return;
    }
    recorder.record(new CommandEvent("archive", new CommandExecuteHandler<String>() {
      @Override
      public void onExecuteComplete(String archiveDirStr) {

        File archiveDir = new File(archiveDirStr);
        if (archiveDir.exists() && archiveDir.isDirectory()) {
          File[] logs = archiveDir.listFiles();
          final ArrayList<File> required = new ArrayList<File>();

          if (logs != null && logs.length > 0) {
            for (File log : logs) {
              if (log.lastModified() > time[0] && log.lastModified() < time[1]) {
                required.add(log);
              }
            }
            if (required.isEmpty()) {
              return;
            }
            Collections.sort(required, new Comparator<File>() {
              public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0) {
                  return 1;
                } else if (diff == 0) {
                  return 0;
                } else {
                  return -1;
                }
              }

              public boolean equals(Object obj) {
                return true;
              }

            });

            new Thread(new Runnable() {
              @Override
              public void run() {
                long filesSize = 0;
                for (File file : required) {
                  if (filesSize > MAX_SIZE_ONE_TIME) {
                    break;
                  }
                  filesSize += file.length();
                  startUpload(file);
                }
              }
            }).start();

          }

        }
      }
    }

    ));
  }

  public void upload() {
    upload(null);
  }

  private void startUpload(final File logfile) {
    try {
      uploadLock.tryAcquire(5, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      e.printStackTrace();
      return;
    }
    Request.Builder requestBuilder = new Request.Builder();
    RequestBody fileBody = new RequestBody() {

      @Override
      public MediaType contentType() {
        return MediaType.parse(TYPE_STREAM);
      }

      @Override
      public void writeTo(BufferedSink sink) throws IOException {
        SnappyInputStream2 snappyInputStream = null;
        Source source = null;
        try {
          BufferedInputStream bin = new BufferedInputStream(new FileInputStream(logfile));
          snappyInputStream = new SnappyInputStream2(bin);
          source = Okio.source(snappyInputStream);
          sink.writeAll(source);

        } finally {
          if (source != null) {
            Util.closeQuietly(source);
          } else {
            Util.closeQuietly(snappyInputStream);
          }
        }

      }
    };
    RequestBody requestBody =
        new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", logfile.getName(), fileBody)
            .build();
    Request request = requestBuilder.url(UPLOAD_URL).post(requestBody).build();
    Call call = client.newCall(request);
    call.enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        AndroidLog.error("UploadManager", "upload error file:" + logfile.getName());
        uploadLock.release();
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        if (response != null && response.code() == 200 &&
            (response.body() != null && "上传成功！".equals(response.body().string()))) {
          logfile.delete();
        }
        uploadLock.release();
      }
    });
  }

  private static final Pattern TIME_PATTERN1 = Pattern.compile("^([1-9]\\d+)$");
  private static final Pattern TIME_PATTERN2 = Pattern.compile("^([1-9]\\d+)-([1-9]\\d+)$");


  private long[] parseTime(String time) {
    if (TextUtils.isEmpty(time)) {
      return null;
    }
    try {
      final Matcher matcher1 = TIME_PATTERN1.matcher(time);
      final Matcher matcher2 = TIME_PATTERN2.matcher(time);
      long current = System.currentTimeMillis();
      if (matcher1.matches()) {
        long start = NumberFormat.getNumberInstance(Locale.getDefault()).parse(matcher1.group(1)).longValue();
        if (start >= current) {
          return null;
        } else {
          return new long[] { start, current };
        }
      } else if (matcher2.matches()) {
        long start = NumberFormat.getNumberInstance(Locale.getDefault()).parse(matcher2.group(1)).longValue();
        long end = NumberFormat.getNumberInstance(Locale.getDefault()).parse(matcher2.group(2)).longValue();
        if (start >= current || start >= end) {
          return null;
        } else {
          return new long[] { start, end };
        }
      }
    } catch (Exception e) {

    }

    return null;
  }
}
