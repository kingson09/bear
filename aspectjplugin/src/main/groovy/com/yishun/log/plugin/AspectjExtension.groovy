package com.yishun.log.plugin;

/**
 * Created by bjliuzhanyong on 2017/9/28.
 */

public class AspectjExtension {
  List<String> includeJarFilter = new ArrayList<String>()
  List<String> excludeJarFilter = new ArrayList<String>()
  List<String> ajcArgs=new ArrayList<>();

  public AspectjExtension includeJarFilter(String...filters) {
    if (filters != null) {
      includeJarFilter.addAll(filters)
    }

    return this
  }

}
