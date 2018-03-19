package com.yishun.log.appender.archive.strategy.action;

import java.io.File;
import java.util.ArrayList;


public class FileDeleteAction extends AbstractAction {
  private final ArrayList<File> toDelete;

  public FileDeleteAction(ArrayList<File> toDelete) {
    this.toDelete = toDelete;
  }

  @Override
  public boolean execute() {
    try {
      for (File file : toDelete) {
        file.delete();
      }
      return true;
    } catch (Exception e) {

    }
    return false;
  }
}
