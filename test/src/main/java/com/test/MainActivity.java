package com.test;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.Semaphore;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.jiechic.library.android.snappy.Snappy;
import com.test.snappy.SnappyOutputStream2;
import com.yishun.log.AndroidLog;
import com.yishun.log.event.DefaultEventFactory;
import com.yishun.log.event.LogEvent;
import com.yishun.log.filter.Level;
import com.yishun.log.upload.UploadManager;
import com.yishun.log.util.snappy.SnappyInputStream2;

import static java.nio.charset.Charset.defaultCharset;

//import com.yishun.log.weaver.MethodLog;


public class MainActivity extends AppCompatActivity {

  private AndroidLog logger = AndroidLog.getLogger();
  private boolean islogenable = true;
  private EditText uploadEt;
  private int c = 0;
  final Handler h = new Handler();
  Runnable r = new Runnable() {
    @Override
    public void run() {
      logger.debug(
          "error clicked error clicked error clicked error clicked error clicked ,error clicked 2 error clicked");
      h.postDelayed(r, 10);
    }
  };
  private Semaphore uploadLock = new Semaphore(1);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    uploadEt = (EditText) findViewById(R.id.archive);
    AndroidLog.debug(MainActivity.class.getSimpleName(), "Activity created");
    initXlog();
    final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        AndroidLog.trace("", "clicked created");
        Snackbar.make(view, "logged click", Snackbar.LENGTH_LONG).setAction("Error", new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            //            h.post(r);
            DefaultLogFormatter formatter = new DefaultLogFormatter("format");
            long start = System.currentTimeMillis();
            //com.yishun.log.util.TestClock.resetAll();
            //com.bear.core.util.TestClock.resetAll();
            int l = 0;
            int st = 0;
            c++;
            while (l < 100000) {
              long kk = System.currentTimeMillis();
              String s =
                  "aaaaaaaaainitBetItem matches:周三008_2018-02-28;周三001_2018-02-28;周二019_2018-02-27;-----" + l + "" +
                      c +
                      "-----;chooseMatches:周二019_2018-02-27;周三001_2018-02-28;周三008_2018-02-28;bbbbbbbbbbbb";
              st += System.currentTimeMillis() - kk;
              AndroidLog.debug(MainActivity.class.getSimpleName(), s);
              //Log.d(MainActivity.class.getSimpleName(), s);
              //formatter.format(event);
              l++;
              //System.out.println("l:"+l);
            }
            System.out.println("time :" + (System.currentTimeMillis() - start) + " st:" + st);
            //com.yishun.log.util.TestClock.printTimeCunsuming();
            //com.bear.core.util.TestClock.printTimeCunsuming();

            //
            //            long ct = System.currentTimeMillis();
            //            int cc = 0;
            //            while (cc < 100000) {
            //              String cr = formatter.formatTimeCalendar(System.currentTimeMillis());
            //              cc++;
            //            }
            //            System.out.println("date:" + (System.currentTimeMillis() - ct));
            //            ct = System.currentTimeMillis();
            //            cc = 0;
            //            while (cc < 100000) {
            //              String cd = formatter.formatTimeCalendarSimpleformat(System.currentTimeMillis());
            //              cc++;
            //            }
            //            System.out.println("calendar:" + (System.currentTimeMillis() - ct));
            //            ct = System.currentTimeMillis();
            //            cc = 0;
            //            while (cc < 100000) {
            //              String cd = formatter.formatTimeCalendarSF(System.currentTimeMillis());
            //              cc++;
            //            }
            //            System.out.println("sf:" + (System.currentTimeMillis() - ct));
            //            ct = System.currentTimeMillis();
            //            cc = 0;
            //            while (cc < 100000) {
            //              String cd = formatter.formatTimeCalendarMF(System.currentTimeMillis());
            //              cc++;
            //            }
            //            System.out.println("msgf:" + (System.currentTimeMillis() - ct));
            //            ct = System.currentTimeMillis();
            //            cc = 0;
            //            while (cc < 100000) {
            //              String cd = formatter.formatTimeTimer(System.currentTimeMillis());
            //              cc++;
            //            }
            //            System.out.println("timer:" + (System.currentTimeMillis() - ct));


          }
        }).show();
      }
    });
    findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) throws NullPointerException {
        UploadManager.getInstance().upload("1509608880000");
        //testCompress();
        //testSnappy();
        //testUnCompress();
      }
    });
    findViewById(R.id.archive).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) throws NullPointerException {

        //UploadManager.getInstance().upload(uploadEt.getText().toString());

      }
    });
    findViewById(R.id.file).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) throws NullPointerException {
        testCompress();
        testUnCompress();
      }
    });
    findViewById(R.id.mb).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) throws NullPointerException {
        //writemb();
      }
    });
    findViewById(R.id.mf).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) throws NullPointerException {
        //writemf();
      }
    });
    findViewById(R.id.f_mb).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) throws NullPointerException {
        try {
          File logs = MainActivity.this.getExternalFilesDir(null);
          File f = new File(logs.getAbsolutePath() + "/logsmb/mf.log");
          File mb = new File(logs.getAbsolutePath() + "/logsmb/mb.log");

          FileInputStream fii = new FileInputStream(f);
          FileInputStream mbi = new FileInputStream(mb);
          byte[] a = new byte[1];
          byte[] b = new byte[1];
          int j = 0;
          while (mbi.read(a) != -1) {
            System.out.println("diff a=" + new String(a));
            fii.read(b);
            j++;
            if (a[0] != b[0]) {
              if (b[0] != -1) {
                System.out.println("diff a=" + new String(a) + " b=" + new String(b) + " j=" + j);
              } else {
                System.out.println("diff a=" + new String(a) + " b=-1");
              }

            }
          }
          fii.close();
          mbi.close();

        } catch (FileNotFoundException e) {

        } catch (IOException ioe) {

        }

      }
    });
    findViewById(R.id.f_mf).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) throws NullPointerException {
        Charset charset = defaultCharset();
        try {
          File logs = MainActivity.this.getExternalFilesDir(null);
          File f = new File(logs.getAbsolutePath() + "/logs/f.log");
          File mb = new File(logs.getAbsolutePath() + "/logs/mf.log");
          f.length();
          mb.length();
          FileInputStream fii = new FileInputStream(f);
          FileInputStream mbi = new FileInputStream(mb);
          byte[] a = new byte[1];
          byte[] b = new byte[1];
          int j = 0;
          while (mbi.read(a) != -1) {
            int br = fii.read(b);
            j++;
            if (a[0] != b[0]) {
              f.length();
              if (br != -1) {
                System.out.println("diff a=" + new String(a) + " b=" + new String(b) + " j=" + j);
              } else {
                System.out.println("diff a=" + new String(a) + " b=-1");
              }

            }
          }
          fii.close();
          mbi.close();

        } catch (FileNotFoundException e) {

        } catch (IOException ioe) {

        }
      }
    });
    findViewById(R.id.test_mmap).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) throws NullPointerException {
        // testMethod();
        File logs = MainActivity.this.getExternalFilesDir(null);
        final String logPath = logs.getAbsolutePath() + "/logs/";
        FileChannel channel = null;
        MappedByteBuffer buffer;
        try {
          channel = new RandomAccessFile(new File(logPath + "/mb.buffer"), "rw").getChannel();
          buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 256 * 1024);
          buffer.put("dajhdj".getBytes());
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException ee) {
          ee.printStackTrace();
        } finally {
          if (channel != null) {
            try {
              channel.close();
              channel = new RandomAccessFile(new File(logPath + "/mb.buffer"), "rw").getChannel();
              buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 256 * 1024);
            } catch (IOException e) {
              e.printStackTrace();
            }
          }

        }


      }
    });
    findViewById(R.id.test_bytes).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) throws NullPointerException {
        File logs = MainActivity.this.getExternalFilesDir(null);
        final String logPath = logs.getAbsolutePath() + "/logs/";
        try {
          FileChannel channel = new RandomAccessFile(new File(logPath + "/mbt.buffer"), "rw").getChannel();
          MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0L, 100);
          String s1 = "aaaaaaaaainitBetItem matches:周三008_2018-02-28;周三001_2018-02-28;周二019_2018-02-27;-----" + 0 +
              "-----;chooseMatches:周二019_2018-02-27;周三001_2018-02-28;周三008_2018-02-28;bbbbbbbbbbbb" + "\0";
          byte[] s1b = s1.getBytes();
          buffer.put(s1b, 0, buffer.capacity());
          int position = buffer.position();
          buffer.clear();
          buffer.position(buffer.capacity());
          buffer.remaining();
          buffer.hasRemaining();
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {

        }


      }
    });
    findViewById(R.id.snappycompress).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) throws NullPointerException {
        snappycompress();
      }
    });
    findViewById(R.id.filecopy).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) throws NullPointerException {
        copy();
      }
    });
    findViewById(R.id.gzipcompress).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) throws NullPointerException {
        gzipcompress();
      }
    });
    findViewById(R.id.snappyUncompress).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) throws NullPointerException {
        snappyUncompress();
      }
    });
    findViewById(R.id.gzipUncompress).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) throws NullPointerException {
        gzipUncompress();
      }
    });
  }

  public void testFormatter() {
    DefaultLogFormatter formatter = new DefaultLogFormatter("format");
    String s = "aaaaaaaaainitBetItem matches:周三008_2018-02-28;周三001_2018-02-28;周二019_2018-02-27;-----" + 1 + "" + c +
        "-----;chooseMatches:周二019_2018-02-27;周三001_2018-02-28;周三008_2018-02-28;bbbbbbbbbbbb";
    LogEvent event = (LogEvent) DefaultEventFactory.createEvent("AndroidLog", "logger", "MainActivity", s, Level.DEBUG);
    long ct = System.currentTimeMillis();
    int cc = 0;
    while (cc < 100000) {
      formatter.format(event);
      cc++;
    }
    System.out.println("format:" + (System.currentTimeMillis() - ct));
    ct = System.currentTimeMillis();
    cc = 0;
    while (cc < 100000) {
      formatter.formatGson(event);
      cc++;
    }
    System.out.println("formatJson:" + (System.currentTimeMillis() - ct));
    ct = System.currentTimeMillis();
    cc = 0;
    while (cc < 100000) {
      formatter.formatJson(event);
      cc++;
    }
    System.out.println("formatGson:" + (System.currentTimeMillis() - ct));
  }

  public void testGson() {
    File logdir = MainActivity.this.getExternalFilesDir(null);
    File f = new File(logdir.getAbsolutePath() + "/logsmb/app.log");
    final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    try {
      FileReader in = new FileReader(f);
      StringBuilder sb = new StringBuilder(2 * 1024 * 1024);
      char[] buffer = new char[256];
      while (in.read(buffer) != -1) {
        sb.append(buffer);
      }
      Type appLogType = new TypeToken<AppLogVo>() {
      }.getType();

      Gson gson = new GsonBuilder().registerTypeAdapter(Long.class, new JsonDeserializer<Long>() {
        @Override
        public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
            JsonParseException {
          String datetime = json.getAsJsonPrimitive().getAsString();
          try {
            return format.parse(datetime).getTime();
          } catch (ParseException e) {
            e.printStackTrace();
            return 0000000000L;
          }
        }
      }).create();
      String[] logs = sb.toString().split("\0");
      String commonContent = logs[0].replace("{", "").replace("}", "");
      for (int i = 1; i < logs.length; i++) {
        String newContent = logs[i].trim().replaceFirst("\\{", "{" + commonContent + ",");
        System.out.println(newContent);
        AppLogVo tmpVo = gson.fromJson(newContent, appLogType);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {

    }
  }

  public void snappycompress() {
    try {
      File logdir = MainActivity.this.getExternalFilesDir(null);
      File f = new File(logdir.getAbsolutePath() + "/logsmb/app.log");
      BufferedInputStream bin = new BufferedInputStream(new FileInputStream(f));
      BufferedOutputStream buffer =
          new BufferedOutputStream(new FileOutputStream(new File(logdir.getAbsolutePath() + "/logsmb/snappy.log")),
              256 * 1024);
      SnappyOutputStream2 snappyOutputStream =
          new SnappyOutputStream2(buffer, f.getParent(), "SnappyBuffer", 32 * 1024);
      long ct = System.currentTimeMillis();
      int rlen = 0;
      byte[] b = new byte[256 * 1024];
      while ((rlen = bin.read(b)) != -1) {
        snappyOutputStream.write(b, 0, rlen);
      }
      snappyOutputStream.close();
      bin.close();
      System.out.println("Snappy compress cost:" + (System.currentTimeMillis() - ct));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void copy() {
    try {
      File logdir = MainActivity.this.getExternalFilesDir(null);
      File f = new File(logdir.getAbsolutePath() + "/logsmb/app.log");

      BufferedInputStream bin = new BufferedInputStream(new FileInputStream(f));
      BufferedOutputStream buffer =
          new BufferedOutputStream(new FileOutputStream(new File(logdir.getAbsolutePath() + "/logsmb/copy.log")),
              256 * 1024);
      long ct = System.currentTimeMillis();
      byte[] b = new byte[256 * 1024];
      int rlen = 0;
      while ((rlen = bin.read(b)) != -1) {
        buffer.write(b, 0, rlen);
      }
      buffer.close();
      bin.close();
      System.out.println("copy cost:" + (System.currentTimeMillis() - ct));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void gzipcompress() {
    try {
      File logdir = MainActivity.this.getExternalFilesDir(null);
      File f = new File(logdir.getAbsolutePath() + "/logsmb/app.log");

      BufferedInputStream bin = new BufferedInputStream(new FileInputStream(f));
      BufferedOutputStream buffer =
          new BufferedOutputStream(new FileOutputStream(new File(logdir.getAbsolutePath() + "/logsmb/gzip.log")),
              256 * 1024);
      long ct = System.currentTimeMillis();
      GZIPOutputStream gzip = new GZIPOutputStream(buffer);
      byte[] b = new byte[256 * 1024];
      int rlen = 0;
      while ((rlen = bin.read(b)) != -1) {
        gzip.write(b, 0, rlen);
      }
      gzip.close();
      buffer.close();
      bin.close();
      System.out.println("GZIP cost:" + (System.currentTimeMillis() - ct));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void testCompress() {
    try {
      File logdir = MainActivity.this.getExternalFilesDir(null);
      File f = new File(logdir.getAbsolutePath() + "/logsmb/app.log");
      BufferedInputStream bin = new BufferedInputStream(new FileInputStream(f));
      BufferedOutputStream buffer =
          new BufferedOutputStream(new FileOutputStream(new File(logdir.getAbsolutePath() + "/logsmb/snappy.log")),
              256 * 1024);
      SnappyOutputStream2 snappyOutputStream =
          new SnappyOutputStream2(buffer, f.getParent(), "SnappyBuffer", 32 * 1024);
      long ct = System.currentTimeMillis();
      int rlen = 0;
      byte[] b = new byte[256 * 1024];
      while ((rlen = bin.read(b)) != -1) {
        snappyOutputStream.write(b, 0, rlen);
      }
      snappyOutputStream.close();
      bin.close();
      System.out.println("Snappy cost:" + (System.currentTimeMillis() - ct));
      bin = new BufferedInputStream(new FileInputStream(f));
      buffer = new BufferedOutputStream(new FileOutputStream(new File(logdir.getAbsolutePath() + "/logsmb/gzip.log")),
          256 * 1024);
      ct = System.currentTimeMillis();
      GZIPOutputStream gzip = new GZIPOutputStream(buffer);
      b = new byte[256 * 1024];
      while ((rlen = bin.read(b)) != -1) {
        gzip.write(b, 0, rlen);
      }
      gzip.close();
      buffer.close();
      bin.close();
      System.out.println("GZIP cost:" + (System.currentTimeMillis() - ct));
      bin = new BufferedInputStream(new FileInputStream(f));
      buffer = new BufferedOutputStream(new FileOutputStream(new File(logdir.getAbsolutePath() + "/logsmb/copy.log")),
          256 * 1024);
      ct = System.currentTimeMillis();
      b = new byte[256 * 1024];
      while ((rlen = bin.read(b)) != -1) {
        buffer.write(b, 0, rlen);
      }
      buffer.close();
      bin.close();
      System.out.println("copy cost:" + (System.currentTimeMillis() - ct));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void snappyUncompress() {
    try {
      File logdir = MainActivity.this.getExternalFilesDir(null);

      BufferedInputStream bin =
          new BufferedInputStream(new FileInputStream(new File(logdir.getAbsolutePath() + "/logsmb/snappy.log")));
      BufferedOutputStream bio = new BufferedOutputStream(
          new FileOutputStream(new File(logdir.getAbsolutePath() + "/logsmb/snappyuncompre.log")));
      SnappyInputStream2 snappyInputStream = new SnappyInputStream2(bin);
      long ct = System.currentTimeMillis();
      byte[] buffer = new byte[256 * 1024];
      int len = 0;
      while ((len = snappyInputStream.read(buffer)) != -1) {
        bio.write(buffer, 0, len);
      }
      bin.close();
      bio.close();
      System.out.println("Snappy uncompress cost:" + (System.currentTimeMillis() - ct));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void gzipUncompress() {
    try {
      File logdir = MainActivity.this.getExternalFilesDir(null);


      byte[] buffer = new byte[256 * 1024];
      BufferedInputStream bin =
          new BufferedInputStream(new FileInputStream(new File(logdir.getAbsolutePath() + "/logsmb/gzip.log")));
      BufferedOutputStream bio = new BufferedOutputStream(
          new FileOutputStream(new File(logdir.getAbsolutePath() + "/logsmb/gzipuncompre.log")));
      long ct = System.currentTimeMillis();
      GZIPInputStream gzip = new GZIPInputStream(bin);
      int len = 0;
      while ((len = gzip.read(buffer)) != -1) {
        bio.write(buffer, 0, len);
      }
      bin.close();
      bio.close();
      System.out.println("GZIP uncompress cost:" + (System.currentTimeMillis() - ct));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void testUnCompress() {
    try {
      File logdir = MainActivity.this.getExternalFilesDir(null);

      BufferedInputStream bin =
          new BufferedInputStream(new FileInputStream(new File(logdir.getAbsolutePath() + "/logsmb/snappy.log")));
      BufferedOutputStream bio = new BufferedOutputStream(
          new FileOutputStream(new File(logdir.getAbsolutePath() + "/logsmb/snappyuncompre.log")));
      SnappyInputStream2 snappyInputStream = new SnappyInputStream2(bin);
      long ct = System.currentTimeMillis();
      byte[] buffer = new byte[256 * 1024];
      int len = 0;
      while ((len = snappyInputStream.read(buffer)) != -1) {
        bio.write(buffer, 0, len);
      }
      bin.close();
      bio.close();
      System.out.println("Snappy uncompress cost:" + (System.currentTimeMillis() - ct));
      buffer = new byte[256 * 1024];
      bin = new BufferedInputStream(new FileInputStream(new File(logdir.getAbsolutePath() + "/logsmb/gzip.log")));
      bio = new BufferedOutputStream(
          new FileOutputStream(new File(logdir.getAbsolutePath() + "/logsmb/gzipuncompre.log")));
      ct = System.currentTimeMillis();
      GZIPInputStream gzip = new GZIPInputStream(bin);
      len = 0;
      while ((len = gzip.read(buffer)) != -1) {
        bio.write(buffer, 0, len);
      }
      bin.close();
      bio.close();
      System.out.println("GZIP uncompress cost:" + (System.currentTimeMillis() - ct));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void testSnappy() {
    try {
      ByteBuffer inputBuffer = ByteBuffer.allocateDirect(34 * 1024);
      ByteBuffer ouputBuffer = ByteBuffer.allocateDirect(34 * 1024);
      String input =
          "Hello snappy-java! Snappy-java is a JNI-based wrapper of " + "Snappy, a fast compresser/decompresser.";
      inputBuffer.put(input.getBytes("UTF-8"));
      inputBuffer.flip();
      int compressed = Snappy.compress(inputBuffer, ouputBuffer);
      byte[] b = new byte[ouputBuffer.limit()];

      ouputBuffer.get(b);
      System.out.println(new String(Snappy.uncompress(b)));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initXlog() {
    //    System.loadLibrary("stlport_shared");
    //    System.loadLibrary("marsxlog");
    //    File logs = MainActivity.this.getExternalFilesDir(null);
    //    final String logPath = logs.getAbsolutePath() + "/logs/xlog/";
    //
    //    // this is necessary, or may cash for SIGBUS
    //    final String cachePath = logPath + "/xlogcache";
    //
    //    //init xlog
    //    if (BuildConfig.DEBUG) {
    //      Xlog.appenderOpen(Xlog.LEVEL_DEBUG, Xlog.AppednerModeSync, cachePath, logPath, "MarsSample", "");
    //      Xlog.setConsoleLogOpen(false);
    //
    //    } else {
    //      Xlog.appenderOpen(Xlog.LEVEL_INFO, Xlog.AppednerModeSync, cachePath, logPath, "MarsSample", "");
    //      Xlog.setConsoleLogOpen(false);
    //    }
    //    Log.setLogImp(new Xlog());
  }

  //@MethodLog
  private void testMethod() {
    File logs = MainActivity.this.getExternalFilesDir(null);
    File f = new File(logs.getAbsolutePath() + "/logs/test_mmap.log");
    f.length();
    if (f.exists()) {
      f.delete();
    }

    try {
      f.createNewFile();
      FileChannel channel = new RandomAccessFile(f, "rw").getChannel();
      MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 1024 * 1024);
      String s = "aaaaaaaaainitBetItem matches:周三008_2018-02-28;周三001_2018-02-28;周二019_2018-02-27;-----" + 0 +
          "-----;chooseMatches:周二019_2018-02-27;周三001_2018-02-28;周三008_2018-02-28;bbbbbbbbbbbb" + "\0";
      byte[] bytes = s.getBytes();
      int l = 0;
      l += bytes.length;
      while (l < 1024 * 1024) {
        buffer.put(bytes);
        l += bytes.length;
      }
      buffer.force();
      channel.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    f.length();
    f.toString();

  }

  private void writefile() {
    File f = this.getExternalFilesDir(null);
    File log = new File(f.getAbsolutePath() + "/logs/f.log");
    //    if (log.exists()) {
    //      log.delete();
    //    }
    if (!log.exists()) {
      try {
        log.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    FileOutputStream logoutput = null;
    BufferedOutputStream bufferedOutputStream = null;
    try {
      logoutput = new FileOutputStream(log);
      bufferedOutputStream = new BufferedOutputStream(logoutput, 512 * 1024);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    String s = "aaaaaaaaainitBetItem matches:周三008_2018-02-28;周三001_2018-02-28;周二019_2018-02-27;-----" + 0 +
        "-----;chooseMatches:周二019_2018-02-27;周三001_2018-02-28;周三008_2018-02-28;bbbbbbbbbbbb" + "\0";
    byte[] bytes = s.getBytes();
    System.out.println("FileOutputStream: start");
    long mb = System.currentTimeMillis();
    int j = 0;
    while (j < 100000) {
      try {
        bufferedOutputStream.write(bytes);
      } catch (IOException e) {
        e.printStackTrace();
      }
      j++;
    }
    try {
      bufferedOutputStream.close();
      logoutput.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("FileOutputStream:" + (System.currentTimeMillis() - mb));
  }

  //  private void writemb() {
  //    File f = this.getExternalFilesDir(null);
  //    File log = new File(f.getAbsolutePath() + "/logsmb/mb.log");
  //    if (log.exists()) {
  //      log.delete();
  //    }
  //    try {
  //      log.createNewFile();
  //    } catch (IOException e) {
  //      e.printStackTrace();
  //    }
  //    FileOutputStream logoutput = null;
  //    try {
  //      logoutput = new FileOutputStream(log);
  //    } catch (FileNotFoundException e) {
  //      e.printStackTrace();
  //    }
  //    DefaultLogFormatter formatter = new DefaultLogFormatter("format");
  //    String s = "aaaaaaaaainitBetItem matches:周三008_2018-02-28;周三001_2018-02-28;周二019_2018-02-27;-----" + 1 + "" + c +
  //        "-----;chooseMatches:周二019_2018-02-27;周三001_2018-02-28;周三008_2018-02-28;bbbbbbbbbbbb";
  //    LogEvent event = (LogEvent) DefaultEventFactory.createEvent("AndroidLog", "logger", "MainActivity", s, Level.DEBUG);
  //    System.out.println("MappedBufferedOutputStream: start");
  //    long mb = System.currentTimeMillis();
  //    MappedBufferedOutputStream mappedBufferedOutputStream =
  //        new MappedBufferedOutputStream(log.getAbsolutePath(), logoutput, 256 * 1024);
  //    //BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(logoutput, 256 * 1024);
  //    String logs =
  //        "{\"logWriteTime\":\"2018-3-5 11:47:8:233\",\"log\":\"[main-1] aaaaaaaaainitBetItem matches:周三008_2018-02-28;周三001_2018-02-28;周二019_2018-02-27;-----10-----;chooseMatches:周二019_2018-02-27;周三001_2018-02-28;周三008_2018-02-28;bbbbbbbbbbbb\",\"logFunction\":\"MainActivity\",\"level\":\"DEBUG\"}\0";
  //    int j = 0;
  //    try {
  //      while (j < 10) {
  //        mappedBufferedOutputStream.write(logs.getBytes(Charset.forName("utf-8")));
  //
  //        j++;
  //      }
  //      mappedBufferedOutputStream.close();
  //    } catch (IOException e) {
  //      e.printStackTrace();
  //    }
  //
  //    System.out.println("MappedBufferedOutputStream:" + (System.currentTimeMillis() - mb));
  //  }
  //
  //  private void writemf() {
  //    File f = this.getExternalFilesDir(null);
  //    File log = new File(f.getAbsolutePath() + "/logs/mf.log");
  //    //    if (log.exists()) {
  //    //      log.delete();
  //    //    }
  //    if (!log.exists()) {
  //      try {
  //        log.createNewFile();
  //      } catch (IOException e) {
  //        e.printStackTrace();
  //      }
  //    }
  //    log.length();
  //    try {
  //      String s = "aaaaaaaaainitBetItem matches:周三008_2018-02-28;周三001_2018-02-28;周二019_2018-02-27;-----" + 0 +
  //          "-----;chooseMatches:周二019_2018-02-27;周三001_2018-02-28;周三008_2018-02-28;bbbbbbbbbbbb" + "\0";
  //      byte[] bytes = s.getBytes();
  //      long mb = System.currentTimeMillis();
  //      System.out.println("MappedFileOutputStream: start=" + mb);
  //      MappedFileOutputStream mappedFileOutputStream = new MappedFileOutputStream(log, 1024 * 1024);
  //      int j = 0;
  //      while (j < 100000) {
  //        mappedFileOutputStream.write(bytes);
  //        j++;
  //      }
  //      mappedFileOutputStream.close();
  //      System.out.println("MappedFileOutputStream:" + (System.currentTimeMillis() - mb));
  //    } catch (FileNotFoundException e) {
  //      e.printStackTrace();
  //    } catch (IOException e1) {
  //      e1.printStackTrace();
  //    }
  //
  //  }
}
