package com.bear.core.config.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.bear.core.util.Assert;




public class ConfigurationSource {
  public static final ConfigurationSource NULL_SOURCE = new ConfigurationSource(new byte[0]);

  private final File file;
  private final URL url;
  private final String location;
  private final InputStream stream;
  private final byte[] data;


  private static byte[] toByteArray(final InputStream inputStream) throws IOException {
    final int buffSize = Math.max(4096, inputStream.available());
    final ByteArrayOutputStream contents = new ByteArrayOutputStream(buffSize);
    final byte[] buff = new byte[buffSize];

    int length = inputStream.read(buff);
    while (length > 0) {
      contents.write(buff, 0, length);
      length = inputStream.read(buff);
    }
    inputStream.close();
    return contents.toByteArray();
  }


  public ConfigurationSource(final InputStream stream) throws IOException {
    this(toByteArray(stream));
  }

  private ConfigurationSource(final byte[] data) {
    this.data = Assert.requireNonNull(data, "data is null");
    this.stream = new ByteArrayInputStream(data);
    this.file = null;
    this.url = null;
    this.location = null;
  }


  public ConfigurationSource(final InputStream stream, final File file) {
    this.stream = Assert.requireNonNull(stream, "stream is null");
    this.file = Assert.requireNonNull(file, "file is null");
    this.location = file.getAbsolutePath();
    this.url = null;
    this.data = null;
  }


  public ConfigurationSource(final InputStream stream, final URL url) {
    this.stream = Assert.requireNonNull(stream, "stream is null");
    this.url = Assert.requireNonNull(url, "URL is null");
    this.location = url.toString();
    this.file = null;
    this.data = null;
  }


  public File getFile() {
    return file;
  }


  public URL getURL() {
    return url;
  }


  public String getLocation() {
    return location;
  }


  public InputStream getInputStream() {
    return stream;
  }


  public ConfigurationSource resetInputStream() throws IOException {
    if (file != null) {
      return new ConfigurationSource(new FileInputStream(file), file);
    } else if (url != null) {
      return new ConfigurationSource(url.openStream(), url);
    } else {
      return new ConfigurationSource(data);
    }
  }

  @Override
  public String toString() {
    if (location != null) {
      return location;
    }
    if (this == NULL_SOURCE) {
      return "NULL_SOURCE";
    }
    final int length = data == null ? -1 : data.length;
    return "stream (" + length + " bytes, unknown location)";
  }
}