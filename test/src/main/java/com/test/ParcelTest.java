package com.test;

import java.io.Serializable;

/**
 * Created by bjliuzhanyong on 2018/3/16.
 */

public class ParcelTest implements Serializable {
  private int ll;
  private String a = "1dfhajhf";
  private String b = "2dfhajhf";
  private String cv = "3dfhajhf";
  private String d = "4dfhajhf";

  public ParcelTest(String s) {
    d = s;
  }

}
