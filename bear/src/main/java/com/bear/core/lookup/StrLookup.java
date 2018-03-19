package com.bear.core.lookup;

import com.bear.core.Event;



public interface StrLookup {




  String lookup(String key);


  String lookup(Event event, String key);
}
